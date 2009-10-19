package org.vpac.grisu.webclient.client;

import java.util.List;

import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GrisuClientServiceAsync {
	public void login(String username, String password, AsyncCallback<Boolean> callback);
	
	public void getCurrentStatus(String handle, AsyncCallback<DtoActionStatus> callback);
	
	public void getFqans(AsyncCallback<String[]> callback);
	
	public void ps(String application, boolean refresh, AsyncCallback<List<GrisuJob>> callback);
	
	public void ls(String url, AsyncCallback<List<GrisuFileObject>> callback);
	
	public void cp(List<String> sources, String target, AsyncCallback<String> callback);
	
}
