package com.utils.gradle.analyzers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.personal.utils.gradle.sub_prj.FactoryGradleSubProject;
import com.personal.utils.gradle.sub_prj.GradleSubProject;
import com.utils.gradle.analyzers.data.Node;
import com.utils.io.PathUtils;
import com.utils.log.Logger;

public final class ProjectDependenciesAnalyzer {

	private ProjectDependenciesAnalyzer() {
	}

	public static void work(
			final String projectPathString,
			final Set<String> projectPathStringSet,
			final List<Node> nodeList,
			final Set<String> dependencySet) {

		final Map<Integer, Set<Integer>> directDependenciesMap = new HashMap<>();
		final Map<String, Node> nodesByProjectNameMap = new LinkedHashMap<>();

		final Map<String, GradleSubProject> gradleSubProjectsByPathMap = new LinkedHashMap<>();
		FactoryGradleSubProject.newInstance(projectPathString, gradleSubProjectsByPathMap);

		for (final GradleSubProject gradleSubProject : gradleSubProjectsByPathMap.values()) {

			final String subProjectPathString = gradleSubProject.getPath();
			projectPathStringSet.add(subProjectPathString);

			final Set<String> dependencyPathSet = gradleSubProject.getDependencyPathSet();
			for (final String dependencySubProjectPathString : dependencyPathSet) {

				final Node parentNode = computeNode(subProjectPathString, nodesByProjectNameMap);
				final Node node = computeNode(dependencySubProjectPathString, nodesByProjectNameMap);

				final int parentNodeIndex = parentNode.getIndex();
				final Set<Integer> nodeIndexSet =
						directDependenciesMap.computeIfAbsent(parentNodeIndex, k -> new HashSet<>());
				final int nodeIndex = node.getIndex();
				nodeIndexSet.add(nodeIndex);

				final String dependency = "n" + parentNodeIndex + " -> n" + nodeIndex;
				dependencySet.add(dependency);
			}
		}

		nodeList.addAll(nodesByProjectNameMap.values());

		for (final Map.Entry<Integer, Set<Integer>> mapEntry : directDependenciesMap.entrySet()) {

			final int nodeIndex = mapEntry.getKey();
			final Set<Integer> directDependencyNodeIndexSet = mapEntry.getValue();

			final Set<Integer> indirectDependenciesNodeIndexSet = new HashSet<>();
			for (final int directDependencyNodeIndex : directDependencyNodeIndexSet) {

				computeAllDependenciesRec(
						directDependencyNodeIndex, directDependenciesMap, indirectDependenciesNodeIndexSet);
			}

			for (final int directDependencyNodeIndex : directDependencyNodeIndexSet) {

				if (indirectDependenciesNodeIndexSet.contains(directDependencyNodeIndex)) {

					final Node node = nodeList.get(nodeIndex);
					final String projectName = node.getProjectName();
					final Node directDependencyNode = nodeList.get(directDependencyNodeIndex);
					final String directDependencyProjectName = directDependencyNode.getProjectName();
					Logger.printWarning("unnecessary dependency " +
							projectName + " -> " + directDependencyProjectName);
				}
			}
		}
	}

	private static void computeAllDependenciesRec(
			final int nodeIndex,
			final Map<Integer, Set<Integer>> directDependenciesMap,
			final Set<Integer> allDependenciesNodeIndexSet) {

		final Set<Integer> directDependenciesNodeIndexSet = directDependenciesMap.get(nodeIndex);
		if (directDependenciesNodeIndexSet != null) {

			for (final int directDependencyNodeIndex : directDependenciesNodeIndexSet) {

				allDependenciesNodeIndexSet.add(directDependencyNodeIndex);
				computeAllDependenciesRec(
						directDependencyNodeIndex, directDependenciesMap, allDependenciesNodeIndexSet);
			}
		}
	}

	private static Node computeNode(
			final String projectPathString,
			final Map<String, Node> nodesByProjectNameMap) {

		final Path projectPath = Paths.get(projectPathString);
		final String projectName = PathUtils.computeFileName(projectPathString);

		Node node = nodesByProjectNameMap.get(projectName);
		if (node == null) {

			final int index = nodesByProjectNameMap.size();

			final String projectType = PathUtils.computeFileName(
					projectPath.getParent().getParent().getParent());
			final String shape = computeShape(projectType);

			final String outputText =
					"n" + index + " [label=\"" + projectName + "\", shape=\"" + shape + "\"];";

			node = new Node(index, projectName, outputText);
			nodesByProjectNameMap.put(projectName, node);
		}
		return node;
	}

	private static String computeShape(
			final String projectType) {

		final String shape;
		if ("Projects".equals(projectType)) {
			shape = "box";
		} else if ("Modules".equals(projectType)) {
			shape = "hexagon";
		} else if ("Utils".equals(projectType)) {
			shape = "oval";
		} else {
			shape = "box";
		}
		return shape;
	}
}
