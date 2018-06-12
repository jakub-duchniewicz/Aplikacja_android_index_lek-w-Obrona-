package com.ciastek.testv11;

/**
 * Created by Ciastek on 31.01.2018.
 */

public class Product {
    private String image;
    private String name;
    private String price;



    public Product(String image, String name, String description) {
        this.image = image;
        this.name = name;
        this.price = description;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
