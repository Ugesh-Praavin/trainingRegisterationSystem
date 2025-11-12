package com.warehouseinventory;

import java.io.*;
import java.util.*;

public class CSVStorage {
    private static final String SPLIT = ",";

    public static void saveToFile(Warehouse warehouse, String filepath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            bw.write("sku,name,category,quantity,location");
            bw.newLine();
            for (Item item : warehouse.listAllItems()) {
                String line = String.join(SPLIT,
                        escape(item.getSku()),
                        escape(item.getName()),
                        escape(item.getCategory()),
                        String.valueOf(item.getQuantity()),
                        escape(item.getLocation().toString())
                );
                bw.write(line);
                bw.newLine();
            }
        }
    }

    public static void loadFromFile(Warehouse warehouse, String filepath) throws IOException {
        File f = new File(filepath);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String header = br.readLine();
            String line;
            warehouse.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = splitCSV(line);
                if (parts.length >= 5) {
                    String sku = unescape(parts[0]);
                    String name = unescape(parts[1]);
                    String category = unescape(parts[2]);
                    int qty = Integer.parseInt(parts[3]);
                    Location loc = Location.parse(unescape(parts[4]));
                    Item item = new Item(sku, name, category, qty, loc);
                    warehouse.addItem(item);
                }
            }
        }
    }
    private static String escape(String s) {
        if (s.contains(",")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String unescape(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }
    private static String[] splitCSV(String line) {
        List<String> parts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                cur.append(c);
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString().trim());
                cur = new StringBuilder();
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString().trim());
        return parts.toArray(new String[0]);
    }
}

