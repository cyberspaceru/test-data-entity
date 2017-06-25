package ru.sbt.qa.tde.core;

import java.lang.reflect.Field;

public interface IDataProcessor {
    Object process(String value, Field destinationField);
}
