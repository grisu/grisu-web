package org.vpac.grisu.webclient.client;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class MainPanel extends LayoutContainer {
	private ContentPanel contentPanel;
	private CenterPanel centerPanel;
	private Hyperlink hyperlink;
	private Hyperlink hyperlink_1;
	private Hyperlink hyperlink_2;

	public MainPanel() {
		setWidth("960");
		setLayout(new BorderLayout());
		getContentPanel().setLayout(new RowLayout(Orientation.VERTICAL));
		add(getContentPanel(), new BorderLayoutData(LayoutRegion.WEST));
		add(getCenterPanel(), new BorderLayoutData(LayoutRegion.CENTER));
	}

	private ContentPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new ContentPanel();
			contentPanel.setHideCollapseTool(true);
			contentPanel.setHeading("Navigation");
			contentPanel.setCollapsible(true);
			contentPanel.add(getHyperlink());
			contentPanel.add(getHyperlink_1());
			contentPanel.add(getHyperlink_2());
		}
		return contentPanel;
	}
	private CenterPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new CenterPanel();
		}
		return centerPanel;
	}
	private Hyperlink getHyperlink() {
		if (hyperlink == null) {
			hyperlink = new Hyperlink("Create generic job", false, "createJob");
			hyperlink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					
					getCenterPanel().setVisiblePanel(CenterPanel.JOBCREATION);
					
				}
			});
		}
		return hyperlink;
	}
	private Hyperlink getHyperlink_1() {
		if (hyperlink_1 == null) {
			hyperlink_1 = new Hyperlink("Monitor running jobs", false, "jobs");
			hyperlink_1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					
					getCenterPanel().setVisiblePanel(CenterPanel.JOBLIST);
					
				}
			});
		}
		return hyperlink_1;
	}
	private Hyperlink getHyperlink_2() {
		if (hyperlink_2 == null) {
			hyperlink_2 = new Hyperlink("Files", false, "files");
			hyperlink_2.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					
					getCenterPanel().setVisiblePanel(CenterPanel.FILEMANAGER);
					
				}
			});
		}
		return hyperlink_2;
	}
}
