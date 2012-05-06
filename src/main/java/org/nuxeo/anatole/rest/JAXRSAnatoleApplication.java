package org.nuxeo.anatole.rest;

import java.util.HashSet;
import java.util.Set;

import org.nuxeo.ecm.webengine.app.WebEngineModule;

/**
 * 
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 * 
 */
public class JAXRSAnatoleApplication extends WebEngineModule {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(RootResource.class);
        return result;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> result = new HashSet<Object>();
        return result;
    }

}
