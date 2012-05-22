package org.nuxeo.anatole.rest;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.anatole.Constants;
import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.anatole.adapter.PageAdapter;
import org.nuxeo.ecm.automation.server.jaxrs.io.writers.JsonDocumentListWriter;
import org.nuxeo.ecm.automation.server.jaxrs.io.writers.JsonDocumentWriter;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

import fr.anatoleapps.almanachdanatole.bo.AlmanachDay;

/**
 * 
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 * 
 */
@WebObject(type = "anatoleRoot")
@Path("/almanachdanatole")
public class RootResource
    extends ModuleRoot
{

  public final String[] schemas = { "dublincore", "almanachSection" };

  @GET
  @Path("ping")
  public String getPong()
  {
    return "pong";
  }

  // @GET
  // @Path("{oldDate}")
  @Deprecated
  public String getOldPage(@PathParam(value = "date")
  String date)
      throws Exception
  {
    Page page = PageAdapter.find(getContext().getCoreSession(), date);
    if (page != null)
    {
      final AlmanachDay almanachDay = page.getDocument().getAdapter(AlmanachDay.class);
      DocumentModelList articles = page.getArticles();
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      final JsonGenerator jsonGenerator = new JsonFactory().createJsonGenerator(out);
      jsonGenerator.writeStartObject();
      jsonGenerator.writeFieldName("day");
      jsonGenerator.writeObject(almanachDay);
      JsonDocumentWriter.writeDocument(jsonGenerator, page.getDocument(), new String[] { Constants.PAGE_TYPE });
      jsonGenerator.writeFieldName("sections");
      JsonDocumentListWriter.writeDocuments(jsonGenerator, articles, schemas);
      jsonGenerator.writeEndObject();
      jsonGenerator.close();
      return new String(out.toByteArray());
    }
    return "not-found";
  }

  @GET
  @Path("almanachDay/{date}")
  public String getAlmanachDay(@PathParam(value = "date")
  String date)
      throws Exception
  {
    final Page page = PageAdapter.find(getContext().getCoreSession(), date);
    if (page != null)
    {
      final AlmanachDay almanachDay = page.getDocument().getAdapter(AlmanachDay.class);
      final ObjectMapper objectMapper = new ObjectMapper();
      final JsonFactory jsonFactory = new JsonFactory();
      final StringWriter writer = new StringWriter();
      final JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(writer);
      objectMapper.writeValue(jsonGenerator, almanachDay);
      return writer.toString();
    }
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @GET
  @Path("exception/{month}")
  public String getDates(@PathParam(value = "month")
  String month)
      throws Exception
  {
   
    throw new WebApplicationException(Response.Status.NOT_FOUND);
  }

  @Override
  public Object handleError(WebApplicationException exception)
  {
    // return Response.status(exception.getResponse().getStatus()).build();
    return Response.status(Status.NOT_FOUND).build();
  }

}
