package com.example.collectable;

public class Item {
    int image;
    String name;
    String field;

    public Item(int image, String name, String field) {
        this.image = image;
        this.name = name;
        this.field = field;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
