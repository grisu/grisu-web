package org.vpac.grisu.webclient.client;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class Node extends BaseTreeModel {
	
	public Node() {
	}
	
	public Node(String name) {
		set("name", name);
	}

}
