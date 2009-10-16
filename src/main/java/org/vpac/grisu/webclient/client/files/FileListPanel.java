package org.vpac.grisu.webclient.client.files;

import java.util.ArrayList;
import java.util.List;

import org.vpac.grisu.webclient.client.GrisuClientService;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class FileListPanel extends LayoutContainer {

	private Grid grid;
	private ListLoader loader;
	
	private String startRootUrl = null;
	private boolean initFinished = false;

	final Image img = new Image("gxt/images/default/tree/folder.gif");

	public FileListPanel() {
		initialize();
	}
	
	public FileListPanel(String startRootUrl) {
		this.startRootUrl = startRootUrl;
		initialize();
	}

	private void initialize() {
		setLayout(new FitLayout());
		add(getGrid());
	}
	
	private List<ColumnConfig> createColumnConfig() {

		GridCellRenderer<GrisuFileObject> filenameRenderer = new GridCellRenderer<GrisuFileObject>() {

			public Object render(GrisuFileObject model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GrisuFileObject> store, Grid<GrisuFileObject> grid) {

				if (GrisuFileObject.FILETYPE_SITE.equals(model
						.get(GrisuFileObject.FILETYPE))) {
					return "<img height=\"11px\" src=\"gxt/images/default/tree/folder.gif\" alt=\"folder\" /> "
							+ model.get(GrisuFileObject.FILENAME);
				} else if (GrisuFileObject.FILETYPE_MOUNTPOINT.equals(model
						.get(GrisuFileObject.FILETYPE))) {
					return "<img height=\"11px\" src=\"gxt/images/default/tree/folder.gif\" alt=\"folder\" /> "
							+ model.get(GrisuFileObject.FILENAME);
				} else if (GrisuFileObject.FILETYPE_FOLDER.equals(model
						.get(GrisuFileObject.FILETYPE))) {
					return "<img height=\"11px\" src=\"gxt/images/default/tree/folder.gif\" alt=\"folder\" /> "
							+ model.get(GrisuFileObject.FILENAME);
				} else if (GrisuFileObject.FILETYPE_FILE.equals(model
						.get(GrisuFileObject.FILETYPE))) {
					return "<img height=\"11px\" src=\"gxt/images/default/tree/leaf.gif\" alt=\"folder\" /> "
							+ model.get(GrisuFileObject.FILENAME);
				}

				return model.get(GrisuFileObject.FILENAME);
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId(GrisuFileObject.FILENAME);
		column.setRenderer(filenameRenderer);
		column.setHeader("Name");
		column.setWidth(120);
		column.setSortable(false);
		configs.add(column);

		GridCellRenderer<GrisuFileObject> sizeRenderer = new GridCellRenderer<GrisuFileObject>() {

			public Object render(GrisuFileObject model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GrisuFileObject> store, Grid<GrisuFileObject> grid) {

				try {

					if (GrisuFileObject.FILETYPE_ROOT.equals(model
							.getFileType())) {
						return "";
					} else if (GrisuFileObject.FILETYPE_SITE.equals(model
							.getFileType())) {
						return "";
					} else if (GrisuFileObject.FILETYPE_MOUNTPOINT.equals(model
							.getFileType())) {
						return "";
					} else if (GrisuFileObject.FILETYPE_FOLDER.equals(model
							.getFileType())) {
						return "";
					} else {

						long size = model.getFileSize();

						String sizeString = size + " B";
						if (size > 1024 * 1024)
							sizeString = size / (1024 * 1024) + " MB";
						else if (size > 1024)
							sizeString = size / 1024 + " KB";

						return sizeString;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return "error";
				}

			}
		};

		column = new ColumnConfig(GrisuFileObject.FILESIZE, "size", 60);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setRenderer(sizeRenderer);
		column.setSortable(false);
		configs.add(column);
		//
		// column = new ColumnConfig(Constants.FQAN_KEY, "Group", 40);
		// column.setAlignment(HorizontalAlignment.LEFT);
		// configs.add(column);
		//
		// column = new ColumnConfig(Constants.SUBMISSION_TIME_KEY,
		// "Submitted on", 100);
		// column.setAlignment(HorizontalAlignment.RIGHT);
		// column.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
		// configs.add(column);

		return configs;
	}

	private GrisuFileObject getCurrentlySelectedFile() {

		if (grid == null) {
			return null;
		}
		ModelData file = grid.getSelectionModel().getSelectedItem();

		if (file == null) {
			return null;
		}

		return (GrisuFileObject) file;

	}

	private Grid getGrid() {
		if (grid == null) {
			// data proxy
			RpcProxy<List<GrisuFileObject>> proxy = new RpcProxy<List<GrisuFileObject>>() {
				@Override
				protected void load(Object loadConfig,
						AsyncCallback<List<GrisuFileObject>> callback) {

					FileListPanel.this.mask("Loading");
					
					String url = null;
					
					if ( startRootUrl != null && startRootUrl.equals("") && ! initFinished ) {
						url = startRootUrl;
					} else {
						GrisuFileObject file = getCurrentlySelectedFile();
						if (file != null) {
							url = file.getUrl();
						}
					}
					GrisuClientService.Util.getInstance().ls(url, callback);
					
					FileListPanel.this.unmask();

				}
			};
			
			loader = new BaseListLoader<ListLoadResult<GrisuFileObject>>(proxy);

			ListStore<GrisuFileObject> store = new ListStore<GrisuFileObject>(
					loader);

			ColumnModel cm = new ColumnModel(createColumnConfig());

			grid = new Grid<GrisuFileObject>(store, cm);
			
			loader.addLoadListener(new LoadListener(){
				
				@Override
				public void loaderBeforeLoad(LoadEvent le) {
					getGrid().mask("Loading...");
				}
				
				@Override
				public void loaderLoad(LoadEvent le) {
					getGrid().unmask();
				}
				
				@Override
				public void loaderLoadException(LoadEvent le) {
					getGrid().unmask();
					le.exception.printStackTrace();
				}
			});

			loader.load();
			initFinished = true;
			
			grid.setStyleAttribute("borderTop", "none");
			grid.setBorders(true);
			grid.setAutoExpandColumn(GrisuFileObject.FILENAME);
			grid.setStripeRows(true);

			grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					GridEvent ge = (GridEvent) be;

					loader.load();

				}

			});
		}
		return grid;
	}

	private static final boolean isDesignTime() {
		return false;
	}
}
