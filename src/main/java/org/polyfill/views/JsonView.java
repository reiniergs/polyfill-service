package org.polyfill.views;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by reinier.guerra on 2/22/17.
 */
public class JsonView implements View {

    private Object data;

    public JsonView(Object data) {
        this.data = data;
    }

    public String getContentType() {
        return MediaType.APPLICATION_JSON_UTF8_VALUE;
    }

    public void render(Map<String, ?> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setContentType(getContentType());

        ServletOutputStream stream = httpServletResponse.getOutputStream();
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();

        stream.print(gson.toJson(this.data));
    }
}
