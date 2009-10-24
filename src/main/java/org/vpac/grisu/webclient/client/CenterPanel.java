package org.vpac.grisu.webclient.client;

import org.vpac.grisu.webclient.client.files.FilePanel;
import org.vpac.grisu.webclient.client.jobcreation.BasicJobCreationPanel;
import org.vpac.grisu.webclient.client.jobmonitoring.JobListPanel;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.CardPanel;

public class CenterPanel extends ContentPanel {
	
	public final static int JOBCREATION = 0;
	public final static int JOBLIST = 1;
	public final static int FILEMANAGER = 2;
	
	private BasicJobCreationPanel basicJobCreationPanel;
	private JobListPanel jobListPanel;
	private FilePanel filePanel;
	private CardPanel cardPanel;
	
	public CenterPanel() {
		setHeaderVisible(false);
		setBodyBorder(false);
		setBorders(false);
		setLayout(new FitLayout());
		add(getCardPanel());
	}
	
	
	public void setVisiblePanel(int panel) {
		switch(panel) {
		case JOBCREATION: cardPanel.setActiveItem(getGenericJobCreationPanel()); break;
		case JOBLIST: getJobListPanel().showJobListTab(); cardPanel.setActiveItem(getJobListPanel()); break;
		case FILEMANAGER: cardPanel.setActiveItem(getFilePanel()); break;
		}
	}

	private BasicJobCreationPanel getGenericJobCreationPanel() {
		if (basicJobCreationPanel == null) {
			basicJobCreationPanel = new BasicJobCreationPanel();
		}
		return basicJobCreationPanel;
	}
	private JobListPanel getJobListPanel() {
		if (jobListPanel == null) {
			jobListPanel = new JobListPanel();
		}
		return jobListPanel;
	}
	private FilePanel getFilePanel() {
		if ( filePanel == null ) {
			filePanel = new FilePanel();
		}
		return filePanel;
	}
	private CardPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new CardPanel();
			cardPanel.add(getGenericJobCreationPanel());
			cardPanel.add(getJobListPanel());
			cardPanel.add(getFilePanel());
		}
		return cardPanel;
	}
}
