package com.hdbsnc.smartiot.service.ui.impl;

import java.net.URL;

import org.eclipse.jetty.util.resource.URLResource;

public class BundleResource extends URLResource{

	public BundleResource(URL url) {
		super(url, null);
	}

}
