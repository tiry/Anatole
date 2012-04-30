package org.nuxeo.anatole.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.anatole.PageManager;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;

public class CheckPagesStructureListener implements EventListener {

	protected static final Log log = LogFactory.getLog(CheckPagesStructureListener.class);
	public static final String PAGE_CHECK_EVENT = "checkPagesStructure";

	@Override
	public void handleEvent(Event event) throws ClientException {
		  if (!PAGE_CHECK_EVENT.equals(event.getName())) {
	            return;
	      }
		  log.info("Running periodic structure check");
		  try {
	            RepositoryManager repositoryManager = Framework.getService(RepositoryManager.class);
	            Repository repository = repositoryManager.getDefaultRepository();
	            UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(repository.getName()) {
					@Override
					public void run() throws ClientException {
						PageManager pm = Framework.getLocalService(PageManager.class);
						pm.checkPagesStructure(session);
					}
				};
				runner.runUnrestricted();
	        } catch (Exception e) {
	        	log.error("Error during scheduled structure check", e);
	            throw new ClientException(e);
	        }


	}

}
