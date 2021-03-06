package org.nuxeo.anatole.adapter;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.nuxeo.anatole.Constants;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public class PageAdapter
    implements Serializable, Page
{

  private static final long serialVersionUID = 1L;

  public static final String PAGE_DATE_PROP = "page:targetDate";

  public static final String ALMANACH_DAY = "almanachDay:date";

  protected final DocumentModel doc;

  protected PageAdapter(DocumentModel doc)
  {
    this.doc = doc;
  }

  @Override
  public DocumentModel getDocument()
  {
    return doc;
  }

  @Override
  public String getTitle()
      throws ClientException
  {
    return doc.getTitle();
  }

  @Override
  public Calendar getIssueDate()
      throws Exception
  {
    String target = (String) doc.getPropertyValue(PAGE_DATE_PROP);
    Date date = getDateFormat().parse(target);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }

  public String getShortLabel()
      throws Exception
  {
    DateFormat df = new SimpleDateFormat("EEE, d MMM", Locale.US);
    return df.format(getIssueDate().getTime());
  }

  protected static DateFormat getDateFormat()
  {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    return df;
  }

  public static String format(Calendar date)
  {
    return getDateFormat().format(date.getTime());
  }

  public static Page create(CoreSession session, String parentPath, Calendar date)
      throws ClientException
  {
    String key = format(date);
    DocumentModel doc = session.createDocumentModel(parentPath, key, Constants.PAGE_TYPE);
    doc.setPropertyValue("dc:title", key);
    doc.setPropertyValue(PAGE_DATE_PROP, key);
    return session.createDocument(doc).getAdapter(Page.class);
  }

  public static Page find(CoreSession session, Calendar date)
      throws ClientException
  {
    String key = format(date);
    return find(session, key);
  }

  public static Page find(CoreSession session, String date)
      throws ClientException
  {
    DocumentModelList docs = session.query("select * from " + Constants.PAGE_TYPE + " where " + PageAdapter.PAGE_DATE_PROP + "='" + date + "'");
    if (docs.size() > 0)
    {
      return docs.get(0).getAdapter(Page.class);
    }
    return null;
  }

  @Override
  public int hashCode()
  {
    return doc.getId().hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof PageAdapter)
    {
      return doc.getId().equals(((PageAdapter) obj).doc.getId());
    }
    return super.equals(obj);
  }

  @Override
  public DocumentModelList getArticles()
      throws ClientException
  {
    return doc.getCoreSession().getChildren(doc.getRef());
  }

}
