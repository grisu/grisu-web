package org.vpac.grisu.webclient.client.jobmonitoring;

import java.util.ArrayList;
import java.util.List;

import org.vpac.grisu.webclient.client.GrisuClientService;
import org.vpac.grisu.webclient.client.external.Constants;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JobListPanel extends LayoutContainer {
	private Grid grid;
	private ContentPanel contentPanel;
	
	private ListLoader loader;
	private Button button;

	public JobListPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		add(getGrid(), new RowData(1.0, 0.8, new Margins(10, 10, 10, 10)));
		add(getContentPanel(), new RowData(1.0, 0.2, new Margins(0, 10, 10, 10)));

	}
	
	private List<ColumnConfig> createColumnConfig() {
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId(Constants.JOBNAME_KEY);
		column.setHeader("Jobname");
		column.setWidth(120);
		configs.add(column);

		column = new ColumnConfig(Constants.APPLICATIONVERSION_KEY, "Version", 60);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig(Constants.FQAN_KEY, "Group", 40);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig(Constants.SUBMISSION_TIME_KEY, "Submitted on", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
		configs.add(column);
		
		return configs;
	}
	
	private Grid getGrid() {

		if (grid == null) {
			// data proxy
			RpcProxy<List<GrisuJob>> proxy = new RpcProxy<List<GrisuJob>>() {
				@Override
				protected void load(Object loadConfig,
						AsyncCallback<List<GrisuJob>> callback) {

					GrisuClientService.Util.getInstance().ps("", true, callback);
					
				}
			};
			
			loader = new BaseListLoader<ListLoadResult<GrisuJob>>(proxy); 
				
			ListStore<GrisuJob> store = new ListStore<GrisuJob>(loader); 
			loader.load();
			
			ColumnModel cm = new ColumnModel(createColumnConfig());
			
			grid = new Grid<GrisuJob>(store, cm);
			grid.setStyleAttribute("borderTop", "none"); 
			grid.setBorders(true);
			grid.setAutoExpandColumn(Constants.JOBNAME_KEY);  
			grid.setStripeRows(true);  
		}
		return grid;
	}

	private ContentPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new ContentPanel();
			contentPanel.setHeading("New ContentPanel");
			contentPanel.setCollapsible(true);
			contentPanel.add(getButton());
		}
		return contentPanel;
	}
	private Button getButton() {
		if (button == null) {
			button = new Button("New Button");
			button.addSelectionListener(new SelectionListener<ButtonEvent>() {
				public void componentSelected(ButtonEvent ce) {
					loader.load();
				}
			});
		}
		return button;
	}
}
