package org.vpac.grisu.webclient.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.vpac.grisu.webclient.client.jobcreation.JobSubmissionFinishedEvent;

public class UserEnvironment implements JobSubmissionFinishedEvent.Handler {
	
	private List<String> allJobnames = new ArrayList<String>();
	private boolean updatingAllJobnames = false;
	
	private Map<String, String> userProperties = new HashMap<String, String>();
	private boolean updatingUserProperties = false;
	
	private static UserEnvironment singleton;
	
	public static UserEnvironment getInstance() {
		if ( singleton == null ) {
			singleton = new UserEnvironment();
			EventBus.get().addHandler(JobSubmissionFinishedEvent.TYPE, singleton);
		}
		return singleton;
	}
	
	public UserEnvironment() {
		updateAllJobnames();
	}
	
	private void updateAllJobnames() {
	
		updatingAllJobnames = true;
		
		GrisuClientService.Util.getInstance().getAllJobnames(null, new AsyncCallback<List<String>>() {
			
			public void onSuccess(List<String> arg0) {
				updatingAllJobnames = false;
				allJobnames = arg0;
			}
			
			public void onFailure(Throwable arg0) {
				updatingAllJobnames = false;
				arg0.printStackTrace();
			}
		});
		
	}
	
	private void updateUserProperties() {
		
		updatingUserProperties = true;
		
		GrisuClientService.Util.getInstance().getUserProperties(new AsyncCallback<Map<String,String>>() {

			public void onFailure(Throwable arg0) {

				arg0.printStackTrace();
				updatingUserProperties = false;

			}

			public void onSuccess(Map<String, String> arg0) {

				userProperties = arg0;
				updatingUserProperties = false;
				
			}
		});
		
	}
	
	public List<String> getAllJobnames() {
		return allJobnames;
	}
	public boolean isUpdatingAllJobnames() {
		return updatingAllJobnames;
	}
	
	public Map<String, String> getUserProperties() {
		return userProperties;
	}
	public boolean isUpdatingUserProperties() {
		return updatingUserProperties;
	}

	public void onJobSubmissionFinished(JobSubmissionFinishedEvent e) {

		getAllJobnames().add(e.getJobname());
		
	}

}
