package ru.akirakozov.sd.refactoring.servlet;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ServerTests {
    public static class Product {
        public String name;
        public Long price;

        public Product(String name, Long price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return "(\"" + name + "\" ," +  price + ")";
        }
    }

    public final static List<Product> products = List.of(new Product("Cucumber", 40L),
            new Product("Potato", 50L),
            new Product("Apple", 60L));
    public final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    public final static String CLEAN_UP_TABLE = "DELETE FROM PRODUCT";
    public final static String INSERT_VALUES = "INSERT INTO PRODUCT (NAME, PRICE) VALUES ";
    private AddProductServlet addProductServlet = new AddProductServlet();
    private GetProductsServlet getProductsServlet = new GetProductsServlet();
    private QueryServlet queryServlet = new QueryServlet();

    private StringWriter responseSource;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @BeforeClass
    public static void init() {
        executeQuery(CREATE_TABLE);
    }

    @Before
    public void doStuff() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        responseSource = new StringWriter();
    }

    @After
    public void afterCleanup() {
        executeQuery(CLEAN_UP_TABLE);
    }

    private static void fillDb() {
        String sql = INSERT_VALUES + products.stream().map(Product::toString).collect(Collectors.joining(",")) +
                ";";
        executeQuery(sql);
    }

    private static void executeQuery(String sql) {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    HttpServletRequest mockRequest(Map<String, String> params) {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);

        for (Map.Entry<String, String> param: params.entrySet()) {
            when(mockRequest.getParameter(param.getKey())).thenReturn(param.getValue());
        }

        return mockRequest;
    }

    @Test
    public void testAddProducts() {
        HttpServletRequest mockRequest = mockRequest(Map.of("name", "Cucumber", "price", "40"));

        try {
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseSource));
            addProductServlet.doGet(mockRequest, mockResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = responseSource.toString();

        assertEquals("OK\n", response);
    }

    @Test
    public void testGetProductsFromEmptyTable() {
        try {
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseSource));
            getProductsServlet.doGet(mockRequest, mockResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = responseSource.toString();

        assertEquals(response, "<html><body>\n</body></html>\n");
    }

    @Test
    public void testGetProducts() {
        fillDb();

        try {
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseSource));
            getProductsServlet.doGet(mockRequest, mockResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = responseSource.toString();

        assertEquals(response, "<html><body>\nCucumber\t40</br>\nPotato\t50</br>\nApple\t60</br>\n</body></html>\n");
    }

    public void testQuery(String query, String expectedResponse) {
        fillDb();

        HttpServletRequest mockRequest = mockRequest(Map.of("command", query));
        try {
            when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseSource));
            queryServlet.doGet(mockRequest, mockResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = responseSource.toString();

        assertEquals(response, expectedResponse);
    }

    @Test
    public void testQueryMax() {
        testQuery("max", "<html><body>\n" +
                "<h1>Product with max price: </h1>\n" +
                "Apple\t60</br>\n" +
                "</body></html>\n");
    }

    @Test
    public void testQueryMin() {
        testQuery("min", "<html><body>\n" +
                "<h1>Product with min price: </h1>\n" +
                "Cucumber\t40</br>\n" +
                "</body></html>\n");
    }

    @Test
    public void testQuerySum() {
        testQuery("sum", "<html><body>\n" +
                "Summary price: \n" +
                "150\n" +
                "</body></html>\n");
    }

    @Test
    public void testQueryCount() {
        testQuery("count", "<html><body>\n" +
                "Number of products: \n" +
                "3\n" +
                "</body></html>\n");
    }

    @Test
    public void testQueryUnknown() {
        testQuery("unknown", "Unknown command: unknown\n");
    }
}
