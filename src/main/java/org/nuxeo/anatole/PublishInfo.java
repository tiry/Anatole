package org.nuxeo.anatole;

import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public class PublishInfo {

    protected final Page page;

    protected final DocumentModel article;

    protected final String versionLabel;

    public PublishInfo(Page page, DocumentModel article) {
        this.page = page;
        this.article = article;
        this.versionLabel = article.getVersionLabel();
    }

    public Page getPage() {
        return page;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    @Override
    public String toString() {
        try {
            return page.getTitle() + " - " + getVersionLabel();
        } catch (ClientException e) {
            return "Error : " + e.getClass().getName() + " :" + e.getMessage();
        }
    }
}
