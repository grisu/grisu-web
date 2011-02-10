package org.vpac.grisu.webclient.client.files;

import grisu.client.model.dto.DtoActionStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vpac.grisu.webclient.client.EventBus;
import org.vpac.grisu.webclient.client.GrisuClientService;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileTransferObject extends BaseModel {

	private static final long serialVersionUID = -4077857668554527074L;

	public static final int STATUS_UNDEFINED = -1;

	public static final String SOURCES_KEY = "sources";
	public static final String TARGET_KEY = "target";
	public static final String FINISHED_KEY = "finished";
	public static final String FAILED_KEY = "failed";
	public static final String STATUS_KEY = "status";
	public static final String STARTED_KEY = "started";

	private final List<GrisuFileObject> sources;
	private final GrisuFileObject target;

	private Boolean started = false;

	private String handle = null;
	private DtoActionStatus currentStatus = null;
	private Date lastStatusUpdate = null;

	public FileTransferObject(List<GrisuFileObject> sources, GrisuFileObject target) {
		this.sources = sources;
		this.target = target;
		set(SOURCES_KEY, this.sources);
		set(TARGET_KEY, this.target);
		set(STARTED_KEY, started);
		set(FAILED_KEY, Boolean.FALSE);
		set(FINISHED_KEY, Boolean.FALSE);
	}

	public DtoActionStatus getCurrentStatus() {
		return currentStatus;
	}

	public String getHandle() {
		return handle;
	}

	public List<GrisuFileObject> getSources() {
		return this.sources;
	}

	public GrisuFileObject getTarget() {
		return this.target;
	}

	public Boolean isFailed() {

		if ( currentStatus == null ) {
			return false;
		} else {
			return currentStatus.getFailed();
		}
	}

	public Boolean isFinished() {

		if ( currentStatus == null ) {
			return false;
		} else {
			return currentStatus.getFinished();
		}
	}

	public boolean startTransfer() {

		if ( ! GrisuFileObject.FILETYPE_FOLDER.equals(target.getFileType())
				&& ! GrisuFileObject.FILETYPE_MOUNTPOINT.equals(target.getFileType()) ) {
			return false;
		}

		final List<String> sourceNames = new ArrayList<String>();
		for ( GrisuFileObject file : sources ) {

			if ( ! GrisuFileObject.FILETYPE_FILE.equals(file.getFileType())
					&& ! GrisuFileObject.FILETYPE_FOLDER.equals(file.getFileType()) ) {
				return false;
			}

			sourceNames.add(file.getUrl());
		}

		GrisuClientService.Util.getInstance().cp(sourceNames, target.getUrl(), new AsyncCallback<String>() {

			public void onFailure(Throwable arg0) {

				arg0.printStackTrace();
				//				Window.alert(arg0.getLocalizedMessage());
				set(FINISHED_KEY, true);
				set(FAILED_KEY, true);
			}

			public void onSuccess(String arg0) {

				handle = arg0;
				started = true;
				set(STARTED_KEY, true);
			}
		});

		FileTransferStartedEvent startEvent = new FileTransferStartedEvent(this);
		EventBus.get().fireEvent(startEvent);

		return true;

	}


	public void update() {

		if ( started == false ) {
			return;
		}
		if ( isFinished() == true ) {
			return;
		}
		if ( handle == null ) {
			return;
		}


		GrisuClientService.Util.getInstance().getCurrentStatus(handle, new AsyncCallback<DtoActionStatus>() {

			public void onFailure(Throwable arg0) {

				arg0.printStackTrace();

			}

			public void onSuccess(DtoActionStatus arg0) {

				currentStatus = arg0;
				lastStatusUpdate = arg0.getLastUpdate();

				System.out.println("Status finished: "+isFinished());
				set(FINISHED_KEY, isFinished());
				set(FAILED_KEY, isFailed());
				set(STATUS_KEY, currentStatus);
				FileTransferStatusChangedEvent statusChangedEvent = new FileTransferStatusChangedEvent(FileTransferObject.this);
				EventBus.get().fireEvent(statusChangedEvent);

			}
		});

	}





}
