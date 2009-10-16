package org.vpac.grisu.webclient.client;

import org.vpac.grisu.webclient.client.files.FileManagerPanel;
import org.vpac.grisu.webclient.client.jobcreation.GenericJobCreationPanel;
import org.vpac.grisu.webclient.client.jobmonitoring.JobListPanel;

import com.extjs.gxt.ui.client.widget.CardPanel;

public class CenterPanel extends CardPanel {
	
	public final static int JOBCREATION = 0;
	public final static int JOBLIST = 1;
	public final static int FILEMANAGER = 2;
	
	private GenericJobCreationPanel genericJobCreationPanel;
	private JobListPanel jobListPanel;
	private FileManagerPanel fileManager;

	public CenterPanel() {
		add(getGenericJobCreationPanel());
		add(getJobListPanel());
		add(getFileManagerPanel());
	}
	
	public void setVisiblePanel(int panel) {
		switch(panel) {
		case JOBCREATION: setActiveItem(getGenericJobCreationPanel()); break;
		case JOBLIST: setActiveItem(getJobListPanel()); break;
		case FILEMANAGER: setActiveItem(getFileManagerPanel()); break;
		}
	}

	private GenericJobCreationPanel getGenericJobCreationPanel() {
		if (genericJobCreationPanel == null) {
			genericJobCreationPanel = new GenericJobCreationPanel();
		}
		return genericJobCreationPanel;
	}
	private JobListPanel getJobListPanel() {
		if (jobListPanel == null) {
			jobListPanel = new JobListPanel();
		}
		return jobListPanel;
	}
	private FileManagerPanel getFileManagerPanel() {
		if ( fileManager == null ) {
			fileManager = new FileManagerPanel();
		}
		return fileManager;
	}
}
