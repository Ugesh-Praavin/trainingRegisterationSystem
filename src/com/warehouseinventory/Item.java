package com.warehouseinventory;

import java.util.Objects;
public class Item {
    private String sku;
    private String name;
    private String category;
    private int quantity;
    private Location location;
    public Item(String sku, String name, String category, int quantity, Location location) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.quantity = Math.max(0, quantity);
        this.location = location;
    }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public Location getLocation() { return location; }
    public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }
    public void setLocation(Location location) { this.location = location; }
    public void increaseQuantity(int amount) {
        if (amount > 0) this.quantity += amount;
    }
    public boolean decreaseQuantity(int amount) {
        if (amount <= 0) return false;
        if (amount > this.quantity) return false;
        this.quantity -= amount;
        return true;
    }
    @Override
    public String toString() {
        return String.format("%s | %s | %s | Qty:%d | Loc:%s", sku, name, category, quantity, location);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;
        return Objects.equals(sku, item.sku);
    }
    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}
