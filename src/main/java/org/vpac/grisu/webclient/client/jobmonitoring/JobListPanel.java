package org.vpac.grisu.webclient.client.jobmonitoring;

import java.util.ArrayList;
import java.util.List;

import org.vpac.grisu.webclient.client.EventBus;
import org.vpac.grisu.webclient.client.UserEnvironment;
import org.vpac.grisu.webclient.client.external.Constants;
import org.vpac.grisu.webclient.client.external.JobConstants;
import org.vpac.grisu.webclient.client.jobcreation.JobSubmissionFinishedEvent;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JobListPanel extends TabPanel implements JobSubmissionFinishedEvent.Handler {
	private Grid<GrisuJob> grid;
	private ContentPanel contentPanel;
	
	private ListLoader loader = UserEnvironment.getInstance().getJobLoader();
	private Button button;
	private TabItem tbtmJobList;
	private ContentPanel joblistParentPanel;
	
	private String fileListName;
	
	private Menu contextMenu;

	public JobListPanel() {
		setBodyBorder(false);
		setBorders(false);
		add(getTbtmJobList());
		
		EventBus.get().addHandler(JobSubmissionFinishedEvent.TYPE, this);
	}
	
	private Menu getRightClickContextMenu() {
		
		if ( contextMenu == null ) {
		    contextMenu = new Menu();
			MenuItem remove = new MenuItem();  
			remove.setText("Kill selected jobs");  
			remove.addSelectionListener(new SelectionListener<MenuEvent>() {
				public void componentSelected(MenuEvent ce) { 
					
					final Dialog simple = new Dialog();  
					simple.setHeading("Dialog Test");  
					simple.setButtons(Dialog.YESNO);  
					simple.setBodyStyleName("pad-text");  
					simple.addText("Do you really want to kill the selected jobs and delete their jobdirectories?");  
					simple.setScrollMode(Scroll.AUTO);  
					simple.setHideOnButtonClick(true);
					
					simple.getButtonById(Dialog.YES).addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							UserEnvironment.getInstance().killJobs(getSelectedItems());
						}
					});

					simple.show();
					
				}
			});
			contextMenu.add(remove); 
		}
		return contextMenu;
	}
	
	public List<GrisuJob> getSelectedItems() {
		
		return getGrid().getSelectionModel().getSelectedItems();
		
	}

	
	public void showJobListTab() {
		
		setSelection(getTbtmJobList());
		
	}
	
	private List<ColumnConfig> createColumnConfig() {
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();


		GridCellRenderer<GrisuJob> statusRenderer = new GridCellRenderer<GrisuJob>() {

			public Object render(GrisuJob model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GrisuJob> store, Grid<GrisuJob> grid) {

				return JobConstants.translateStatus(model.getStatus());
				
			}
		};
		
		ColumnConfig column = new ColumnConfig();
		column.setId(Constants.JOBNAME_KEY);
		column.setHeader("Jobname");
		column.setWidth(120);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId(Constants.STATUS_STRING);
		column.setHeader("Status");
		column.setWidth(120);
		column.setRenderer(statusRenderer);
		configs.add(column);

//		column = new ColumnConfig(Constants.APPLICATIONVERSION_KEY, "Version", 90);
//		column.setAlignment(HorizontalAlignment.LEFT);
//		configs.add(column);

		column = new ColumnConfig(Constants.FQAN_KEY, "Group", 80);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);
		
		column = new ColumnConfig(Constants.SUBMISSION_SITE_KEY, "Site", 80);
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

			
			ListStore<GrisuJob> store = new ListStore<GrisuJob>(loader); 
			loader.load();
			
			ColumnModel cm = new ColumnModel(createColumnConfig());
			
			GridView view = new GridView();
			view.setForceFit(true);
			view.setSortingEnabled(true);
			
			grid = new Grid<GrisuJob>(store, cm);
			grid.setView(view);
			grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
			grid.setStyleAttribute("borderTop", "none");
			grid.setAutoExpandColumn(Constants.JOBNAME_KEY);  
			grid.setStripeRows(true);  
			grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {

					GrisuJob selected = (GrisuJob)(getGrid().getSelectionModel().getSelectedItem());
					
					SingleJobPanel newPanel = new SingleJobPanel(selected);
					TabItem newTab = new TabItem(selected.getJobname());
					newTab.setLayout(new FitLayout());
					newTab.add(newPanel);
					newTab.setClosable(true);
					
					add(newTab);
					
					setSelection(newTab);
				}

			});
		}
		return grid;
	}

	private Button getButton() {
		if (button == null) {
			button = new Button("Refresh");
			button.addSelectionListener(new SelectionListener<ButtonEvent>() {
				public void componentSelected(ButtonEvent ce) {
					loader.load();
				}
			});
		}
		return button;
	}
	private TabItem getTbtmJobList() {
		if (tbtmJobList == null) {
			tbtmJobList = new TabItem("Job list");
			tbtmJobList.setLayout(new FitLayout());
			tbtmJobList.add(getJoblistParentPanel());
		}
		return tbtmJobList;
	}
	private ContentPanel getJoblistParentPanel() {
		if (joblistParentPanel == null) {
			joblistParentPanel = new ContentPanel();
			joblistParentPanel.setButtonAlign(HorizontalAlignment.RIGHT);
			joblistParentPanel.setHeaderVisible(false);
			joblistParentPanel.setBodyBorder(false);
			joblistParentPanel.setLayout(new FitLayout());
			joblistParentPanel.setCollapsible(true);
			joblistParentPanel.add(getGrid(), new FitData(5));
			joblistParentPanel.addButton(getButton());
			getGrid().setContextMenu(getRightClickContextMenu());
		}
		return joblistParentPanel;
	}

	public void onJobSubmissionFinished(JobSubmissionFinishedEvent e) {

		loader.load();
	}
}
