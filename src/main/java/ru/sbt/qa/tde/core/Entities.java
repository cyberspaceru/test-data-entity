package ru.sbt.qa.tde.core;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import ru.sbt.qa.tde.processors.BaseDataProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static rx.Observable.from;

/**
 * Created by cyberspace on 6/25/2017.
 */
public class Entities {

    private final static List<Entity> entities = new ArrayList<>();

    public static void add(ILoader loader, String entitiesPackage) {
        add(loader, new BaseDataProcessor(), entitiesPackage);
    }

    public static void add(ILoader loader, IDataProcessor dataProcessor, String entitiesPackage) {
        List<RowEntity> rowEntityList = loader.load();
        if (rowEntityList != null) {
            MManager.execute(from(rowEntityList), x -> createEntity(x, dataProcessor, entitiesPackage))
                    .subscribe(x -> ofNullable(x).map(entities::add));
        }
    }

    public static <T> T get(Class<T> tClass, String name) {
        return entities.stream().filter(x ->  x.getObject().getClass().equals(tClass))
                .filter(x -> x.getName().equals(name))
                .distinct()
                .findFirst()
                .map(Entity::getObject)
                .map(x -> (T) x)
                .orElse(null);
    }

    public static <T> T getFirst(Class<T> tClass) {
        return entities.stream().filter(x ->  x.getObject().getClass().equals(tClass))
                .distinct()
                .findFirst()
                .map(Entity::getObject)
                .map(x -> (T) x)
                .orElse(null);
    }

    public static Object getFirst(String name) {
        return entities.stream().filter(x -> x.getName().equals(name))
                .distinct()
                .findFirst()
                .map(Entity::getObject)
                .orElse(null);
    }

    private static Entity createEntity(RowEntity rowEntity, IDataProcessor dataProcessor, String entitiesPackage) {
        String absolutePackage = entitiesPackage + "." + rowEntity.getRelativeClassPackage();
        Set<Class<?>> set = new Reflections(absolutePackage).getTypesAnnotatedWith(TestData.class);
        if (set == null || set.size() != 1) {
            Logger.getRootLogger().error(absolutePackage + " not found.");
            return null;
        }
        return of(set).map(x -> x.iterator().next())
                .map(x -> {
                    try {
                        final Entity result = new Entity(rowEntity.getName(), x.newInstance());
                        // Для каждого значения "Имя поля":
                        rowEntity.getFieldsName().forEach(fieldName -> {
                            Field field = null;
                            try {
                                // Находим поле, которое соотвествует значению "Имя поля"
                                Object object = result.getObject();
                                field = object.getClass().getDeclaredField(fieldName);
                                field.set(object, dataProcessor.process(rowEntity.getFieldValue(fieldName), field));
                            } catch (NoSuchFieldException e) {
                                Logger.getRootLogger().error("Can't find the field with name: " + fieldName);
                            } catch (IllegalAccessException e) {
                                Logger.getRootLogger().error("Field with name \""+ fieldName + "\"" + " must be PUBLIC in order to set value: " + rowEntity.getFieldValue(fieldName));
                            } catch (IllegalArgumentException e) {
                                Logger.getRootLogger().error("Field with name \""+ fieldName + "\": " + e.getMessage());
                            } catch (Exception e) {
                                Logger.getRootLogger().error("UnException for the field with name \""+ fieldName + "\": " + e.getMessage());
                            }
                        });

                        return result;
                    } catch (Exception e) {
                        Logger.getRootLogger().error("Can't create an entity from \""+ absolutePackage + "\": " + e.getMessage());
                        return null;
                    }
                })
                .orElse(null);
    }

}
