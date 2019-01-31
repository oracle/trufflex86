package org.graalvm.vm.util.exception;

public class Messages {
    // @formatter:off
	public static final ExceptionId UNKNOWN		= new ExceptionId("UTIL0001E", "Unknown error");
	public static final ExceptionId NO_RESOURCE	= new ExceptionId("UTIL0002W", "Resource \"{1}\" not found for class {0}");
	public static final ExceptionId GET_UNSAFE_FAIL	= new ExceptionId("UTIL0003E", "Error retrieving Unsafe instance");
	// @formatter:on
}
