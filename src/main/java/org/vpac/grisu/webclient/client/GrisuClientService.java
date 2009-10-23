package org.vpac.grisu.webclient.client;

import java.util.List;
import java.util.Map;

import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.webclient.client.exceptions.LoginException;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;
import org.vpac.grisu.webclient.client.files.GwtGrisuCacheFile;
import org.vpac.grisu.webclient.client.jobcreation.JobCreationException;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;

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
	
	public boolean login(String username, String password) throws LoginException;
	
	public DtoActionStatus getCurrentStatus(String handle);
	
	public String[] getFqans();
	
	public List<GrisuJob> ps(String application, boolean refresh);
	
	public List<String> getAllJobnames(String application);
	
	public List<GrisuFileObject> ls(String url);
	
	public GrisuFileObject getFile(String url);
	
	public String cp(List<String> sources, String target);
	
	public GwtGrisuCacheFile download(String fileUrl);
	
	public Map<String, String> getUserProperties();
	
	public String getUserProperty(String key);
	
	public void setUserProperty(String key, String value);
	
	public String[] getApplicationForExecutable(String executable);
	
	public String[] getVersionsOfApplicationForVO(String[] applicationNames, String fqan);
	
	public void submitJob(Map<String, String> jobProperties) throws JobCreationException;
	
}
