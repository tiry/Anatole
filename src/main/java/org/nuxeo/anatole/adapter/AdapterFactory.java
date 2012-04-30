package org.nuxeo.anatole.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class AdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> adapterClass) {

        if (doc.getType().equals(PageAdapter.PAGE_TYPE)
                && adapterClass.getName().equals(Page.class.getName())) {
            return new PageAdapter(doc);
        }

        if (adapterClass.getName().equals(Article.class.getName())) {
            return new ArticleAdapter(doc);
        }

        return null;
    }

}
