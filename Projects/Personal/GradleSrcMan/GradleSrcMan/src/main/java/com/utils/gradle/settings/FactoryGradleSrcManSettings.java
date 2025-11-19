package com.utils.gradle.settings;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.utils.io.IoUtils;
import com.utils.io.PathUtils;
import com.utils.log.Logger;
import com.utils.xml.dom.XmlDomUtils;

public final class FactoryGradleSrcManSettings {

	private FactoryGradleSrcManSettings() {
	}

	public static GradleSrcManSettings newInstance() {

		GradleSrcManSettings gradleSrcManSettings = null;
		try {
			final String settingsFilePathString =
					PathUtils.computeAbsolutePath(null, null, "GradleSrcManSettings.xml");
			Logger.printProgress("parsing the settings file:");
			Logger.printLine(settingsFilePathString);
			if (!IoUtils.fileExists(settingsFilePathString)) {
				Logger.printError("settings file missing:" +
						System.lineSeparator() + settingsFilePathString);

			} else {
				final Document document = XmlDomUtils.openDocument(settingsFilePathString);
				final Element documentElement = document.getDocumentElement();

				final List<String> projectPathStringList = new ArrayList<>();
				final List<Element> projectElementList =
						XmlDomUtils.getElementsByTagName(documentElement, "Project");
				for (final Element projectElement : projectElementList) {

					final String projectPathString = projectElement.getAttribute("Path");
					projectPathStringList.add(projectPathString);
				}
				if (projectPathStringList.isEmpty()) {
					Logger.printError("no project paths found in the settings file");

				} else {
					String outputFolderPathString = null;
					final Element outputFolderElement = XmlDomUtils
							.getFirstChildElementByTagName(documentElement, "OutputFolder");
					if (outputFolderElement != null) {
						outputFolderPathString = outputFolderElement.getAttribute("Path");
					}
					if (StringUtils.isBlank(outputFolderPathString)) {
						Logger.printError("missing output folder path in the settings file");

					} else {
						gradleSrcManSettings =
								new GradleSrcManSettings(projectPathStringList, outputFolderPathString);
					}
				}
			}

		} catch (final Throwable throwable) {
			Logger.printError("failed to parse the settings file");
			Logger.printThrowable(throwable);
		}
		return gradleSrcManSettings;
	}
}
