package org.nuxeo.anatole.adapter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Édouard Mercier
 * @since 2012.05.08
 */
public class AlmanachSection
    implements Serializable
{

  public final static class AlmanachLink
      implements Serializable
  {

    private static final long serialVersionUID = -7778115590276243140L;

    public final String label;

    public final String url;

    public AlmanachLink(String label, String url)
    {
      this.label = label;
      this.url = url;
    }

  }

  public static enum SectionType
  {
    ALaUne, AnatoleFranceTour, FreeSection, TodaysChallenge, WhatDoIDoToday, WhosThatPerson
  }

  public final static class ALaUneSection
      extends AlmanachSection
  {

    private static final long serialVersionUID = 4334918872591777727L;

    public final Date date;

    public ALaUneSection(String title, String text, String illustrationUrl, List<AlmanachLink> links, Date date)
    {
      super(SectionType.ALaUne, title, text, illustrationUrl, links);
      this.date = date;
    }

  }

  public final static class WhosThatPersonSection
      extends AlmanachSection
  {

    private static final long serialVersionUID = 1616936002137797038L;

    public final String answer;

    public WhosThatPersonSection(String title, String text, String illustrationUrl, List<AlmanachLink> links, String answer)
    {
      super(SectionType.WhosThatPerson, title, text, illustrationUrl, links);
      this.answer = answer;
    }

  }

  private static final long serialVersionUID = 7292403308938772396L;

  public final SectionType sectionType;

  public final String title;

  public final String text;

  public final String illustrationUrl;

  public final List<AlmanachLink> links;

  public AlmanachSection(SectionType sectionType, String title, String text, String illustrationUrl, List<AlmanachLink> links)
  {
    this.sectionType = sectionType;
    this.title = title;
    this.text = text;
    this.illustrationUrl = illustrationUrl;
    this.links = links;
  }

}
