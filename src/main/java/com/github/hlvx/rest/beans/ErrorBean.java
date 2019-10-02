package com.github.hlvx.rest.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorBean {
	private int code;
	private String error;
	
	public ErrorBean(int code, String error) {
		this.code = code;
		this.error = error;
	}
	
	@JsonProperty("code")
	public int getCode() {
		return code;
	}
	
	@JsonProperty("error")
	public String getError() {
		return error;
	}
}
