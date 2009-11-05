package org.vpac.grisu.webclient.client.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vpac.grisu.webclient.client.EventBus;
import org.vpac.grisu.webclient.client.GrisuClientService;
import org.vpac.grisu.webclient.client.UserEnvironment;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

import org.vpac.grisu.webclient.client.jobmonitoring.KillingJobsFinishedEvent;

public class FileListPanel extends LayoutContainer 
	implements FileTransferStatusChangedEvent.Handler, HasValueChangeHandlers<GrisuFileObject>,
	FileDeletionFinishedEvent.Handler, KillingJobsFinishedEvent.Handler {

	private Grid grid;
	private ListLoader loader;
	
	private GrisuFileObject nextUrlToLoad = null;
	
	private DragSource gridDragSource = null;
	private DropTarget gridDropTarget = null;
	
	private GrisuFileObject currentDirectory = null;
	
	private final String fileListName;
	private String lastRetrievedValue;
	
	private ContentPanel containerPanel = null;
	
	final Image img = new Image("gxt/images/default/tree/folder.gif");
	
	private Menu contextMenu;

//	/**
//	 * @wbp.parser.constructor
//	 */
//	public FileListPanel() {
//		this(null, null);
//	}
	
	public FileListPanel(final String startRootUrl, final String optionalFileListName) {

		this.fileListName = optionalFileListName;
		
		setBorders(false);
		initialize();

		if ( startRootUrl == null && this.fileListName != null && ! "".equals(this.fileListName) ) {
			retrieveAndSetLastUsedDirectory();
		}
		
		if ( startRootUrl != null && !"".equals(startRootUrl) ) {
			setCurrentDirectory(startRootUrl);
		}
		
		
	}
	
	private Menu getRightClickContextMenu() {
		
		if ( contextMenu == null ) {
		    contextMenu = new Menu();
			MenuItem remove = new MenuItem();  
			remove.setText("Delete selected files");  
			remove.addSelectionListener(new SelectionListener<MenuEvent>() {
				public void componentSelected(MenuEvent ce) { 
					
					final Dialog simple = new Dialog();  
					simple.setHeading("Dialog Test");  
					simple.setButtons(Dialog.YESNO);  
					simple.setBodyStyleName("pad-text");  
					simple.addText("Do you really want to delete the selected files?");  
					simple.setScrollMode(Scroll.AUTO);  
					simple.setHideOnButtonClick(true);
					
					simple.getButtonById(Dialog.YES).addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							getGrid().mask("Deleting files...");
							UserEnvironment.getInstance().deleteFiles(getSelectedItems());
						}
					});

					simple.show();
					
				}
			});
			contextMenu.add(remove); 
		}
		return contextMenu;
	}
	
//	private ContentPanel getContainerPanel() {
//		
//		if ( containerPanel == null ) {
//			containerPanel = new ContentPanel(new FitLayout());
//			containerPanel.setHeaderVisible(false);
//			containerPanel.setBodyBorder(false);
//			containerPanel.add(getGrid());
//		}
//		return containerPanel;
//	}

	private void initialize() {
		setLayout(new FitLayout());
		add(getGrid());
		getGrid().setContextMenu(getRightClickContextMenu());
		gridDragSource = new DragSource(getGrid()) {
			@Override  
			protected void onDragStart(DNDEvent e) {  

			    Element r = grid.getView().findRow(e.getTarget()).cast();
			    if (r == null) {
			      e.setCancelled(true);
			      return;
			    }

			    List<GrisuFileObject> sel = getSelectedItems();
			    
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
			protected void onDragEnter(DNDEvent event) {
				
				getGrid().mask();
				
				int size = ((List<GrisuFileObject>)(event.getData())).size();
				String html = "Copy "+size+" files to: <br/>"+getCurrentDirectory().getFileName();
				event.getStatus().update(html);
				
			}
			
			@Override
			protected void onDragLeave(DNDEvent event) {
				getGrid().unmask();
				
				DragSource s = event.getDragSource();
			      if ( s.getStatusText() == null) {
			    	  String html = GXT.MESSAGES.grid_ddText(((List<GrisuFileObject>)(event.getData())).size());
			        event.getStatus().update(html);
			      } else {
			        event.getStatus().update(Format.substitute(s.getStatusText(), ((List<GrisuFileObject>)(event.getData())).size()));
			      }
			}
			
			@Override  
			protected void onDragDrop(DNDEvent event) {  
				
			    super.onDragDrop(event);  
				getGrid().unmask();

			    final List<GrisuFileObject> sources = event.getData();  

			    copyFilesToCurrentDirectory(sources);

			}  
		};
		gridDropTarget.setAllowSelfAsSource(false);
		
		EventBus.get().addHandler(FileTransferStatusChangedEvent.TYPE, this);
		EventBus.get().addHandler(FileDeletionFinishedEvent.TYPE, this);
		EventBus.get().addHandler(KillingJobsFinishedEvent.TYPE, this);

	}
	
	public List<GrisuFileObject> getSelectedItems() {
		
		return getGrid().getSelectionModel().getSelectedItems();
		
	}
	
	/**
	 * Copies the specfied files into the current directory.
	 * 
	 * @param sources the sources
	 * @return whether the filetransfer was started or not
	 */
	public boolean copyFilesToCurrentDirectory(List<GrisuFileObject> sources) {
		
	    final GrisuFileObject target = getCurrentDirectory();
	    
	    FileTransferObject fto = new FileTransferObject(sources, target);
	    
	    return fto.startTransfer();
	}
	

	private List<ColumnConfig> createColumnConfig() {

		GridCellRenderer<GrisuFileObject> filenameRenderer = new GridCellRenderer<GrisuFileObject>() {

			public Object render(GrisuFileObject model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GrisuFileObject> store, Grid<GrisuFileObject> grid) {

				if ( rowIndex == 0 && ! GrisuFileObject.FILETYPE_SITE.equals(model.getFileType())) {
					return "..";
				}
				
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
						final AsyncCallback<List<GrisuFileObject>> callback) {

					GrisuFileObject file = null;
					if ( nextUrlToLoad != null ) {
						currentDirectory = nextUrlToLoad;
						nextUrlToLoad = null;
					} else {
						file = getCurrentlySelectedFile();
						if (file != null) {
							if ( GrisuFileObject.FILETYPE_FILE.equals(file.getFileType()) ) {
								return;
							}
							currentDirectory = file;
						} else {
							currentDirectory = new GrisuFileObject(GrisuFileObject.FILETYPE_ROOT, 
									GrisuFileObject.FILETYPE_ROOT, GrisuFileObject.FILETYPE_ROOT, 0L, null);
						}
					}
					setStatusPath();

					GrisuClientService.Util.getInstance().ls(currentDirectory.getUrl(), callback);

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
						super.loaderBeforeLoad(le);
						getGrid().mask("Loading...");
					}
					
					@Override
					public void loaderLoad(LoadEvent le) {
						
						super.loaderLoad(le);
						getGrid().unmask();
						
						if ( fileListName != null && !"".equals(fileListName) ) {
							
							if ( getCurrentDirectory() == null || getCurrentDirectory().getUrl() == null 
									|| getCurrentDirectory().getFileType().equals(GrisuFileObject.FILETYPE_ROOT) ) {
								return;
							}
							
							if ( lastRetrievedValue != null && lastRetrievedValue.equals(getCurrentDirectory().getUrl())) {
								return;
							}
							
							UserEnvironment.getInstance().setUserProperty(fileListName, getCurrentDirectory().getUrl());
						
						}
					}
					
					@Override
					public void loaderLoadException(LoadEvent le) {
						super.loaderLoadException(le);
						getGrid().unmask();
						le.exception.printStackTrace();
					}
				});

				loader.load();
				
				grid.setStyleAttribute("borderTop", "none");
				grid.setBorders(false);
				grid.setAutoExpandColumn(GrisuFileObject.FILENAME);
				grid.setStripeRows(true);

				grid.addListener(Events.CellDoubleClick, new Listener<BaseEvent>() {

					public void handleEvent(BaseEvent be) {

						try {
						if ( GrisuFileObject.FILETYPE_FILE.equals(getCurrentlySelectedFile().getFileType()) ) {
							ValueChangeEvent.fire(FileListPanel.this, getCurrentlySelectedFile());
							return;
						}
						
						loader.load();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
				
			} else {
				cm = new ColumnModel(Collections.<ColumnConfig>emptyList());
				grid = new Grid(new ListStore(), cm);
			}
			

			

		}
		return grid;
	}
	
	public void setCurrentDirectory(GrisuFileObject file) {
		nextUrlToLoad = file;
		loader.load();
	}
	
	public void setCurrentDirectory(final String url) {
		
		if ( url == null || "".equals(url) || GrisuFileObject.FILETYPE_ROOT.equals(url) ) {
			setCurrentDirectory((GrisuFileObject)null);
			return;
		}
		
		GrisuClientService.Util.getInstance().getFile(url, new AsyncCallback<GrisuFileObject>() {

			public void onFailure(Throwable arg0) {

				Window.alert("Could not access "+url);
				arg0.printStackTrace();
			}

			public void onSuccess(GrisuFileObject arg0) {

				setCurrentDirectory(arg0);
			}
		});

	}
	
	public void retrieveAndSetLastUsedDirectory() {
		
		if ( fileListName != null && ! "".equals(fileListName) ) {
			
			String startUrl = UserEnvironment.getInstance().getUserProperty(fileListName);
			setCurrentDirectory(startUrl);
			
		}
		
	}
	
	public GrisuFileObject getCurrentDirectory() {
		return currentDirectory;
	}
	
	public void refreshCurrentDirectory() {
		
		setCurrentDirectory(getCurrentDirectory());
		
		
	}

	private static final boolean isDesignTime() {
		return false;
	}

	public void onFileTransferStatusChanged(FileTransferStatusChangedEvent e) {

		if ( e.isFileTransferFinished() ) {
			
			if ( e.getTarget().getUrl().equals(getCurrentDirectory().getUrl()) ) {
				refreshCurrentDirectory();
			}
			
		}
		
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<GrisuFileObject> arg0) {

		return addHandler(arg0, ValueChangeEvent.getType());
		
	}
	
	private void setStatusPath() {

//		ToolTipConfig tt = new ToolTipConfig("Absolute path", getCurrentDirectory().getUrl());
//		tt.setShowDelay(3000);
//		getGrid().setToolTip(tt);
	}

	public void onFilesDeletionFinished(FileDeletionFinishedEvent e) {

		for ( String url : e.getFoldersToRefresh() ) {
			if ( getCurrentDirectory().getUrl().equals(url) ) {
				refreshCurrentDirectory();
				return;
			}
		}
		
	}

	public void onJobsKilled(KillingJobsFinishedEvent e) {

		for ( String url : e.getFoldersToRefresh() ) {
			if ( getCurrentDirectory().getUrl().equals(url) ) {
				refreshCurrentDirectory();
				return;
			}
		}
		
	}
}
