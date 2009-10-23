package org.vpac.grisu.webclient.client.files;

import org.vpac.grisu.webclient.client.external.Constants;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class FilePanel extends LayoutContainer {
	private FileManagerPanel fileManagerPanel;
	private FileTransferMonitorPanel fltrnsfrmntrpnlFileTransfers;
	private BorderLayout layout = new BorderLayout();

	public FilePanel() {
		setLayout(layout);
		add(getFileManagerPanel(), new BorderLayoutData(LayoutRegion.CENTER));
		BorderLayoutData borderLayoutData = new BorderLayoutData(LayoutRegion.SOUTH, 120.0f);
		borderLayoutData.setMargins(new Margins(5, 5, 5, 5));
		borderLayoutData.setSplit(true);
		borderLayoutData.setCollapsible(true);
		add(getFltrnsfrmntrpnlFileTransfers(), borderLayoutData);
	}
	private FileManagerPanel getFileManagerPanel() {
		if (fileManagerPanel == null) {
			fileManagerPanel = new FileManagerPanel(null, Constants.LEFT_FILE_BROWSER_LAST_URL, Constants.RIGHT_FILE_BROWSER_LAST_URL);
		}
		return fileManagerPanel;
	}
	private FileTransferMonitorPanel getFltrnsfrmntrpnlFileTransfers() {
		if (fltrnsfrmntrpnlFileTransfers == null) {
			fltrnsfrmntrpnlFileTransfers = new FileTransferMonitorPanel();
			fltrnsfrmntrpnlFileTransfers.setHeading("File Transfers");
		}
		return fltrnsfrmntrpnlFileTransfers;
	}
	public void collapseTransferPanel() {
		layout.collapse(LayoutRegion.SOUTH);
	}
	
}
