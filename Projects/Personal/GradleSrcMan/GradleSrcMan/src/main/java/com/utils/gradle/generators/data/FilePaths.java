package com.utils.gradle.generators.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.utils.io.IoUtils;
import com.utils.io.PathUtils;
import com.utils.string.StrUtils;

public class FilePaths {

	private final List<String> sourceCodeFilePathStringList;
	private final List<String> testCodeFilePathStringList;

	public FilePaths() {

		sourceCodeFilePathStringList = new ArrayList<>();
		testCodeFilePathStringList = new ArrayList<>();
	}

	public void addSourceCodeFilePath(
			final String sourceCodeFilePathString) {

		sourceCodeFilePathStringList.add(sourceCodeFilePathString);
	}

	public void addTestCodeFilePath(
			final String testCodeFilePathString) {

		testCodeFilePathStringList.add(testCodeFilePathString);
	}

	public void fillFilePathsByFileNameMap(
			final Map<String, List<String>> filePathsByFileNameMap) {

		for (final String sourceCodeFilePathString : sourceCodeFilePathStringList) {
			fillFilePathsByFileNameMap(sourceCodeFilePathString, filePathsByFileNameMap);
		}
		for (final String testCodeFilePathString : testCodeFilePathStringList) {
			fillFilePathsByFileNameMap(testCodeFilePathString, filePathsByFileNameMap);
		}
	}

	private static void fillFilePathsByFileNameMap(
			final String testCodeFilePathString,
			final Map<String, List<String>> filePathsByFileNameMap) {

		final String fileName = PathUtils.computeFileName(testCodeFilePathString);
		final List<String> filePathStringList =
				filePathsByFileNameMap.computeIfAbsent(fileName, k -> new ArrayList<>());
		filePathStringList.add(testCodeFilePathString);
	}

	public LineCountData createLineCountData() {

		final LineCountData lineCountData = new LineCountData();
		for (final String sourceCodeFilePathString : sourceCodeFilePathStringList) {

			final int lineCount = IoUtils.computeLineCount(sourceCodeFilePathString);
			lineCountData.incrementSourceCodeLineCount(lineCount);
		}
		for (final String testCodeFilePathString : testCodeFilePathStringList) {

			final int lineCount = IoUtils.computeLineCount(testCodeFilePathString);
			lineCountData.incrementTestCodeLineCount(lineCount);
		}
		return lineCountData;
	}

	public void fillFilePathLengthDataList(
			final String projectName,
			final List<FilePathLengthData> filePathLengthDataList) {

		for (final String sourceCodeFilePathString : sourceCodeFilePathStringList) {

			final FilePathLengthData filePathLengthData =
					new FilePathLengthData(projectName, sourceCodeFilePathString);
			filePathLengthDataList.add(filePathLengthData);
		}
		for (final String testCodeFilePathString : testCodeFilePathStringList) {

			final FilePathLengthData filePathLengthData =
					new FilePathLengthData(projectName, testCodeFilePathString);
			filePathLengthDataList.add(filePathLengthData);
		}
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
