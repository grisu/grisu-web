package org.vpac.grisu.webclient.client.files;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vpac.grisu.client.model.dto.DtoActionStatus;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FileDeletionFinishedEvent extends GwtEvent<FileDeletionFinishedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onFilesDeletionFinished(FileDeletionFinishedEvent e);
    }

    public static final GwtEvent.Type<FileDeletionFinishedEvent.Handler> TYPE = new GwtEvent.Type<FileDeletionFinishedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onFilesDeletionFinished(this);
		
	}
	
    @Override
    public GwtEvent.Type<FileDeletionFinishedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private List<GrisuFileObject> files = null;
    private DtoActionStatus status;
    
    public FileDeletionFinishedEvent(List<GrisuFileObject> files, DtoActionStatus status) {
    	this.files = files;
    	this.status = status;
    }

	public List<GrisuFileObject> getFiles() {
		return this.files;
	}

	public Set<String> getFoldersToRefresh() {
		
		Set<String> folders = new HashSet<String>();
		
		for ( GrisuFileObject file : getFiles() ) {
			folders.add(file.getUrl().substring(0, file.getUrl().lastIndexOf("/")));
		}
		
		return folders;
		
	}

	public boolean wasSuccessful() {
		return !status.getFailed();
	}
	
	public DtoActionStatus getStatus() {
		return this.status;
	}

}
