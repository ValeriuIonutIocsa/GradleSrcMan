package com.utils.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

public class DynamicTestSuite {

	public enum Mode {
		ALL, SELECTED
	}

	private final Mode mode;
	private final Executable executable;
	private final DynamicTestOptions<?>[] dynamicTestOptionsArray;

	public DynamicTestSuite(
			final Mode mode,
			final Executable executable,
			final DynamicTestOptions<?>... dynamicTestOptionsArray) {

		this.mode = mode;
		this.executable = executable;
		this.dynamicTestOptionsArray = dynamicTestOptionsArray;
	}

	public List<DynamicTest> createDynamicTestList() {

		if (executable == null) {

			System.err.println("ERROR - no executable is configured");
			Assertions.fail();
		}

		final List<DynamicTest> dynamicTestList = new ArrayList<>();
		if (mode == Mode.ALL) {
			addDynamicTestsRec(0, dynamicTestList);
		} else if (mode == Mode.SELECTED) {
			addDynamicTest(dynamicTestList);
		} else {
			System.err.println("ERROR - invalid mode");
			Assertions.fail();
		}
		return dynamicTestList;
	}

	private void addDynamicTestsRec(
			final int optionsIndex,
			final List<DynamicTest> dynamicTestList) {

		if (optionsIndex == dynamicTestOptionsArray.length) {
			addDynamicTest(dynamicTestList);

		} else {
			final DynamicTestOptions<?> dynamicTestOptions = dynamicTestOptionsArray[optionsIndex];
			final List<? extends DynamicTestOption<?>> dynamicTestOptionList =
					dynamicTestOptions.getDynamicTestOptionList();
			for (final DynamicTestOption<?> dynamicTestOption : dynamicTestOptionList) {

				final int index = dynamicTestOption.getIndex();
				dynamicTestOptions.setSelectedOptionIndex(index);
				addDynamicTestsRec(optionsIndex + 1, dynamicTestList);
			}
		}
	}

	private void addDynamicTest(
			final List<DynamicTest> dynamicTestList) {

		final List<String> optionsDisplayNameList = new ArrayList<>();
		for (final DynamicTestOptions<?> dynamicTestOptions : dynamicTestOptionsArray) {

			final String optionsDisplayName = dynamicTestOptions.createDisplayName();
			optionsDisplayNameList.add(optionsDisplayName);
		}
		final String displayName = String.join(" ", optionsDisplayNameList);

		dynamicTestList.add(DynamicTest.dynamicTest(displayName, executable));
	}
}
