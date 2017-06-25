package ru.sbt.qa.tde.core;

/**
 * Created by cyberspace on 6/25/2017.
 */

class Entity {
    private final String name;
    private final Object object;

    public Entity(String name, Object object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public Object getObject() {
        return object;
    }
}