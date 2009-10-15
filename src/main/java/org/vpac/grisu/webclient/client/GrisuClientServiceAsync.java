package org.vpac.grisu.webclient.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GrisuClientServiceAsync {
	public void login(String username, String password, AsyncCallback<Void> callback);	
	
	public void getFqans(AsyncCallback<String[]> callback);
	
	public void ps(String application, boolean refresh, AsyncCallback<List<GrisuJob>> callback);
	
}
