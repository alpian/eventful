package com.ind.eventful.name;

public class StringName implements Name {
    private final String name;

    public StringName(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
    
    public static Name named(String name) {
        return new StringName(name);
    }
}