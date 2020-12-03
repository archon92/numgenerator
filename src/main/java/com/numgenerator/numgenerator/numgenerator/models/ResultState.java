package com.numgenerator.numgenerator.numgenerator.models;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author adithyar
 */

@Getter
@RequiredArgsConstructor
public enum ResultState {
	SUCCESS("SUCCESS"),
	IN_PROGRESS("IN_PROGRESS"),
	ERROR("ERROR"),
	INVALID_TASK("INVALID_TASK_UDID");

	private final String state;
}
