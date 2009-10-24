package org.vpac.grisu.webclient.client.jobmonitoring;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class JobStatusChangedEvent extends GwtEvent<JobStatusChangedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onJobStatusChanged(JobStatusChangedEvent e);
    }

    public static final GwtEvent.Type<JobStatusChangedEvent.Handler> TYPE = new GwtEvent.Type<JobStatusChangedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onJobStatusChanged(this);
	}
	
    @Override
    public GwtEvent.Type<JobStatusChangedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private final GrisuJob job;
    private final int oldStatus;

    public JobStatusChangedEvent(GrisuJob job, int oldStatus) {
    	this.job = job;
    	this.oldStatus = oldStatus;
    }

    public GrisuJob getJob() {
    	return this.job;
    }
    
    public int getOldStatus() {
    	return this.oldStatus;
    }
    

}
