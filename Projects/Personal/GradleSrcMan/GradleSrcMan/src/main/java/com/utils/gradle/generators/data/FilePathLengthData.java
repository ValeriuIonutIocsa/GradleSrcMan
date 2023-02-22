package com.utils.gradle.generators.data;

import java.io.PrintStream;

import com.utils.string.StrUtils;

public class FilePathLengthData {

	private final String projectName;
	private final String filePathString;

	public FilePathLengthData(
            final String projectName,
            final String filePathString) {

		this.projectName = projectName;
		this.filePathString = filePathString;
	}

	public void printCsvRow(
            final PrintStream printStream) {

		printStream.print(projectName);
		printStream.print(',');
		printStream.print(filePathString);
		printStream.print(',');
		printStream.print(filePathString.length());
		printStream.println();
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}

	public String getFilePathString() {
		return filePathString;
	}
}
