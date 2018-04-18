package com.tripquiz.webgeneric;

public enum Event {
	None,
	// errors
	Error,
	// functional events
	NoChange, Progress, PartialComplete, Complete
}