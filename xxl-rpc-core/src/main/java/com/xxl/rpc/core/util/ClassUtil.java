package com.xxl.rpc.core.util;

import com.xxl.tool.core.AssertTool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author xuxueli 2019-02-19
 */
public class ClassUtil {

    // ---------------------- class ----------------------

    private static final HashMap<String, Class<?>> primClasses = new HashMap<>();

    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
    }

    public static Class<?> resolveClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            Class<?> cl = primClasses.get(className);
            if (cl != null) {
                return cl;
            } else {
                throw ex;
            }
        }
    }


    // ---------------------- bean ----------------------

    /**
     * convert map-field to target javabean
     *
     * @param value   the value to convert
     * @param targetClass  target class
     * @return convert result, map-field will be converted to target javabean
     */
    public static Object convertMapFieldToBean(Object value, Class<?> targetClass) {
        if (value == null) {
            return null;
        }

        // skip if same type
        if (targetClass.isAssignableFrom(value.getClass())) {
            return value;
        }

        // 1、convert primitive type
        if (targetClass == boolean.class || targetClass == Boolean.class) {
            // boolean
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                return Boolean.valueOf(String.valueOf(value));
            }
        } else if (targetClass == byte.class || targetClass == Byte.class) {
            // byte
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            } else {
                return Byte.valueOf(String.valueOf(value));
            }
        } else if (targetClass == short.class || targetClass == Short.class) {
            // short
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            } else {
                return Short.valueOf(String.valueOf(value));
            }
        } else if (targetClass == int.class || targetClass == Integer.class) {
            // int
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                return Integer.valueOf(String.valueOf(value));
            }
        } else if (targetClass == long.class || targetClass == Long.class) {
            // long
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return Long.valueOf(String.valueOf(value));
            }
        } else if (targetClass == float.class || targetClass == Float.class) {
            // float
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else {
                return Float.valueOf(String.valueOf(value));
            }
        } else if (targetClass == double.class || targetClass == Double.class) {
            // double
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                return Double.valueOf(String.valueOf(value));
            }
        } else if (targetClass == char.class || targetClass == Character.class) {
            // char
            if (value instanceof Character) {
                return (Character) value;
            } else {
                String str = String.valueOf(value);
                return str.isEmpty() ? '\0' : str.charAt(0);
            }
        } else if (targetClass == String.class) {
            // string
            return value.toString();
        }

        // enum
        if (targetClass.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetClass, String.valueOf(value));
        }

        // 2、convert map
        if (value instanceof Map) {
            return mapToBean((Map<String, Object>) value, targetClass);
        }

        // 3、pass
        return value;
    }

    /**
     * convert Map to Bean
     *
     * @param map       map to convert
     * @param targetClass     target bean class
     * @param properties    properties to convert, null means all properties
     * @return target bean
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> targetClass, String... properties) {
        if (map == null || targetClass == null) {
            return null;
        }

        try {
            // new instance
            T instance = targetClass.getDeclaredConstructor().newInstance();
            // get all fields
            Field[] fields = getAllFields(targetClass, false);

            // property specified to convert
            Set<String> propertySet = new HashSet<>();
            if (properties != null && properties.length > 0) {
                propertySet.addAll(Arrays.asList(properties));
            }

            // convert map-entity 2 field
            for (Field field : fields) {
                // skip static and final fields
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                // skip properties if not specified
                if (!propertySet.isEmpty() && !propertySet.contains(field.getName())) {
                    continue;
                }

                // map 2 field
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    try {
                        field.setAccessible(true);
                        Object value = map.get(fieldName);

                        // convert 2 target class
                        Object convertedValue = convertMapFieldToBean(value, field.getType());

                        // write field value
                        field.set(instance, convertedValue);
                    } catch (Exception e) {
                        throw new RuntimeException("mapToBean error, failed to set field: " + fieldName, e);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + targetClass.getSimpleName(), e);
        }
    }


    /**
     * get all fields, contains current and parent class fields
     *
     * @param clazz     class to find fields
     * @param getFieldOrDeclared true use getFields, false use getDeclaredFields
     * @return all fields
     */
    public static Field[] getAllFields(Class<?> clazz, boolean getFieldOrDeclared) {
        AssertTool.notNull(clazz, "Class must not be null");
        return getFieldOrDeclared
                ? clazz.getFields()
                : clazz.getDeclaredFields();
    }

}
