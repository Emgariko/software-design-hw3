package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.repository.Repository;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = Repository.selectAll();

        response.getWriter().println("<html><body>");

        for (Product product: products) {
            String name = product.getName();
            Long price = product.getPrice();
            response.getWriter().println(name + "\t" + price + "</br>");
        }
        response.getWriter().println("</body></html>");


        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
