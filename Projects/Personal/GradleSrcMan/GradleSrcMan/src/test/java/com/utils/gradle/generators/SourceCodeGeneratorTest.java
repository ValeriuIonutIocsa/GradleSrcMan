package com.utils.gradle.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class SourceCodeGeneratorTest {

	@Test
	void testComputePathInZip() {

		final Path path = Paths.get("C:\\IVI\\Conti\\Projects\\AllocCtrl\\AllocCtrl\\AllocCtrl");
		final String pathInZip = SourceCodeGenerator.computePathInZip(path);
		assertEquals("Projects\\AllocCtrl\\AllocCtrl\\AllocCtrl", pathInZip);
	}
}
