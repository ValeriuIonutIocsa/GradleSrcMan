package com.personal.utils.gradle.sub_prj;

import java.util.LinkedHashSet;
import java.util.Set;

import com.utils.string.StrUtils;

public class GradleSubProject {

	private final String path;

	private final Set<String> dependencyPathSet;

	GradleSubProject(
			final String path) {

		this.path = path;

		dependencyPathSet = new LinkedHashSet<>();
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public String getPath() {
		return path;
	}

	public Set<String> getDependencyPathSet() {
		return dependencyPathSet;
	}
}
