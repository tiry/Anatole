package org.nuxeo.anatole.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.anatole.Constants;
import org.nuxeo.anatole.adapter.AlmanachSection.SectionType;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

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

    if (document.getType().equals(Constants.ALAUNE_TYPE) && adapterClass.getName().equals(AlmanachSection.class.getName()))
    {
      return new AlmanachSection(SectionType.ALaUne);
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
        return new AlmanachDay(date == null ? null : date.getTime(), (String) document.getPropertyValue("almanachDay:when"), almanachSections);
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
