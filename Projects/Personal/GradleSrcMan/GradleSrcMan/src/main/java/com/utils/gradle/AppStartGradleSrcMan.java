package com.utils.gradle;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandlerLinesCollect;
import com.utils.log.Logger;

final class AppStartGradleSrcMan {

	private AppStartGradleSrcMan() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();

		final Map<String, String> cliArgsByNameMap = new HashMap<>();
		CliUtils.fillCliArgsByNameMap(args, cliArgsByNameMap);

		final String debugModeString = cliArgsByNameMap.get("debug");
		final boolean debugMode = Boolean.parseBoolean(debugModeString);
		Logger.setDebugMode(debugMode);

		final GradleSrcManSettings gradleSrcManSettings = FactoryGradleSrcManSettings.newInstance();
		if (gradleSrcManSettings != null) {

			final String outputFolderPathString =
					PathUtils.computePath(PathUtils.createRootPath(), "tmp", "GradleSrcMan");
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

					final List<String> subProjectDependencyTreeOutputLineList =
							executeSubProjectDependencyTreeCommand(valProjectPathString);
					if (subProjectDependencyTreeOutputLineList != null) {

						final Set<String> dependencyTreeProjectPathStringSet = new LinkedHashSet<>();
						dependencyTreeProjectPathStringSet.add(valProjectPathString);
						final List<Node> nodeList = new ArrayList<>();
						final Set<String> dependencySet = new LinkedHashSet<>();
						ProjectDependenciesAnalyzer.work(
								valProjectPathString, subProjectDependencyTreeOutputLineList,
								dependencyTreeProjectPathStringSet, nodeList, dependencySet);

						final String outputFolderPathString =
								PathUtils.computeAbsolutePath(null, null, rootOutputFolderPathString, projectName);
						FactoryFolderCreator.getInstance().createDirectories(outputFolderPathString, true);

						SourceCodeGenerator.work(projectName, valProjectPathString,
								dependencyTreeProjectPathStringSet, outputFolderPathString);

						DependencyTreeGenerator.work(
								projectName, nodeList, dependencySet, outputFolderPathString);
					}
				}
			}

		} catch (final Exception exc) {
			Logger.printError("failed to run Gradle source code manager " +
					"on project " + projectName);
			Logger.printException(exc);
		}
	}

	private static List<String> executeSubProjectDependencyTreeCommand(
			final String projectPathString) {

		List<String> subProjectDependencyTreeOutputLineList = null;
		try {
			final String[] command = { "cmd", "/c", "gradlew.bat", "subProjectDependencyTree" };
			final ProcessBuilder processBuilder = new ProcessBuilder(command)
					.directory(new File(projectPathString))
					.redirectErrorStream(true);
			final Process process = processBuilder.start();
			final ReadBytesHandlerLinesCollect readBytesHandlerLinesCollect = new ReadBytesHandlerLinesCollect();
			new InputStreamReaderThread("subProjectDependencyTree",
					process.getInputStream(), StandardCharsets.UTF_8, readBytesHandlerLinesCollect).start();
			process.waitFor();
			subProjectDependencyTreeOutputLineList = readBytesHandlerLinesCollect.getLineList();

		} catch (final Exception exc) {
			Logger.printException(exc);
		}
		return subProjectDependencyTreeOutputLineList;
	}
}
