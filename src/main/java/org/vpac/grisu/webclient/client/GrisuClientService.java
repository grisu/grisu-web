package org.vpac.grisu.webclient.client;

import grisu.client.model.dto.DtoActionStatus;

import java.util.List;
import java.util.Map;

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

	public String cp(List<String> sources, String target);

	public GwtGrisuCacheFile download(String fileUrl);

	public List<String> getAllJobnames(String application);

	public String[] getApplicationForExecutable(String executable);

	public DtoActionStatus getCurrentStatus(String handle);

	public GrisuFileObject getFile(String url);

	public String[] getFqans();

	public Map<String, String> getUserProperties();

	public String getUserProperty(String key);

	public String[] getVersionsOfApplicationForVO(String[] applicationNames, String fqan);

	public void killJobs(List<GrisuJob> jobs);

	public boolean login(String username, String password) throws LoginException;

	public List<GrisuFileObject> ls(String url);

	public List<GrisuJob> ps(String application, boolean refresh);

	public void rm(List<GrisuFileObject> files);

	public void setUserProperty(String key, String value);

	public void submitJob(Map<String, String> jobProperties) throws JobCreationException;

}
