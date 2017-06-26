package ru.sbt.qa.tde.core;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import ru.sbt.qa.tde.processors.BaseDataProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static Integer count() {
        return of(entities).map(x -> entities.size()).orElse(0);
    }

    public static void add(ILoader loader, IDataProcessor dataProcessor, String entitiesPackage) {
        List<RudeEntity> rudeEntityList = loader.load();
        if (rudeEntityList != null) {
            MManager.execute(from(rudeEntityList), x -> createEntity(x, dataProcessor, entitiesPackage))
                    .subscribe(x -> ofNullable(x).map(entities::add));
        }
    }

    public static <T> Stream<T> entities(Class<T> tClass) {
        return ofNullable(getAll(tClass)).map(List::stream).orElse(null);
    }

    public static <T> List<T> getAllByPredicate(Class<T> tClass, Predicate<T> objectPredicate) {
        List<T> result = entities.stream().filter(x ->  x.getObject().getClass().equals(tClass))
                .map(Entity::getObject)
                .map(x -> (T) x)
                .filter(objectPredicate)
                .collect(Collectors.toList());
        return of(result).filter(x -> x.size() != 0).orElse(null);
    }

    public static <T> List<T> getAll(Class<T> tClass) {
        List<T> result = entities.stream().filter(x ->  x.getObject().getClass().equals(tClass))
                .map(Entity::getObject)
                .map(x -> (T) x)
                .collect(Collectors.toList());
        return of(result).filter(x -> x.size() != 0).orElse(null);
    }

    public static <T> List<T> getAll(Class<T> tClass, Predicate<String> namePredicate) {
        List<T> result = entities.stream().filter(x ->  x.getObject().getClass().equals(tClass))
                .filter(x -> namePredicate.test(x.getName()))
                .map(Entity::getObject)
                .map(x -> (T) x)
                .collect(Collectors.toList());
        return of(result).filter(x -> x.size() != 0).orElse(null);
    }

    public static <T> T getFirst(Class<T> tClass, String name) {
        return entities.stream().filter(x ->  x.getObject().getClass().equals(tClass))
                .filter(x -> x.getName() != null && x.getName().equals(name))
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
        return entities.stream().filter(x -> x.getName() != null && x.getName().equals(name))
                .distinct()
                .findFirst()
                .map(Entity::getObject)
                .orElse(null);
    }

    private static Entity createEntity(RudeEntity rudeEntity, IDataProcessor dataProcessor, String entitiesPackage) {
        String absolutePackage = entitiesPackage + "." + rudeEntity.getRelativeClassPackage();
        Set<Class<?>> set = new Reflections(absolutePackage).getTypesAnnotatedWith(TestData.class);
        if (set == null || set.size() != 1) {
            Logger.getRootLogger().error(absolutePackage + " not found.");
            return null;
        }
        return of(set).map(x -> x.iterator().next())
                .map(x -> {
                    try {
                        final Entity result = new Entity(rudeEntity.getName(), x.newInstance());
                        // Для каждого значения "Имя поля":
                        rudeEntity.getFieldsName().forEach(fieldName -> {
                            Field field = null;
                            try {
                                // Находим поле, которое соотвествует значению "Имя поля"
                                Object object = result.getObject();
                                field = getAllFields(object.getClass()).stream()
                                        .filter(n -> n.getName().equals(fieldName))
                                        .findFirst()
                                        .orElseThrow(NoSuchFieldException::new);
                                field.set(object, dataProcessor.process(rudeEntity.getFieldValue(fieldName), field));
                            } catch (NoSuchFieldException e) {
                                Logger.getRootLogger().error("Can't find the field with name: " + fieldName + " for \"" + x + "\"");
                            } catch (IllegalAccessException e) {
                                Logger.getRootLogger().error("Field with name \""+ fieldName + "\"" + " must be PUBLIC in order to set value: " + rudeEntity.getFieldValue(fieldName));
                            } catch (IllegalArgumentException e) {
                                Logger.getRootLogger().error("Field with name \""+ fieldName + "\": " + e.getMessage());
                            } catch (Exception e) {
                                Logger.getRootLogger().error("UnException for the field with name \""+ fieldName + "\": " + e.getMessage());
                            }
                        });

                        return result;
                    } catch (Exception e) {
                        Logger.getRootLogger().error("Can't create an entity of \""+ absolutePackage + "\": " + e.getMessage());
                        return null;
                    }
                })
                .orElse(null);
    }

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass())
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        return fields;
    }

}
