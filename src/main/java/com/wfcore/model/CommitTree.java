package com.wfcore.model;

import java.io.Serializable;

public class CommitTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sha;
	private String url;

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
