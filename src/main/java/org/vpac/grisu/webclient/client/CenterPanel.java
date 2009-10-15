package org.vpac.grisu.webclient.client;

import com.extjs.gxt.ui.client.widget.CardPanel;

public class CenterPanel extends CardPanel {
	
	public final static int JOBCREATION = 0;
	public final static int JOBLIST = 1;
	
	private GenericJobCreationPanel genericJobCreationPanel;
	private JobListPanel jobListPanel;

	public CenterPanel() {
		add(getGenericJobCreationPanel());
		add(getJobListPanel());
	}
	
	public void setVisiblePanel(int panel) {
		switch(panel) {
		case JOBCREATION: setActiveItem(getGenericJobCreationPanel()); break;
		case JOBLIST: setActiveItem(getJobListPanel()); break;
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
}
