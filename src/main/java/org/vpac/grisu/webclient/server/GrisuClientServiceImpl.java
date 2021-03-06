package org.vpac.grisu.webclient.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.vpac.grisu.client.model.dto.DtoActionStatus;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.control.info.CachedMdsInformationManager;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.model.dto.DtoStringList;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.FileHelpers;
import org.vpac.grisu.webclient.client.GrisuClientService;
import org.vpac.grisu.webclient.client.exceptions.LoginException;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;
import org.vpac.grisu.webclient.client.files.GwtGrisuCacheFile;
import org.vpac.grisu.webclient.client.jobcreation.JobCreationException;
import org.vpac.grisu.webclient.client.jobmonitoring.GrisuJob;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.interfaces.InformationManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GrisuClientServiceImpl extends RemoteServiceServlet implements GrisuClientService {

	static final Logger myLogger = Logger.getLogger(GrisuClientServiceImpl.class.getName());

	private final Mapper mapper = new DozerBeanMapper();

	private final Map<String, org.vpac.grisu.model.dto.DtoActionStatus> actionStatus = new HashMap<String, org.vpac.grisu.model.dto.DtoActionStatus>();

	private FileSpaceManager fileSpaceManager;

	private InformationManager infoManager = null;

	public String cp(List<String> sources, String target) {

		DtoStringList dtoSources = DtoStringList.fromStringList(sources);
		String handle = null;
		try {
			handle = getServiceInterface().cp(dtoSources, target, true, false);
		} catch (RemoteFileSystemException e) {
			e.printStackTrace();
		}

		return handle;
	}

	public GwtGrisuCacheFile download(String fileUrl) {

		DataSource source = null;
		GwtGrisuCacheFile newFile = null;
		try {
			source = getServiceInterface().download(fileUrl).getDataSource();
			String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

			File tempFile = new File(CacheFileSpace.getUploadFolderAsFile("markusTest"), filename);

			FileHelpers.saveToDisk(source, tempFile);
			String localPath = tempFile.getAbsolutePath();
			String url = CacheFileSpace.relativePathToWebRoot(localPath);

			String mimeType = new MimetypesFileTypeMap()
			.getContentType(tempFile);

			newFile = new GwtGrisuCacheFile(localPath, url, mimeType, fileUrl);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newFile;
	}

	public List<String> getAllJobnames(String application) {

		return getServiceInterface().getAllJobnames(application).getStringList();
	}


	public String[] getApplicationForExecutable(String executable) {

		String[] exes = getInfoManager().getApplicationsThatProvideExecutable(
				executable);

		return exes;

	}

	public DtoActionStatus getCurrentStatus(String handle) {

		org.vpac.grisu.model.dto.DtoActionStatus status = getServiceInterface().getActionStatus(handle);

		if ( status == null ) {
			status = actionStatus.get(handle);
		}

		if ( status == null ) {
			return null;
		}

		DtoActionStatus newStatus = null;
		try {
			newStatus = mapper.map(status, org.vpac.grisu.client.model.dto.DtoActionStatus.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return newStatus;
	}


	public GrisuFileObject getFile(String url) {

		return getFileSpaceManager().createFileObject(url);

	}


	private FileSpaceManager getFileSpaceManager() {

		if ( fileSpaceManager == null ) {
			fileSpaceManager = new FileSpaceManager(getServiceInterface());
		}

		return fileSpaceManager;

	}


	public String[] getFqans() {

		return getServiceInterface().getFqans().asArray();

	}

	private InformationManager getInfoManager() {

		if (infoManager == null) {
			infoManager = new CachedMdsInformationManager(Environment.getVarGrisuDirectory().toString());
		}
		return infoManager;

	}

	private ServiceInterface getServiceInterface() {

		ServiceInterface si = (ServiceInterface) (getSession()
				.getAttribute("serviceInterface"));
		if (si == null) {
			myLogger.error("ServiceInterface not in session (yet?).");
			throw new RuntimeException("Not logged in.");
		}
		return si;
	}

	private HttpSession getSession() {

		// Get the current request and then return its session
		HttpSession session = this.getThreadLocalRequest().getSession();

		return session;
	}

	public Map<String, String> getUserProperties() {

		return getServiceInterface().getUserProperties().propertiesAsMap();
	}

	public String getUserProperty(String key) {

		return getServiceInterface().getUserProperty(key);

	}

	public String[] getVersionsOfApplicationForVO(String[] applicationNames,
			String fqan) {

		SortedSet<String> result = new TreeSet<String>();
		for ( String app : applicationNames ) {
			String[] temp = getInfoManager().getAllVersionsOfApplicationOnGridForVO(app, fqan);
			result.addAll(Arrays.asList(temp));

		}

		return result.toArray(new String[]{});

	}

	public void killJobs(List<GrisuJob> jobs) {

		List<String> list = new ArrayList<String>();

		for ( GrisuJob job : jobs ) {
			list.add(job.getJobname());
		}

		getServiceInterface().killJobs(DtoStringList.fromStringColletion(list), true);

	}

	public boolean login(String username, String password) throws LoginException {

		// first check whether session is still active...
		myLogger.debug("Trying to get serviceinterface from session.");

		try {
			if ( getServiceInterface() != null ) {
				return true;
			} else if ( StringUtils.isBlank(username) ) {
				return false;
			}

		} catch (Exception e) {
			myLogger.debug("Could not get serviceinterface from session: "+e.getLocalizedMessage()+". Continuing with real login process...");
		}

//		String serviceInterfaceUrl = ClientPropertiesManager.getDefaultServiceInterfaceUrl();

		String serviceInterfaceUrl = "Local";

		myLogger.debug("Logging in...");

		LoginParams loginParams = new LoginParams(
				serviceInterfaceUrl, username, password.toCharArray(), "myproxy2.arcs.org.au", "7512");

		ServiceInterface si = null;

		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
			myLogger.debug("Serviceinterface created. Calling login method...");
			si.login(username, new String(password));
			myLogger.debug("Login successfull.");
		} catch (Exception e) {
			myLogger.debug("Login unsuccessfull.", e);
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LoginException(e.getLocalizedMessage());
		}
		myLogger.info("ServiceInterface created...");
		getSession().setAttribute("serviceInterface", si);

		myLogger.info("Logged in...");
		return true;
	}

	public List<GrisuFileObject> ls(String url) {

		try {
			return getFileSpaceManager().getChildrenFileObjects(true, url);
		} catch (RemoteFileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<GrisuJob> ps(String application, boolean refresh) {

		myLogger.debug("ps");

		DtoJobs jobs = getServiceInterface().ps(application, refresh);

		List<GrisuJob> result = new ArrayList<GrisuJob>();

		for ( DtoJob job : jobs.getAllJobs() ) {
			GrisuJob grisuJob = new GrisuJob(job.getStatus(), job.propertiesAsMap());
			result.add(grisuJob);
		}

		return result;

	}

	public void rm(List<GrisuFileObject> files) {

		List<String> list = new ArrayList<String>();

		for ( GrisuFileObject file : files ) {
			if ( file.isFileOrFolder() ) {
				list.add(file.getUrl());
			}
		}

		DtoStringList toDelete = DtoStringList.fromStringList(list);

		getServiceInterface().deleteFiles(toDelete);

	}

	public void setUserProperty(String key, String value) {

		getServiceInterface().setUserProperty(key, value);
	}

	public void submitJob(Map<String, String> jobProperties)
	throws JobCreationException {

		String fqan = jobProperties.get(Constants.FQAN_KEY);

		org.vpac.grisu.model.dto.DtoActionStatus dummyStatus = new org.vpac.grisu.model.dto.DtoActionStatus();
		dummyStatus.setTotalElements(5);
		dummyStatus.setFinished(false);
		dummyStatus.setFailed(false);
		dummyStatus.setCurrentElements(0);
		dummyStatus.setLastUpdate(new Date());
		dummyStatus.setLog(new ArrayList<org.vpac.grisu.model.dto.DtoLogItem>());
		dummyStatus.addElement("Creating job on backend...");
		actionStatus.put(jobProperties.get(Constants.JOBNAME_KEY), dummyStatus);

		JobSubmissionObjectImpl jso = new JobSubmissionObjectImpl(jobProperties);

		try {
			getServiceInterface().createJob(jso.getJobDescriptionDocumentAsString(), fqan, "force-name");
			actionStatus.remove(jobProperties.get(Constants.JOBNAME_KEY));
		} catch (Exception e) {
			dummyStatus.setFailed(true);
			dummyStatus.setFinished(true);
			e.printStackTrace();
			throw new JobCreationException(e.getLocalizedMessage());
		}

		try {
			getServiceInterface().submitJob(jso.getJobname());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}

	}


}
