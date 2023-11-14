package com.example.demo.exceptions;

public final class ErrorMessage {
	public static final String USER_NOT_FOUND = "USER NOT FOUND";
	public static final String USER_NOT_VALID = "USER NOT VALID";

	public static final String QUERY_NOT_FOUND = "QUERY NOT FOUND";
	public static final String QUERY_NOT_VALID = "QUERY NOT VALID";

	public static final String COMMENT_NOT_FOUND = "COMMENT NOT FOUND";
	public static final String COMMENT_NOT_VALID = "COMMENT NOT VALID";


	private ErrorMessage() {
		throw new IllegalStateException("Utility class");
	}
	
}