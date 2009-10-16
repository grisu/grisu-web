package org.vpac.grisu.webclient.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.webclient.client.GrisuClientService;
import org.vpac.grisu.webclient.client.exceptions.LoginException;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GrisuClientServiceImpl extends RemoteServiceServlet implements GrisuClientService {
	
	static final Logger myLogger = Logger.getLogger(GrisuClientServiceImpl.class.getName());
	
	private final Mapper mapper = new DozerBeanMapper();
	
	private final Map<String, org.vpac.grisu.model.dto.DtoActionStatus> actionStatus = new HashMap<String, org.vpac.grisu.model.dto.DtoActionStatus>();

	private FileSpaceManager fileSpaceManager;
	
	private FileSpaceManager getFileSpaceManager() {
		
		if ( fileSpaceManager == null ) {
			fileSpaceManager = new FileSpaceManager(getServiceInterface());
		}
		
		return fileSpaceManager;
		
	}
	
	private ServiceInterface getServiceInterface() {

		ServiceInterface si = (ServiceInterface) (getSession()
				.getAttribute("serviceInterface"));
		if (si == null) {
			myLogger.error("ServiceInterface not in session (yet?).");
			throw new RuntimeException("Not logged in.");
		}
		return si;
	}
	

	private HttpSession getSession() {

		// Get the current request and then return its session
		HttpSession session = this.getThreadLocalRequest().getSession();

		return session;
	}

	public boolean login(String username, String password) throws LoginException {
		
		// first check whether session is still active...
		myLogger.debug("Trying to get serviceinterface from session.");
		
		try {
			if ( getServiceInterface() != null ) {
				return true;
			} else if ( StringUtils.isBlank(username) ) {
				return false;
			}
			
		} catch (Exception e) {
			myLogger.debug("Could not get serviceinterface from session: "+e.getLocalizedMessage()+". Continuing with real login process...");
		} 

		String serviceInterfaceUrl = ClientPropertiesManager.getDefaultServiceInterfaceUrl();
		
		serviceInterfaceUrl = "Local";
		
		myLogger.info("Logging in...");
		
		LoginParams loginParams = new LoginParams(
				serviceInterfaceUrl, username, password.toCharArray(), "myproxy2.arcs.org.au", "7512");

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
			si.login(username, new String(password));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LoginException(e.getLocalizedMessage());
		}
		myLogger.info("ServiceInterface created...");
		getSession().setAttribute("serviceInterface", si);

		myLogger.info("Logged in...");
		return true;
	}


	public String[] getFqans() {
		
		return getServiceInterface().getFqans().asArray();

	}


	public List<GrisuJob> ps(String application, boolean refresh) {
		
		myLogger.debug("ps");

		DtoJobs jobs = getServiceInterface().ps(application, refresh);
		
		List<GrisuJob> result = new ArrayList<GrisuJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			GrisuJob grisuJob = new GrisuJob(job.getStatus(), job.propertiesAsMap());
			result.add(grisuJob);
		}
		
		return result;
		
	}


	public DtoActionStatus getCurrentStatus(String handle) {
		
		org.vpac.grisu.model.dto.DtoActionStatus status = getServiceInterface().getActionStatus(handle);

		if ( status == null ) {
			status = actionStatus.get(handle);		
		}
		
		if ( status == null ) {
			return null;
		}
		
		DtoActionStatus newStatus = null;
		try {
			newStatus = mapper.map(status, org.vpac.grisu.client.model.dto.DtoActionStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return newStatus;
	}

	public List<GrisuFileObject> ls(String url) {

		try {
			return getFileSpaceManager().getChildrenFileObjects(true, url);
		} catch (RemoteFileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

}
