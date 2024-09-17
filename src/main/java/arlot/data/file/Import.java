package arlot.data.file;



import javax.management.openmbean.KeyAlreadyExistsException;
import java.lang.reflect.Method;
import java.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class DynamicClassLoader extends ClassLoader {
    public Class<?> loadClassFromFile(String filePath) throws IOException, ClassNotFoundException {
        byte[] classData = Files.readAllBytes(Paths.get(filePath));
        String className = extractClassName(filePath);
        return defineClass(className, classData, 0, classData.length);
    }

    private String extractClassName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}

class ClassInstance {
    private Class<?> cls;
    private Object clsInstance;

    private Map<String, Method> methods;

    public ClassInstance(Class<?> clazz, Object... initargs) throws Exception {
        this.cls = clazz;
        this.clsInstance = this.cls.getDeclaredConstructor().newInstance(initargs);
        this.methods = new HashMap<>();
        for (Method method : this.cls.getMethods()) {
            this.methods.put(method.getName(), method);
        }
    }

    public Method getMethod(String methodName) throws NoSuchMethodException {
        Method method = this.methods.get(methodName);
        if (method == null) {
            throw new NoSuchMethodException("Method identified via the following name does not exist: " + methodName);
        }
        return method;
    }
    public Object callMethod(String methodName, Object... args) throws Exception {
        Method method = this.getMethod(methodName);
        return method.invoke(this.clsInstance, args);
    }

    public Class<?> getCls() {
        return this.cls;
    }
    public Object getInstance() {
        return this.clsInstance;
    }
}

class ClassHolder {
    private Map<String, Class<?>> importedClasses;
    private DynamicClassLoader classLoader;

    private ArrayList<String> keys = new ArrayList<>();

    public String add(String key, String className) throws ClassNotFoundException, KeyAlreadyExistsException {
        Class<?> clazz = Class.forName(className);
        key = (key==null?className:key);
        if (keys.contains(key)) {
            throw new KeyAlreadyExistsException("The provided key already exists: "+key);
        } else {
            keys.add(key);
        }
        importedClasses.put(key, clazz);
        return key;
    }
    public String add(String className) throws ClassNotFoundException {
        return this.add(className, className);
    }

    public String addFromFile(String key, String filePath) throws IOException, ClassNotFoundException, KeyAlreadyExistsException {
        Class<?> clazz = classLoader.loadClassFromFile(filePath);
        String usingKey = (key==null?clazz.getName():key);
        if (keys.contains(usingKey)) {
            throw new KeyAlreadyExistsException("The provided key already exists: "+usingKey);
        } else {
            keys.add(key);
        }
        importedClasses.put(usingKey, clazz);
        return usingKey;
    }
    public String addFromFile(String filePath) throws IOException, ClassNotFoundException {
        return this.addFromFile(null, filePath);
    }

    public ClassInstance createInstance(String key, Object... initargs) throws Exception {
        Class<?> clazz = this.get(key);
        return new ClassInstance(clazz, initargs);
    }

    public ClassHolder() {
        this.importedClasses = new HashMap<>();
        this.classLoader = new DynamicClassLoader();
    }

    public Class<?> get(String key) throws ClassNotFoundException {
        Class<?> clazz = importedClasses.get(key);
        if (clazz == null) {
            throw new ClassNotFoundException("Class identified via the following key was not imported: " + key);
        }
        return clazz;
    }
}

public class Import {
    public static final String[] TYPES = new String[]{"java", "json", "python"};

    private static final ClassHolder classHolder = new ClassHolder();

    public static Class<?> getFromClasses(String key) throws ClassNotFoundException {
        return classHolder.get(key);
    }

    private String type;
    private String key;
    private String path;

    public Import() {
        this.type = "empty";
        this.key = null;
        this.path = null;
    }
    public Import(String type, String key, String path) throws Exception {
        if (type==null) {
            throw new IllegalArgumentException("The `type` argument cannot be null.");
        }
        if (path==null) {
            throw new IllegalArgumentException("The `path` argument cannot be null.");
        }
        update(type, key, path);
    }
    public Import(String type, String path) throws Exception {
        new Import(type, null, path);
    }

    public ClassInstance newInstance(Object... initargs) throws Exception {
        return classHolder.createInstance(this.key, initargs);
    }

    public void update(String type, String key, String path) throws Exception {
        if (type!=null) {
            this.type = type;
        }
        if (!Arrays.stream(TYPES).toList().contains(this.type)) {
            throw new IllegalArgumentException("The provided type is illegal and can't be imported: "+this.type);
        }
        if (key!=null) {
            this.key = key;
        }
        if (path!=null) {
            this.path = path;
        }
        if (Objects.equals(this.type, TYPES[0])) { // java
            try {
                this.key = classHolder.add(this.key, this.path);
            } catch (ClassNotFoundException e) {
                try {
                    this.key = classHolder.addFromFile(this.key, this.path);
                } catch (ClassNotFoundException e2) {
                    throw new Exception("Class provided (via filePath or className) as `path` doesn't exist: "+this.path);
                }
            }
        } else if (Objects.equals(this.type, TYPES[1])) { // json

        }
    }
    public void update(String key, String path) throws Exception {
        update(null, key, path);
    }
    public void updateKey(String key) throws Exception {
        update(null, key, null);
    }
    public void updatePath(String path) throws Exception {
        update(null, null, path);
    }

    public Class<?> getCls() throws ClassNotFoundException {
        return getFromClasses(this.key);
    }
    public String getType() {
        return this.type;
    }
    public String getName() {
        return this.key;
    }
}
