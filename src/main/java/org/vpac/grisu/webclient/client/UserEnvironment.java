package org.vpac.grisu.webclient.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.webclient.client.external.Constants;
import org.vpac.grisu.webclient.client.files.FileDeletionFinishedEvent;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;
import org.vpac.grisu.webclient.client.jobcreation.JobSubmissionFinishedEvent;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;
import org.vpac.grisu.webclient.client.jobmonitoring.JobStatusChangedEvent;
import org.vpac.grisu.webclient.client.jobmonitoring.KillingJobsFinishedEvent;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserEnvironment implements JobSubmissionFinishedEvent.Handler,
		KillingJobsFinishedEvent.Handler {

	private ListLoader jobLoader = null;
	private Map<String, GrisuJob> jobMap = new HashMap<String, GrisuJob>();
	
	private Timer jobListRefreshTimer;
	private int jobListRefreshInMilliSeconds = 100000;

	private List<String> allJobnames = new ArrayList<String>();
	private boolean updatingAllJobnames = false;
	
	

	private Map<String, String> userProperties = new HashMap<String, String>();
	private boolean updatingUserProperties = false;

	private String[] fqans = null;
	private boolean updatingFqans = false;

	private static UserEnvironment singleton;
	private boolean initialized = false;

	public static UserEnvironment getInstance() {
		if (singleton == null) {
			singleton = new UserEnvironment();
			EventBus.get().addHandler(JobSubmissionFinishedEvent.TYPE,
					singleton);
			EventBus.get().addHandler(KillingJobsFinishedEvent.TYPE, singleton);
		}
		return singleton;
	}

	public UserEnvironment() {
		
		jobListRefreshTimer = new Timer() {

			@Override
			public void run() {

				getJobLoader().load();
				
			}
			
		};
		jobListRefreshTimer.scheduleRepeating(jobListRefreshInMilliSeconds);
	}

	public void initOrUpdate() {
		updateAllJobnames();
		updateUserProperties();
		updateFqans();
	}

	private void fireUserEnvironmentInitialized() {

		if (!initialized) {
			if (updatingAllJobnames == false && updatingUserProperties == false
					&& updatingFqans == false) {

				UserEnvironmentLoadedEvent event = new UserEnvironmentLoadedEvent(
						this);

				EventBus.get().fireEvent(event);
				initialized = true;
			}
		}

	}

	private void updateAllJobnames() {

		updatingAllJobnames = true;

		GrisuClientService.Util.getInstance().getAllJobnames(null,
				new AsyncCallback<List<String>>() {

					public void onSuccess(List<String> arg0) {
						updatingAllJobnames = false;
						allJobnames = arg0;
						fireUserEnvironmentInitialized();
					}

					public void onFailure(Throwable arg0) {
						updatingAllJobnames = false;
						arg0.printStackTrace();
					}
				});

	}

	private void updateUserProperties() {

		updatingUserProperties = true;

		GrisuClientService.Util.getInstance().getUserProperties(
				new AsyncCallback<Map<String, String>>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
						updatingUserProperties = false;

					}

					public void onSuccess(Map<String, String> arg0) {

						userProperties = arg0;
						updatingUserProperties = false;
						System.out.println("Userproperties loaded");
						fireUserEnvironmentInitialized();
					}
				});

	}

	public void updateFqans() {

		updatingFqans = true;

		GrisuClientService.Util.getInstance().getFqans(
				new AsyncCallback<String[]>() {

					public void onFailure(Throwable arg0) {

						updatingFqans = false;
						arg0.printStackTrace();

					}

					public void onSuccess(String[] arg0) {

						fqans = arg0;
						updatingFqans = false;
						fireUserEnvironmentInitialized();
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

	public String getUserProperty(String key) {
		String result = userProperties.get(key);
		return result;
	}

	public void setUserProperty(final String key, final String value) {

		userProperties.put(key, value);

		GrisuClientService.Util.getInstance().setUserProperty(key, value,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
					}

					public void onSuccess(Void arg0) {

						System.out.println("Set user property: " + key + ", "
								+ value);
					}
				});

	}

	public boolean isUpdatingUserProperties() {
		return updatingUserProperties;
	}

	public void onJobSubmissionFinished(JobSubmissionFinishedEvent e) {

		getAllJobnames().add(e.getJobname());

	}

	public ListLoader getJobLoader() {

		if (jobLoader == null) {
			RpcProxy<List<GrisuJob>> proxy = new RpcProxy<List<GrisuJob>>() {
				@Override
				protected void load(Object loadConfig,
						AsyncCallback<List<GrisuJob>> callback) {

					GrisuClientService.Util.getInstance()
							.ps("", true, callback);

				}
			};

			jobLoader = new BaseListLoader<ListLoadResult<GrisuJob>>(proxy);

			jobLoader.setSortDir(SortDir.ASC);
			jobLoader.setSortField(Constants.SUBMISSION_TIME_KEY);
			jobLoader.setRemoteSort(false);
			
			jobLoader.addLoadListener(new LoadListener(){
				
				@Override
				public void loaderLoad(LoadEvent le) {
					
					super.loaderLoad(le);
					
					for ( GrisuJob job : (List<GrisuJob>)le.getData() ) {
						
						GrisuJob cachedJob = jobMap.get(job.getJobname());
						if ( cachedJob != null ) {
							
							int oldStatus = cachedJob.getStatus();
							int newStatus = job.getStatus();
							if ( oldStatus != newStatus ) {
								JobStatusChangedEvent event = new JobStatusChangedEvent(job, cachedJob.getStatus());
								EventBus.get().fireEvent(event);
							}
						}
						
						jobMap.put(job.getJobname(), job);
						
					}
					
				}
				
			});
		}
		return jobLoader;
	}

	public String[] getAllFqans() {
		return fqans;
	}
	
	public void submitJob(final Map<String, String> jobProperties) {
		
		GrisuClientService.Util.getInstance().submitJob(jobProperties,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
						JobSubmissionFinishedEvent event = new JobSubmissionFinishedEvent(
								jobProperties.get(Constants.JOBNAME_KEY), true);
						EventBus.get().fireEvent(event);
						Window.alert(arg0.getLocalizedMessage());

					}

					public void onSuccess(Void arg0) {

						JobSubmissionFinishedEvent event = new JobSubmissionFinishedEvent(
								jobProperties.get(Constants.JOBNAME_KEY), false);
						EventBus.get().fireEvent(event);
					}
				});
	}
	
	public void killJobs(final List<GrisuJob> jobsToDelete) {
		
		if ( jobsToDelete == null || jobsToDelete.size() == 0 ) {
			return;
		}
		
		GrisuClientService.Util.getInstance().killJobs(jobsToDelete, new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
			}

			public void onSuccess(Void arg0) {

				GrisuClientService.Util.getInstance().getCurrentStatus(jobsToDelete.get(0).getJobname(), new AsyncCallback<DtoActionStatus>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
					}

					public void onSuccess(DtoActionStatus arg0) {

						KillingJobsFinishedEvent event = new KillingJobsFinishedEvent(jobsToDelete, arg0);
						
						EventBus.get().fireEvent(event);
						
					}
				});
				
			}
		});
		
	}
	
	public void deleteFiles(final List<GrisuFileObject> filesToDelete) {
		
		GrisuClientService.Util.getInstance().rm(filesToDelete, new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {

				arg0.printStackTrace();
			}

			public void onSuccess(Void arg0) {

				GrisuClientService.Util.getInstance().getCurrentStatus(filesToDelete.get(0).getUrl(), new AsyncCallback<DtoActionStatus>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
						
					}

					public void onSuccess(DtoActionStatus arg0) {

						FileDeletionFinishedEvent event = new FileDeletionFinishedEvent(filesToDelete, arg0);
						
						EventBus.get().fireEvent(event);
						
					}
				});
				
			}
		});
		
	}	

	public void onJobsKilled(KillingJobsFinishedEvent e) {
	
		getJobLoader().load();
	
	}

}
