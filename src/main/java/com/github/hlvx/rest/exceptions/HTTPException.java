package com.github.hlvx.rest.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Basic REST-Style Error
 * @author AlexMog
 */
public class HTTPException extends RuntimeException {
	private static final long serialVersionUID = 5107806786982269082L;
	private int code;

	public HTTPException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	@JsonProperty("code")
	public int getCode() {
		return code;
	}
	
	@JsonProperty("error")
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
