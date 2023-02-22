package com.utils.gradle.generators.data;

import java.io.PrintStream;

import com.utils.string.StrUtils;

public class LineCountData {

	private int sourceCodeLineCount;
	private int testCodeLineCount;

	public void printCsvRow(
			final String projectName,
			final PrintStream printStream) {

		printStream.print(projectName);
		printStream.print(',');
		printStream.print(sourceCodeLineCount);
		printStream.print(',');
		printStream.print(testCodeLineCount);
		printStream.print(',');
		final int totalCodeLineCount = sourceCodeLineCount + testCodeLineCount;
		printStream.print(totalCodeLineCount);
		printStream.println();
	}

	public void incrementAll(
			final LineCountData lineCountData) {

		incrementSourceCodeLineCount(lineCountData.sourceCodeLineCount);
		incrementTestCodeLineCount(lineCountData.testCodeLineCount);
	}

	public void incrementSourceCodeLineCount(
			final int increment) {
		sourceCodeLineCount += increment;
	}

	public void incrementTestCodeLineCount(
			final int increment) {
		testCodeLineCount += increment;
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
