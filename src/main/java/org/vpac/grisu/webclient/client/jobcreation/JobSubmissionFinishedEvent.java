package org.vpac.grisu.webclient.client.jobcreation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class JobSubmissionFinishedEvent extends GwtEvent<JobSubmissionFinishedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onJobSubmissionFinished(JobSubmissionFinishedEvent e);
    }

    public static final GwtEvent.Type<JobSubmissionFinishedEvent.Handler> TYPE = new GwtEvent.Type<JobSubmissionFinishedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onJobSubmissionFinished(this);
	}
	
    @Override
    public GwtEvent.Type<JobSubmissionFinishedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private String jobname;
    private boolean failed;
    
    public JobSubmissionFinishedEvent() {
    }
    
    public JobSubmissionFinishedEvent(String jobname, boolean failed) {
    	this.jobname = jobname;
    	this.failed = failed;
    }

    public String getJobname() {
    	return jobname;
    }
    public void setJobname(String jobname) {
    	this.jobname = jobname;
    }
	
    public boolean isFailed() {
    	return failed;
    }
    public void setFailed(boolean failed) {
    	this.failed = failed;
    }

}
