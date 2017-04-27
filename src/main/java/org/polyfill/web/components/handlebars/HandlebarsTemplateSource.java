package org.polyfill.web.components.handlebars;

import com.github.jknack.handlebars.io.TemplateSource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public class HandlebarsTemplateSource implements TemplateSource {
    private File target;

    public HandlebarsTemplateSource(String path) throws IOException {
        File target = new File(path);

        if (!target.isFile()) {
            throw new IOException("No template found on path : " + path);
        }
        this.target = target;
    }

    @Override
    public String content() throws IOException {
        return FileUtils.readFileToString(target, Charset.forName("UTF-8"));
    }

    @Override
    public String filename() {
        return target.getName();
    }

    @Override
    public long lastModified() {
        return target.lastModified();
    }
}
