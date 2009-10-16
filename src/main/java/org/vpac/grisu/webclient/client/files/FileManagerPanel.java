package org.vpac.grisu.webclient.client.files;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;

public class FileManagerPanel extends LayoutContainer {
	private FileListPanel fileListPanel;

	public FileManagerPanel() {
		setLayout(new FitLayout());
		add(getFileListPanel());
	}

	private FileListPanel getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new FileListPanel();
		}
		return fileListPanel;
	}
}
