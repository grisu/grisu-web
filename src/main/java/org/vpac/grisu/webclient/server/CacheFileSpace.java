package org.vpac.grisu.webclient.server;

import java.io.File;

public class CacheFileSpace {
	
	public static final String APACHE_WEB_ROOT = "/var/www/html";
	public static final String APACHE_SUBFOLDER = "tomcat";
	
	public static final String UPLOAD_FOLDER_NAME = "uploads";
	
	public static File getLocalFileRootForUserAsFile(String usertoken) {
		
		String path = APACHE_WEB_ROOT + "/" + APACHE_SUBFOLDER + "/" +usertoken;
		
		File folder = new File(path);
		
		if ( ! folder.exists() ) {
			folder.mkdirs();
		}
		
		return folder;
		
	}
	
	public static File getUploadFolderAsFile(String usertoken) {
		
		File folder = new File(getLocalFileRootForUserAsFile(usertoken), UPLOAD_FOLDER_NAME);
		
		if ( ! folder.exists() ) {
			folder.mkdirs();
		}
		return folder;
		
	}
	
	public static String relativePathToWebRoot(String localPath) {
		String url = localPath.substring(APACHE_WEB_ROOT.length());
		return url;
	}

}
