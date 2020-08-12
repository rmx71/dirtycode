package com.wfcore.model;

import java.io.Serializable;

public class Commit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CommitAuthor author;
	private CommitCommitter committer;
	private String message;
	private CommitTree tree;
	private String url;
	private String comment_count;
	private CommitVerification verification;

	public CommitAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CommitAuthor author) {
		this.author = author;
	}

	public CommitCommitter getCommitter() {
		return committer;
	}

	public void setCommitter(CommitCommitter committer) {
		this.committer = committer;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public CommitTree getTree() {
		return tree;
	}

	public void setTree(CommitTree tree) {
		this.tree = tree;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}

	public CommitVerification getVerification() {
		return verification;
	}

	public void setVerification(CommitVerification verification) {
		this.verification = verification;
	}

}
