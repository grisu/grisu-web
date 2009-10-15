package org.vpac.grisu.webclient.client;

import java.util.ArrayList;
import java.util.List;

import org.vpac.grisu.webclient.client.external.Constants;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JobListPanel extends LayoutContainer {
	private Grid grid;
	private ContentPanel contentPanel;

	public JobListPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		add(getGrid(), new RowData(Style.DEFAULT, 0.8, new Margins()));
		add(getContentPanel(), new RowData(Style.DEFAULT, 0.2, new Margins()));

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

//		column = new ColumnConfig(Constants.SUBMISSION_TIME_KEY, "Submitted on", 100);
//		column.setAlignment(HorizontalAlignment.RIGHT);
//		column.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
//		configs.add(column);
		
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
			
			ListLoader loader = new BaseListLoader<ListLoadResult<GrisuJob>>(proxy); 
				
			ListStore<GrisuJob> store = new ListStore<GrisuJob>(loader); 
			loader.load();
			
			ColumnModel cm = new ColumnModel(createColumnConfig());
			
			grid = new Grid<GrisuJob>(store, cm);
			grid.setBorders(true);
			grid.setAutoExpandColumn(Constants.JOBNAME_KEY);  
			grid.setWidth(400);  
			grid.setAutoHeight(true);  
		}
		return grid;
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
