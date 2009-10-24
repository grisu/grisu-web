package org.vpac.grisu.webclient.client.files;

import java.util.List;

import org.vpac.grisu.webclient.client.GrisuClientService;

import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class FilePreviewPanel extends LayoutContainer implements ValueChangeHandler<GrisuFileObject> {

	private com.google.gwt.user.client.ui.Grid infoGrid;
	private Text txtPleaseDoubleClick;
	
	private DropTarget dropTarget = null;
	
	public FilePreviewPanel() {
		setLayout(new FitLayout());
		setBorders(false);
		
		ContentPanel info = new ContentPanel();
		info.setLayout(new FitLayout());
		info.setHeaderVisible(false);
		info.add(getInfoGrid());
		
		add(info);
		
		dropTarget = new DropTarget(this) {
			@Override  
			protected void onDragDrop(DNDEvent event) {  
				
			    super.onDragDrop(event);  

			    final List<GrisuFileObject> sources = event.getData();  

			    setFileToDisplay(sources.get(0));

			}  
		};
	}
	
	private void setFileToDisplay(GrisuFileObject file) {
		
		removeAll(true);
		ContentPanel loadingPanel = new ContentPanel();
		loadingPanel.setHeaderVisible(false);
		loadingPanel.mask("Loading");
		add(loadingPanel);
		doLayout();
		
		GrisuClientService.Util.getInstance().download(file.getUrl(), new AsyncCallback<GwtGrisuCacheFile>() {
			
			public void onSuccess(GwtGrisuCacheFile file) {

				System.out.println("Public url: "+file.getPublicUrl());
				System.out.println("Local path: "+file.getLocalPath());
				
				String html = generateObjectHtmlCode(file.getPublicUrl(), file.getMimeType());
				
				removeAll(true);
				ContentPanel temp = new ContentPanel();
				temp.setHeaderVisible(true);
				temp.setHeading("File preview: " + file.getFilename());
				Html html_temp = new Html(generateObjectHtmlCode(file.getPublicUrl(), file.getMimeType()));
				
//				DropTarget dropTargetTemp = new DropTarget(html_temp) {
//					@Override  
//					protected void onDragDrop(DNDEvent event) {  
//						
//					    super.onDragDrop(event);  
//
//					    final List<GrisuFileObject> sources = event.getData();  
//
//					    setFileToDisplay(sources.get(0));
//
//					}  
//				};
				
				
				
				temp.add(html_temp);
				
				add(temp);

				doLayout();
			}
			
			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
				Window.alert("Couldn't download file: "+arg0.getLocalizedMessage());
			}
		});
		
		
	}

	public void onValueChange(ValueChangeEvent<GrisuFileObject> arg0) {

		setFileToDisplay(arg0.getValue());
		
	}
	
	private String generateObjectHtmlCode(String url, String mimeType) {
		String html = "<object width=\"100%\" height=\"100%\" data=\""+url+"\" type=\""+mimeType+"\"></object>";
		return html;
	}
	
	private com.google.gwt.user.client.ui.Grid getInfoGrid() {
		if (infoGrid == null) {
			infoGrid = new com.google.gwt.user.client.ui.Grid(1, 1);
			infoGrid.setWidget(0, 0, getTxtPleaseDoubleClick());
			infoGrid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
			infoGrid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		}
		return infoGrid;
	}
	private Text getTxtPleaseDoubleClick() {
		if (txtPleaseDoubleClick == null) {
			txtPleaseDoubleClick = new Text("Double click a file on the left to preview.");
		}
		return txtPleaseDoubleClick;
	}

}
