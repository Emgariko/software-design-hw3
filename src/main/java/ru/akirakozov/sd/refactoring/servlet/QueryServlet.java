package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.Repository;
import ru.akirakozov.sd.refactoring.servlet.utils.HttpResponseBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        HttpResponseBuilder builder = new HttpResponseBuilder(response);
        if ("max".equals(command)) {
            Product product = Repository.getProductWithMaxPrice();
            builder.appendHeader("Product with max price: ").append(product);
        } else if ("min".equals(command)) {
            Product product = Repository.getProductWithMinPrice();
            builder.appendHeader("Product with min price: ").append(product);
        } else if ("sum".equals(command)) {
            int sum = Repository.getProductPricesSum();
            builder.append("Summary price: ").append(sum);
        } else if ("count".equals(command)) {
            int count = Repository.getProductsCount();
            builder.append("Number of products: ").append(count);
        } else {
            HttpResponseBuilder.asText(response, "Unknown command: " + command);
            return;
        }

        builder.buildResponse();
    }

}
