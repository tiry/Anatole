package org.nuxeo.anatole.web;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.anatole.PageManager;
import org.nuxeo.anatole.adapter.Article;
import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

@Name("Anatole")
@Scope(ScopeType.PAGE)
public class AnatoleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true)
    protected NavigationContext navigationContext;

    @In(create = true)
    protected CoreSession documentManager;

    protected String targetPageId;

    public String getTargetPageId() {
        return targetPageId;
    }

    public void setTargetPageId(String targetPageId) {
        this.targetPageId = targetPageId;
    }

    @Factory(value = "currentArticle", scope = ScopeType.EVENT)
    public Article getCurrentArticle() throws Exception {
        return navigationContext.getCurrentDocument().getAdapter(Article.class);
    }

    @Factory(value = "currentPage", scope = ScopeType.EVENT)
    public Page getCurrentPage() throws Exception {
        DocumentModel doc = navigationContext.getCurrentDocument();
        Page page = doc.getAdapter(Page.class);
        if (page == null) {
            DocumentModel superSpace = documentManager.getSuperSpace(doc);
            if (superSpace != null) {
                page = superSpace.getAdapter(Page.class);
            }
        }
        return page;
    }

    public String publishCurrentArticle() throws ClientException {

        if (targetPageId == null) {
            return null;
        }
        return publishCurrentArticle(targetPageId);
    }

    public String publishCurrentArticle(String docId) throws ClientException {

        DocumentModel target = documentManager.getDocument(new IdRef(docId));

        PageManager pm = Framework.getLocalService(PageManager.class);
        DocumentModel source = navigationContext.getCurrentDocument();
        Page page = target.getAdapter(Page.class);
        pm.publish(source, page);

        return null;
    }

}
