package com.ashera.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuplicateClassDetector {
	public static void main(String[] args) {
		String[] paths = J2ObjcPrefixCodeGen.paths;

		List<String> classPaths = Arrays.stream(paths).map(path -> {
			try {
				return new File(path).getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());

		List<File> fileNames = classPaths.stream().map((path) -> {
			return getFileStream(path).filter((filePath) -> filePath.toFile().isFile())
					.map((filePath) -> filePath.toFile()).collect(Collectors.toList());
		}).flatMap(list -> list.stream()).collect(Collectors.toList());
		findDuplicates(fileNames).stream().forEach((st) -> System.out.println(st));
	}

	public static Set<String> findDuplicates(List<File> listContainingDuplicates) {
		final Set<String> setToReturn = new HashSet<>();
		final HashMap<String, File> set1 = new HashMap<>();
		
		for (File yourInt : listContainingDuplicates) {
			if (!set1.containsKey(yourInt.getName())) {
				set1.put(yourInt.getName(), yourInt);
			} else {
				if (!yourInt.getAbsolutePath().equals(set1.get(yourInt.getName()).getAbsolutePath())) {
					setToReturn.add(yourInt.getAbsolutePath() + " " + set1.get(yourInt.getName()).getAbsolutePath());
				}
			}
		}
		return setToReturn;
	}

	private static Stream<Path> getFileStream(String path) {
		try {
			return Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
