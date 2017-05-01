package org.polyfill.interfaces;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by smo on 4/30/17.
 */
public interface ResourceLoaderService {

    default String resourceToString(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    default Resource getResource(String first, String ... more) throws IOException {
        String filePath = Paths.get(first, more).toString();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + filePath);
    }

    default List<Resource> getResources(String first, String ... more) throws IOException {
        String filePattern = Paths.get(first, more).toString();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ResourceUtils.CLASSPATH_URL_PREFIX + filePattern);
        return Arrays.asList(resources);
    }
}
