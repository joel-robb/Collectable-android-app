package com.example.collectable;

public class CollectionItem {
    String icon;
    String name;
    int itemGoal;
    int numItems;
    String uid;

    public CollectionItem(String icon, String name, int itemGoal, int numItems, String uid) {
        this.icon = icon;
        this.name = name;
        this.itemGoal = itemGoal;
        this.numItems = numItems;
        this.uid = uid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemGoal() {
        return itemGoal;
    }

    public void setItemGoal(int itemGoal) {
        this.itemGoal = itemGoal;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
