package com.personal.utils.gradle.sub_prj;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Strings;

import com.utils.log.Logger;
import com.utils.string.StrUtils;

public final class FactoryGradleSubProject {

	private FactoryGradleSubProject() {
	}

	public static void newInstance(
			final String rootProjectPathString,
			final Map<String, GradleSubProject> gradleSubProjectsByPathMap) {

		try {
			final List<String> lineList =
					GradleSubProjectUtils.executeSubProjectDependencyTreeCommand(rootProjectPathString);
			if (lineList != null) {

				GradleSubProject gradleSubProject = null;
				for (final String line : lineList) {

					final String projectPathString = StrUtils.removePrefix(line, "project: ");
					if (!Strings.CS.equals(projectPathString, line)) {

						gradleSubProject = new GradleSubProject(projectPathString);
						gradleSubProjectsByPathMap.put(projectPathString, gradleSubProject);

					} else {
						if (gradleSubProject != null) {

							final String subProjectPathString = StrUtils.removePrefix(line, "subProject: ");
							if (!Strings.CS.equals(subProjectPathString, line)) {

								gradleSubProject.getDependencyPathSet().add(subProjectPathString);
							}
						}
					}
				}
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
	}
}
