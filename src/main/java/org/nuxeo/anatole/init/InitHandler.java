package org.nuxeo.anatole.init;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.anatole.PageManager;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.content.template.service.PostContentCreationHandler;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * Called during repository initialization <p>
 * <ul>
 * <li> create the page structure if needed </li>
 * <li> create future pages containers if needed </li>
 * <li> do cleanup in pages to archive if needed </li>
 * </ul>
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public class InitHandler implements PostContentCreationHandler {

	protected static final Log log = LogFactory.getLog(InitHandler.class);

	@Override
	public void execute(CoreSession session) {
		PageManager pm = Framework.getLocalService(PageManager.class);
		try {
			pm.checkPagesStructure(session);
		} catch (Exception e) {
			log.error("Error during repository structure init / check", e);
		}
	}

}
