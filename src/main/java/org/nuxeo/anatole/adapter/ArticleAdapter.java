package org.nuxeo.anatole.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.nuxeo.anatole.PageManager;
import org.nuxeo.anatole.PublishInfo;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

public class ArticleAdapter implements Serializable, Article {

    private static final long serialVersionUID = 1L;

    protected final DocumentModel doc;

    protected ArticleAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public List<PublishInfo> getPublishInfo() throws ClientException {

        List<PublishInfo> info = new ArrayList<PublishInfo>();

        CoreSession session = doc.getCoreSession();
        List<DocumentModel> versions = session.getVersions(doc.getRef());
        if (versions.size() > 0) {
            for (DocumentModel version : versions) {
                DocumentModelList proxies = session.getProxies(
                        version.getRef(), null);
                for (DocumentModel proxy : proxies) {
                    DocumentModel page = session.getDocument(proxy.getParentRef());

                    if (page.getType().equals("Page")) {
                        info.add(new PublishInfo(page.getAdapter(Page.class),
                                proxy));
                    }
                }
            }
        }

        Collections.sort(info, new Comparator<PublishInfo>() {
            @Override
            public int compare(PublishInfo pi1, PublishInfo pi2) {
                try {
                    return pi1.getPage().getIssueDate().compareTo(
                            pi2.getPage().getIssueDate());
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        return info;
    }

    @Override
    public List<Page> getPotentialTargets() throws ClientException {
        PageManager pm = Framework.getLocalService(PageManager.class);

        List<Page> pages = pm.getLivePages(doc.getCoreSession());

        List<PublishInfo> alreadyPublishedTargets = getPublishInfo();

        for (PublishInfo info : alreadyPublishedTargets) {
            if (pages.contains(info.getPage())) {
                pages.remove(info.getPage());
            }
        }

        return pages;
    }
}
