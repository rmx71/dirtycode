package com.wfcore.model;

import java.io.Serializable;

public class Parents implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sha;
	private String url;
	private String html_url;

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

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

}
