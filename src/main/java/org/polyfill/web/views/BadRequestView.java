package org.polyfill.web.views;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.polyfill.web.components.handlebars.HandlebarsTemplateLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by reinier.guerra on 12/5/16.
 */
public class BadRequestView implements View {

    private final String target;
    private Handlebars handlebars;
    private Object context;

    public BadRequestView(String target) {
        TemplateLoader loader = new HandlebarsTemplateLoader();
        loader.setPrefix("src/main/pages/handlebars");
        loader.setSuffix(".hbs");
        handlebars = new Handlebars(loader);
        this.target = target;
    }

    public BadRequestView(String target, Object context) {
        this(target);
        this.context = context;
    }

    @Override
    public String getContentType() {
        return "text/html; charset=utf-8";
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ServletOutputStream stream = httpServletResponse.getOutputStream();
        Template template = handlebars.compile(this.target);

        httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        httpServletResponse.setContentType(getContentType());
        stream.print(template.apply(this.context));
    }
}
