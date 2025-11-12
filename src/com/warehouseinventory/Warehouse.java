package com.warehouseinventory;

import java.util.*;
import java.util.stream.Collectors;
public class Warehouse {
    private Map<String, Item> items;

    public Warehouse() {
        this.items = new HashMap<>();
    }

    public boolean addItem(Item item) {
        if (item == null || item.getSku() == null || item.getSku().isEmpty()) return false;
        if (items.containsKey(item.getSku())) return false; // unique SKU enforced
        items.put(item.getSku(), item);
        return true;
    }

    public boolean removeItem(String sku) {
        return items.remove(sku) != null;
    }

    public Item findItemBySKU(String sku) {
        return items.get(sku);
    }

    public List<Item> findItemsByName(String name) {
        String q = name.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Item> findItemsByCategory(String category) {
        String q = category.toLowerCase();
        return items.values().stream()
                .filter(i -> i.getCategory().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public boolean moveItem(String sku, Location newLocation) {
        Item it = items.get(sku);
        if (it == null) return false;
        it.setLocation(newLocation);
        return true;
    }

    public List<Item> listAllItems() {
        return new ArrayList<>(items.values()).stream()
                .sorted(Comparator.comparing(Item::getSku))
                .collect(Collectors.toList());
    }

    public Map<String, Item> getItemsMap() {
        return items;
    }

    public void clear() {
        items.clear();
    }
}

