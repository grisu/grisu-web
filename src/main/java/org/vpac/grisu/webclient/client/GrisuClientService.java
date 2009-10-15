package org.vpac.grisu.webclient.client;

import java.util.List;

import org.vpac.grisu.webclient.client.exceptions.LoginException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GrisuClientService")
public interface GrisuClientService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static GrisuClientServiceAsync instance;
		public static GrisuClientServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(GrisuClientService.class);
			}
			return instance;
		}
	}
	
	public void login(String username, String password) throws LoginException;	
	
	public String[] getFqans();
	
	public List<GrisuJob> ps(String application, boolean refresh);
	
}
