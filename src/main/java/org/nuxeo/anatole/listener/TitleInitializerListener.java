package org.nuxeo.anatole.listener;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class TitleInitializerListener implements EventListener {

    @Override
    public void handleEvent(Event event) throws ClientException {
        if (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName())) {
            EventContext ctx = event.getContext();
            if (ctx instanceof DocumentEventContext) {
                DocumentEventContext docCtx = (DocumentEventContext) ctx;
                DocumentModel doc = docCtx.getSourceDocument();
                String title = (String) doc.getPropertyValue("dc:title");
                if (title == null || title.isEmpty()) {
                    setDefaultTitle(doc);
                }
            }
        }
    }

    protected void setDefaultTitle(DocumentModel doc) throws ClientException {
        String title = "";
        if (doc.getType().equals("alaune")) {
            title = "A la une";
        } else if (doc.getType().equals("whatDoIDoToday")) {
            title = "Je fais quoi, aujourd’hui ?";
        } else if (doc.getType().equals("anatoleFranceTour")) {
            title = "Le Tour de France d'Anatole";
        } else if (doc.getType().equals("todaysChallenge")) {
            title = "Le défi du jour";
        } else if (doc.getType().equals("whosThatPerson")) {
            title = "Qui c’est celui-là ?";
        }
        doc.setPropertyValue("dc:title", title);
    }

}
