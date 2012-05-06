/**
 *
 */

package org.nuxeo.anatole;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.anatole.adapter.PageAdapter;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */
public class PageManagerImpl extends DefaultComponent implements PageManager {

    protected Bundle bundle;

    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void activate(ComponentContext context) {
        this.bundle = context.getRuntimeContext().getBundle();
    }

    @Override
    public void deactivate(ComponentContext context) {
        this.bundle = null;
    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        // do nothing by default. You can remove this method if not used.
    }

    protected Calendar getMaxDate() {
        Calendar max = new GregorianCalendar();
        max.add(Calendar.DAY_OF_MONTH, -Constants.MAX_DAYS_KEPT_LIVE - 1);
        return max;
    }

    protected boolean shouldBeLive(Calendar date) {
        return date.after(getMaxDate());
    }

    protected DocumentModel getRoot(CoreSession session) throws ClientException {
        DocumentModelList docs = session.query("select * from "
                + Constants.PAGE_ROOT_TYPE);
        if (docs.size() > 0) {
            return docs.get(0);
        }
        return null;
    }

    public DocumentModel getLiveContainer(CoreSession session)
            throws ClientException {
        DocumentModel root = getRoot(session);
        if (root == null) {
            return null;
        }
        Path path = root.getPath();
        path = path.append(Constants.LIVE_CONTAINER_NAME);

        PathRef ref = new PathRef(path.toString());
        if (session.exists(ref)) {
            return session.getDocument(ref);
        } else {
            if (session.hasPermission(root.getRef(),
                    SecurityConstants.ADD_CHILDREN)) {
                DocumentModel doc = session.createDocumentModel(
                        root.getPathAsString(), Constants.LIVE_CONTAINER_NAME,
                        Constants.PAGE_CONTAINER_TYPE);
                doc.setPropertyValue("dc:title", "Live pages");
                return session.createDocument(doc);
            }
        }
        return null;
    }

    public DocumentModel getArchiveContainer(CoreSession session)
            throws ClientException {
        DocumentModel root = getRoot(session);
        if (root == null) {
            return null;
        }
        Path path = root.getPath();
        path = path.append(Constants.ARCHIVES_CONTAINER_NAME);

        PathRef ref = new PathRef(path.toString());
        if (session.exists(ref)) {
            return session.getDocument(ref);
        } else {
            if (session.hasPermission(root.getRef(),
                    SecurityConstants.ADD_CHILDREN)) {
                DocumentModel doc = session.createDocumentModel(
                        root.getPathAsString(), Constants.LIVE_CONTAINER_NAME,
                        Constants.PAGE_CONTAINER_TYPE);
                doc.setPropertyValue("dc:title", "Archived Pages");
                return session.createDocument(doc);
            }
        }
        return null;
    }

    public Page getPage(CoreSession session, Calendar date, boolean create)
            throws ClientException {

        Page page = PageAdapter.find(session, date);
        if (page != null) {
            return page;
        } else if (create) {
            return createLivePage(session, date);
        } else {
            return null;
        }
    }

    public Page createLivePage(CoreSession session, Calendar date)
            throws ClientException {
        DocumentModel container = getLiveContainer(session);
        if (container == null) {
            return null;
        }
        if (session.hasPermission(container.getRef(),
                SecurityConstants.ADD_CHILDREN)) {

            return PageAdapter.create(session, container.getPathAsString(),
                    date);
        }
        return null;
    }

    protected void moveOldPages(CoreSession session) throws Exception {
        DocumentModel archive = getArchiveContainer(session);
        if (archive == null) {
            return;
        }
        DocumentModel live = getLiveContainer(session);
        if (live != null) {
            DocumentModelList list = session.getChildren(live.getRef());
            for (DocumentModel doc : list) {
                Calendar issueDate = doc.getAdapter(Page.class).getIssueDate();
                if (!shouldBeLive(issueDate)) {
                    session.move(doc.getRef(),
                            getArchiveContainer(session).getRef(),
                            doc.getName());
                }
            }
        }
    }

    @Override
    public void checkPagesStructure(CoreSession session) throws ClientException {
        // force flush before check
        session.save();
        // Move old pages to archive
        try {
            moveOldPages(session);
        } catch (Exception e) {
            throw new ClientException(e);
        }
        // Create new pages if needed
        for (int i = -Constants.MAX_DAYS_KEPT_LIVE; i <= Constants.MAX_DAYS_FUTURE; i++) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_YEAR, i);
            getPage(session, date, true);
        }

    }

    public List<Page> getLivePages(CoreSession session) throws ClientException {

        List<Page> pages = new ArrayList<Page>();

        String query = "select * from Document where ecm:parentId='"
                + getLiveContainer(session).getId();
        query = query + "' order by " + PageAdapter.PAGE_DATE_PROP + " DESC";

        // DocumentModelList docs =
        // session.getChildren(getLiveContainer(session).getRef());
        DocumentModelList docs = session.query(query);
        for (DocumentModel doc : docs) {
            pages.add(doc.getAdapter(Page.class));
        }
        return pages;
    }

    @Override
    public DocumentModel publish(DocumentModel source, Calendar date)
            throws ClientException {

        CoreSession session = source.getCoreSession();

        Page page = getPage(session, date, true);

        if (page == null) {
            throw new ClientException("Can not find or create page for date "
                    + date.toString());
        }

        return publish(source, page);
    }

    @Override
    public DocumentModel publish(DocumentModel source, Page page)
            throws ClientException {

        CoreSession session = source.getCoreSession();

        return session.publishDocument(source, page.getDocument(), true);
    }

}
