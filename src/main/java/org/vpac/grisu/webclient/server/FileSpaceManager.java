package org.vpac.grisu.webclient.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.dto.DtoMountPoints;
import org.vpac.grisu.webclient.client.files.GrisuFileObject;

public class FileSpaceManager {
	
	private final Mapper mapper = new DozerBeanMapper();
	
	private DtoMountPoints allMountpoints = null;

	private Set<String> allSites = null;

	private Set<String> allRootUrls = null;
	private Set<String> mpAliases = null;

	private Map<String, Set<MountPoint>> mountPointsPerSite = null;

	private final ServiceInterface si;

	public FileSpaceManager(ServiceInterface si) {
		this.si = si;
		getAllMountPoints();
	}

	public Set<String> getAllSites() {
		return allSites;
	}

	public Set<String> getAllRootUrls() {
		return allRootUrls;
	}

	private void getAllMountPoints() {

		allMountpoints = si.df();
		allRootUrls = new HashSet<String>();
		mpAliases = new HashSet<String>();
		allSites = new HashSet<String>();
		mountPointsPerSite = new HashMap<String, Set<MountPoint>>();

		for (MountPoint mp : allMountpoints.getMountpoints()) {
			allRootUrls.add(mp.getRootUrl());
			mpAliases.add(mp.getAlias());
			allSites.add(mp.getSite());
			Set<MountPoint> tempSet = mountPointsPerSite.get(mp.getSite());
			if (tempSet == null) {
				tempSet = new HashSet<MountPoint>();
				mountPointsPerSite.put(mp.getSite(), tempSet);
			}
			tempSet.add(mp);
		}
	}

	public Set<MountPoint> getMountPointsForSite(String site) {

		return mountPointsPerSite.get(site);
	}

	public MountPoint getMountPointForAlias(String alias) {

		for (MountPoint mp : allMountpoints.getMountpoints()) {
			if (mp.getAlias().equals(alias)) {
				return mp;
			}
		}

		return null;
	}

	public MountPoint getMountPoint(String url) {

		if (isMountPointAlias(url)) {
			return getMountPointForAlias(url);
		}

		for (MountPoint mp : allMountpoints.getMountpoints()) {
			if (url.startsWith(mp.getRootUrl())) {
				return mp;
			}
		}
		return null;
	}

	public boolean isSiteName(String site) {

		for (String sitename : allSites) {
			if (sitename.equals(site)) {
				return true;
			}
		}

		return false;
	}

	public boolean isMountPointRoot(String url) {

		for (String rootUrl : allRootUrls) {
			if (rootUrl.equals(url)) {
				return true;
			}
		}

		return false;

	}

	public boolean isMountPointAlias(String url) {

		for (String alias : mpAliases) {
			if (alias.equals(url)) {
				return true;
			}
		}

		return false;

	}

	private String calculateParentFolderUrl(String url) {

		String parent = url.substring(0, url.lastIndexOf("/"));

		return parent;
	}

	public String getParent(String url_or_name) {

		if (isMountPointAlias(url_or_name) || isMountPointRoot(url_or_name)) {
			// means mountPointRoot
			return getMountPoint(url_or_name).getSite();
		}
		if (getMountPoint(url_or_name) != null) {
			// means normal url
			return calculateParentFolderUrl(url_or_name);
		} else if (isSiteName(url_or_name)) {
			return null;
		}

		throw new RuntimeException("Could not find parent for url: "
				+ url_or_name);
	}

	public List<GrisuFileObject> getChildrenFileObjects(boolean includeParentDirectory, String name) throws RemoteFileSystemException {
		
		List<GrisuFileObject> result = new ArrayList<GrisuFileObject>();
		
		if ( StringUtils.isBlank(name) || GrisuFileObject.FILETYPE_ROOT.equals(name) ) {
			List<String> allSites = new ArrayList<String>(getAllSites());
			Collections.sort(allSites);
			for ( String site : allSites ) {
				result.add(new GrisuFileObject(site));
			}
			
		} else if ( isSiteName(name) ) {

			if ( includeParentDirectory ) {
				result.add(new GrisuFileObject("..", GrisuFileObject.FILETYPE_ROOT, GrisuFileObject.FILETYPE_ROOT, 0L, null));
			}
			
			List<MountPoint> allMps = new ArrayList<MountPoint>(getMountPointsForSite(name));
			Collections.sort(allMps);
			
			for ( MountPoint mp : allMps ) {
				result.add(new GrisuFileObject(mp.getAlias(), mp.getRootUrl(),
						GrisuFileObject.FILETYPE_MOUNTPOINT, 0L, null));
			}
			
		} else {
			
			if ( isMountPointAlias(name) ) {
				name = getMountPointForAlias(name).getRootUrl();
			}
			
			if ( includeParentDirectory ) {
				if ( isMountPointRoot(name) ) {
					MountPoint mp = getMountPoint(name);
					result.add(new GrisuFileObject("..", mp.getSite(), GrisuFileObject.FILETYPE_SITE, 0L, null));
				} else {
					String parentUrl = getParent(name);
					result.add(new GrisuFileObject("..", parentUrl, GrisuFileObject.FILETYPE_FOLDER, 0L, null));
				}
			}
			DtoFolder folder = si.ls(name, 1);
			
			for ( DtoFolder childFolder : folder.getChildrenFolders() ) {
				result.add(new GrisuFileObject(childFolder.getName(), childFolder.getRootUrl(),
						GrisuFileObject.FILETYPE_FOLDER, 0L, null));
			}
			for ( DtoFile child : folder.getChildrenFiles() ) {
				result.add(new GrisuFileObject(child.getName(), child.getRootUrl(),
						GrisuFileObject.FILETYPE_FILE, child.getSize(), new Date(child.getLastModified())));
			}
		}
		
		return result;
		
	}
	
	public GrisuFileObject createFileObject(String name) {
		
		GrisuFileObject fo = null;
		if ( isSiteName(name) ) {
			fo = new GrisuFileObject(name);
		} else if ( isMountPointAlias(name) ) {
			MountPoint mp = getMountPointForAlias(name);
			
			org.vpac.grisu.client.model.MountPoint gwtMountPoint;
			try {
				gwtMountPoint = mapper.map(mp, org.vpac.grisu.client.model.MountPoint.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			fo = new GrisuFileObject(gwtMountPoint);
		} else {
			try {
			if ( si.isFolder(name) ) {
				fo = new GrisuFileObject(calculateFileName(name), name, GrisuFileObject.FILETYPE_FOLDER, 0L, null);
			} else {
				fo = new GrisuFileObject(calculateFileName(name), name, GrisuFileObject.FILETYPE_FILE, si.getFileSize(name), new Date(si.lastModified(name)));
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return fo;
		
	}
	
	public String calculateFileName(String url) {
		return url.substring(url.lastIndexOf("/"));
	}
}
