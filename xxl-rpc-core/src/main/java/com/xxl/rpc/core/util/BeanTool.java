package com.xxl.rpc.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * bean tool
 * 
 * @author xuxueli 2025-11-30
 */
public class BeanTool {

    // ---------------------- convert object vs primitive ----------------------

    /**
     * convert object to primitive, support primitive, map, collection, bean
     *
     * @param value   the value to convert
     * @return convert result, primitive or map or collection; complex object will be converted to map;
     */
    public static Object objectToPrimitive(Object value) {
        // parse complex object, such as Collection, Map, Bean;
        if (value!=null && !isPrimitive(value.getClass())) {
            if (value instanceof Collection collection) {
                // convert collection
                ArrayList<Object> result = new ArrayList<>();
                for (Object item : collection) {
                    item = objectToPrimitive(item);
                    result.add(item);
                }

                value = result;
            } else if (value instanceof Map map) {
                // convert map
                Map<Object, Object> result = new HashMap<>();
                for (Object mapKey : map.entrySet()) {
                    Object convertedKey = objectToPrimitive(mapKey);
                    Object convertedValue = objectToPrimitive(map.get(mapKey));

                    result.put(convertedKey, convertedValue);
                }

                value = result;
            } else {
                // convert bean
                value = beanToMap(value);
            }
        }
        return value;
    }

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

    // ---------------------- convert bean vs map ----------------------

    /**
     * convert Bean to Map
     *
     * @param bean          bean to convert
     * @param properties    properties to convert, null means all properties
     * @return map contains all bean properties
     */
    public static Map<String, Object> beanToMap(Object bean, String... properties) {
        // valid
        if (bean == null) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();

        // get all fields
        Field[] fields = getAllFields(bean.getClass());

        // property specified to convert
        Set<String> propertySet = new HashSet<>();
        if (properties != null && properties.length > 0) {
            propertySet.addAll(Arrays.asList(properties));
        }

        // convert field 2 map-entity
        for (Field field : fields) {
            // skip static fields
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // skip properties if not specified
            if (!propertySet.isEmpty() && !propertySet.contains(field.getName())) {
                continue;
            }

            // field 2 map
            try {
                field.setAccessible(true);
                Object value = field.get(bean);

                // convert 2 primitive or map
                value = objectToPrimitive(value);

                // write field value
                resultMap.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("beanToMap error, failed to get field value: " + field.getName(), e);
            }
        }

        return resultMap;
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
            Field[] fields = getAllFields(clazz);

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


    // ---------------------- class ----------------------

    /**
     * get all fields, contains current and parent class fields
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList.toArray(new Field[0]);
    }

    /**
     * is primitive, include wrapper class
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz==null || clazz.isPrimitive()) {
            return true;
        }

        return clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == String.class;
    }

}
