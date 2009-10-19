package org.vpac.grisu.webclient.client.files;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FillData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.button.Button;

public class FileManagerPanel extends LayoutContainer {
	private ContentPanel contentPanel;
	private ContentPanel contentPanel_1;
	private FileListPanel fileListPanel;
	private FileListPanel fileListPanel_1;
	private ContentPanel buttonPanelLeft;
	private ContentPanel buttonPanelRight;
	private Button btnRefresh;
	private Button copyButtonLeft;
	private Button button;

	public FileManagerPanel() {
		setLayout(new FillLayout(Orientation.HORIZONTAL));
		getContentPanel_2().setLayout(new RowLayout(Orientation.VERTICAL));
		add(getContentPanel_2(), new FillData(5));
		getContentPanel_1_1().setLayout(new RowLayout(Orientation.VERTICAL));
		add(getContentPanel_1_1(), new FillData(5));
	}
	private ContentPanel getContentPanel_2() {
		if (contentPanel == null) {
			contentPanel = new ContentPanel();
			contentPanel.setBodyBorder(false);
			contentPanel.add(getFileListPanel(), new RowData(Style.DEFAULT, 1.0, new Margins(10, 5, 10, 5)));
			contentPanel.setHeading("New ContentPanel");
			contentPanel.setCollapsible(true);
			getButtonPanelLeft().setLayout(new HBoxLayout());
			contentPanel.add(getButtonPanelLeft(), new RowData(Style.DEFAULT, 20.0, new Margins(0, 5, 10, 5)));
		}
		return contentPanel;
	}
	private ContentPanel getContentPanel_1_1() {
		if (contentPanel_1 == null) {
			contentPanel_1 = new ContentPanel();
			contentPanel_1.setBodyBorder(false);
			contentPanel_1.add(getFileListPanel_1_2(), new RowData(Style.DEFAULT, 1.0, new Margins(10, 5, 10, 5)));
			contentPanel_1.setHeading("New ContentPanel");
			contentPanel_1.setCollapsible(true);
			getButtonPanelRight().setLayout(new HBoxLayout());
			contentPanel_1.add(getButtonPanelRight(), new RowData(Style.DEFAULT, 20.0, new Margins(0, 5, 10, 5)));
		}
		return contentPanel_1;
	}
	private FileListPanel getFileListPanel() {
		if (fileListPanel == null) {
			fileListPanel = new FileListPanel();
		}
		return fileListPanel;
	}
	private FileListPanel getFileListPanel_1_2() {
		if (fileListPanel_1 == null) {
			fileListPanel_1 = new FileListPanel();
		}
		return fileListPanel_1;
	}
	private ContentPanel getButtonPanelLeft() {
		if (buttonPanelLeft == null) {
			buttonPanelLeft = new ContentPanel();
			buttonPanelLeft.setHeaderVisible(false);
			buttonPanelLeft.setBodyBorder(false);
			buttonPanelLeft.addButton(getBtnRefresh());
			buttonPanelLeft.addButton(getCopyButtonLeft());
			buttonPanelLeft.setCollapsible(false);
			buttonPanelLeft.setButtonAlign(HorizontalAlignment.RIGHT);
		}
		return buttonPanelLeft;
	}
	private ContentPanel getButtonPanelRight() {
		if (buttonPanelRight == null) {
			buttonPanelRight = new ContentPanel();
			buttonPanelRight.setHeaderVisible(false);
			buttonPanelRight.setBodyBorder(false);
			buttonPanelRight.addButton(getButton());
			buttonPanelRight.setCollapsible(false);
		}
		return buttonPanelRight;
	}
	private Button getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new Button("Refresh");
		}
		return btnRefresh;
	}
	private Button getCopyButtonLeft() {
		if (copyButtonLeft == null) {
			copyButtonLeft = new Button("Copy -->");
		}
		return copyButtonLeft;
	}
	private Button getButton() {
		if (button == null) {
			button = new Button("New Button");
		}
		return button;
	}
}
