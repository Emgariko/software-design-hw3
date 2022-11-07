package ru.akirakozov.sd.refactoring.model;

public class Product {
    protected String name;
    protected Long price;

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public Product(String name, Long price) {
        this.name = name;
        this.price = price;
    }

    public String toSQLValue() {
        return "(\"" + name + "\" ," +  price + ")";
    }
}