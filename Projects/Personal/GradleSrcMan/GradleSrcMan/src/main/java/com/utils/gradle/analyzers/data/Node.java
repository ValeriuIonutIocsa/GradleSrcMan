package com.utils.gradle.analyzers.data;

import com.utils.string.StrUtils;

public class Node {

	private final int index;
	private final String projectName;
	private final String outputText;

	public Node(
			final int index,
			final String projectName,
			final String outputText) {

		this.index = index;
		this.projectName = projectName;
		this.outputText = outputText;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public int getIndex() {
		return index;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getOutputText() {
		return outputText;
	}
}
