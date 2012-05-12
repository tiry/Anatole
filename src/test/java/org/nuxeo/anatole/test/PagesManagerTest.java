package org.nuxeo.anatole.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.nuxeo.anatole.Constants;
import org.nuxeo.anatole.PageManager;
import org.nuxeo.anatole.PageManagerImpl;
import org.nuxeo.anatole.PublishInfo;
import org.nuxeo.anatole.adapter.AlmanachDay;
import org.nuxeo.anatole.adapter.Article;
import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.anatole.adapter.PageAdapter;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.runtime.api.Framework;

public class PagesManagerTest
    extends SQLRepositoryTestCase
{

  @Override
  public void setUp()
      throws Exception
  {
    super.setUp();
    deployBundle("org.nuxeo.ecm.platform.content.template");
    deployBundle(Constants.STUDIO_BUNDLE);
    deployBundle("Anatole");
    fireFrameworkStarted();
    openSession();
  }

  @Test
  public void checkInitialLayout()
      throws Exception
  {
    String query = " select * from Document order by ecm:path";
    DocumentModelList docs = session.query(query);
    StringBuffer sb = new StringBuffer();
    for (DocumentModel doc : docs)
    {
      sb.append(doc.getPathAsString() + " - " + doc.getType() + " -- " + doc.getTitle() + "\n");
    }
    // domain + section root + ws roots + live + archive + pages + 2WS
    int target = 7 + Constants.MAX_DAYS_FUTURE + Constants.MAX_DAYS_KEPT_LIVE + 1;

    System.out.println(sb.toString());

    // assertEquals(target, docs.size());
  }

  @Test
  public void checkService()
      throws Exception
  {
    PageManager pm = Framework.getLocalService(PageManager.class);
    assertNotNull(pm);

    DocumentModel live = pm.getLiveContainer(session);
    DocumentModelList pages = session.getChildren(live.getRef());

    int target = Constants.MAX_DAYS_FUTURE + Constants.MAX_DAYS_KEPT_LIVE + 1;

    assertEquals(target, pages.size());
  }

  @Test
  public void checkArchivingAndCleanup()
      throws Exception
  {
    PageManager pm = Framework.getLocalService(PageManager.class);

    DocumentModel live = pm.getLiveContainer(session);

    DocumentModelList pages = session.getChildren(live.getRef());

    // delete some live pages
    session.removeDocument(pages.get(0).getRef());
    session.removeDocument(pages.get(1).getRef());

    // create an outdated page in the Live part
    PageManagerImpl component = (PageManagerImpl) pm;
    // create an old date
    Calendar date = Calendar.getInstance();
    date.add(Calendar.DAY_OF_YEAR, -20);
    component.createLivePage(session, date);

    session.save();

    // now run the check
    pm.checkPagesStructure(session);

    session.save();

    String query = " select * from Document order by ecm:path";
    DocumentModelList docs = session.query(query);
    StringBuffer sb = new StringBuffer();
    for (DocumentModel doc : docs)
    {
      sb.append(doc.getPathAsString() + " - " + doc.getType() + " -- " + doc.getTitle() + "\n");
    }

    // System.out.println(sb.toString());

    // check that all pages were recreated
    pages = session.getChildren(live.getRef());
    int target = Constants.MAX_DAYS_FUTURE + Constants.MAX_DAYS_KEPT_LIVE + 1;
    assertEquals(target, pages.size());

    // check that archive was created
    DocumentModel archive = pm.getArchiveContainer(session);
    assertEquals(1, session.getChildren(archive.getRef()).size());

  }

  // @Test
  public void testPublishOptions()
      throws Exception
  {
    DocumentModel articleDoc = session.createDocumentModel("/", "article1", "Note");
    articleDoc.setPropertyValue("dc:title", "Test Article");
    articleDoc.setPropertyValue("note:note", "Some content");
    articleDoc = session.createDocument(articleDoc);
    session.save();

    PageManager pm = Framework.getLocalService(PageManager.class);
    List<Page> pages = pm.getLivePages(session);

    int nbPages = Constants.MAX_DAYS_FUTURE + Constants.MAX_DAYS_KEPT_LIVE + 1;

    // all pages are created
    assertEquals(nbPages, pages.size());

    Article article = articleDoc.getAdapter(Article.class);
    assertNotNull(article);

    // check that doc is not published
    List<PublishInfo> infos = article.getPublishInfo();
    assertEquals(0, infos.size());

    // check that all pages are available for publish
    List<Page> targets = article.getPotentialTargets();
    assertEquals(nbPages, targets.size());

    // do publish
    pm.publish(articleDoc, pages.get(1));

    // should have one less target available
    targets = article.getPotentialTargets();
    assertEquals(nbPages - 1, targets.size());

    // should have 1 publishInfo
    infos = article.getPublishInfo();
    assertEquals(1, infos.size());

    // check that pubslih info is accurate
    assertEquals(pages.get(1), infos.get(0).getPage());

  }

  @Test
  public void testAdapters()
      throws Exception
  {

    DocumentModel doc = session.getRootDocument();

    Page page = doc.getAdapter(Page.class);
    assertNull(page);

    PageManager pm = Framework.getLocalService(PageManager.class);
    List<Page> pages = pm.getLivePages(session);

    doc = pages.get(0).getDocument();
    page = doc.getAdapter(Page.class);
    assertNotNull(page);
  }

  @Test
  public void testPojoAdapter()
      throws Exception
  {
    PageManager pm = Framework.getLocalService(PageManager.class);
    List<Page> pages = pm.getLivePages(session);

    for (Page page : pages)
    {
      final AlmanachDay almanachDay = page.getDocument().getAdapter(AlmanachDay.class);
      assertNotNull(almanachDay);
      assertEquals("The 'when' field is not OK", page.getDocument().getPropertyValue("almanachDay:when"), almanachDay.when);
      assertEquals("The 'date' field is not OK", page.getDocument().getPropertyValue(PageAdapter.ALMANACH_DAY), almanachDay.date);
      assertNotNull("The 'sections' field is not OK", almanachDay.sections);
      assertEquals("The 'sections' field size is not OK", true, almanachDay.sections.size() >= 1);
    }
  }

  @Test
  public void testAlmanchDayTargetDate()
      throws Exception
  {
    PageManager pageManager = Framework.getLocalService(PageManager.class);
    DocumentModel liveContainer = pageManager.getLiveContainer(session);
    DocumentModel document = session.createDocumentModel(liveContainer.getPathAsString(), UUID.randomUUID().toString(), Constants.PAGE_TYPE);
    document.setPropertyValue("dc:title", "A title");
    final Calendar calendar = new GregorianCalendar();
    document.setPropertyValue(PageAdapter.ALMANACH_DAY, calendar);
    final DocumentModel newDocument = session.createDocument(document);
    final String targetDate = (String) newDocument.getPropertyValue(PageAdapter.PAGE_DATE_PROP);
    assertNotNull("The 'targetDate' has not been set automatically", targetDate);
    assertEquals("The 'targetDate' field is not OK", PageAdapter.format(calendar), targetDate);

    calendar.add(Calendar.MONTH, 1);
    document.setPropertyValue(PageAdapter.ALMANACH_DAY, calendar);
    final DocumentModel savedDocument = session.saveDocument(newDocument);
    assertEquals("The 'targetDate' field is not OK", PageAdapter.format(calendar), (String) savedDocument.getPropertyValue(PageAdapter.PAGE_DATE_PROP));
  }

}
