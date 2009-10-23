package org.vpac.grisu.webclient.client;

import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application
    implements EntryPoint
{

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {

	  MessagePopUp mp = new MessagePopUp();
	  
	  Viewport v = new Viewport();  
	  v.setLayout(new FitLayout());  
	  
	  LoginPanel l = new LoginPanel();

	  v.add(l);
	  RootPanel.get().add( v );
	  v.layout();
  }
}
