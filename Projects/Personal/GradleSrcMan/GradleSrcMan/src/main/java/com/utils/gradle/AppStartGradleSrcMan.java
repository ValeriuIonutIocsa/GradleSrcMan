package com.utils.gradle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.utils.cli.CliUtils;
import com.utils.gradle.analyzers.ProjectDependenciesAnalyzer;
import com.utils.gradle.analyzers.data.Node;
import com.utils.gradle.generators.DependencyTreeGenerator;
import com.utils.gradle.generators.SourceCodeGenerator;
import com.utils.gradle.settings.FactoryGradleSrcManSettings;
import com.utils.gradle.settings.GradleSrcManSettings;
import com.utils.io.IoUtils;
import com.utils.io.PathUtils;
import com.utils.io.folder_creators.FactoryFolderCreator;
import com.utils.log.Logger;

final class AppStartGradleSrcMan {

	private AppStartGradleSrcMan() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();
		Logger.printProgress("starting GradleSrcMan");

		final Map<String, String> cliArgsByNameMap = new HashMap<>();
		CliUtils.fillCliArgsByNameMap(args, cliArgsByNameMap);

		final String debugModeString = cliArgsByNameMap.get("debug");
		final boolean debugMode = Boolean.parseBoolean(debugModeString);
		Logger.setDebugMode(debugMode);

		final GradleSrcManSettings gradleSrcManSettings = FactoryGradleSrcManSettings.newInstance();
		if (gradleSrcManSettings != null) {

			final String outputFolderPathString = gradleSrcManSettings.getOutputFolderPathString();
			Logger.printStatus("Output folder path:" + System.lineSeparator() + outputFolderPathString);

			final List<String> projectPathStringList = gradleSrcManSettings.getProjectPathStringList();
			for (final String projectPathString : projectPathStringList) {
				work(projectPathString, outputFolderPathString);
			}
		}

		Logger.printNewLine();
		Logger.printFinishMessage(start);
	}

	static void work(
			final String projectPathString,
			final String rootOutputFolderPathString) {

		final String projectName = PathUtils.computeFileName(projectPathString);
		try {
			Logger.printNewLine();
			Logger.printProgress("running Gradle source code manager on project " + projectName);

			if (!IoUtils.directoryExists(projectPathString)) {
				Logger.printError("project does not exist:" + System.lineSeparator() + projectPathString);

			} else {
				final String valProjectPathString = PathUtils.computePath(projectPathString);
				if (valProjectPathString != null) {

					final Set<String> dependencyTreeProjectPathStringSet = new LinkedHashSet<>();
					dependencyTreeProjectPathStringSet.add(valProjectPathString);
					final List<Node> nodeList = new ArrayList<>();
					final Set<String> dependencySet = new LinkedHashSet<>();
					ProjectDependenciesAnalyzer.work(valProjectPathString,
							dependencyTreeProjectPathStringSet, nodeList, dependencySet);

					final String outputFolderPathString =
							PathUtils.computeAbsolutePath(null, null, rootOutputFolderPathString, projectName);
					FactoryFolderCreator.getInstance().createDirectories(outputFolderPathString, true, false);

					SourceCodeGenerator.work(projectName, valProjectPathString,
							dependencyTreeProjectPathStringSet, outputFolderPathString);

					DependencyTreeGenerator.work(
							projectName, nodeList, dependencySet, outputFolderPathString);
				}
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to run Gradle source code manager " +
					"on project " + projectName);
			Logger.printThrowable(throwable);
		}
	}
}
