package org.vpac.grisu.webclient.client.jobmonitoring;

import org.vpac.grisu.webclient.client.files.FileManagerPanel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;

public class SingleJobPanel extends LayoutContainer {
	private TabPanel tabPanel;
	private TabItem tbtmFiles;
	private TabItem tbtmJobDetails;
	private FileManagerPanel fileManagerPanel;

	private GrisuJob job;
	private JobPropertiesPanel jobPropertiesPanel;
	
	/**
	 * @wbp.parser.constructor
	 */
	public SingleJobPanel() {
		setLayout(new FitLayout());
		add(getTabPanel());
	}
	
	public SingleJobPanel(GrisuJob job) {
		setLayout(new FitLayout());
		this.job = job;
		add(getTabPanel());
	}

	
	public void setJob(GrisuJob job) {
		this.job = job;
		getFileManagerPanel().setLeftUrl(job.getJobDirectory());
		getJobPropertiesPanel().setJob(job);
	}

	private TabPanel getTabPanel() {
		if (tabPanel == null) {
			tabPanel = new TabPanel();
			tabPanel.setTabPosition(TabPosition.BOTTOM);
			tabPanel.add(getTbtmFiles());
			tabPanel.add(getTbtmJobDetails());
		}
		return tabPanel;
	}
	private TabItem getTbtmFiles() {
		if (tbtmFiles == null) {
			tbtmFiles = new TabItem("Files");
			tbtmFiles.setLayout(new FitLayout());
			tbtmFiles.add(getFileManagerPanel());
		}
		return tbtmFiles;
	}
	private TabItem getTbtmJobDetails() {
		if (tbtmJobDetails == null) {
			tbtmJobDetails = new TabItem("Job details");
			tbtmJobDetails.setLayout(new FitLayout());
			tbtmJobDetails.add(getJobPropertiesPanel());
		}
		return tbtmJobDetails;
	}
	private FileManagerPanel getFileManagerPanel() {
		if (fileManagerPanel == null) {
			if ( job == null ) {
				fileManagerPanel = new FileManagerPanel(null, null, null);
			} else { 
				fileManagerPanel = new FileManagerPanel(job.getJobDirectory(), null, null);
			}
			fileManagerPanel.setVisiblePanel(FileManagerPanel.PREVIEWPANEL);
		}
		return fileManagerPanel;
	}
	

	private JobPropertiesPanel getJobPropertiesPanel() {
		if (jobPropertiesPanel == null) {
			if ( job == null ) {
				jobPropertiesPanel = new JobPropertiesPanel();
			} else {
				jobPropertiesPanel = new JobPropertiesPanel(job);
			}
		}
		return jobPropertiesPanel;
	}
}
