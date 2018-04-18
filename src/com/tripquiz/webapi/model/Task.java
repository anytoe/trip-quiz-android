package com.tripquiz.webapi.model;

import java.io.Serializable;

import com.tripquiz.webapi.model.Challenge.State;
import com.tripquiz.webapi.model.Challenge.TaskType;

public class Task implements Serializable {

	private static final long serialVersionUID = -4835535075856150154L;

	private EntityKey key;
	private TaskType taskType;
	private State taskStatus;
	private String description;
	private QuestionType solutionType;
	private String solutionHint;
	private int repeat;
	private int repeatCount;

	public Task(EntityKey key, TaskType taskType, State taskStatus, String description, QuestionType solutionType, String solutionHint, int repeat,
			int repeatCount) {
		super();
		this.key = key;
		this.taskType = taskType;
		this.taskStatus = taskStatus;
		this.description = description;
		this.solutionType = solutionType;
		this.solutionHint = solutionHint;
		this.repeat = repeat;
		this.repeatCount = repeatCount;
	}

	public boolean isSolved() {
		return taskStatus == State.SOLVED;
	}

	public State getTaskState() {
		return taskStatus;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public String getDescription() {
		return description;
	}

	public int getQuantityMax() {
		return repeat;
	}

	public EntityKey getKey() {
		return key;
	}

	public int getQuantityCount() {
		return repeatCount;
	}

	public boolean isRepeatableTask() {
		return repeat > 1;
	}

	public QuestionType getSolutionType() {
		return solutionType;
	}

	public String getSolutionHint() {
		return solutionHint;
	}

	public enum QuestionType {
		Undefined, Natural, Real, Text, Date, DateDayMonth, DateMonthYear
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + repeat;
		result = prime * result + repeatCount;
		result = prime * result + ((solutionHint == null) ? 0 : solutionHint.hashCode());
		result = prime * result + ((solutionType == null) ? 0 : solutionType.hashCode());
		result = prime * result + ((taskStatus == null) ? 0 : taskStatus.hashCode());
		result = prime * result + ((taskType == null) ? 0 : taskType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (repeat != other.repeat)
			return false;
		if (repeatCount != other.repeatCount)
			return false;
		if (solutionHint == null) {
			if (other.solutionHint != null)
				return false;
		} else if (!solutionHint.equals(other.solutionHint))
			return false;
		if (solutionType != other.solutionType)
			return false;
		if (taskStatus != other.taskStatus)
			return false;
		if (taskType != other.taskType)
			return false;
		return true;
	}

}
