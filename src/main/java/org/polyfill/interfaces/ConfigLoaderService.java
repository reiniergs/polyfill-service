package org.polyfill.interfaces;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by bvenkataraman on 10/19/16.
 */
public interface ConfigLoaderService {

    Map<String, Object> getConfig(String path) throws IOException;

    boolean isConfig(String fileName);

    /**
     * Get a list of config file paths from a directory
     * @param directoryPath - directory to load the configs
     * @param checkSubDir - whether to load configs recursively when there's sub directory
     * @param test - callback to check if file is wanted
     * @return - a list of config file paths
     * @throws IOException - when directoryPath doesn't exist
     */
    default List<String> getAllConfigPaths(String directoryPath,
            boolean checkSubDir, Function<Path, Boolean> test) throws IOException {
        return getAllConfigPaths(Paths.get(directoryPath), checkSubDir, test);
    }

    /**
     * Get a list of config file paths from a directory
     * @param directoryPath - directory to load the configs
     * @param checkSubDir - whether to load configs recursively when there's sub directory
     * @param test - callback to check if file is wanted
     * @return - a list of config file paths
     * @throws IOException - when directoryPath doesn't exist
     */
    default List<String> getAllConfigPaths(Path directoryPath,
            boolean checkSubDir, Function<Path, Boolean> test) throws IOException {

        List<String> configPaths = new ArrayList<>();

        DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath);
        for (Path path : stream) {
            // add the path if it passes the test or if there's no test
            if (path.toFile().isFile()) {
                String pathString = path.toString();
                if (isConfig(pathString) && (test == null || test.apply(path))) {
                    configPaths.add(pathString);
                }

            // a directory, recursively search load paths if checkSubDir
            } else if (path.toFile().isDirectory() && checkSubDir) {
                configPaths.addAll(getAllConfigPaths(path, checkSubDir, test));
            }
        }

        return configPaths;
    }

    /**
     * Get a list of configs from a directory
     * @param directoryPath - directory to load the configs
     * @param checkSubDir - whether to load configs recursively when there's sub directory
     * @param test - callback to check if file is wanted
     * @return - a list of configs
     * @throws IOException - when directoryPath doesn't exist
     */
    default List<Map<String, Object>> getConfigsFromDirectory(String directoryPath,
            boolean checkSubDir, Function<Path, Boolean> test) throws IOException {

        List<String> allFilePaths = getAllConfigPaths(Paths.get(directoryPath), checkSubDir, test);

        List<Map<String, Object>> configs = new ArrayList<>();
        for (String path : allFilePaths) {
            try {
                Map<String, Object> config = getConfig(path);
                configs.add(config);
            } catch(IOException e) {
                System.err.println("Failed to load file: " + path.toString());
                e.printStackTrace();
            }
        }

        return configs;
    }
}
