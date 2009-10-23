package org.vpac.grisu.webclient.client;

import java.util.List;
import java.util.Map;

import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;
import org.vpac.grisu.webclient.client.files.GwtGrisuCacheFile;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GrisuClientServiceAsync {
	public void login(String username, String password, AsyncCallback<Boolean> callback);
	
	public void getCurrentStatus(String handle, AsyncCallback<DtoActionStatus> callback);
	
	public void getFqans(AsyncCallback<String[]> callback);
	
	public void ps(String application, boolean refresh, AsyncCallback<List<GrisuJob>> callback);
	
	public void getAllJobnames(String application, AsyncCallback<List<String>> callback);
	
	public void ls(String url, AsyncCallback<List<GrisuFileObject>> callback);
	
	public void getFile(String url, AsyncCallback<GrisuFileObject> callback);
	
	public void cp(List<String> sources, String target, AsyncCallback<String> callback);
	
	public void download(String fileUrl, AsyncCallback<GwtGrisuCacheFile> callback);
	
	public void getUserProperties(AsyncCallback<Map<String, String>> callback);
	
	public void getUserProperty(String key, AsyncCallback<String> callback);
	
	public void setUserProperty(String key, String value, AsyncCallback<Void> callback);
	
	public void getApplicationForExecutable(String executable, AsyncCallback<String[]> callback);
	
	public void getVersionsOfApplicationForVO(String[] applicationNames, String fqan, AsyncCallback<String[]> callback);
	
	public void submitJob(Map<String, String> jobProperties, AsyncCallback<Void> callback);
	
}
