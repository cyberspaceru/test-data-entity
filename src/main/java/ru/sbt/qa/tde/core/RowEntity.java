package ru.sbt.qa.tde.core;

import javafx.util.Pair;

import java.util.*;

import static java.util.Optional.of;

/**
 * Created by cyberspace on 6/25/2017.
 */
public class RowEntity {

    private final String clazz, name;
    private final Dictionary<String, String> fields;

    public RowEntity(String clazz, String name) {
        this.clazz = clazz.replace("*", "");
        this.name = name.trim();
        fields = new Hashtable<>();
    }

    public String getRelativeClassPackage() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public boolean addField(String fieldName, String fieldValue) {
        Pair<String, String> pair = new Pair<>(fieldName, fieldValue);
        return of(pair).filter(x -> !x.getKey().isEmpty() && x.getKey() != null && x.getValue() != null)
                .map(x -> fields.put(x.getKey().trim(), x.getValue()))
                .map(x -> true)
                .orElse(false);
    }

    public ArrayList<String> getFieldsName() {
        return Collections.list(fields.keys());
    }

    public String getFieldValue(String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("{ clazz = \"" + clazz + "\", name = \"" + name + "\" }");
        Collections.list(fields.keys()).forEach(x -> {
            result.append("\n\tfield = \"")
                    .append(x)
                    .append("\", value = \"")
                    .append(fields.get(x))
                    .append("\"");
        });
        return result.toString();
    }
}
