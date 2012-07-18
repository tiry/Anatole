package fr.anatoleapps.almanachdanatole.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Édouard Mercier
 * @since 2012.05.08
 */
public final class AlmanachDay
    implements Serializable
{

  private static final long serialVersionUID = 5285689154275864956L;

  public final Date date;

  public final String when;

  public final String sunRise;

  public final String sunSet;

  public final String moonRise;

  public final String moonSet;

  public final String moonComment;

  public final String astrology;

  public final String republicanCalendar;

  public final String saint;

  public final List<AlmanachSection> sections;

  public AlmanachDay()
  {
    this(null, null, null, null, null, null, null, null, null, null, null);
  }

  public AlmanachDay(Date date, String when, String sunRise, String sunSet, String moonRise, String moonSet, String moonComment, String astrology,
      String republicanCalendar, String saint, List<AlmanachSection> sections)
  {
    this.date = date;
    this.when = when;
    this.sunRise = sunRise;
    this.sunSet = sunSet;
    this.moonRise = moonRise;
    this.moonSet = moonSet;
    this.moonComment = moonComment;
    this.astrology = astrology;
    this.republicanCalendar = republicanCalendar;
    this.saint = saint;
    this.sections = sections;
  }

  public String toText(boolean isHtml)
  {
    final StringBuilder sb = new StringBuilder();
    if (isHtml == true)
    {
      sb.append("<b>");
    }
    sb.append(saint);
    if (isHtml == true)
    {
      sb.append("</b>");
    }
    sb.append(isHtml == true ? "<br/>" : "\n");
    sb.append(isHtml == true ? " &#8226; " : " - ");
    sb.append(isHtml == true ? republicanCalendar: AlmanachSection.cleanHtml(republicanCalendar));
    sb.append(isHtml == true ? "<br/>" : "\n");
    sb.append(isHtml == true ? " &#8226; " : " - ");
    sb.append(isHtml == true ? astrology: AlmanachSection.cleanHtml(astrology));
    return sb.toString();
  }

}
