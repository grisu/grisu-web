package org.vpac.grisu.webclient.client;

import org.vpac.grisu.webclient.client.external.JobConstants;
import org.vpac.grisu.webclient.client.files.FileTransferStartedEvent;
import org.vpac.grisu.webclient.client.files.FileTransferStatusChangedEvent;
import org.vpac.grisu.webclient.client.jobcreation.JobSubmissionFinishedEvent;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;
import org.vpac.grisu.webclient.client.jobmonitoring.JobStatusChangedEvent;
import org.vpac.grisu.webclient.client.jobmonitoring.KillingJobsFinishedEvent;

import com.extjs.gxt.ui.client.widget.Info;

public class MessagePopUp implements FileTransferStartedEvent.Handler, FileTransferStatusChangedEvent.Handler,
				JobSubmissionFinishedEvent.Handler, JobStatusChangedEvent.Handler,
				KillingJobsFinishedEvent.Handler{
	
	public MessagePopUp() {
		
		EventBus.get().addHandler(FileTransferStartedEvent.TYPE, this);
		EventBus.get().addHandler(FileTransferStatusChangedEvent.TYPE, this);
		EventBus.get().addHandler(JobSubmissionFinishedEvent.TYPE, this);
		EventBus.get().addHandler(JobStatusChangedEvent.TYPE, this);
		EventBus.get().addHandler(KillingJobsFinishedEvent.TYPE, this);
	}

	public void onFileTransferStarted(FileTransferStartedEvent e) {

		Info.display("File transfer started in background", "Started file transfer to target "+e.getTarget().getFileName());
		
	}

	public void onFileTransferStatusChanged(FileTransferStatusChangedEvent e) {

		StringBuffer message = new StringBuffer("Filetransfer to "+e.getFileTransferObject().getTarget().getFileName()+":\n\n");
		if ( e.isFileTransferFailed() ) {
			message.append("Failed.");
			Info.display("Filetransfer failed", message.toString());	
		} else if ( e.isFileTransferFinished() ) {
			message.append("Finished.");
			Info.display("Filetransfer finished.", message.toString());
		}

	}

	public void onJobSubmissionFinished(JobSubmissionFinishedEvent e) {

		if ( ! e.isFailed() ) {
			Info.display("Job submission finished", "Job submission for job "+e.getJobname()+" finished successfully.");
		}
		
	}

	public void onJobStatusChanged(JobStatusChangedEvent e) {

		if ( e.getJob().getStatus() >= JobConstants.FINISHED_EITHER_WAY ) {
			Info.display("Job "+e.getJob().getJobname()+" finished.", "Status: "+JobConstants.translateStatus(e.getJob().getStatus()));
		} else if ( e.getJob().getStatus() > JobConstants.UNSUBMITTED ) {
			Info.display("Status for job "+e.getJob().getJobname()+" changed.", "New status: "+JobConstants.translateStatus(e.getJob().getStatus()));
		}
		
	}

	public void onJobsKilled(KillingJobsFinishedEvent e) {

		StringBuffer killedJobs = new StringBuffer();
		
		for ( GrisuJob job : e.getJobs() ) {
			killedJobs.append(job.getJobname()+"/n");
		}
		
		if ( e.wasSuccessful() ) {
			Info.display("Jobs killed successfully.", "Killed jobs:\n\n"+killedJobs.toString());
		} else {
			Info.display("Error(s) killing jobs.", "Could not kill all of the following jobs successfully:\n\n"+killedJobs.toString());
		}
		
	}
	
	

}
