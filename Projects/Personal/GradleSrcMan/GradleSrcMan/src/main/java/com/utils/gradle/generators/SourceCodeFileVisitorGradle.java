package com.utils.gradle.generators;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.utils.log.Logger;
import com.utils.string.StrUtils;

class SourceCodeFileVisitorGradle extends SimpleFileVisitor<Path> {

	private final Path rootZipPath;

	private Path sourcePath;

	SourceCodeFileVisitorGradle(
			final Path rootZipPath) {

		this.rootZipPath = rootZipPath;
	}

	@Override
	public FileVisitResult preVisitDirectory(
			final Path dir,
			final BasicFileAttributes attrs) throws IOException {

		final FileVisitResult fileVisitResult;
		final String dirPathString = dir.toString().replace('\\', '/');
		if (dirPathString.endsWith("/.gradle") ||
				dirPathString.endsWith("/.settings") ||
				dirPathString.endsWith("/build") ||
				dirPathString.endsWith("/out") ||
				dirPathString.endsWith("/bin")) {
			fileVisitResult = FileVisitResult.SKIP_SUBTREE;

		} else {
			if (sourcePath == null) {
				sourcePath = dir;

			} else {
				final Path resolvedPath = createResolvedPath(dir, rootZipPath);
				Files.createDirectories(resolvedPath);
			}
			fileVisitResult = FileVisitResult.CONTINUE;
		}
		return fileVisitResult;
	}

	@Override
	public FileVisitResult visitFile(
			final Path file,
			final BasicFileAttributes attrs) {

		final String filePathString = file.toString().replace('\\', '/');
		if (!filePathString.endsWith("/.classpath") &&
				!filePathString.endsWith("/.project")) {

			final Path resolvedPath = createResolvedPath(file, rootZipPath);

			try {
				final Path dstFolderPath = resolvedPath.getParent();
				if (dstFolderPath != null) {
					Files.createDirectories(dstFolderPath);
				}

				final List<CopyOption> copyOptionList = new ArrayList<>();
				copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
				final CopyOption[] copyOptionArray = copyOptionList.toArray(new CopyOption[] {});

				Files.copy(file, resolvedPath, copyOptionArray);

			} catch (final Exception exc) {
				Logger.printException(exc);
			}
		}
		return FileVisitResult.CONTINUE;
	}

	private Path createResolvedPath(
			final Path path,
			final Path rootZipPath) {

		final String relativePathString = sourcePath.relativize(path).toString();
		return rootZipPath.resolve(relativePathString);
	}

	@Override
	public String toString() {
		return StrUtils.reflectionToString(this);
	}
}
