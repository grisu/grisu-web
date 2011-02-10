package org.vpac.grisu.webclient.client.jobcreation;

import grisu.client.model.dto.DtoActionStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vpac.grisu.webclient.client.EventBus;
import org.vpac.grisu.webclient.client.GrisuClientService;
import org.vpac.grisu.webclient.client.UserEnvironment;
import org.vpac.grisu.webclient.client.external.Constants;
import org.vpac.grisu.webclient.client.files.FileSelectorAndUploadWindow;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BasicJobCreationPanel extends LayoutContainer implements
JobSubmissionFinishedEvent.Handler,
ValueChangeHandler<List<GrisuFileObject>> {

	public static final String NOT_AVAILABLE_STRING = "Not available";

	private static final boolean isDesignTime() {
		return false;
	}
	private FormPanel frmpnlCreateJob;
	private TextField<String> jobnameTextField;
	private SimpleComboBox<String> voComboBox;
	private HorizontalPanel secondRow;
	private LayoutContainer firstRowLeftSide;
	private LayoutContainer firstRowRightSide;
	private TextField<String> emailTextField;
	private CheckBox checkBox;
	private HorizontalPanel thirdRow;
	private HorizontalPanel WallTimeFormPanel;
	private LayoutContainer daysContainer;
	private LayoutContainer hoursContainer;
	private LayoutContainer minutesContainer;
	private SimpleComboBox<Integer> daysComboBox;
	private SimpleComboBox<Integer> hoursComboBox;
	private SimpleComboBox<Integer> minutesComboBox;
	private ContentPanel walltimeContainer;
	private ContentPanel cpusPanel;
	private LayoutContainer layoutContainer;
	private SimpleComboBox<Integer> cpusComboBox;
	private ContentPanel jobnamePanel;
	private ContentPanel emailPanel;
	private ContentPanel applicationPanel;
	private LayoutContainer layoutContainer_1;
	private SimpleComboBox<String> versionComboBox;
	private HorizontalPanel firstRow;
	private ContentPanel commandLinePanel;
	private ContentPanel stageFileList;
	private TextArea txtrEnterTheCommand;
	private ListField<GrisuFileObject> listField;
	private Button submitButton;

	private HorizontalPanel horizontalPanel;

	private String[] currentApplications;

	private final ListStore<GrisuFileObject> inputFileStore = new ListStore<GrisuFileObject>();
	private String lastCalculatedExecutable;

	private LayoutContainer textAreaContainer;
	private Button addFileButton;

	private Button removeFileButton;

	private String currentStatus;

	private boolean userTypedInTextField = false;
	private DragSource dragSource;

	private DropTarget dropTarget;

	private FileSelectorAndUploadWindow fileSelectorWindow;

	public BasicJobCreationPanel() {
		setLayout(new FitLayout());
		add(getFrmpnlCreateJob(), new FitData(0));

		fileSelectorWindow = new FileSelectorAndUploadWindow(
				Constants.GENERIC_JOB_LAST_INPUTFILE_URL);
		fileSelectorWindow.addValueChangeHandler(this);

		EventBus.get().addHandler(JobSubmissionFinishedEvent.TYPE, this);

		dragSource = new DragSource(getListView()) {
			@Override
			protected void onDragStart(DNDEvent e) {

				List<GrisuFileObject> selectedFiles = getListView()
				.getSelection();

				if ((selectedFiles == null) || (selectedFiles.size() == 0)) {
					e.setCancelled(true);
					return;
				}

				for (GrisuFileObject file : selectedFiles) {
					if (!GrisuFileObject.FILETYPE_FOLDER.equals(file
							.getFileType())
							&& !GrisuFileObject.FILETYPE_FILE.equals(file
									.getFileType())) {
						e.setCancelled(true);
						return;
					}
				}
				if (selectedFiles.size() > 0) {
					e.setCancelled(false);
					e.setData(selectedFiles);

					if (getStatusText() == null) {
						String html = GXT.MESSAGES.grid_ddText(selectedFiles
								.size());
						e.getStatus().update(html);
					} else {
						e.getStatus().update(
								Format.substitute(getStatusText(),
										selectedFiles.size()));
					}
				}

			}
		};

		dropTarget = new DropTarget(getCommandLineTextArea()) {
			@Override
			protected void onDragDrop(DNDEvent event) {

				super.onDragDrop(event);

				final List<GrisuFileObject> sources = event.getData();

				String currentValue = getCommandLineTextArea().getValue();
				if (currentValue == null) {
					currentValue = "";
				}

				int insertPoint = getCommandLineTextArea().getCursorPos();

				StringBuffer newValue = new StringBuffer();

				for (GrisuFileObject file : sources) {
					newValue.append(file.getFileName() + " ");
				}

				String firstPart = currentValue.substring(0, insertPoint)
				.trim();
				String lastPart;
				if (currentValue.length() <= insertPoint) {
					lastPart = "";
				} else {
					lastPart = currentValue.substring(insertPoint).trim();
				}

				String newText = null;
				if ((firstPart == null) || "".equals(firstPart)) {
					newText = newValue + lastPart;
				} else {
					newText = firstPart + " " + newValue + lastPart;
				}

				getCommandLineTextArea().setValue(newText);

			}
		};
		dropTarget.setAllowSelfAsSource(false);

	}

	public Map<String, String> calculateJobProperties()
	throws JobCreationException {

		Map<String, String> properties = new HashMap<String, String>();

		// jobname
		String jobname = getJobnameTextField().getValue();
		properties.put(Constants.JOBNAME_KEY, jobname);

		// commandline
		String commandline = getCommandLineTextArea().getValue().trim();
		properties.put(Constants.COMMANDLINE_KEY, commandline);

		// application
		Set<String> apps = new HashSet<String>();
		for (String app : currentApplications) {
			apps.add(app.toLowerCase());
		}
		if (apps.size() > 1) {
			throw new JobCreationException(
			"More than one applications found for specified executable. This is not supported (at least not at the moment. Please contact help@arcs.org.au if you experience this problem.");
		}
		properties.put(Constants.APPLICATIONNAME_KEY, apps.iterator().next());

		// version
		String version = getVersionComboBox().getSimpleValue();
		properties.put(Constants.APPLICATIONVERSION_KEY, version);

		// walltime
		Integer walltime = (getDaysComboBox().getSimpleValue() * 60 * 24)
		+ (getHoursComboBox().getSimpleValue() * 60)
		+ getMinutesComboBox().getSimpleValue();
		properties.put(Constants.WALLTIME_IN_MINUTES_KEY, walltime.toString());

		// cpus
		Integer cpus = getCpusComboBox().getSimpleValue();
		properties.put(Constants.NO_CPUS_KEY, cpus.toString());

		// fqan
		String fqan = getVoComboBox().getSimpleValue();
		properties.put(Constants.FQAN_KEY, fqan);

		// send email
		Boolean sendEmail = getCheckBox().getValue();
		if (sendEmail) {
			properties.put(Constants.EMAIL_ON_FINISH_KEY, "true");
			String emailAddress = getEmailTextField().getValue();
			properties.put(Constants.EMAIL_ADDRESS_KEY, emailAddress);
		}

		// input files
		StringBuffer inputFiles = new StringBuffer();
		for (GrisuFileObject file : inputFileStore.getModels()) {
			inputFiles.append(file.getUrl() + ",");
		}
		properties.put(Constants.INPUT_FILE_URLS_KEY, inputFiles.toString());

		return properties;
	}

	private void fillVersionsComboBox() {

		if ((this.currentApplications == null) || (currentApplications.length == 0)) {
			setStatus("Current application could not be determined. Not getting versions...");
			setAppNotAvailable();
			return;
		}

		String fqan = getVoComboBox().getSimpleValue();
		if ((fqan == null) || "".equals(fqan)) {
			setStatus("No groups loaded (yet?). Not getting versions...");
			return;
		}
		final String[] apps = this.currentApplications;
		final String currentVersion = getVersionComboBox().getSimpleValue();

		GrisuClientService.Util.getInstance().getVersionsOfApplicationForVO(
				apps, fqan, new AsyncCallback<String[]>() {

					public void onFailure(Throwable arg0) {

						arg0.printStackTrace();
					}

					public void onSuccess(final String[] versions) {

						getVersionComboBox().removeAll();
						getVersionComboBox().clearSelections();

						if ((versions == null) || (versions.length == 0)) {
							setVersionsNotAvailable();
							return;
						}

						getVersionComboBox().add(
								Constants.NO_VERSION_INDICATOR_STRING);

						for (String version : versions) {
							getVersionComboBox().add(version);
						}

						setStatus("Found " + versions.length + " versions.");

						if ((currentVersion == null) || "".equals(currentVersion)
								|| NOT_AVAILABLE_STRING.equals(currentVersion)) {

							if ((lastCalculatedExecutable != null)
									&& !"".equals(lastCalculatedExecutable)) {
								String lastVersion = UserEnvironment
								.getInstance()
								.getUserProperty(
										Constants.DEFAULT_VERSION
										+ lastCalculatedExecutable);

								if ((lastVersion != null)
										&& (Arrays.binarySearch(versions,
												lastVersion) >= 0)) {
									getVersionComboBox().setSimpleValue(
											lastVersion);
								} else {
									getVersionComboBox()
									.setSimpleValue(
											Constants.NO_VERSION_INDICATOR_STRING);
								}
							} else {
								getVersionComboBox().setSimpleValue(
										Constants.NO_VERSION_INDICATOR_STRING);
							}
						} else {

							if (Arrays.binarySearch(versions, currentVersion) >= 0) {
								getVersionComboBox().setSimpleValue(
										currentVersion);
							} else {
								getVersionComboBox().setSimpleValue(
										Constants.NO_VERSION_INDICATOR_STRING);
							}

						}

					}
				});
	}

	private Button getAddFileButton() {

		if (addFileButton == null) {
			addFileButton = new Button("Add");
			addFileButton.addListener(Events.Select, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {

					fileSelectorWindow.show();
				}
			});
		}
		return addFileButton;
	}

	private ContentPanel getApplicationPanel() {
		if (applicationPanel == null) {
			applicationPanel = new ContentPanel();
			applicationPanel.setSize("221px", "85px");
			applicationPanel.setHeading("Application");
			applicationPanel.setLayout(new FitLayout());
			applicationPanel.add(getLayoutContainer_1(), new FitData(10, 30,
					10, 10));
		}
		return applicationPanel;
	}

	private CheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new CheckBox();
			checkBox.setBoxLabel("Send email when job finishes");
			checkBox.setHideLabel(true);
			checkBox.addListener(Events.Change, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					if (checkBox.getValue()) {
						getEmailTextField().setAllowBlank(false);
						getEmailTextField().setEnabled(true);
					} else {
						getEmailTextField().setAllowBlank(true);
						getEmailTextField().setEnabled(false);
					}

				}
			});
		}
		return checkBox;
	}

	private ContentPanel getCommandLinePanel() {
		if (commandLinePanel == null) {
			commandLinePanel = new ContentPanel();
			commandLinePanel.setHideCollapseTool(true);
			commandLinePanel.setSize("413px", "196px");
			commandLinePanel.setLayout(new FitLayout());
			commandLinePanel.setHeading("Commandline");
			commandLinePanel.setCollapsible(true);
			commandLinePanel.add(getTextAreaContainer(), new FitData(10));
		}
		return commandLinePanel;
	}

	private TextArea getCommandLineTextArea() {
		if (txtrEnterTheCommand == null) {
			txtrEnterTheCommand = new TextArea();
			txtrEnterTheCommand.setFieldLabel("Enter the command to run");
			txtrEnterTheCommand.setAllowBlank(false);
			txtrEnterTheCommand.addListener(Events.KeyUp,
					new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {

					String text = txtrEnterTheCommand.getValue();
					String exe;
					if (text == null) {
						exe = "";
					} else {
						int firstWhitespace = text.indexOf(" ");
						if (firstWhitespace == -1) {
							exe = text;
						} else {
							exe = text.substring(0, firstWhitespace);
						}
					}

					if ((lastCalculatedExecutable != null)
							&& lastCalculatedExecutable.equals(exe)) {
						return;
					}

					final String executable = exe;
					lastCalculatedExecutable = exe;

					suggestJobname();

					if (exe.length() == 0) {
						lastCalculatedExecutable = null;
						setStatus("No executable specified.");
						setAppNotAvailable();
						return;
					}

					setStatus("Trying to find application(s) for executable: "
							+ executable);

					GrisuClientService.Util.getInstance()
					.getApplicationForExecutable(exe,
							new AsyncCallback<String[]>() {

								public void onFailure(
										Throwable arg0) {
									getApplicationPanel()
									.setHeading(
											"Application");
									arg0.printStackTrace();
								}

								public void onSuccess(
										String[] arg0) {

									if (arg0.length == 0) {
										setStatus("Could not find application for executable: "
												+ executable);
										setAppNotAvailable();
										return;
									}

									if (arg0.length > 1) {
										setStatus("Found more than one application for executable: "
												+ executable
												+ ". Please contact help@arcs.org.au and tell them to bloody fix this.");
										// return;
									}

									setStatus("Found "
											+ arg0.length
											+ " applications for executable "
											+ executable
											+ ". Getting Versions...");

									setCurrentApplication(arg0);

									fillVersionsComboBox();

								}
							});

				}
			});
		}
		return txtrEnterTheCommand;
	}

	private SimpleComboBox<Integer> getCpusComboBox() {
		if (cpusComboBox == null) {
			cpusComboBox = new SimpleComboBox<Integer>();
			cpusComboBox.setAllowBlank(false);
			cpusComboBox.setFieldLabel("No. CPUs");
			cpusComboBox.add(1);
			cpusComboBox.add(2);
			cpusComboBox.add(4);
			cpusComboBox.add(8);
			cpusComboBox.add(16);
			cpusComboBox.add(32);
			cpusComboBox.add(64);
			cpusComboBox.add(128);
			cpusComboBox.setSimpleValue(1);
		}
		return cpusComboBox;
	}

	private ContentPanel getCpusPanel() {
		if (cpusPanel == null) {
			cpusPanel = new ContentPanel();
			cpusPanel.setSize("106px", "84px");
			cpusPanel.setHideCollapseTool(true);
			cpusPanel.setHeading("CPUs");
			cpusPanel.setLayout(new FitLayout());
			cpusPanel.add(getLayoutContainer(), new FitData(10));
		}
		return cpusPanel;
	}

	private SimpleComboBox<Integer> getDaysComboBox() {
		if (daysComboBox == null) {
			daysComboBox = new SimpleComboBox<Integer>();
			daysComboBox.setAllowBlank(false);
			daysComboBox.setFieldLabel("Days");
			daysComboBox.add(0);
			daysComboBox.add(1);
			daysComboBox.add(2);
			daysComboBox.add(3);
			daysComboBox.add(4);
			daysComboBox.add(7);
			daysComboBox.add(14);
			daysComboBox.add(21);
			daysComboBox.setSimpleValue(0);
		}
		return daysComboBox;
	}

	private LayoutContainer getDaysContainer() {
		if (daysContainer == null) {
			daysContainer = new LayoutContainer();
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelAlign(LabelAlign.TOP);
			daysContainer.setLayout(formLayout);
			FormData formData = new FormData("");
			formData.setWidth(70);
			formData.setMargins(new Margins(0, 0, 0, 0));
			daysContainer.add(getDaysComboBox(), formData);
		}
		return daysContainer;
	}

	private ContentPanel getEmailPanel() {
		if (emailPanel == null) {
			emailPanel = new ContentPanel();
			emailPanel.setWidth("275px");
			emailPanel.setLayout(new FitLayout());
			emailPanel.setHeading("Email");
			emailPanel.setCollapsible(false);
			emailPanel.add(getFirstRowRightSide(), new FitData(10));
		}
		return emailPanel;
	}

	private TextField<String> getEmailTextField() {
		if (emailTextField == null) {
			emailTextField = new TextField<String>();
			emailTextField.setEnabled(false);
			emailTextField.setWidth("");
			emailTextField.setFieldLabel("Email");
			emailTextField.setValidator(new Validator() {
				public String validate(Field<?> field, String value) {
					if (field == emailTextField) {
						if (!emailTextField
								.getValue()
								.toLowerCase()
								.matches(
								"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
							return "Bad E-mail Address";
						}
					}

					return null;
				}
			});
		}
		return emailTextField;
	}

	private HorizontalPanel getFirstRow() {
		if (firstRow == null) {
			firstRow = new HorizontalPanel();
			firstRow.setSpacing(10);
			firstRow.add(getCommandLinePanel());
			firstRow.add(getStageFileList());
		}
		return firstRow;
	}

	private LayoutContainer getFirstRowLeftSide() {
		if (firstRowLeftSide == null) {
			firstRowLeftSide = new LayoutContainer();
			firstRowLeftSide.setLayout(new FormLayout());
			FormData formData = new FormData(null);
			formData.setWidth(155);
			formData.setMargins(new Margins(0, 0, 0, 0));
			firstRowLeftSide.add(getJobnameTextField(), formData);
			FormData formData_1 = new FormData(null);
			formData_1.setWidth(155);
			firstRowLeftSide.add(getVoComboBox(), formData_1);
		}
		return firstRowLeftSide;
	}

	private LayoutContainer getFirstRowRightSide() {
		if (firstRowRightSide == null) {
			firstRowRightSide = new LayoutContainer();
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelWidth(50);
			firstRowRightSide.setLayout(formLayout);
			firstRowRightSide.add(getCheckBox(), new FormData("100%"));
			FormData formData = new FormData("");
			formData.setWidth(150);
			firstRowRightSide.add(getEmailTextField(), formData);
		}
		return firstRowRightSide;
	}

	private FormPanel getFrmpnlCreateJob() {
		if (frmpnlCreateJob == null) {
			frmpnlCreateJob = new FormPanel();
			frmpnlCreateJob.setBodyBorder(false);
			frmpnlCreateJob.setHeading("Create job");
			frmpnlCreateJob.setSize("744px", "530px");
			frmpnlCreateJob.setLabelAlign(LabelAlign.LEFT);
			frmpnlCreateJob.setCollapsible(false);
			frmpnlCreateJob.setLayout(new RowLayout(Orientation.VERTICAL));
			frmpnlCreateJob.add(getFirstRow(), new RowData(Style.DEFAULT, -1.0,
					new Margins(0, 0, 0, 0)));
			frmpnlCreateJob.add(getThirdRow(), new RowData(Style.DEFAULT, -1.0,
					new Margins(0, 0, 0, 0)));
			frmpnlCreateJob.add(getSecondRow(), new RowData(Style.DEFAULT,
					Style.DEFAULT, new Margins(0, 0, 0, 0)));
			frmpnlCreateJob.addButton(getSubmitButton());
			FormButtonBinding binding = new FormButtonBinding(frmpnlCreateJob);
			binding.addButton(getSubmitButton());
			frmpnlCreateJob.add(getHorizontalPanel());
		}
		return frmpnlCreateJob;
	}

	private HorizontalPanel getHorizontalPanel() {
		if (horizontalPanel == null) {
			horizontalPanel = new HorizontalPanel();
			horizontalPanel.setSpacing(10);
			horizontalPanel.setEnabled(false);
		}
		return horizontalPanel;
	}

	private SimpleComboBox<Integer> getHoursComboBox() {
		if (hoursComboBox == null) {
			hoursComboBox = new SimpleComboBox<Integer>();
			hoursComboBox.setAllowBlank(false);
			hoursComboBox.setFieldLabel("Hours");
			hoursComboBox.add(0);
			hoursComboBox.add(1);
			hoursComboBox.add(2);
			hoursComboBox.add(4);
			hoursComboBox.add(8);
			hoursComboBox.add(12);
			hoursComboBox.add(18);
			hoursComboBox.setSimpleValue(0);
		}
		return hoursComboBox;
	}

	private LayoutContainer getHoursContainer() {
		if (hoursContainer == null) {
			hoursContainer = new LayoutContainer();
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelAlign(LabelAlign.TOP);
			hoursContainer.setLayout(formLayout);
			FormData formData = new FormData("");
			formData.setWidth(70);
			hoursContainer.add(getHoursComboBox(), formData);
		}
		return hoursContainer;
	}

	private ContentPanel getJobnamePanel() {
		if (jobnamePanel == null) {
			jobnamePanel = new ContentPanel();
			jobnamePanel.setWidth("314px");
			jobnamePanel.setHeading("Jobname & group");
			jobnamePanel.setLayout(new FitLayout());
			jobnamePanel.add(getFirstRowLeftSide(), new FitData(10));
		}
		return jobnamePanel;
	}

	private TextField<String> getJobnameTextField() {
		if (jobnameTextField == null) {
			jobnameTextField = new TextField<String>();
			jobnameTextField.setAllowBlank(false);
			jobnameTextField.setWidth("100");
			jobnameTextField.setFieldLabel("Jobname");
			jobnameTextField.setValidator(new Validator() {

				public String validate(Field<?> field, String value) {

					if (field == jobnameTextField) {

						if (value.indexOf(" ") >= 0) {
							return "Jobname contains whitespace";
						}

						if (UserEnvironment.getInstance().getAllJobnames()
								.contains(value)) {
							return "Jobname already exists.";
						}

					}
					return null;
				}
			});
			jobnameTextField.addListener(Events.KeyUp,
					new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					userTypedInTextField = true;
				}
			});
		}
		return jobnameTextField;
	}

	private LayoutContainer getLayoutContainer() {
		if (layoutContainer == null) {
			layoutContainer = new LayoutContainer();
			layoutContainer.setWidth("167px");
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelAlign(LabelAlign.TOP);
			layoutContainer.setLayout(formLayout);
			layoutContainer.add(getCpusComboBox(), new FormData("100%"));
		}
		return layoutContainer;
	}

	private LayoutContainer getLayoutContainer_1() {
		if (layoutContainer_1 == null) {
			layoutContainer_1 = new LayoutContainer();
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelAlign(LabelAlign.TOP);
			layoutContainer_1.setLayout(formLayout);
			layoutContainer_1.add(getVersionComboBox(), new FormData("100%"));
		}
		return layoutContainer_1;
	}

	private ListField<GrisuFileObject> getListView() {
		if (listField == null) {
			listField = new ListField<GrisuFileObject>();
			listField.setDisplayField(GrisuFileObject.FILENAME);
			listField.setStore(inputFileStore);
			listField.setHeight("81px");
		}
		return listField;
	}

	private SimpleComboBox<Integer> getMinutesComboBox() {
		if (minutesComboBox == null) {
			minutesComboBox = new SimpleComboBox<Integer>();
			minutesComboBox.setAllowBlank(false);
			minutesComboBox.setFieldLabel("Minutes");
			minutesComboBox.add(15);
			minutesComboBox.add(30);
			minutesComboBox.add(45);
			minutesComboBox.setSimpleValue(15);
		}
		return minutesComboBox;
	}

	private LayoutContainer getMinutesContainer() {
		if (minutesContainer == null) {
			minutesContainer = new LayoutContainer();
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelAlign(LabelAlign.TOP);
			minutesContainer.setLayout(formLayout);
			FormData formData = new FormData(null);
			formData.setWidth(70);
			minutesContainer.add(getMinutesComboBox(), formData);
		}
		return minutesContainer;
	}

	private Button getRemoveFileButton() {

		if (removeFileButton == null) {
			removeFileButton = new Button("Remove");
			removeFileButton.addListener(Events.Select,
					new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {

					for (GrisuFileObject file : getListView()
							.getSelection()) {
						inputFileStore.remove(file);
					}
				}
			});
		}
		return removeFileButton;
	}

	private HorizontalPanel getSecondRow() {
		if (secondRow == null) {
			secondRow = new HorizontalPanel();
			secondRow.setSpacing(10);
			secondRow.add(getJobnamePanel());
			secondRow.add(getEmailPanel());
		}
		return secondRow;
	}

	private ContentPanel getStageFileList() {
		if (stageFileList == null) {
			stageFileList = new ContentPanel();
			stageFileList.setBodyBorder(false);
			stageFileList.setBorders(true);
			stageFileList.setSize("177px", "195px");
			stageFileList.setHideCollapseTool(true);
			stageFileList.setLayout(new FitLayout());
			stageFileList.add(getListView(), new FitData(10));
			stageFileList.setHeading("Job directory");
			stageFileList.setCollapsible(true);
			stageFileList.addButton(getRemoveFileButton());
			stageFileList.addButton(getAddFileButton());
		}
		return stageFileList;
	}

	private Button getSubmitButton() {
		if (submitButton == null) {
			submitButton = new Button("Submit");
			submitButton.addListener(Events.Select, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {

					submitJob();

				}
			});
		}
		return submitButton;
	}

	private LayoutContainer getTextAreaContainer() {
		if (textAreaContainer == null) {
			textAreaContainer = new LayoutContainer();
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelAlign(LabelAlign.TOP);
			textAreaContainer.setLayout(formLayout);
			textAreaContainer.add(getCommandLineTextArea(), new FormData(
			"-40 -35"));
		}
		return textAreaContainer;
	}

	private HorizontalPanel getThirdRow() {
		if (thirdRow == null) {
			thirdRow = new HorizontalPanel();
			thirdRow.setHorizontalAlign(HorizontalAlignment.RIGHT);
			thirdRow.setSpacing(10);
			thirdRow.add(getApplicationPanel());
			thirdRow.add(getWalltimeContainer());
			thirdRow.add(getCpusPanel());
		}
		return thirdRow;
	}

	private SimpleComboBox<String> getVersionComboBox() {
		if (versionComboBox == null) {
			versionComboBox = new SimpleComboBox<String>();
			versionComboBox.setWidth("187px");
			versionComboBox.setAllowBlank(false);
			versionComboBox.setEditable(false);
			versionComboBox.setFieldLabel("Version");
			versionComboBox.setValidator(new Validator() {

				public String validate(Field<?> field, String value) {

					if (value.equals(NOT_AVAILABLE_STRING)) {
						return "The specified application is not available for the selected group.";
					}

					return null;
				}
			});
			// versionComboBox.addListener(Events.Valid, new
			// Listener<BaseEvent>() {
			//
			// public void handleEvent(BaseEvent be) {
			//
			// System.out.println("Event: "+versionComboBox.getSimpleValue());
			//
			// }
			// });
		}
		return versionComboBox;
	}

	private SimpleComboBox<String> getVoComboBox() {
		if (voComboBox == null) {
			voComboBox = new SimpleComboBox<String>();
			voComboBox.setAllowBlank(false);
			voComboBox.setForceSelection(true);
			voComboBox.setEditable(false);
			voComboBox.setFieldLabel("Submit as");
			voComboBox.addListener(Events.Valid, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {

					//					xxx

					fillVersionsComboBox();
					String fqan = voComboBox.getSimpleValue();
					if ((fqan != null) && !"".equals(fqan)) {
						UserEnvironment.getInstance().setUserProperty(
								Constants.DEFAULT_FQAN, fqan);
					}
				}
			});

			String lastFqan = UserEnvironment.getInstance().getUserProperty(
					Constants.DEFAULT_FQAN);
			boolean useLastFqan = false;
			String firstValue = null;
			for (String vo : UserEnvironment.getInstance().getAllFqans()) {

				// I know, I know.
				if ("/ARCS".equals(vo)) {
					continue;
				}
				if (firstValue == null) {
					firstValue = vo;
				}
				if (vo.equals(lastFqan)) {
					useLastFqan = true;
				}
				voComboBox.add(vo);
			}

			//			if (useLastFqan) {
			//				voComboBox.setSimpleValue(lastFqan);
			//			} else {
			//				voComboBox.setSimpleValue(firstValue);
			//			}
		}
		return voComboBox;
	}

	private ContentPanel getWalltimeContainer() {
		if (walltimeContainer == null) {
			walltimeContainer = new ContentPanel();
			walltimeContainer.setHeight("83px");
			walltimeContainer.setHideCollapseTool(true);
			walltimeContainer.setHeading("Walltime");
			walltimeContainer.setCollapsible(true);
			FitLayout fitLayout = new FitLayout();
			fitLayout.setExtraStyle("");
			walltimeContainer.setLayout(fitLayout);
			walltimeContainer.add(getWallTimeFormPanel(), new FitData(0));
		}
		return walltimeContainer;
	}

	private HorizontalPanel getWallTimeFormPanel() {
		if (WallTimeFormPanel == null) {
			WallTimeFormPanel = new HorizontalPanel();
			WallTimeFormPanel.setHeight("66px");
			WallTimeFormPanel.setVerticalAlign(VerticalAlignment.BOTTOM);
			WallTimeFormPanel.setSpacing(10);
			TableData tableData = new TableData();
			tableData.setVerticalAlign(VerticalAlignment.BOTTOM);
			WallTimeFormPanel.add(getDaysContainer(), tableData);
			TableData tableData_1 = new TableData();
			tableData_1.setVerticalAlign(VerticalAlignment.BOTTOM);
			WallTimeFormPanel.add(getHoursContainer(), tableData_1);
			TableData tableData_2 = new TableData();
			tableData_2.setVerticalAlign(VerticalAlignment.BOTTOM);
			WallTimeFormPanel.add(getMinutesContainer(), tableData_2);

			// setting last walltime
			int walltimeInMinutes = -1;
			try {
				String wtString = UserEnvironment.getInstance()
				.getUserProperty(
						Constants.GENERIC_JOB_LAST_WALLTIME_IN_MINUTES);
				walltimeInMinutes = Integer.parseInt(wtString);
				if (walltimeInMinutes > 0) {
					int days = walltimeInMinutes / (60 * 24);
					int hours = (walltimeInMinutes - (days * 60 * 24)) / 3600;
					int minutes = (walltimeInMinutes - ((days * 60 * 24) + (hours * 3600))) / 60;

					getMinutesComboBox().setSimpleValue(minutes);
					getHoursComboBox().setSimpleValue(hours);
					getDaysComboBox().setSimpleValue(days);
				}

			} catch (Exception e) {
				// do nothing
			}

		}
		return WallTimeFormPanel;
	}

	public void onJobSubmissionFinished(JobSubmissionFinishedEvent e) {

		suggestJobname();

	}

	public void onValueChange(ValueChangeEvent<List<GrisuFileObject>> arg0) {

		for (GrisuFileObject file : arg0.getValue()) {

			if (!inputFileStore.contains(file)) {
				inputFileStore.add(file);
			}
		}

	}

	private void setAppNotAvailable() {
		getVersionComboBox().removeAll();
		getVersionComboBox().clearSelections();
		getVersionComboBox().add(NOT_AVAILABLE_STRING);
		getVersionComboBox().setSimpleValue(NOT_AVAILABLE_STRING);
	}

	private void setCurrentApplication(String[] apps) {
		this.currentApplications = apps;
		if ((apps != null) && (apps.length > 0)) {
			getApplicationPanel().setHeading("Application: " + apps[0]);
		} else {
			getApplicationPanel().setHeading("Application");
		}
	}

	private void setStatus(String status) {
		this.currentStatus = status;
	}

	private void setVersionsNotAvailable() {
		getVersionComboBox().removeAll();
		getVersionComboBox().clearSelections();
		getVersionComboBox().add(NOT_AVAILABLE_STRING);
		getVersionComboBox().setSimpleValue(NOT_AVAILABLE_STRING);
	}

	private void submitJob() {

		mask();

		final Map<String, String> jobProperties;
		try {
			jobProperties = calculateJobProperties();
		} catch (JobCreationException e) {
			unmask();
			e.printStackTrace();
			Window.alert(e.getLocalizedMessage());
			return;
		}

		final MessageBox box = MessageBox.progress("Please wait",
				"Submitting job " + jobProperties.get(Constants.JOBNAME_KEY)
				+ "...", "Initializing...");
		final ProgressBar bar = box.getProgressBar();
		final Timer t = new Timer() {
			float i;

			@Override
			public void run() {

				GrisuClientService.Util.getInstance().getCurrentStatus(
						jobProperties.get(Constants.JOBNAME_KEY),
						new AsyncCallback<DtoActionStatus>() {

							public void onFailure(Throwable arg0) {

								box.close();
								unmask();

								cancel();
								arg0.printStackTrace();

							}

							public void onSuccess(DtoActionStatus arg0) {

								try {

									if ((arg0 != null) && arg0.getFinished()) {

										box.close();
										unmask();

										cancel();

										if (!arg0.getFailed()) {
											UserEnvironment
											.getInstance()
											.setUserProperty(
													Constants.DEFAULT_VERSION
													+ lastCalculatedExecutable,
													jobProperties
													.get(Constants.APPLICATIONVERSION_KEY));
										}
									}

									if (arg0 == null) {
										bar.updateProgress(0, "Contacting...");
										return;
									}
									double current = arg0.getCurrentElements();
									double total = arg0.getTotalElements();

									bar.updateProgress(current / total, arg0
											.getLog().get(
													arg0.getLog().size() - 1)
													.getLogMessage());

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						});

			}
		};
		t.scheduleRepeating(500);

		UserEnvironment.getInstance().submitJob(jobProperties);

	}

	private void suggestJobname() {

		if (userTypedInTextField) {
			return;
		}

		if ((lastCalculatedExecutable == null)
				|| "".equals(lastCalculatedExecutable)) {
			getJobnameTextField().setValue("");
			return;
		}

		if (UserEnvironment.getInstance().getAllJobnames().contains(
				lastCalculatedExecutable + "_job")) {
			int i = 1;

			while (UserEnvironment.getInstance().getAllJobnames().contains(
					lastCalculatedExecutable + "_job_" + i)) {
				i = i + 1;
			}
			getJobnameTextField().setValue(
					lastCalculatedExecutable + "_job_" + i);

		} else {
			getJobnameTextField().setValue(lastCalculatedExecutable + "_job");
		}

	}
}
