package org.vpac.grisu.webclient.client.files;

import java.util.List;

import org.vpac.grisu.webclient.client.external.Constants;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class FileManagerPanel extends LayoutContainer implements
		ValueChangeHandler<GrisuFileObject> {

	public static final int FILELISTPANEL = 0;
	public static final int PREVIEWPANEL = 1;

	public static final String FILELISTPANEL_STRING = "Files";
	public static final String PREVIEWPANEL_STRING = "Preview";

	public final String COPY_LEFT = "COPY_LEFT";
	public final String REFRESH_LEFT = "REFRESH_LEFT";
	public final String REFRESH_RIGHT = "REFRESH_RIGHT";
	public final String TOGGLE_BUTTON = "TOGGLE";
	
	private String leftStartUrl = null;

	SelectionListener<ButtonEvent> l = new SelectionListener<ButtonEvent>() {

		@Override
		public void componentSelected(ButtonEvent ce) {

			String id = ce.getComponent().getId();

			if (TOGGLE_BUTTON.equals(id)) {

				String buttonText = ce.getButton().getText();

				if (FILELISTPANEL_STRING.equals(buttonText)) {
					setVisiblePanel(FILELISTPANEL);
				} else if (PREVIEWPANEL_STRING.equals(buttonText)) {
					setVisiblePanel(PREVIEWPANEL);
				}
			} else if ( COPY_LEFT.equals(id) ) {
				
				List<GrisuFileObject> sources = leftFileListPanel.getSelectedItems();
				
				boolean started = getRightFileListPanel().copyFilesToCurrentDirectory(sources);
				
				if ( ! started ) {
					MessageBox.alert("Transfer error.", "The selected files could not be copied.", null);
				}
				
			} else if ( REFRESH_LEFT.equals(id) ) {
				
				getLeftFileListPanel().refreshCurrentDirectory();
				
			} else if ( REFRESH_RIGHT.equals(id) ) {
				
			}
		}

	};

	private ContentPanel leftFileManagerPanel;
	private ContentPanel rightFileManagerPanel;
	private FileListPanel leftFileListPanel;
	private FileListPanel rightFileListPanel;

	private Button btnRefresh;
	private Button copyButtonLeft;
	private CardPanel rightCardPanel;
	private FilePreviewPanel filePreviewPanel;
	private ContentPanel rightPanel;
	private Button toggleButton;
	
	private final String leftFilePanelName;
	private final String rightFilePanelName;

	private String currentPanel = FILELISTPANEL_STRING;

//	public FileManagerPanel() {
//		this(null, null, null);
//	}
//	
	public FileManagerPanel(String leftStartUrl, String leftFilePanelName, String rightFilePanelName) {
		this.leftStartUrl = leftStartUrl;
		this.leftFilePanelName = leftFilePanelName;
		this.rightFilePanelName = rightFilePanelName;
		setLayout(new FillLayout(Orientation.HORIZONTAL));
		getLeftContentPanel().setLayout(new RowLayout(Orientation.VERTICAL));
		add(getLeftContentPanel(), new FillData(5));
		add(getRightPanel(), new FillData(5));
	}


	public void setLeftUrl(String url) {
		getLeftFileListPanel().setCurrentDirectory(url);
	}

	public void setRightUrl(String url) {
		getRightFileListPanel().setCurrentDirectory(url);
	}

	public void setVisiblePanel(int panel) {

		switch (panel) {
		case FILELISTPANEL:
			getRightCardPanel().setActiveItem(getRightContentPanel());
			getToggleButton().setText(PREVIEWPANEL_STRING);
			getCopyButtonLeft().setEnabled(true);
			break;
		case PREVIEWPANEL:
			getRightCardPanel().setActiveItem(getFilePreviewPanel());
			getToggleButton().setText(FILELISTPANEL_STRING);
			getCopyButtonLeft().setEnabled(false);
			break;
		}

	}

	private ContentPanel getLeftContentPanel() {
		if (leftFileManagerPanel == null) {
			leftFileManagerPanel = new ContentPanel();
			leftFileManagerPanel.setBodyBorder(false);
			leftFileManagerPanel.add(getLeftFileListPanel(), new RowData(
					Style.DEFAULT, 1.0, new Margins(10, 5, 10, 5)));
			leftFileManagerPanel.setHeaderVisible(false);
			leftFileManagerPanel.setCollapsible(false);
			leftFileManagerPanel.addButton(getBtnRefresh());
			leftFileManagerPanel.addButton(getCopyButtonLeft());
		}
		return leftFileManagerPanel;
	}

	private ContentPanel getRightContentPanel() {
		if (rightFileManagerPanel == null) {
			rightFileManagerPanel = new ContentPanel();
			rightFileManagerPanel
					.setLayout(new RowLayout(Orientation.VERTICAL));
			rightFileManagerPanel.setBodyBorder(false);
			rightFileManagerPanel.add(getRightFileListPanel(), new RowData(
					Style.DEFAULT, 1.0, new Margins(10, 5, 10, 5)));
			rightFileManagerPanel.setHeaderVisible(false);
			rightFileManagerPanel.setCollapsible(false);
		}
		return rightFileManagerPanel;
	}

	private FileListPanel getLeftFileListPanel() {
		if (leftFileListPanel == null) {
			leftFileListPanel = new FileListPanel(leftStartUrl, leftFilePanelName);
			leftFileListPanel.addValueChangeHandler(this);
		}
		return leftFileListPanel;
	}

	private FileListPanel getRightFileListPanel() {
		if (rightFileListPanel == null) {
			rightFileListPanel = new FileListPanel(null, rightFilePanelName);
		}
		return rightFileListPanel;
	}

	private Button getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new Button("Refresh", l);
			btnRefresh.setId(REFRESH_LEFT);
		}
		return btnRefresh;
	}

	private Button getCopyButtonLeft() {
		if (copyButtonLeft == null) {
			copyButtonLeft = new Button("Copy -->", l);
			copyButtonLeft.setId(COPY_LEFT);
		}
		return copyButtonLeft;
	}

	private CardPanel getRightCardPanel() {
		if (rightCardPanel == null) {
			rightCardPanel = new CardPanel();
			rightCardPanel.add(getRightContentPanel());
			rightCardPanel.add(getFilePreviewPanel());
		}
		return rightCardPanel;
	}

	private FilePreviewPanel getFilePreviewPanel() {
		if (filePreviewPanel == null) {
			filePreviewPanel = new FilePreviewPanel();
			getLeftFileListPanel().addValueChangeHandler(filePreviewPanel);
		}
		return filePreviewPanel;
	}

	public void onValueChange(ValueChangeEvent<GrisuFileObject> arg0) {

		setVisiblePanel(PREVIEWPANEL);

	}

	private ContentPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new ContentPanel();
			rightPanel.setHeaderVisible(false);
			rightPanel.setBodyBorder(false);
			rightPanel.setCollapsible(true);
			rightPanel.setLayout(new FitLayout());
			rightPanel.add(getRightCardPanel());
			rightPanel.addButton(getToggleButton());
		}
		return rightPanel;
	}

	private Button getToggleButton() {
		if (toggleButton == null) {
			toggleButton = new Button(PREVIEWPANEL_STRING, l);
			toggleButton.setId(TOGGLE_BUTTON);
		}
		return toggleButton;
	}
}
