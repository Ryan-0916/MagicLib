package com.magicrealms.magiclib.common.utils;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public final class SerializationUtils {

    private SerializationUtils() {}

    static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
        private static final Map<String, Class<?>> PRIMITIVE_TYPES = new HashMap<>();

        static {
            PRIMITIVE_TYPES.put("byte", byte.class);
            PRIMITIVE_TYPES.put("short", short.class);
            PRIMITIVE_TYPES.put("int", int.class);
            PRIMITIVE_TYPES.put("long", long.class);
            PRIMITIVE_TYPES.put("float", float.class);
            PRIMITIVE_TYPES.put("double", double.class);
            PRIMITIVE_TYPES.put("boolean", boolean.class);
            PRIMITIVE_TYPES.put("char", char.class);
            PRIMITIVE_TYPES.put("void", void.class);
        }

        private final ClassLoader classLoader;

        ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
            super(in);
            this.classLoader = classLoader;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String className = desc.getName();
            try {
                return Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException e) {
                Class<?> primitiveClass = PRIMITIVE_TYPES.get(className);
                if (primitiveClass != null) {
                    return primitiveClass;
                }
                return super.resolveClass(desc);
            }
        }
    }

    public static <T extends Serializable> T clone(T object) {
        if (object == null) {
            return null;
        }
        byte[] objectData = serialize(object);
        try (ByteArrayInputStream basis = new ByteArrayInputStream(objectData);
             ClassLoaderAwareObjectInputStream ois = new ClassLoaderAwareObjectInputStream(basis, object.getClass().getClassLoader())) {
            @SuppressWarnings("unchecked")
            T clonedObject = (T) ois.readObject();
            return clonedObject;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Exception occurred while cloning object", e);
        }
    }

    public static <T> T deserialize(byte[] objectData) {
        Objects.requireNonNull(objectData);
        return deserialize(new ByteArrayInputStream(objectData));
    }

    public static <T> T deserialize(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            @SuppressWarnings("unchecked")
            T obj = (T) ois.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Exception occurred while deserializing object", e);
        }
    }

    public static byte[] serialize(Serializable obj) {
        try (ByteArrayOutputStream bas = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bas)) {
            oos.writeObject(obj);
            return bas.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while serializing object", e);
        }
    }

    public static String serializeByBase64(Serializable obj) {
        return Base64.getEncoder().encodeToString(serialize(obj));
    }

    public static <T> T deserializeByBase64(String base64) {
        return deserialize(Base64.getDecoder().decode(base64));
    }
}
