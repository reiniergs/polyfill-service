package org.polyfill.perf.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by smo on 6/9/17.
 */
public class TablePrinter {
    private List<List<String>> rows;
    private String colDelimiter;

    public TablePrinter(String colDelimiter) {
        this.rows = new ArrayList<>();
        this.colDelimiter = colDelimiter;
    }

    public void addRow(String ... fields) {
        this.rows.add(Arrays.asList(fields));
    }

    public String getOutput() {
        return this.rows.stream()
            .map(row -> row.stream().collect(Collectors.joining(colDelimiter)))
            .collect(Collectors.joining("\n"));
    }

    public boolean printToFile(String path) {
        try {
            Files.write(Paths.get(path), getOutput().getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("Unable to write to: " + path);
            e.printStackTrace();
        }
        return false;
    }
}
