package org.vpac.grisu.webclient.client.jobmonitoring;

import java.util.Date;
import java.util.Map;

import org.vpac.grisu.webclient.client.external.Constants;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BeanModelTag;

public class GrisuJob extends BaseModel {
	
	private static final long serialVersionUID = 1L;  
	
	public static final String ALLPROPERTIES = "allProperties";

	public Integer getStatus() {
		return (Integer)get(Constants.STATUS_STRING);
	}

	public String getJobname() {
		return (String)get(Constants.JOBNAME_KEY);
	}

	public String getApplication() {
		return (String)get(Constants.APPLICATIONNAME_KEY);
	}

	public String getVersion() {
		return (String)get(Constants.APPLICATIONVERSION_KEY);
	}

	public Integer getCpus() {
		return (Integer)get(Constants.NO_CPUS_KEY);
	}

	public Integer getWalltime() {
		return (Integer)get(Constants.WALLTIME_IN_MINUTES_KEY);
	}

	public Date getSubmissionTime() {
		return (Date)get(Constants.SUBMISSION_TIME_KEY);
	}

	public String getFqan() {
		return (String)get(Constants.FQAN_KEY);
	}

	public String getQueue() {
		return (String)get(Constants.QUEUE_KEY);
	}

	public String getSubmissionSite() {
		return (String)get(Constants.SUBMISSION_SITE_KEY);
	}

	public String getJobDirectory() {
		return (String)get(Constants.JOBDIRECTORY_KEY);
	}

	public Map<String, String> getAllProperties() {
		return (Map<String, String>)get(ALLPROPERTIES);
	}
	
	public String toString() {
		return getJobname();
	}
	
	public GrisuJob() {
		
	}
	
	public GrisuJob(Integer status, Map<String, String> allJobProperties) {
		
		set(ALLPROPERTIES, allJobProperties);
		set(Constants.STATUS_STRING, status);

		for ( String key : allJobProperties.keySet() ) {
			if ( Constants.NO_CPUS_KEY.equals(key) ||
					Constants.WALLTIME_IN_MINUTES_KEY.equals(key) ) {
				set(key, Integer.parseInt(allJobProperties.get(key)));
			} else if ( Constants.SUBMISSION_TIME_KEY.equals(key) ) {
				set(key, new Date(Long.parseLong(allJobProperties.get(key))));
			} else {
				set(key, allJobProperties.get(key));
			}
		}
	}

}
