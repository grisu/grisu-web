package org.vpac.grisu.webclient.client;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Image;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import java.util.Collections;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.table.Table;
import com.extjs.gxt.ui.client.widget.table.TableColumnModel;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class TestPanel extends LayoutContainer {
	private LabelField labelField;
	private Label label;
	private Image image;
	private HorizontalPanel horizontalPanel;
	private Grid grid;
	private ProgressBar progressBar;
	private ContentPanel contentPanel;
	private com.google.gwt.user.client.ui.Grid infoGrid;
	private Text txtPleaseDoubleClick;

	public TestPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		add(getLabelField());
		add(getLabel());
		add(getImage());
		add(getHorizontalPanel(), new RowData(Style.DEFAULT, Style.DEFAULT, new Margins(0, 0, 0, 5)));
		add(getGrid());
		add(getProgressBar());
		getContentPanel().setLayout(new FitLayout());
		add(getContentPanel(), new RowData(Style.DEFAULT, 155.0, new Margins()));
	}

	private LabelField getLabelField() {
		if (labelField == null) {
			labelField = new LabelField("New LabelField");
		}
		return labelField;
	}
	private Label getLabel() {
		if (label == null) {
			label = new Label("New label");
		}
		return label;
	}
	private Image getImage() {
		if (image == null) {
			image = new Image("gxt/images/default/tree/folder.gif");
		}
		return image;
	}
	private HorizontalPanel getHorizontalPanel() {
		if (horizontalPanel == null) {
			final Image img = new Image("gxt/images/default/tree/folder.gif");
			horizontalPanel = new HorizontalPanel();
			horizontalPanel.add(img);
			horizontalPanel.add(new LabelField("test"));
		}
		return horizontalPanel;
	}
	private Grid getGrid() {
		if (grid == null) {
			grid = new Grid(new ListStore(), new ColumnModel(Collections.<ColumnConfig>emptyList()));
			grid.setBorders(true);
		}
		return grid;
	}
	private ProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new ProgressBar();
			progressBar.setHeight("10");
			progressBar.updateProgress(0.2, "Saving...");
		}
		return progressBar;
	}
	private ContentPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new ContentPanel();
			contentPanel.add(getGrid_1());
			contentPanel.setHeading("New ContentPanel");
			contentPanel.setCollapsible(true);
		}
		return contentPanel;
	}
	private com.google.gwt.user.client.ui.Grid getGrid_1() {
		if (infoGrid == null) {
			infoGrid = new com.google.gwt.user.client.ui.Grid(1, 1);
//			infoGrid.setStyleName("centerCell");
			infoGrid.setWidget(0, 0, getTxtPleaseDoubleClick());
			infoGrid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
			infoGrid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		}
		return infoGrid;
	}
	private Text getTxtPleaseDoubleClick() {
		if (txtPleaseDoubleClick == null) {
			txtPleaseDoubleClick = new Text("Please double click a file you want to preview.");
		}
		return txtPleaseDoubleClick;
	}
}
