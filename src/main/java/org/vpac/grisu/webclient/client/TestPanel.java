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

public class TestPanel extends LayoutContainer {
	private LabelField labelField;
	private Label label;
	private Image image;
	private HorizontalPanel horizontalPanel;
	private Grid grid;

	public TestPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		add(getLabelField());
		add(getLabel());
		add(getImage());
		add(getHorizontalPanel(), new RowData(Style.DEFAULT, Style.DEFAULT, new Margins(0, 0, 0, 5)));
		add(getGrid());
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
}
