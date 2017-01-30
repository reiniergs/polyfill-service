package org.polyfill.views;

import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by reinier.guerra on 10/12/16.
 */

public class PolyfillsView implements View {
    private String polyfills;

    public PolyfillsView(String polyfills) {
        this.polyfills = polyfills;
    }

    public String getContentType() {
        return "text/javascript;charset=UTF-8";
    }

    public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ServletOutputStream stream = httpServletResponse.getOutputStream();

        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setContentType(getContentType());
        stream.print(polyfills);
    }
}
