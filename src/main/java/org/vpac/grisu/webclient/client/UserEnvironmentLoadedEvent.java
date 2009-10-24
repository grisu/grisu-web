package org.vpac.grisu.webclient.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class UserEnvironmentLoadedEvent extends GwtEvent<UserEnvironmentLoadedEvent.Handler> {
	
    /**
     * Interface to describe this event. Handlers must implement.
     */
    public interface Handler extends EventHandler {
            public void onUserEnvironmentLoaded(UserEnvironmentLoadedEvent e);
    }

    public static final GwtEvent.Type<UserEnvironmentLoadedEvent.Handler> TYPE = new GwtEvent.Type<UserEnvironmentLoadedEvent.Handler>();

    @Override
	protected void dispatch(Handler handler) {
		handler.onUserEnvironmentLoaded(this);
		
	}
	
    @Override
    public GwtEvent.Type<UserEnvironmentLoadedEvent.Handler> getAssociatedType() {
            return TYPE;
    }
    
    private UserEnvironment ue;
    
    public UserEnvironmentLoadedEvent() {
    }
    
    public UserEnvironmentLoadedEvent(UserEnvironment ue) {
    	this.ue = ue;
    }

    public UserEnvironment getUserEnvironment() {
    	return ue;
    }
	

}
