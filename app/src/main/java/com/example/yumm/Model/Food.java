package com.example.yumm.Model;

import android.widget.Button;

public class Food {

    private String name,image,price,discount,menuId;



    public Food() {
    }

    public Food(String name, String image, String price, String discount, String menuId) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.discount = discount;
        this.menuId = menuId;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
