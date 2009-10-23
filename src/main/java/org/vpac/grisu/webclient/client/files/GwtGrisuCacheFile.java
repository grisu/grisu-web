package org.vpac.grisu.webclient.client.files;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A file that was downloaded and now sits in the cache with an url that can be accessed via the gwt client
 * @author markus
 *
 */
public class GwtGrisuCacheFile implements IsSerializable {
	
	private String localPath = null;
	private String publicUrl = null;
	private String mimeType = null;
	private String gridUrl = null;
	
	public GwtGrisuCacheFile() {
		
	}
	
	public GwtGrisuCacheFile(String localPath, String publicUrl, String mimeType, String gridUrl) {
		this.localPath = localPath;
		this.publicUrl = publicUrl;
		this.mimeType = mimeType;
		this.gridUrl = gridUrl;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getPublicUrl() {
		return "http://localhost"+publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getGridUrl() {
		return gridUrl;
	}

	public void setGridUrl(String gridUrl) {
		this.gridUrl = gridUrl;
	}
	
	public String getFilename() {
		if ( localPath != null && ! "".equals(localPath) ) {
			return localPath.substring(localPath.lastIndexOf("/")+1);
		} else {
			return "n/a";
		}
	}
	
}
