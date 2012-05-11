package org.nuxeo.anatole.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.nuxeo.anatole.Constants;
import org.nuxeo.anatole.adapter.AlmanachSection.ALaUneSection;
import org.nuxeo.anatole.adapter.AlmanachSection.AlmanachLink;
import org.nuxeo.anatole.adapter.AlmanachSection.SectionType;
import org.nuxeo.anatole.adapter.AlmanachSection.WhosThatPersonSection;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;

public class AdapterFactory
    implements DocumentAdapterFactory
{

  @Override
  public Object getAdapter(DocumentModel document, Class<?> adapterClass)
  {

    if (document.getType().equals(Constants.PAGE_TYPE) && adapterClass.getName().equals(Page.class.getName()))
    {
      return new PageAdapter(document);
    }

    if (adapterClass == AlmanachSection.class)
    {
      String title;
      String text;
      String illustrationUrl;
      final List<AlmanachLink> links = new ArrayList<AlmanachLink>();
      try
      {
        title = (String) document.getPropertyValue("dc:title");
        text = (String) document.getPropertyValue("as:text");
        final AbstractBlob abstractBlob = (AbstractBlob) document.getPropertyValue("as:illustration");
        illustrationUrl = abstractBlob == null ? null : abstractBlob.getFilename();
        @SuppressWarnings("unchecked")
        final List<HashMap<String, String>> linksAs = (List<HashMap<String, String>>) document.getPropertyValue("as:links");
        for (HashMap<String, String> map : linksAs)
        {
          links.add(new AlmanachLink(map.get("label"), map.get("url")));
        }
      }
      catch (Exception exception)
      {
        // TODO: handle each property exception
        exception.printStackTrace();
        title = null;
        text = null;
        illustrationUrl = null;
      }
      if (document.getType().equals(Constants.ALAUNE_TYPE) == true)
      {
        Calendar calendar = null;
        try
        {
          calendar = (Calendar) document.getPropertyValue("alaune:date");
        }
        catch (Exception exception)
        {
          exception.printStackTrace();
        }
        return new ALaUneSection(title, text, illustrationUrl, links, calendar == null ? null : calendar.getTime());
      }
      else if (document.getType().equals(Constants.ANATOLE_FRANCE_TOUR_TYPE) == true)
      {
        return new AlmanachSection(SectionType.AnatoleFranceTour, title, text, illustrationUrl, links);
      }
      else if (document.getType().equals(Constants.FREE_SECTION_TYPE) == true)
      {
        return new AlmanachSection(SectionType.FreeSection, title, text, illustrationUrl, links);
      }
      else if (document.getType().equals(Constants.TODAYS_CHALLENGE_TYPE) == true)
      {
        return new AlmanachSection(SectionType.TodaysChallenge, title, text, illustrationUrl, links);
      }
      else if (document.getType().equals(Constants.TODAYS_CHALLENGE_TYPE) == true)
      {
        return new AlmanachSection(SectionType.TodaysChallenge, title, text, illustrationUrl, links);
      }
      else if (document.getType().equals(Constants.WHAT_DO_ID_DO_TODAY_TYPE) == true)
      {
        return new AlmanachSection(SectionType.WhatDoIDoToday, title, text, illustrationUrl, links);
      }
      else if (document.getType().equals(Constants.WHOS_THAT_PERSON_TYPE) == true)
      {
        String answer = null;
        try
        {
          answer = (String) document.getPropertyValue("whosthat:answer");
        }
        catch (Exception exception)
        {
          exception.printStackTrace();
        }
        return new WhosThatPersonSection(title, text, illustrationUrl, links, answer);
      }
    }

    if (document.getType().equals(Constants.PAGE_TYPE) && adapterClass.getName().equals(AlmanachDay.class.getName()))
    {
      final Page page = document.getAdapter(Page.class);
      final List<AlmanachSection> almanachSections = new ArrayList<AlmanachSection>();
      try
      {
        for (DocumentModel childDocument : page.getArticles())
        {
          final AlmanachSection almanachSection = childDocument.getAdapter(AlmanachSection.class);
          if (almanachSection != null)
          {
            almanachSections.add(almanachSection);
          }
        }
        final Calendar date = (Calendar) document.getPropertyValue("almanachDay:date");
        return new AlmanachDay(date == null ? null : date.getTime(), (String) document.getPropertyValue("almanachDay:when"), (String) document.getPropertyValue("almanachDay:sunRise"), (String) document.getPropertyValue("almanachDay:sunSet"), (String) document.getPropertyValue("almanachDay:moonRise"), (String) document.getPropertyValue("almanachDay:moonSet"), (String) document.getPropertyValue("almanachDay:astrology"), (String) document.getPropertyValue("almanachDay:republicanCalendar"), (String) document.getPropertyValue("almanachDay:saint"), almanachSections);
      }
      catch (Exception exception)
      {
        exception.printStackTrace();
        return null;
      }
    }

    if (adapterClass.getName().equals(Article.class.getName()))
    {
      return new ArticleAdapter(document);
    }

    return null;
  }

}
