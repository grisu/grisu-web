package org.vpac.grisu.webclient.client.jobmonitoring;

import grisu.client.model.dto.DtoActionStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class KillingJobsFinishedEvent extends GwtEvent<KillingJobsFinishedEvent.Handler> {

	/**
	 * Interface to describe this event. Handlers must implement.
	 */
	public interface Handler extends EventHandler {
		public void onJobsKilled(KillingJobsFinishedEvent e);
	}

	public static final GwtEvent.Type<KillingJobsFinishedEvent.Handler> TYPE = new GwtEvent.Type<KillingJobsFinishedEvent.Handler>();

	private List<GrisuJob> jobs = null;

	private final DtoActionStatus status;

	public KillingJobsFinishedEvent(List<GrisuJob> jobs, DtoActionStatus status) {
		this.jobs = jobs;
		this.status = status;
	}
	@Override
	protected void dispatch(Handler handler) {
		handler.onJobsKilled(this);

	}

	@Override
	public GwtEvent.Type<KillingJobsFinishedEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Set<String> getFoldersToRefresh() {

		Set<String> folders = new HashSet<String>();

		for ( GrisuJob job : getJobs() ) {
			folders.add(job.getJobDirectory().substring(0, job.getJobDirectory().lastIndexOf("/")));
		}

		return folders;

	}

	public List<GrisuJob> getJobs() {
		return this.jobs;
	}

	public DtoActionStatus getStatus() {
		return this.status;
	}

	public boolean wasSuccessful() {
		return !status.getFailed();
	}

}
