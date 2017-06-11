package org.polyfill.perf.services;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

/**
 * Created by smo on 6/9/17.
 */
@Service
public class BundleSizeMeasureService {

    public int getByteSize(String input) {
        return input.getBytes(StandardCharsets.UTF_8).length;
    }

    public int getGzipByteSize(String input) {
        int size = -1;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(input.length());
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(input.getBytes());
            gzip.close();
            size = out.toByteArray().length;
            out.close();
        } catch (IOException e) {}

        return size;
    }
}
