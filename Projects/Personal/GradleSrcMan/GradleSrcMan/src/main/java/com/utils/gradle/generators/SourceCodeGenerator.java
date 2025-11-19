package com.utils.gradle.generators;

import java.io.PrintStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.utils.csv.AbstractCsvWriter;
import com.utils.gradle.generators.data.FilePathLengthData;
import com.utils.gradle.generators.data.FilePaths;
import com.utils.gradle.generators.data.LineCountData;
import com.utils.io.IoUtils;
import com.utils.io.PathUtils;
import com.utils.io.file_deleters.FactoryFileDeleter;
import com.utils.log.Logger;

public final class SourceCodeGenerator {

	private SourceCodeGenerator() {
	}

	public static void work(
			final String projectName,
			final String projectPathString,
			final Set<String> dependencyTreeProjectPathStringSet,
			final String outputFolderPathString) {

		Logger.printProgress("generating source code for project: " + projectName);

		generateGradleSourceCode(
				projectName, projectPathString,
				dependencyTreeProjectPathStringSet, outputFolderPathString);

		final Map<String, FilePaths> filePathsByProjectMap = new LinkedHashMap<>();
		generateCombinedSourceCode(
				projectName, dependencyTreeProjectPathStringSet,
				filePathsByProjectMap, outputFolderPathString);

		checkForFileNameDuplicates(filePathsByProjectMap);

		generateLineCountsFile(projectName, filePathsByProjectMap, outputFolderPathString);
		generateFilePathLengthsFile(projectName, filePathsByProjectMap, outputFolderPathString);
	}

	private static void generateGradleSourceCode(
			final String projectName,
			final String projectPathString,
			final Set<String> dependencyTreeProjectPathStringSet,
			final String outputFolderPathString) {

		final String sourceCodeZipFilePathString =
				PathUtils.computePath(outputFolderPathString, projectName + "_SourcecodeGradle.zip");
		FactoryFileDeleter.getInstance().deleteFile(sourceCodeZipFilePathString, true, false);
		try (FileSystem zipFileSystem = createZipFileSystem(sourceCodeZipFilePathString)) {

			final Path zipFileRootPath = zipFileSystem.getPath("/");

			final Path projectPath = Paths.get(projectPathString);
			copyTopLevelFile(projectPath, zipFileRootPath, "common_build.gradle");
			copyTopLevelFile(projectPath, zipFileRootPath, "common_settings.gradle");
			copyTopLevelFile(projectPath, zipFileRootPath, ".gitattributes");

			for (final String dependencyTreeProjectPathString : dependencyTreeProjectPathStringSet) {

				final Path dependencyTreeProjectPath = Paths.get(dependencyTreeProjectPathString);
				final String pathInZipString = computePathInZip(dependencyTreeProjectPath);
				final Path pathInZipFile = zipFileRootPath.resolve(pathInZipString);
				if (Files.notExists(pathInZipFile)) {
					Files.createDirectories(pathInZipFile);
				}

				final SourceCodeFileVisitorGradle sourceCodeFileVisitorGradle =
						new SourceCodeFileVisitorGradle(pathInZipFile);
				Files.walkFileTree(dependencyTreeProjectPath, sourceCodeFileVisitorGradle);
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
	}

	private static void generateCombinedSourceCode(
			final String projectName,
			final Set<String> dependencyTreeProjectPathStringSet,
			final Map<String, FilePaths> filePathsByProjectMap,
			final String outputFolderPathString) {

		final String sourceCodeZipFilePathString =
				PathUtils.computePath(outputFolderPathString, projectName + "_SourcecodeCombined.zip");
		FactoryFileDeleter.getInstance().deleteFile(sourceCodeZipFilePathString, true, false);
		try (FileSystem zipFileSystem = createZipFileSystem(sourceCodeZipFilePathString)) {

			final Path rootZipPath = zipFileSystem.getPath("/");

			for (final String dependencyTreeProjectPathString : dependencyTreeProjectPathStringSet) {

				final String srcFolderPathString =
						PathUtils.computePath(dependencyTreeProjectPathString, "src");
				if (IoUtils.directoryExists(srcFolderPathString)) {

					final SourceCodeFileVisitorCombined sourceCodeFileVisitorCombined =
							new SourceCodeFileVisitorCombined(rootZipPath);
					Files.walkFileTree(Paths.get(srcFolderPathString), sourceCodeFileVisitorCombined);

					final FilePaths filePaths = sourceCodeFileVisitorCombined.getFilePaths();
					filePathsByProjectMap.put(dependencyTreeProjectPathString, filePaths);
				}
			}

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
	}

	private static void checkForFileNameDuplicates(
			final Map<String, FilePaths> filePathsByProjectMap) {

		final Map<String, List<String>> filePathsByFileNameMap = new HashMap<>();
		for (final Map.Entry<String, FilePaths> mapEntry : filePathsByProjectMap.entrySet()) {

			final FilePaths filePaths = mapEntry.getValue();
			filePaths.fillFilePathsByFileNameMap(filePathsByFileNameMap);
		}

		for (final Map.Entry<String, List<String>> mapEntry : filePathsByFileNameMap.entrySet()) {

			final String fileName = mapEntry.getKey();
			final List<String> filePathStringList = mapEntry.getValue();
			if (filePathStringList.size() > 1) {
				Logger.printWarning("found multiple files with the name \"" + fileName + "\":" +
						System.lineSeparator() + StringUtils.join(filePathStringList, System.lineSeparator()));
			}
		}
	}

	private static void generateLineCountsFile(
			final String projectName,
			final Map<String, FilePaths> filePathsByProjectMap,
			final String outputFolderPathString) {

		final String lineCountsFilePathString =
				PathUtils.computePath(outputFolderPathString, projectName + "_ReportLineCounts.csv");
		new AbstractCsvWriter(projectName + " line counts", lineCountsFilePathString) {

			@Override
			protected void write(
					final PrintStream printStream) {

				printStream.println("Project Name,Source Code Line Count,Test Code Line Count,Total Line Count");
				final LineCountData totalLineCountData = new LineCountData();
				for (final Map.Entry<String, FilePaths> mapEntry : filePathsByProjectMap.entrySet()) {

					final String dependencyProjectPathString = mapEntry.getKey();
					final String dependencyProjectName = PathUtils.computeFileName(dependencyProjectPathString);

					final FilePaths filePaths = mapEntry.getValue();
					final LineCountData lineCountData = filePaths.createLineCountData();
					totalLineCountData.incrementAll(lineCountData);
					lineCountData.printCsvRow(dependencyProjectName, printStream);
				}
				totalLineCountData.printCsvRow(projectName, printStream);
			}
		}.writeCsv();
	}

	private static void generateFilePathLengthsFile(
			final String projectName,
			final Map<String, FilePaths> filePathsByProjectMap,
			final String outputFolderPathString) {

		final String filePathLengthsFilePathString =
				PathUtils.computePath(outputFolderPathString, projectName + "_ReportFilePathLengths.csv");
		new AbstractCsvWriter(projectName + " file path lengths", filePathLengthsFilePathString) {

			@Override
			protected void write(
					final PrintStream printStream) {

				printStream.println("Source Code File Path,File Path Length");
				final List<FilePathLengthData> filePathLengthDataList = new ArrayList<>();
				for (final Map.Entry<String, FilePaths> mapEntry : filePathsByProjectMap.entrySet()) {

					final String dependencyProjectPathString = mapEntry.getKey();
					final String dependencyProjectName = PathUtils.computeFileName(dependencyProjectPathString);

					final FilePaths filePaths = mapEntry.getValue();
					filePaths.fillFilePathLengthDataList(dependencyProjectName, filePathLengthDataList);
				}

				filePathLengthDataList.sort(Comparator.comparing(
						filePathLengthData -> filePathLengthData.getFilePathString().length(),
						Comparator.reverseOrder()));

				for (final FilePathLengthData filePathLengthData : filePathLengthDataList) {
					filePathLengthData.printCsvRow(printStream);
				}
			}
		}.writeCsv();
	}

	private static void copyTopLevelFile(
			final Path projectPath,
			final Path zipFileRootPath,
			final String fileName) {

		final String folderPathString =
				projectPath.getParent().getParent().getParent().getParent().toString();
		final String srcFilePathString = PathUtils.computePath(folderPathString, fileName);
		final Path dstFilePath = zipFileRootPath.resolve(fileName);

		try {
			final Path dstFolderPath = dstFilePath.getParent();
			if (dstFolderPath != null) {
				Files.createDirectories(dstFolderPath);
			}

			final List<CopyOption> copyOptionList = new ArrayList<>();
			copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
			final CopyOption[] copyOptionArray = copyOptionList.toArray(new CopyOption[] {});

			final Path srcFilePath = Paths.get(srcFilePathString);
			Files.copy(srcFilePath, dstFilePath, copyOptionArray);

		} catch (final Throwable throwable) {
			Logger.printThrowable(throwable);
		}
	}

	private static FileSystem createZipFileSystem(
			final String zipFilePathString) throws Exception {

		final URI uri = URI.create("jar:file:" + Paths.get(zipFilePathString).toUri().getPath());
		final Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		return FileSystems.newFileSystem(uri, env);
	}

	static String computePathInZip(
			final Path path) {

		final String pathString = path.toString();

		final Path parentPath = path.getParent().getParent().getParent().getParent();
		final String parentPathString = parentPath.toString();

		return pathString.substring(parentPathString.length() + 1);
	}
}
