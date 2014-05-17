package it.massimobarbieri.mbloan.controller.rest_support;

import org.springframework.hateoas.ResourceSupport;

public class RestResponse extends ResourceSupport {

	private static final String STATUS_SUCCESS = "success";
	private static final String STATUS_ERROR = "error";

	private String status;
	private long objectId;
	private String errorMessage;

	public RestResponse(String status, long objectId, String errorMessage) {
		this.status = status;
		this.objectId = objectId;
		this.errorMessage = errorMessage;
	}

	public static RestResponse Success(long objectId) {
		return new RestResponse(STATUS_SUCCESS, objectId, "");
	}

	public static RestResponse Error(String errorMessage) {
		return new RestResponse(STATUS_ERROR, 0, errorMessage);
	}

	public String getStatus() {
		return status;
	}

	public long getObjectId() {
		return objectId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}