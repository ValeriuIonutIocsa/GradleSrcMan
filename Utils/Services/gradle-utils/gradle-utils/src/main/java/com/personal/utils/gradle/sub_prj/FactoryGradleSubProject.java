package com.personal.utils.gradle.sub_prj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utils.io.PathUtils;
import com.utils.log.Logger;

public final class FactoryGradleSubProject {

	private FactoryGradleSubProject() {
	}

	public static void newInstance(
			final String projectPathString,
			final Map<String, GradleSubProject> gradleSubProjectsByPathMap) {

		try {
			final GradleSubProject rootGradleSubProject = new GradleSubProject(projectPathString);
			gradleSubProjectsByPathMap.put(projectPathString, rootGradleSubProject);

			final List<String> lineList =
					GradleSubProjectUtils.executeSubProjectDependencyTreeCommand(projectPathString);
			if (lineList != null) {

				final Map<Integer, String> levelsInTreeMap = new HashMap<>();
				levelsInTreeMap.put(-1, projectPathString);

				final String projectRootFolderPathString = PathUtils.computeParentPath(projectPathString, 4);
				for (final String line : lineList) {

					final String subProjectPathString = line.trim();
					if (subProjectPathString.startsWith(projectRootFolderPathString)) {

						final GradleSubProject gradleSubProject =
								new GradleSubProject(subProjectPathString);
						gradleSubProjectsByPathMap.put(subProjectPathString, gradleSubProject);

						final int levelInTree = line.length() - subProjectPathString.length();
						levelsInTreeMap.put(levelInTree, subProjectPathString);

						final int parentLevel = levelInTree - 1;
						final String parentSubProjectPathString = levelsInTreeMap.get(parentLevel);

						GradleSubProject parentGradleSubProject =
								gradleSubProjectsByPathMap.get(parentSubProjectPathString);
						if (parentGradleSubProject == null) {
							parentGradleSubProject = rootGradleSubProject;
						}
						parentGradleSubProject.getDependencyPathSet().add(subProjectPathString);
					}
				}
			}

		} catch (final Exception exc) {
			Logger.printException(exc);
		}
	}
}
