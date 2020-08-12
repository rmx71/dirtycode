package com.wfcore.model;

import java.io.Serializable;
import java.util.List;

public class Commits implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sha;
	private String node_id;
	private Commit commit;
	private String url;
	private String html_url;
	private String comments_url;
	private Owner author;
	private Owner committer;
	private List<Parents> parents;

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
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

	public String getComments_url() {
		return comments_url;
	}

	public void setComments_url(String comments_url) {
		this.comments_url = comments_url;
	}

	public Owner getAuthor() {
		return author;
	}

	public void setAuthor(Owner author) {
		this.author = author;
	}

	public Owner getCommitter() {
		return committer;
	}

	public void setCommitter(Owner committer) {
		this.committer = committer;
	}

	public List<Parents> getParents() {
		return parents;
	}

	public void setParents(List<Parents> parents) {
		this.parents = parents;
	}

}
