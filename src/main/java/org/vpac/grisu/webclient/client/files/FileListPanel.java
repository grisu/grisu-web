package org.vpac.grisu.webclient.client.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.webclient.client.EventBus;
import org.vpac.grisu.webclient.client.GrisuClientService;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class FileListPanel extends LayoutContainer {

	private Grid grid;
	private ListLoader loader;
	
	private String startRootUrl = null;
	private boolean initFinished = false;
	
	private DragSource gridDragSource = null;
	private DropTarget gridDropTarget = null;
	
	private GrisuFileObject currentDirectory = null;

	final Image img = new Image("gxt/images/default/tree/folder.gif");

	/**
	 * @wbp.parser.constructor
	 */
	public FileListPanel() {
		setBorders(false);
		initialize();
		gridDragSource = new DragSource(getGrid()) {
			@Override  
			protected void onDragStart(DNDEvent e) {  

			    Element r = grid.getView().findRow(e.getTarget()).cast();
			    if (r == null) {
			      e.setCancelled(true);
			      return;
			    }

			    List<GrisuFileObject> sel = grid.getSelectionModel().getSelectedItems();
			    
			    for ( GrisuFileObject file : sel ) {
			    	if ( ! GrisuFileObject.FILETYPE_FOLDER.equals(file.getFileType()) &&
			    			! GrisuFileObject.FILETYPE_FILE.equals(file.getFileType()) ) {
			    		e.setCancelled(true);
			    		return;
			    	}
			    }
			    if (sel.size() > 0) {
			      e.setCancelled(false);
			      e.setData(sel);

			      if (getStatusText() == null) {
			        e.getStatus().update(GXT.MESSAGES.grid_ddText(sel.size()));
			      } else {
			        e.getStatus().update(Format.substitute(getStatusText(), sel.size()));
			      }
			    }
				
			}
			
			
			
		};
		gridDropTarget = new DropTarget(getGrid()) {
			@Override  
			protected void onDragDrop(DNDEvent event) {  
				
			    super.onDragDrop(event);  
			    final List<GrisuFileObject> sources = event.getData();  

			    final List<String> sourceNames = new ArrayList<String>();
			    for ( GrisuFileObject file : sources ) {
			    	sourceNames.add(file.getUrl());
			    }

			    final GrisuFileObject target = getCurrentDirectory();
			    
			    GrisuClientService.Util.getInstance().cp(sourceNames, target.getUrl(), new AsyncCallback<String>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
						Window.alert(arg0.getLocalizedMessage());
						
					}

					public void onSuccess(final String arg0) {

						FileTransferStartedEvent startEvent = new FileTransferStartedEvent(sources, target);
						EventBus.get().fireEvent(startEvent);
						
						final Timer timer = new Timer() {

							@Override
							public void run() {

								GrisuClientService.Util.getInstance().getCurrentStatus(arg0, new AsyncCallback<DtoActionStatus>() {

									public void onFailure(Throwable arg0) {

										System.out.println("Failure: "+arg0.getLocalizedMessage());
									}

									public void onSuccess(DtoActionStatus arg0) {

										System.out.println("Status: ");
										for ( int i=0; i<arg0.getLog().size(); i++ ) {
											System.out.println(arg0.getLog().get(i));
										}
										System.out.println("-------------------------------------");
										
//										if ( arg0.getCurrentElements() == arg0.getTotalElements() ) {
//											timer.cancel();
//										}

									}
								});
								
								
							}
						};
						
						timer.scheduleRepeating(1000);
						
					}
				});
			    
			}  
		};
		gridDropTarget.setAllowSelfAsSource(false);
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
		
		column = new ColumnConfig(GrisuFileObject.LASTMODIFIED, "Date modified", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setDateTimeFormat(DateTimeFormat.getShortDateTimeFormat());
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

					String url = null;
					
					GrisuFileObject file = null;
					if ( startRootUrl != null && startRootUrl.equals("") && ! initFinished ) {
						url = startRootUrl;
					} else {
						file = getCurrentlySelectedFile();
						if (file != null) {
							url = file.getUrl();
							if ( GrisuFileObject.FILETYPE_FILE.equals(file.getFileType()) ) {
								System.out.println("Filetype: file, doing nothing..., shouldn't happen");
								return;
							}
							currentDirectory = file;
						} else {
							System.out.println("Nothing selected, doing nothing, this shouldn't happen I think...");
//							return;
						}
					}
					GrisuClientService.Util.getInstance().ls(url, callback);
				}
			};
			
			loader = new BaseListLoader<ListLoadResult<GrisuFileObject>>(proxy);

			ListStore<GrisuFileObject> store = new ListStore<GrisuFileObject>(
					loader);

			ColumnModel cm = null;
			
			if ( ! isDesignTime() ) {
				cm = new ColumnModel(createColumnConfig());
				grid = new Grid<GrisuFileObject>(store, cm);
				
				loader.addLoadListener(new LoadListener(){
					
					@Override
					public void loaderBeforeLoad(LoadEvent le) {
						getGrid().mask("Loading...");
					}
					
					@Override
					public void loaderLoad(LoadEvent le) {
//						le.g
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
				grid.setBorders(false);
				grid.setAutoExpandColumn(GrisuFileObject.FILENAME);
				grid.setStripeRows(true);

				grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {

						if ( GrisuFileObject.FILETYPE_FILE.equals(getCurrentlySelectedFile().getFileType()) ) {
							return;
						}
						
						loader.load();

					}

				});
				
			} else {
				cm = new ColumnModel(Collections.<ColumnConfig>emptyList());
				grid = new Grid(new ListStore(), cm);
			}
			

			

		}
		return grid;
	}
	
	public GrisuFileObject getCurrentDirectory() {
		return currentDirectory;
	}

	private static final boolean isDesignTime() {
		return false;
	}
}
