package com.personal.utils.gradle.sub_prj;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.utils.io.PathUtils;
import com.utils.io.processes.InputStreamReaderThread;
import com.utils.io.processes.ReadBytesHandlerLinesCollect;
import com.utils.log.Logger;

public final class GradleSubProjectUtils {

	private GradleSubProjectUtils() {
	}

	public static List<String> executeSubProjectDependencyTreeCommand(
			final String projectPathString) {

		List<String> subProjectDependencyTreeOutputLineList = null;
		try {
			final List<String> commandList = new ArrayList<>();
			final String gradleWrapperPathString = PathUtils.computePath(projectPathString, "gradlew.bat");
			commandList.add(gradleWrapperPathString);
			commandList.add("subProjectDependencyTree");
			commandList.add("--console=plain");

			Logger.printProgress("executing command:");
			Logger.printLine(StringUtils.join(commandList, " "));

			final ProcessBuilder processBuilder = new ProcessBuilder()
					.command(commandList)
					.directory(new File(projectPathString))
					.redirectErrorStream(true);
			final Process process = processBuilder.start();

			final ReadBytesHandlerLinesCollect readBytesHandlerLinesCollect = new ReadBytesHandlerLinesCollect();
			new InputStreamReaderThread("subProjectDependencyTree",
					process.getInputStream(), StandardCharsets.UTF_8, readBytesHandlerLinesCollect).start();

			process.waitFor();

			subProjectDependencyTreeOutputLineList = readBytesHandlerLinesCollect.getLineList();

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
		return subProjectDependencyTreeOutputLineList;
	}
}
