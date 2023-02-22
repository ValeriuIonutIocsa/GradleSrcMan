package com.utils.gradle.settings;

import java.util.ArrayList;
import java.util.List;

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

				gradleSrcManSettings = new GradleSrcManSettings(projectPathStringList);
			}

		} catch (final Exception exc) {
			Logger.printError("failed to parse the settings file");
			Logger.printException(exc);
		}
		return gradleSrcManSettings;
	}
}
