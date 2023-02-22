package com.utils.gradle;

import org.junit.jupiter.api.Test;

import com.utils.io.PathUtils;
import com.utils.log.Logger;

class AppStartGradleSrcManTest {

	@Test
	void work() {

		final String projectPathString;
		final int input = Integer.parseInt("11");
		if (input == 1) {
			projectPathString = "C:\\IVI\\Prog\\JavaGradle\\UtilsManager\\" +
					"Projects\\Personal\\UtilsManagerAllModules\\UtilsManagerAllModules";

		} else if (input == 11) {
			projectPathString = "C:\\IVI\\Prog\\JavaGradle\\Scripts\\General\\Archiver\\" +
					"Projects\\Personal\\ArchiverAllModules\\ArchiverAllModules";

		} else {
			throw new RuntimeException();
		}

		final String rootOutputFolderPathString =
				PathUtils.computePath(PathUtils.createRootPath(), "tmp", "GradleSrcMan");
		Logger.printStatus("Output folder path:" + System.lineSeparator() + rootOutputFolderPathString);

		AppStartGradleSrcMan.work(projectPathString, rootOutputFolderPathString);
	}
}
