package org.vpac.grisu.webclient.client.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.webclient.client.EventBus;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Timer;

public class FileTransferMonitorPanel extends ContentPanel implements
		FileTransferStartedEvent.Handler {

	private Grid grid;

	private ListStore<FileTransferObject> fileTransfersToMonitor = new ListStore<FileTransferObject>();

	private Timer t = new Timer() {
		public void run() {
			updateStatus();
			System.out.println("update");
		}
	};

	public FileTransferMonitorPanel() {
		setTitle("File transfers");
		setStateful(true);
//		getState().put("collapsed", Boolean.TRUE);
//		setCollapsible(true);
		setLayout(new FitLayout());
		add(getGrid());

		EventBus.get().addHandler(FileTransferStartedEvent.TYPE, this);

	}

	private List<ColumnConfig> createColumnConfig() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		GridCellRenderer<FileTransferObject> targetCellRenderer = new GridCellRenderer<FileTransferObject>() {

			public Object render(FileTransferObject model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FileTransferObject> store,
					Grid<FileTransferObject> grid) {

				return model.getTarget().getFileName();

			}
		};

		ColumnConfig column = new ColumnConfig();
		column.setId(FileTransferObject.TARGET_KEY);
		column.setRenderer(targetCellRenderer);
		column.setHeader("Target");
		column.setWidth(120);
		column.setSortable(false);
		configs.add(column);
		
		GridCellRenderer<FileTransferObject> percentageCellRenderer = new GridCellRenderer<FileTransferObject>() {

			public Object render(FileTransferObject model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FileTransferObject> store,
					Grid<FileTransferObject> grid) {

				try {
				DtoActionStatus currentStatus = model.getCurrentStatus();

				double percentage = 0;
				if ( currentStatus != null ) {

					double current = currentStatus.getCurrentElements();
					double total = currentStatus.getTotalElements();
					percentage = current / total;
				}
				
				
				ProgressBar progressBar = new ProgressBar();
				progressBar.setHeight("10");
				progressBar.updateProgress(percentage, "");
				int percent = new Double(percentage * 100).intValue();
				System.out.println("Percent: "+percentage);
				
				return progressBar;
				} catch (Exception e) {
					e.printStackTrace();
					return "Error: "+e.getLocalizedMessage();
				}
			}
		};
		
		column = new ColumnConfig();
		column.setId(FileTransferObject.STATUS_KEY);
		column.setRenderer(percentageCellRenderer);
		column.setHeader("Progress");
		column.setWidth(160);
		column.setSortable(false);
		configs.add(column);

		GridCellRenderer<FileTransferObject> failedCellRenderer = new GridCellRenderer<FileTransferObject>() {

			public Object render(FileTransferObject model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FileTransferObject> store,
					Grid<FileTransferObject> grid) {

				if (model.isFinished()) {
					if (model.isFailed()) {
						return "Failed";
					} else {
						return "Success";
					}
				} else {
					return "Transferring...";
				}

			}
		};

		column = new ColumnConfig(FileTransferObject.FINISHED_KEY, "Status", 100);
		column.setRenderer(failedCellRenderer);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setSortable(false);
		configs.add(column);

		return configs;
	}

	private Grid getGrid() {
		if (grid == null) {

			if (!isDesignTime()) {

				ColumnModel cm = null;
				cm = new ColumnModel(createColumnConfig());

				grid = new Grid<FileTransferObject>(fileTransfersToMonitor, cm);
				grid.setStyleAttribute("borderTop", "none");
				grid.setBorders(false);
				grid.setAutoExpandColumn(FileTransferObject.TARGET_KEY);
				grid.setStripeRows(true);
			} else {
				grid = new Grid(new ListStore(), new ColumnModel(Collections
						.<ColumnConfig> emptyList()));
			}
		}
		return grid;
	}

	public void updateStatus() {

		boolean allJobsFinished = true;
		for (FileTransferObject fto : fileTransfersToMonitor.getModels()) {

			if (!fto.isFinished()) {
				allJobsFinished = false;
				fto.update();
			}
			getGrid().getView().refresh(true);
		}
		
		if (allJobsFinished) {
			t.cancel();
		}

	}

	private static final boolean isDesignTime() {
		return false;
	}

	public void onFileTransferStarted(FileTransferStartedEvent e) {

		fileTransfersToMonitor.add(e.getFileTransferObject());

		t.scheduleRepeating(2000);

	}

}
