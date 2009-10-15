package org.vpac.grisu.webclient.client;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GenericJobCreationPanel extends LayoutContainer {
	private FormPanel formPanel;
	private TextField<String> jobnameTextField;
	private SimpleComboBox<String> voComboBox;

	public GenericJobCreationPanel() {
		setLayout(new FitLayout());
		add(getFormPanel());
	}

	private FormPanel getFormPanel() {
		if (formPanel == null) {
			formPanel = new FormPanel();
			formPanel.add(getJobnameTextField());
			formPanel.setCollapsible(false);
			formPanel.add(getVoComboBox());
		}
		return formPanel;
	}

	private TextField<String> getJobnameTextField() {
		if (jobnameTextField == null) {
			jobnameTextField = new TextField<String>();
			jobnameTextField.setWidth("100");
			jobnameTextField.setFieldLabel("Jobname");
		}
		return jobnameTextField;
	}

	private SimpleComboBox<String> getVoComboBox() {
		if (voComboBox == null) {
			voComboBox = new SimpleComboBox<String>();
			voComboBox.setForceSelection(true);
			voComboBox.setEditable(false);
			voComboBox.setFieldLabel("Group");
			if (!isDesignTime()) {
				GrisuClientService.Util.getInstance().getFqans(
						new AsyncCallback<String[]>() {

							public void onSuccess(String[] arg0) {

								for (String vo : arg0) {
									voComboBox.add(vo);
								}
							}

							public void onFailure(Throwable arg0) {

								Window.alert(arg0.getLocalizedMessage());
							}
						});
			}
		}
		return voComboBox;
	}

	private static final boolean isDesignTime() {
		return false;
	}
}
