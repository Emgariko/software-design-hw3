package ru.akirakozov.sd.refactoring.repository;

import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Repository {
    private static final String CONNECTION_URL = "jdbc:sqlite:test.db";

    public static final String SELECT_ALL = "SELECT * FROM PRODUCT";
    public static final String INSERT_PRODUCT_TEMPLATE = "INSERT INTO PRODUCT (NAME, PRICE) VALUES ";
    public static final String INSERT_PRODUCT = INSERT_PRODUCT_TEMPLATE + "(\"%s\", %s);";
    public static final String SELECT_PRODUCT_WITH_MAX_PRICE = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
    public static final String SELECT_PRODUCT_WITH_MIN_PRICE = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
    public static final String SELECT_PRICES_SUM = "SELECT SUM(price) FROM PRODUCT";
    public static final String SELECT_PRICES_COUNT = "SELECT COUNT(*) FROM PRODUCT";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";
    public final static String CLEAN_UP_TABLE = "DELETE FROM PRODUCT";

    public static void createTable() {
        executeUpdate(CREATE_TABLE);
    }

    public static void cleanUpTable() {
        executeUpdate(CLEAN_UP_TABLE);
    }
    public static <T> T executeQuery(String query, Function<ResultSet, T> func)  {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            T res = func.apply(rs);
            rs.close();
            stmt.close();
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeUpdate(String query) {
        try (Connection c = DriverManager.getConnection(CONNECTION_URL)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Product> parseProducts(ResultSet rs) {
        List<Product> res = new ArrayList<>();
        try {
            while (rs.next()) {
                Product product = new Product(rs.getString("name"), rs.getLong("price"));
                res.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public static final Function<ResultSet, Product> extractProduct = resultSet -> parseProducts(resultSet).get(0);
    public static final Function<ResultSet, Integer> extractInt = resultSet -> {
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };
    public static List<Product> selectAll() {
        return executeQuery(SELECT_ALL, Repository::parseProducts);
    }

    public static void addProduct(Product product) {
        executeUpdate(String.format(INSERT_PRODUCT, product.getName(), product.getPrice()));
    }

    public static Product getProductWithMaxPrice() {
        return executeQuery(SELECT_PRODUCT_WITH_MAX_PRICE, extractProduct);
    }

    public static Product getProductWithMinPrice() {
        return executeQuery(SELECT_PRODUCT_WITH_MIN_PRICE, extractProduct);
    }

    public static int getProductPricesSum() {
        return executeQuery(SELECT_PRICES_SUM, extractInt);
    }

    public static int getProductsCount() {
        return executeQuery(SELECT_PRICES_COUNT, extractInt);
    }
}
