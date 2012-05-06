package org.nuxeo.anatole.rest;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.nuxeo.anatole.adapter.Page;
import org.nuxeo.anatole.adapter.PageAdapter;
import org.nuxeo.ecm.automation.server.jaxrs.io.writers.JsonDocumentListWriter;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 * 
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 * 
 */
@WebObject(type = "anatoleRoot")
@Path("/anatole")
public class RootResource extends ModuleRoot {

    public final String[] schemas = { "dublincore", "almanachSection" };

    @GET
    @Path("ping")
    public String getPong() {
        return "pong";
    }

    @GET
    @Path("{date}")
    public String getPage(@PathParam(value = "date")
    String date) throws Exception {
        Page page = PageAdapter.find(getContext().getCoreSession(), date);
        if (page != null) {
            // yerk : should do better !!!
            DocumentModelList articles = page.getArticles();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JsonDocumentListWriter.writeDocuments(out, articles, schemas);
            return new String(out.toByteArray());
        }
        return "not-found";
    }

}
