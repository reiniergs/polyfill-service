package org.polyfill.views;

import org.polyfill.components.Polyfill;
import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by reinier.guerra on 10/12/16.
 */

public class PolyfillsView implements View {
    private List<Polyfill> polyfills;
    private String variant = "minify";

    public PolyfillsView(List<Polyfill> polyfills) {
        this.polyfills = polyfills;
    }

    public PolyfillsView(List<Polyfill> polyfills, String variant) {
        this.polyfills = polyfills;
        this.variant = variant.equals("raw") ? variant : "minify";
    }

    public String getContentType() {
        return "text/javascript;charset=UTF-8";
    }

    public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setContentType(getContentType());


        ServletOutputStream stream = httpServletResponse.getOutputStream();
        stream.print(getPolyfillSources());
    }

    private String getPolyfillSources() {
        return polyfills.stream()
                .map(this.variant.equals("raw") ? Polyfill::getRawSource : Polyfill::getMinSource)
                .reduce("", String::concat);
    }
}
