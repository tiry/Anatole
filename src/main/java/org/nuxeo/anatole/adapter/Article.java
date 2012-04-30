package org.nuxeo.anatole.adapter;

import java.util.List;

import org.nuxeo.anatole.PublishInfo;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface Article {

    public abstract DocumentModel getDocument();

    public abstract List<PublishInfo> getPublishInfo() throws ClientException;

    public abstract List<Page> getPotentialTargets() throws ClientException;

}