package org.polyfill.components.handlebars;

import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by reinier.guerra on 10/12/16.
 */
public class HandlebarsTemplateLoader implements TemplateLoader {

    private String prefix;
    private String suffix;

    @Override
    public TemplateSource sourceAt(String s) throws IOException {
        return new HandlebarsTemplateSource(this.resolve(s));
    }

    @Override
    public String resolve(String s) {
        Path basePath = Paths.get(prefix);
        return basePath.resolve(s + suffix).toString();
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public void setPrefix(String s) {
        this.prefix = s;
    }

    @Override
    public void setSuffix(String s) {
        this.suffix = s;
    }
}
