package org.vpac.grisu.webclient.client.files;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import gwtupload.client.MultiUploader;

public class FileUploadPanel extends LayoutContainer {
	private MultiUploader multiUploader;

	public FileUploadPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		add(getMultiUploader(), new RowData(1.0, 1.0, new Margins()));
	}
	private MultiUploader getMultiUploader() {
		if (multiUploader == null) {
			multiUploader = new MultiUploader();
		}
		return multiUploader;
	}
}
