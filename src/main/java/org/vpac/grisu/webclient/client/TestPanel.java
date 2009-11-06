package org.vpac.grisu.webclient.client;

import java.util.Collections;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class TestPanel extends LayoutContainer {
	private LabelField labelField;
	private Label label;
	private Image image;
	private HorizontalPanel horizontalPanel;
	private Grid grid;
	private ProgressBar progressBar;
	private ContentPanel contentPanel;

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
			contentPanel.setHeading("New ContentPanel");
			contentPanel.setCollapsible(true);
		}
		return contentPanel;
	}
}
