package org.nuxeo.anatole.adapter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import org.nuxeo.anatole.Constants;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;

import fr.anatoleapps.almanachdanatole.bo.AlmanachDay;
import fr.anatoleapps.almanachdanatole.bo.AlmanachDay.AlmanachSaint;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.ALaUneSection;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.AlmanachIllustration;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.AlmanachLink;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.AnatolesAgendaSection;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.AnatolesIdeasSection;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.Challenge;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.SectionType;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.SimpleAlmanachImage;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.TodaysChallengeSection;
import fr.anatoleapps.almanachdanatole.bo.AlmanachSection.WhosThatPersonSection;

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
      String title = null;
      String text = null;
      AlmanachIllustration almanachIllustration = null;
      final List<AlmanachLink> links = new ArrayList<AlmanachLink>();
      if (document.getType().equals(Constants.TODAYS_CHALLENGE_TYPE) == false)
      {
        try
        {
          title = (String) document.getPropertyValue("dc:title");
          text = (String) document.getPropertyValue("as:text");

          final String illustrationFieldName = "as:illustration";
          final AbstractBlob abstractBlob = (AbstractBlob) document.getPropertyValue(illustrationFieldName);
          final AtomicInteger illustrationWidth = new AtomicInteger(-1);
          final AtomicInteger illustrationHeight = new AtomicInteger(-1);
          final AtomicReference<String> illustrationUrl = new AtomicReference<String>();
          extractImageDimensions(abstractBlob, illustrationWidth, illustrationHeight, illustrationUrl, document, illustrationFieldName);

          final String illustrationCreditLabel = (String) document.getPropertyValue("as:illustrationCreditLabel");
          final String illustrationCreditUrl = (String) document.getPropertyValue("as:illustrationCreditUrl");
          almanachIllustration = new AlmanachIllustration(illustrationCreditLabel, illustrationCreditUrl, illustrationUrl.get(), illustrationWidth.get(), illustrationHeight.get());
          @SuppressWarnings("unchecked")
          final List<HashMap<String, String>> innerList = (List<HashMap<String, String>>) document.getPropertyValue("as:links");
          for (HashMap<String, String> map : innerList)
          {
            links.add(new AlmanachLink(map.get("label"), map.get("url")));
          }
        }
        catch (Exception exception)
        {
          // TODO: handle each property exception
          exception.printStackTrace();
        }
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
        return new ALaUneSection(title, extractSubTitle(document, Constants.ALAUNE_TYPE), text, almanachIllustration, links, calendar == null ? null
            : calendar.getTime());
      }
      else if (document.getType().equals(Constants.ANATOLE_AT_THE_MARKET_TYPE) == true)
      {
        return new AlmanachSection(SectionType.AnatoleAtTheMarket, title, extractSubTitle(document, Constants.ANATOLE_AT_THE_MARKET_TYPE), text, almanachIllustration, links);
      }
      else if (document.getType().equals(Constants.ANATOLE_FRANCE_TOUR_TYPE) == true)
      {
        return new AlmanachSection(SectionType.AnatoleFranceTour, title, extractSubTitle(document, Constants.ANATOLE_FRANCE_TOUR_TYPE), text, almanachIllustration, links);
      }
      else if (document.getType().equals(Constants.ANATOLES_AGENDA_TYPE) == true)
      {
        return new AnatolesAgendaSection(title, extractSubTitle(document, Constants.ANATOLES_AGENDA_TYPE), text, almanachIllustration, links);
      }
      else if (document.getType().equals(Constants.ANATOLES_IDEAS_TYPE) == true)
      {
        String subTitle = null;
        try
        {
          subTitle = (String) document.getPropertyValue("anatolesIdeas:subTitle");
        }
        catch (Exception exception)
        {
          exception.printStackTrace();
        }
        return new AnatolesIdeasSection(title, subTitle, text, almanachIllustration, links);
      }
      else if (document.getType().equals(Constants.FREE_SECTION_TYPE) == true)
      {
        return new AlmanachSection(SectionType.FreeSection, title, extractSubTitle(document, Constants.FREE_SECTION_TYPE), text, almanachIllustration, links);
      }
      else if (document.getType().equals(Constants.TODAYS_CHALLENGE_TYPE) == true)
      {
        final List<Challenge> challenges = new ArrayList<Challenge>();
        try
        {
          @SuppressWarnings("unchecked")
          final List<HashMap<String, ?>> innerList = (List<HashMap<String, ?>>) document.getPropertyValue("todaysChallenge:challenges");
          for (HashMap<String, ?> object : innerList)
          {
            String question = null;
            int answerIndex = -1;
            String answerLabel = null;
            final List<String> possibleAnswers = new ArrayList<String>();
            for (Entry<String, ?> entry : object.entrySet())
            {
              if (entry.getKey().equals("question") == true)
              {
                question = (String) entry.getValue();
              }
              else if (entry.getKey().equals("answerIndex") == true)
              {
                answerIndex = ((Long) entry.getValue()).intValue();
              }
              else if (entry.getKey().equals("possibleAnswers") == true)
              {
                final String[] strings = (String[]) entry.getValue();
                for (String string : strings)
                {
                  possibleAnswers.add(string);
                }
              }
              else if (entry.getKey().equals("answerLabel") == true)
              {
                answerLabel = (String) entry.getValue();
              }
            }
            challenges.add(new Challenge(question, possibleAnswers, answerIndex, answerLabel));
          }
        }
        catch (Exception exception)
        {
          exception.printStackTrace();
        }
        return new TodaysChallengeSection(challenges);
      }
      else if (document.getType().equals(Constants.TODAYS_CHALLENGE_TYPE) == true)
      {
        return new AlmanachSection(SectionType.TodaysChallenge, title, extractSubTitle(document, Constants.TODAYS_CHALLENGE_TYPE), text, almanachIllustration, links);
      }
      else if (document.getType().equals(Constants.WHAT_DO_ID_DO_TODAY_TYPE) == true)
      {
        return new AlmanachSection(SectionType.WhatDoIDoToday, title, extractSubTitle(document, Constants.WHAT_DO_ID_DO_TODAY_TYPE), text, almanachIllustration, links);
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
        return new WhosThatPersonSection(title, extractSubTitle(document, Constants.WHOS_THAT_PERSON_TYPE), text, almanachIllustration, links, answer);
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
          if (childDocument.getCurrentLifeCycleState().equals("deleted") == true)
          {
            // The document has been deleted
            continue;
          }
          final AlmanachSection almanachSection = childDocument.getAdapter(AlmanachSection.class);
          if (almanachSection != null)
          {
            almanachSections.add(almanachSection);
          }
        }
        final Calendar date = (Calendar) document.getPropertyValue(PageAdapter.ALMANACH_DAY);

        SimpleAlmanachImage illustration = null;
        try
        {
          final String illustrationFieldName = "almanachDay:illustration";
          final AbstractBlob abstractBlob = (AbstractBlob) document.getPropertyValue(illustrationFieldName);
          final AtomicInteger illustrationWidth = new AtomicInteger(-1);
          final AtomicInteger illustrationHeight = new AtomicInteger(-1);
          final AtomicReference<String> illustrationUrl = new AtomicReference<String>();
          extractImageDimensions(abstractBlob, illustrationWidth, illustrationHeight, illustrationUrl, document, illustrationFieldName);
          illustration = illustrationUrl.get() == null ? null
              : new SimpleAlmanachImage(illustrationUrl.get(), illustrationWidth.get(), illustrationHeight.get());
        }
        catch (Exception exception)
        {
          // That's normal for the old days
        }

        final ArrayList<AlmanachSaint> saints = new ArrayList<AlmanachSaint>();
        try
        {
          final List<HashMap<String, ?>> innerList = (List<HashMap<String, ?>>) document.getPropertyValue("almanachDay:saints");
          for (HashMap<String, ?> object : innerList)
          {
            String saint = null;
            for (Entry<String, ?> entry : object.entrySet())
            {
              if (entry.getKey().equals("firstname") == true)
              {
                saint = (String) entry.getValue();
              }
            }
            saints.add(new AlmanachSaint(saint));
          }
        }
        catch (Exception exception)
        {
          // That's normal for the old days
        }

        return new AlmanachDay(date == null ? null : date.getTime(), (String) document.getPropertyValue("almanachDay:when"), (String) document.getPropertyValue("almanachDay:sunRise"), (String) document.getPropertyValue("almanachDay:sunSet"), (String) document.getPropertyValue("almanachDay:moonRise"), (String) document.getPropertyValue("almanachDay:moonSet"), (String) document.getPropertyValue("almanachDay:moonComment"), (String) document.getPropertyValue("almanachDay:astrology"), (String) document.getPropertyValue("almanachDay:republicanCalendar"), (String) document.getPropertyValue("almanachDay:saint"), saints, illustration, almanachSections);
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

  private String extractSubTitle(DocumentModel document, String schemaPrefix)
  {
    String subTitle = null;
    try
    {
      subTitle = (String) document.getPropertyValue(schemaPrefix + ":subTitle");
    }
    catch (Exception exception)
    {
      // That may be totally normal
    }
    return subTitle;
  }

  // private void extractImageDimensions(AbstractBlob abstractBlob, AtomicInteger illustrationWidth, AtomicInteger illustrationHeight,
  // AtomicReference<String> illustrationUrl, DocumentModel document, String fieldName)
  // {
  // final ToolkitImage illustrationImage = new ToolkitImage(new ByteArrayImageSource(abstractBlob.getByteArray()));
  // illustrationWidth.set(illustrationImage.getWidth());
  // illustrationHeight.set(illustrationImage.getHeight());
  // // We need to encode the file name
  // illustrationUrl.set(abstractBlob == null ? null : document.getId() + "/" + fieldName + "/" + URLEncoder.encode(abstractBlob.getFilename(),
  // "UTF-8"));
  // }

  private void extractImageDimensions(AbstractBlob abstractBlob, AtomicInteger illustrationWidth, AtomicInteger illustrationHeight,
      AtomicReference<String> illustrationUrl, DocumentModel document, String fieldName)
  {
    if (abstractBlob != null)
    {
      try
      {
        final BufferedImage illustrationImage = ImageIO.read(new ByteArrayInputStream(abstractBlob.getByteArray()));
        illustrationWidth.set(illustrationImage.getWidth());
        illustrationHeight.set(illustrationImage.getHeight());
      }
      catch (IOException exception)
      {
        exception.printStackTrace();
      }
      // We need to encode the file name
      try
      {
        illustrationUrl.set(abstractBlob == null ? null : document.getId() + "/" + fieldName + "/" + URLEncoder.encode(abstractBlob.getFilename(), "UTF-8"));
      }
      catch (UnsupportedEncodingException exception)
      {
        exception.printStackTrace();
      }
    }
  }

}
