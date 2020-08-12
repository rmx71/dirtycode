package com.wfcore.model;

import java.io.Serializable;

public class CommitVerification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String verified;
	private String reason;
	private String signature;
	private String payload;

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

}
