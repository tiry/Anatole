package fr.anatoleapps.almanachdanatole.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * @author Édouard Mercier
 * @since 2012.05.08
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class AlmanachSection
    implements Serializable
{

  public final static class AlmanachLink
      implements Serializable
  {

    private static final long serialVersionUID = -7778115590276243140L;

    public final String label;

    public final String url;

    public AlmanachLink()
    {
      this(null, null);
    }

    public AlmanachLink(String label, String url)
    {
      this.label = label;
      this.url = url;
    }

  }

  public static enum SectionType
  {
    ALaUne, AnatoleAtTheMarket, AnatoleFranceTour, AnatolesAgenda, AnatolesIdeas, FreeSection, TodaysChallenge, WhatDoIDoToday, WhosThatPerson
  }

  public final static class ALaUneSection
      extends AlmanachSection
  {

    private static final long serialVersionUID = 4334918872591777727L;

    public final Date date;

    public ALaUneSection()
    {
      this(null, null, null, null, null);
    }

    public ALaUneSection(String title, String text, String illustrationUrl, List<AlmanachLink> links, Date date)
    {
      super(SectionType.ALaUne, title, text, illustrationUrl, links);
      this.date = date;
    }

  }

  public final static class AnatolesAgenda
      extends AlmanachSection
  {

    private static final long serialVersionUID = -3455951155936810391L;

    public final String subTitle;

    public AnatolesAgenda()
    {
      this(null, null, null, null, null);
    }

    public AnatolesAgenda(String title, String text, String illustrationUrl, List<AlmanachLink> links, String subTitle)
    {
      super(SectionType.AnatolesAgenda, title, text, illustrationUrl, links);
      this.subTitle = subTitle;
    }

  }

  public final static class Challenge
      implements Serializable
  {

    private static final long serialVersionUID = -9202437493180078842L;

    public final String question;

    public final List<String> possibleAnswers;

    public final int answerIndex;

    public final String answerLabel;

    public Challenge()
    {
      this(null, null, -1, null);
    }

    public Challenge(String question, List<String> possibleAnswers, int answerIndex, String answerLabel)
    {
      this.question = question;
      this.possibleAnswers = possibleAnswers;
      this.answerIndex = answerIndex;
      this.answerLabel = answerLabel;
    }

  }

  public final static class TodaysChallengeSection
      extends AlmanachSection
  {

    private static final long serialVersionUID = -8060324417910730655L;

    public final List<Challenge> challenges;

    public TodaysChallengeSection()
    {
      this(null);
    }

    public TodaysChallengeSection(List<Challenge> challenges)
    {
      super(SectionType.TodaysChallenge, null, null, null, null);
      this.challenges = challenges;
    }

  }

  public final static class WhosThatPersonSection
      extends AlmanachSection
  {

    private static final long serialVersionUID = 1616936002137797038L;

    public final String answer;

    public WhosThatPersonSection()
    {
      this(null, null, null, null, null);
    }

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

  public AlmanachSection()
  {
    this(null, null, null, null, null);
  }

}