package org.vpac.grisu.webclient.client;

import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.webclient.client.exceptions.LoginException;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class LoginPanel extends LayoutContainer implements UserEnvironmentLoadedEvent.Handler {
	private ContentPanel loginBox;
	private FormPanel formPanel;
	private TextField<String> textField;
	private TextField<String> textField_1;
	private Button button;
	
	private final UserEnvironment ue;

	public LoginPanel() {
		
		EventBus.get().addHandler(UserEnvironmentLoadedEvent.TYPE, this);
		ue = UserEnvironment.getInstance();

		setLayout(new CenterLayout());
		
		getLoginBox().disable();
		GrisuClientService.Util.getInstance().login(
				getTextField_1().getValue(),
				getTextField_1_1().getValue(),
				new AsyncCallback<Boolean>() {

					public void onFailure(Throwable arg0) {
						getLoginBox().enable();
						// doesn't matter
						add(getLoginBox());
						layout();

					}

					public void onSuccess(Boolean arg0) {
						if ( arg0 ) {
							// init user environment
							getLoginBox().mask("Login successful. Getting user environment...");
							ue.initOrUpdate();
						}
						
					}
				});

	}

	private ContentPanel getLoginBox() {
		if (loginBox == null) {
			loginBox = new ContentPanel();
			loginBox.setBorders(false);
			loginBox.setHideCollapseTool(true);
			loginBox.setSize("260", "140");
			loginBox.setHeading("Login");
			loginBox.setCollapsible(false);
			loginBox.setLayout(new FitLayout());
			loginBox.add(getFormPanel());
		}
		return loginBox;
	}

	private FormPanel getFormPanel() {
		if (formPanel == null) {
			formPanel = new FormPanel();
			formPanel.setLabelAlign(LabelAlign.RIGHT);
			formPanel.setBodyBorder(false);
			formPanel.setFieldWidth(144);
			formPanel.setHeaderVisible(false);
			formPanel.setCollapsible(true);
			formPanel.add(getTextField_1());
			formPanel.add(getTextField_1_1());
			formPanel.addButton(getButton());
			FormButtonBinding binding = new FormButtonBinding(formPanel);
			binding.addButton(getButton());
		}
		return formPanel;
	}

	protected TextField<String> getTextField_1() {
		if (textField == null) {
			textField = new TextField<String>();
			textField.setAllowBlank(false);
			textField.setFieldLabel("Username");
		}
		return textField;
	}

	protected TextField<String> getTextField_1_1() {
		if (textField_1 == null) {
			textField_1 = new TextField<String>();
			textField_1.setPassword(true);
			textField_1.setFieldLabel("Password");
			textField_1.setAllowBlank(false);
		}
		return textField_1;
	}

	private Button getButton() {
		if (button == null) {
			button = new Button("Login", new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {

					getLoginBox().mask("Logging in...");
					
					GrisuClientService.Util.getInstance().login(
							getTextField_1().getValue(),
							getTextField_1_1().getValue(),
							new AsyncCallback<Boolean>() {

								public void onFailure(Throwable arg0) {

									getLoginBox().unmask();
									
									String message = arg0.getMessage();
									
									if ( arg0 instanceof LoginException ) {
										message = ((LoginException)arg0).getLoginError();
										
									}
									
									MessageBox.alert("Login error", message, new Listener<MessageBoxEvent>() {

										public void handleEvent(
												MessageBoxEvent be) {
										}
									});

								}

								public void onSuccess(Boolean arg0) {

									getLoginBox().unmask();
									
									if ( arg0 ) {
										getLoginBox().mask("Login successful. Getting user environment...");
										ue.initOrUpdate();
									} else {
										Window.alert("Could not log in for some reason... This is a bug.");
									}
									
									
								}
							});
				}
			});
			button.setIconAlign(IconAlign.RIGHT);
		}
		return button;
	}
	
	public void loadMainPanel() {

		  Viewport v = new Viewport();  
		  v.setLayout(new FitLayout());  
		  
		  MainPanel p = new MainPanel();

		  v.add(p);
		  
		  RootPanel.get().add( v );
		
	}

	public void onUserEnvironmentLoaded(UserEnvironmentLoadedEvent e) {

		getLoginBox().unmask();
		loadMainPanel();
		
	}
}
