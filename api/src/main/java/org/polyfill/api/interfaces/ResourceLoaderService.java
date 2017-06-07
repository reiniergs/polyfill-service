package org.polyfill.api.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by smo on 4/30/17.
 * Utility interface for all loader service that needs to load project resources.
 */
public interface ResourceLoaderService {

    default String resourceToString(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }
    }

    default Resource getResource(String first, String ... more) throws IOException {
        String filePattern = Paths.get(first, more).toString();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + filePattern);
    }

    default List<Resource> getResources(String first, String ... more) throws IOException {
        String filePattern = Paths.get(first, more).toString();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ResourceUtils.CLASSPATH_URL_PREFIX + filePattern);
        if (resources.length == 0) {
            throw new IOException("No matching resources are found in:" + filePattern);
        }
        return Arrays.asList(resources);
    }
}
