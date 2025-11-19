package com.utils.gradle.settings;

import java.util.List;

import com.utils.string.StrUtils;

public class GradleSrcManSettings {

	private final List<String> projectPathStringList;
	private final String outputFolderPathString;

	GradleSrcManSettings(
			final List<String> projectPathStringList,
			final String outputFolderPathString) {

		this.projectPathStringList = projectPathStringList;
		this.outputFolderPathString = outputFolderPathString;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public List<String> getProjectPathStringList() {
		return projectPathStringList;
	}

	public String getOutputFolderPathString() {
		return outputFolderPathString;
	}
}
