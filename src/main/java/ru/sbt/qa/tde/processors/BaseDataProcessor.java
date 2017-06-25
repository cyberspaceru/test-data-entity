package ru.sbt.qa.tde.processors;

import ru.sbt.qa.tde.core.IDataProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by cyberspace on 6/25/2017.
 */
public class BaseDataProcessor implements IDataProcessor {

    public Object process(String value, Field destinationField) {
        Type type = destinationField.getType();
        if (type.equals(String.class))
            return value;
        else if(type.equals(Integer.class))
            return Integer.parseInt(value);
        else if(type.equals(Boolean.class))
            return Boolean.parseBoolean(value);
        else if(type.equals(Double.class))
            return Double.parseDouble(value);
        else if(type.equals(Float.class))
            return Float.parseFloat(value);
        else if(type.equals(boolean.class))
            return Boolean.parseBoolean(value);
        return null;
    }

}
