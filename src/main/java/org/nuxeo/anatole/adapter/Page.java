package org.nuxeo.anatole.adapter;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public interface Page {

    public abstract DocumentModel getDocument();

    public abstract String getTitle() throws ClientException;

    public abstract Calendar getIssueDate() throws Exception;

    public abstract DocumentModelList getArticles() throws ClientException;
}