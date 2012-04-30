package org.nuxeo.anatole;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface PageManager {

    List<Page> getLivePages(CoreSession session) throws ClientException;

    Page getPage(CoreSession session, Calendar date, boolean create)
            throws ClientException;

    void checkPagesStructure(CoreSession session) throws ClientException;

    DocumentModel getLiveContainer(CoreSession session) throws ClientException;

    DocumentModel getArchiveContainer(CoreSession session)
            throws ClientException;

    DocumentModel publish(DocumentModel source, Calendar date)
            throws ClientException;

    DocumentModel publish(DocumentModel source, Page page)
            throws ClientException;

}
