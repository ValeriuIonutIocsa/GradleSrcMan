package com.utils.gradle.generators;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import com.utils.gradle.analyzers.data.Node;
import com.utils.io.PathUtils;
import com.utils.io.StreamUtils;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.log.Logger;

public final class DependencyTreeGenerator {

	private DependencyTreeGenerator() {
	}

	public static void work(
			final String projectName,
			final List<Node> nodeList,
			final Set<String> dependencySet,
			final String outputFolderPathString) {

		try {
			Logger.printProgress("generating dependency tree image for project: " + projectName);

			final String dotFilePathString =
					PathUtils.computePath(outputFolderPathString, projectName + "_DependencyTree.txt");
			try (PrintStream printStream = StreamUtils.openPrintStream(dotFilePathString)) {

				printStream.println("digraph {");
				printStream.println("subgraph cluster_0 {");

				final String title = projectName + " Dependencies";
				printStream.print("label=");
				printStream.print('"');
				printStream.print(title);
				printStream.print('"');
				printStream.print(';');
				printStream.println();

				for (final Node node : nodeList) {

					final String outputText = node.getOutputText();
					printStream.println(outputText);
				}

				for (final String dependency : dependencySet) {
					printStream.println(dependency);
				}

				printStream.print('}');
				printStream.println();
				printStream.print('}');
				printStream.println();
			}

			runGraphVizCommand(dotFilePathString);

			FactoryFileDeleter.getInstance().deleteFile(dotFilePathString, true, false);

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
	}

	private static void runGraphVizCommand(
			final String dotFilePathString) throws Exception {

		final String outputFilePathString = PathUtils.computePathWoExt(dotFilePathString) + ".png";
		final String[] command = { "cmd", "/c", "dot", "-Tpng",
				"-o" + outputFilePathString, dotFilePathString };
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		final Process process = processBuilder.start();
		process.waitFor();
	}
}
