package ru.akirakozov.sd.refactoring.servlet.utils;

import ru.akirakozov.sd.refactoring.model.Product;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponseBuilder {
    private final HttpServletResponse response;
    private final StringBuilder responseHTML = new StringBuilder();
    public HttpResponseBuilder(HttpServletResponse response) {
        this.response = response;
        responseHTML.append("<html><body>\n");
    }

    public HttpResponseBuilder append(Product product) {
        String name = product.getName();
        Long price = product.getPrice();
        responseHTML.append(name).append("\t").append(price).append("</br>\n");
        return this;
    }

    public HttpResponseBuilder appendHeader(String header) {
        responseHTML.append("<h1>").append(header).append("</h1>\n");
        return this;
    }

    public HttpResponseBuilder append(String line) {
        responseHTML.append(line).append("\n");
        return this;
    }

    public HttpResponseBuilder append(int val) {
        responseHTML.append(val).append("\n");
        return this;
    }
    public void buildResponse() throws IOException {
        responseHTML.append("</body></html>");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(responseHTML);
    }
    public static void asText(HttpServletResponse response, String text) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(text);
    }
}
