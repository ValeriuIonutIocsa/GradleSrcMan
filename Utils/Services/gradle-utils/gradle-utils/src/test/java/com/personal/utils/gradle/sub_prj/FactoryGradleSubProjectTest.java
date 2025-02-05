package com.personal.utils.gradle.sub_prj;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.log.Logger;

class FactoryGradleSubProjectTest {

	@Test
	void testNewInstance() {

		final String projectPathString = "C:\\IVI\\Prog\\JavaGradle\\Scripts\\General\\" +
				"UtilsManager\\Projects\\Personal\\UtilsManagerAllModules\\UtilsManagerAllModules";
		final Map<String, GradleSubProject> gradleSubProjectsByPathMap = new HashMap<>();
		FactoryGradleSubProject.newInstance(projectPathString, gradleSubProjectsByPathMap);

		Assertions.assertFalse(gradleSubProjectsByPathMap.isEmpty());

		Logger.printNewLine();
		for (final GradleSubProject gradleSubProject : gradleSubProjectsByPathMap.values()) {
			Logger.printLine(gradleSubProject);
		}
	}
}
