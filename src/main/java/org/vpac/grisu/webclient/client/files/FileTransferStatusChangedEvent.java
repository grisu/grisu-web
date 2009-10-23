package org.vpac.grisu.webclient.client.files;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FileTransferStatusChangedEvent extends GwtEvent<FileTransferStatusChangedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onFileTransferStatusChanged(FileTransferStatusChangedEvent e);
    }

    public static final GwtEvent.Type<FileTransferStatusChangedEvent.Handler> TYPE = new GwtEvent.Type<FileTransferStatusChangedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onFileTransferStatusChanged(this);
		
	}
	
    @Override
    public GwtEvent.Type<FileTransferStatusChangedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private FileTransferObject fto = null;
    
    public FileTransferStatusChangedEvent() {
    }
    
    public FileTransferStatusChangedEvent(FileTransferObject fto) {
    	this.fto = fto;
    }

	public List<GrisuFileObject> getSources() {
		return fto.getSources();
	}

	public GrisuFileObject getTarget() {
		return fto.getTarget();
	}

	public void setFileTransferObject(FileTransferObject fto) {
		this.fto = fto;
	}
	
	public FileTransferObject getFileTransferObject() {
		return this.fto;
	}
	
	public boolean isFileTransferFinished() {
		return fto.isFinished();
	}

	public boolean isFileTransferFailed() {
		return fto.isFailed();
	}

}
