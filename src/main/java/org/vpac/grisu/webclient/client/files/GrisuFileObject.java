package org.vpac.grisu.webclient.client.files;

import java.util.Date;

import org.vpac.grisu.client.model.MountPoint;
import org.vpac.grisu.client.model.dto.DtoFile;
import org.vpac.grisu.client.model.dto.DtoFolder;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GrisuFileObject extends BaseModel {
	
	public static final String URL = "url";
	public static final String FILENAME = "fileName";
	public static final String FILESIZE = "fileSize";
	public static final String LASTMODIFIED = "lastModified";
	
	public static final String FILETYPE = "fileType";
	public static final String FILETYPE_SITE = "Site";
	public static final String FILETYPE_MOUNTPOINT = "MountPoint";
	public static final String FILETYPE_FOLDER = "Folder";
	public static final String FILETYPE_FILE = "File";
	public static final String FILETYPE_ROOT = "Root";
	

	private static final long serialVersionUID = 1L;  
	
	public GrisuFileObject() {
	}
	
	public GrisuFileObject(String name, String url, String type, Long size, Date lastModified) {
		set(FILENAME, name);
		set(URL, url);
		set(FILETYPE, type);
		set(FILESIZE, size);
		set(LASTMODIFIED, lastModified);
	}
	
	public GrisuFileObject(String sitename) {
		set(FILENAME, sitename);
		set(FILETYPE, FILETYPE_SITE);
		set(FILESIZE, 0L);
		set(URL, sitename);
	}
	
	public GrisuFileObject(MountPoint mp) {
		set(FILENAME, mp.getAlias());
		set(FILETYPE, FILETYPE_MOUNTPOINT);
		set(FILESIZE, 0L);
		set(URL, mp.getRootUrl());
	}
	
	public GrisuFileObject(DtoFolder folder) {
		set(FILENAME, folder.getName());
		set(FILETYPE, FILETYPE_FOLDER);
		set(FILESIZE, 0L);
		set(URL, folder.getRootUrl());
	}
	
	public GrisuFileObject(DtoFile file) {
		set(FILENAME, file.getName());
		set(FILETYPE, FILETYPE_FILE);
		set(FILESIZE, file.getSize());
		set(LASTMODIFIED, new Date(file.getLastModified()));
		set(URL, file.getRootUrl());
	}
	
	public String getUrl() {
		return (String)get(URL);
	}
	
	
	public String getFileName() {
			return (String)get(FILENAME);
	}

	public Long getFileSize() {
		return (Long)get(FILESIZE);
	}
	
	public String getFileType() {
		return (String)get(FILETYPE);
	}
	
	public Date getLastModified() {
		return (Date)get(LASTMODIFIED);
	}
}
