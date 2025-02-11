package cn.rommy.dhud.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DarkMode {

    DISABLED(0, "disabled", "always disabled"),
    ENABLED(1, "enabled", "always enabled"),
    AUTO(2, "auto", "automatic");

    public static final List<String> OPTIONS_LIST = Arrays.stream(DarkMode.values())
            .map(DarkMode::toString)
            .collect(Collectors.toList());
    public static String cmdName = "darkMode";
    public static String cfgKey = "darkMode";

    public int id;
    public String name;
    public String description;

    DarkMode(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static DarkMode get(int id) {
        for (DarkMode dm : DarkMode.values()) {
            if (dm.id == id) {
                return dm;
            }
        }
        return null;
    }

    public static DarkMode get(String id) {
        for (DarkMode dm : DarkMode.values()) {
            if (dm.name.equalsIgnoreCase(id)) {
                return dm;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
