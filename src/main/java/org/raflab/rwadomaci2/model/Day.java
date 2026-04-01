package org.raflab.rwadomaci2.model;

public enum Day {
    PONEDELJAK("ponedeljak", "Ponedeljak"),
    UTORAK("utorak", "Utorak"),
    SREDA("sreda", "Sreda"),
    CETVRTAK("cetvrtak", "Četvrtak"),
    PETAK("petak", "Petak");

    private final String key;
    private final String displayName;

    Day(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }
}