package com.utils.gradle.settings;

import java.util.List;

import com.utils.string.StrUtils;

public class GradleSrcManSettings {

	private final List<String> projectPathStringList;

	GradleSrcManSettings(
			final List<String> projectPathStringList) {

		this.projectPathStringList = projectPathStringList;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public List<String> getProjectPathStringList() {
		return projectPathStringList;
	}
}
