package org.vpac.grisu.webclient.client.jobmonitoring;

import java.util.List;

import org.vpac.grisu.webclient.client.GrisuClientService;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class JobFinishedEvent extends GwtEvent<JobFinishedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onJobFinished(JobFinishedEvent e);
    }

    public static final GwtEvent.Type<JobFinishedEvent.Handler> TYPE = new GwtEvent.Type<JobFinishedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onJobFinished(this);
	}
	
    @Override
    public GwtEvent.Type<JobFinishedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private String jobname;
    private boolean failed;
    
    public JobFinishedEvent() {
    }
    
    public JobFinishedEvent(String jobname, boolean failed) {
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
