package org.nuxeo.anatole.adapter;

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

  public final List<AlmanachSection> sections;

  public AlmanachDay(Date date, String when, List<AlmanachSection> sections)
  {
    this.date = date;
    this.when = when;
    this.sections = sections;
  }

}
