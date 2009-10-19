package org.vpac.grisu.webclient.client.files;

import java.util.List;

import org.vpac.grisu.webclient.client.GrisuClientService;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FileTransferStartedEvent extends GwtEvent<FileTransferStartedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onFileTransferStarted(FileTransferStartedEvent e);
    }

    public static final GwtEvent.Type<FileTransferStartedEvent.Handler> TYPE = new GwtEvent.Type<FileTransferStartedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onFileTransferStarted(this);
		
	}
	
    @Override
    public GwtEvent.Type<FileTransferStartedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private List<GrisuFileObject> sources = null;
    private GrisuFileObject target = null;
    
    public FileTransferStartedEvent() {
    }
    
    public FileTransferStartedEvent(List<GrisuFileObject> sources, GrisuFileObject target) {
    	this.sources = sources;
    	this.target = target;
    }

	public List<GrisuFileObject> getSources() {
		return sources;
	}

	public void setSources(List<GrisuFileObject> sources) {
		this.sources = sources;
	}

	public GrisuFileObject getTarget() {
		return target;
	}

	public void setTarget(GrisuFileObject target) {
		this.target = target;
	}

	
	public static void startNewFileTransfer(List<GrisuFileObject> sources, GrisuFileObject target) {
		
//		GrisuClientService.Util.getInstance()
		
	}


}
