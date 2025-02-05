package com.personal.utils.gradle.sub_prj;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.utils.log.Logger;

class GradleSubProjectUtilsTest {

	@Test
	void testExecuteSubProjectDependencyTreeCommand() {

		final String projectPathString = "C:\\IVI\\Prog\\JavaGradle\\Scripts\\General\\" +
				"UtilsManager\\Projects\\Personal\\UtilsManagerAllModules\\UtilsManagerAllModules";
		final List<String> lineList =
				GradleSubProjectUtils.executeSubProjectDependencyTreeCommand(projectPathString);
		Assertions.assertNotNull(lineList);

		Logger.printNewLine();
		for (final String line : lineList) {
			Logger.printLine(line);
		}
	}
}
