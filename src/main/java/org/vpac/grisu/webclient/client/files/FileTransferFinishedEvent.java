package org.vpac.grisu.webclient.client.files;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FileTransferFinishedEvent extends GwtEvent<FileTransferFinishedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onFileTransferStarted(FileTransferFinishedEvent e);
    }

    public static final GwtEvent.Type<FileTransferFinishedEvent.Handler> TYPE = new GwtEvent.Type<FileTransferFinishedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onFileTransferStarted(this);
		
	}
	
    @Override
    public GwtEvent.Type<FileTransferFinishedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private List<GrisuFileObject> sources = null;
    private GrisuFileObject target = null;
    
    public FileTransferFinishedEvent() {
    }
    
    public FileTransferFinishedEvent(List<GrisuFileObject> sources, GrisuFileObject target) {
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



}
