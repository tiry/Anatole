package org.nuxeo.anatole.listener;

import java.util.Calendar;

import org.nuxeo.anatole.Constants;
import org.nuxeo.anatole.adapter.PageAdapter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class TitleInitializerListener
    implements EventListener
{

  @Override
  public void handleEvent(Event event)
      throws ClientException
  {
    if (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName()))
    {
      EventContext eventContext = event.getContext();
      if (eventContext instanceof DocumentEventContext)
      {
        DocumentEventContext documentEventContext = (DocumentEventContext) eventContext;
        DocumentModel document = documentEventContext.getSourceDocument();
        if (Constants.PAGE_TYPE.equals(document.getType()) == true)
        {
          final String title = (String) document.getPropertyValue("dc:title");
          if (title == null || title.isEmpty())
          {
            setDefaultTitle(document);
          }
        }
      }
    }
    if (DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName()) || DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName()))
    {
      EventContext eventContext = event.getContext();
      if (eventContext instanceof DocumentEventContext)
      {
        DocumentEventContext documentEventContext = (DocumentEventContext) eventContext;
        DocumentModel document = documentEventContext.getSourceDocument();
        if (Constants.PAGE_TYPE.equals(document.getType()) == true)
        {
          final Calendar date = (Calendar) document.getPropertyValue(PageAdapter.ALMANACH_DAY);
          if (date != null)
          {
            document.setPropertyValue(PageAdapter.PAGE_DATE_PROP, PageAdapter.format(date));
          }
        }
      }
    }
  }

  protected void setDefaultTitle(DocumentModel document)
      throws ClientException
  {
    String title = "";
    if (document.getType().equals(Constants.ALAUNE_TYPE))
    {
      title = "À la une";
    }
    else if (document.getType().equals(Constants.ANATOLE_FRANCE_TOUR_TYPE))
    {
      title = "Le Tour de France d'Anatole";
    }
    else if (document.getType().equals(Constants.ANATOLES_AGENDA_TYPE))
    {
      title = "Les rendez-vous d'Anatole";
    }
    else if (document.getType().equals(Constants.ANATOLES_IDEAS_TYPE))
    {
      title = "Les idées d'Anatole";
    }
    // else if (doc.getType().equals(Constants.FREE_SECTION_TYPE))
    // {
    // title = "Section libre";
    // }
    else if (document.getType().equals(Constants.TODAYS_CHALLENGE_TYPE))
    {
      title = "Le défi du jour";
    }
    else if (document.getType().equals(Constants.WHAT_DO_ID_DO_TODAY_TYPE))
    {
      title = "Je fais quoi, aujourd'hui ?";
    }
    else if (document.getType().equals(Constants.WHOS_THAT_PERSON_TYPE))
    {
      title = "Qui c'est celui-là ?";
    }
    document.setPropertyValue("dc:title", title);
  }

}
