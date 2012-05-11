package org.nuxeo.anatole.adapter;

import java.io.Serializable;

/**
 * @author Édouard Mercier
 * @since 2012.05.08
 */
public final class AlmanachSection
    implements Serializable
{

  public static enum SectionType
  {
    ALaUne, WhatDoIDoToday
  }

  private static final long serialVersionUID = 7292403308938772396L;

  public final SectionType sectionType;

  public AlmanachSection(SectionType sectionType)
  {
    this.sectionType = sectionType;
  }

}
