package org.vpac.grisu.webclient.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.webclient.client.GrisuClientService;
import org.vpac.grisu.webclient.client.GrisuJob;
import org.vpac.grisu.webclient.client.exceptions.LoginException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GrisuClientServiceImpl extends RemoteServiceServlet implements GrisuClientService {
	
	static final Logger myLogger = Logger.getLogger(GrisuClientServiceImpl.class.getName());
	
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

	public void login(String username, String password) throws LoginException {

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
		
	}


	public String[] getFqans() {
		
		return getServiceInterface().getFqans().asArray();

	}


	public List<GrisuJob> ps(String application) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<GrisuJob> ps(String application, boolean refresh) {

		DtoJobs jobs = getServiceInterface().ps(application, refresh);
		
		List<GrisuJob> result = new ArrayList<GrisuJob>();
		
		for ( DtoJob job : jobs.getAllJobs() ) {
			GrisuJob grisuJob = new GrisuJob(job.getStatus(), job.propertiesAsMap());
			result.add(grisuJob);
		}
		
		return result;
		
	}
	

}
