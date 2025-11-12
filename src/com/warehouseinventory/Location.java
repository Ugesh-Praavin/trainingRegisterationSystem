package com.warehouseinventory;

public class Location {
    private String aisle;
    private String rack;
    private String bin;
    public Location(String aisle, String rack, String bin) {
        this.aisle = aisle;
        this.rack = rack;
        this.bin = bin;
    }
    public String getAisle() { return aisle; }
    public String getRack() { return rack; }
    public String getBin() { return bin; }
    @Override
    public String toString() {
        return String.format("%s-%s-%s", aisle, rack, bin);
    }
    public static Location parse(String locStr) {
        String[] parts = locStr.split("[-]");
        if (parts.length >= 3) {
            return new Location(parts[0], parts[1], parts[2]);
        } else {
            return new Location(locStr, "", "");
        }
    }
}
