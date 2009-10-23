package org.vpac.grisu.webclient.client.jobmonitoring;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class JobPropertiesPanel extends LayoutContainer {
	private TextArea textArea;
	
	private GrisuJob job;

	public JobPropertiesPanel() {
		setLayout(new FitLayout());
		add(getTextArea());
	}
	
	public JobPropertiesPanel(GrisuJob job) {
		this();
		setJob(job);
	}
	
	public void setJob(GrisuJob job) {
		this.job = job;
		getTextArea().setValue(getJobPropertiesText());
	}

	private TextArea getTextArea() {
		if (textArea == null) {
			textArea = new TextArea();
			textArea.setFieldLabel("New TextArea");
		}
		return textArea;
	}
	
	public String getJobPropertiesText() {
		
		if ( job == null ) {
			return "n/a";
		}
		
		StringBuffer result = new StringBuffer("Job properties: \n-----------------\n\n");
		for ( String key : job.getAllProperties().keySet() ) {
			result.append("\t"+key+": "+"\t"+job.getAllProperties().get(key)+"\n");
		}
		
		return result.toString();
		
	}
}
