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
     * convert primitive(+map/enum) to target type
     *
     * @param value   the value to convert
     * @param targetType  target type
     * @return convert result
     */
    public static Object primitiveToTargetClass(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // skip if same type
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        // 1、convert primitive type
        if (targetType == boolean.class || targetType == Boolean.class) {
            // boolean
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                return Boolean.valueOf(String.valueOf(value));
            }
        } else if (targetType == byte.class || targetType == Byte.class) {
            // byte
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            } else {
                return Byte.valueOf(String.valueOf(value));
            }
        } else if (targetType == short.class || targetType == Short.class) {
            // short
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            } else {
                return Short.valueOf(String.valueOf(value));
            }
        } else if (targetType == int.class || targetType == Integer.class) {
            // int
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                return Integer.valueOf(String.valueOf(value));
            }
        } else if (targetType == long.class || targetType == Long.class) {
            // long
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return Long.valueOf(String.valueOf(value));
            }
        } else if (targetType == float.class || targetType == Float.class) {
            // float
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else {
                return Float.valueOf(String.valueOf(value));
            }
        } else if (targetType == double.class || targetType == Double.class) {
            // double
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                return Double.valueOf(String.valueOf(value));
            }
        } else if (targetType == char.class || targetType == Character.class) {
            // char
            if (value instanceof Character) {
                return (Character) value;
            } else {
                String str = String.valueOf(value);
                return str.isEmpty() ? '\0' : str.charAt(0);
            }
        } else if (targetType == String.class) {
            // string
            return value.toString();
        }

        // enum
        if (targetType.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetType, String.valueOf(value));
        }

        // 2、convert map
        if (value instanceof Map) {
            return mapToBean((Map<String, Object>) value, targetType);
        }

        // 3、pass
        return value;
    }

    /**
     * convert Map to Bean
     *
     * @param map       map to convert
     * @param clazz     target bean class
     * @param properties    properties to convert, null means all properties
     * @return target bean
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz, String... properties) {
        if (map == null || clazz == null) {
            return null;
        }

        try {
            // new instance
            T instance = clazz.getDeclaredConstructor().newInstance();
            // get all fields
            Field[] fields = getAllFields(clazz, false);

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
                        Object convertedValue = primitiveToTargetClass(value, field.getType());

                        // write field value
                        field.set(instance, convertedValue);
                    } catch (Exception e) {
                        throw new RuntimeException("mapToBean error, failed to set field: " + fieldName, e);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getSimpleName(), e);
        }
    }


    // ---------------------- reflection ----------------------

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
