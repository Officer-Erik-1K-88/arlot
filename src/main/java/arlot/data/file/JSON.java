package arlot.data.file;

import arlot.data.Data;
import arlot.data.Info;
import arlot.error.FileReadingException;
import arlot.error.FileUpdateException;
import arlot.error.NotImplementedException;

import java.io.Serial;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import java.io.Serializable;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

/**
 * Handles JSON files.
 */
public class JSON extends Info implements Serializable {
    @Serial
    private static final long serialVersionUID = 623486887L;

    /**
     * Turns a JSON file into a JSON object.
     * <br>
     * The JSON file should be formatted like the following:
     * <br>
     * <ul>
     *     <li>All data must be strings.</li>
     *     <li>In each string there cannot be any " as it probably will be removed.</li>
     *     <li>Primitive (and String) data is stated like the following: "[data type] -;- [The data]"</li>
     *     <li>
     *         Non-primitive data is formatted like the following: "Base64 Data Value | [The base64 representation of the data]"
     *         <br>
     *         This is only to be done with data that is translated to base64 using {@link Data}'s {@code formattedString()}.
     *     </li>
     *     <li>Nested JSON objects are as JSON that follows the above parameters as well.</li>
     * </ul>
     * <hr><br>
     * An example of a formatted file:
     * <br>
     * <pre>
     *     {
     *         "test": {
     *             "subTest": {
     *                 "subSubTest": "String -;- This is a nested string."
     *             }
     *         },
     *         "test2": {
     *             "subTest": "Integer -;- 553676"
     *         },
     *         "test3": "Double -;- 564.3344"
     *     }
     * </pre>
     * <hr><br>
     * An example of an unformatted file:
     * <br>
     * <pre>
     *     {"test":{"subTest":{"subSubTest":"String -;- This is a nested string."}},"test2":{"subTest":"Integer -;- 553676"},"test3":"Double -;- 564.3344"}
     * </pre>
     * <hr><br>
     * To get a JSON file string from a JSON object then use {@link #toString()} for formatted
     * and use {@link #toUnformattedString()} for unformatted.
     * @param jsonString The JSON file as a text file to convert.
     * @param name The name of the JSON file.
     * @param formatted If the jsonString is formatted with line breaks and tabs then this should be set to true.
     * @return The JSON object representation of the provided JSON file as a string.
     * @see #toString()
     * @see #toUnformattedString()
     */
    public static JSON stringToJSON(String jsonString, String name, boolean formatted) {
        JSON jsonFile = new JSON(name);
        jsonString = jsonString.substring(1, jsonString.length()-1);
        if (formatted) {
            String[] lines = jsonString.split("\n");
            StringBuilder nestKey = new StringBuilder();
            for (String line : lines) {
                line = line.strip();
                if (line.isBlank()) {
                    continue;
                }
                if (line.endsWith(",")) {
                    line = line.substring(0, line.length()-1);
                }
                if (line.endsWith("}")) {
                    int lastDot = nestKey.lastIndexOf(".");
                    if (lastDot < 0) {
                        lastDot = 0;
                    }
                    nestKey.delete(lastDot, nestKey.length());
                    continue;
                }
                String[] keyVal = line.split(": ");
                String key = "";
                if (!nestKey.isEmpty()) {
                    key = nestKey + ".";
                }
                key += keyVal[0].replace("\"","").strip();
                String value = keyVal[1];
                if (value.isBlank()) {
                    continue;
                }
                if (value.startsWith("{")) {
                    if (!nestKey.isEmpty()) {
                        nestKey = new StringBuilder(key);
                    } else {
                        nestKey.append(key);
                    }
                } else {
                    value = value.replace("\"","");
                    if (value.startsWith("Base64 Data Value | ")) {
                        value = value.replace("Base64 Data Value | ", "");
                        jsonFile.put(key, Data.toData(value));
                    } else {
                        keyVal = value.split(" -;- ");
                        String type = keyVal[0]
                                .toLowerCase()
                                .replace("\"", "")
                                .strip();
                        value = keyVal[1];
                        if (type.equals("char")||type.equals("character")) {
                            jsonFile.put(key, new Data<Character>(value.charAt(0)));
                        } else if (type.equals("int")||type.equals("integer")) {
                            jsonFile.put(key, new Data<Integer>(Integer.parseInt(value)));
                        } else if (type.equals("long")) {
                            jsonFile.put(key, new Data<Long>(Long.parseLong(value)));
                        } else if (type.equals("short")) {
                            jsonFile.put(key, new Data<Short>(Short.parseShort(value)));
                        } else if (type.equals("double")) {
                            jsonFile.put(key, new Data<Double>(Double.parseDouble(value)));
                        } else if (type.equals("float")) {
                            jsonFile.put(key, new Data<Float>(Float.parseFloat(value)));
                        } else if (type.equals("string")) {
                            jsonFile.put(key, new Data<String>(value));
                        } else if (type.equals("byte")) {
                            jsonFile.put(key, new Data<Byte>(Byte.parseByte(value)));
                        }
                    }
                }
            }
        } else {
            throw new NotImplementedException("`formatted` as `false` has not been implemented yet.");
        }
        return jsonFile;
    }

    /**
     * Same as {@link #stringToJSON(String, String, boolean)} but loads the String from an actual JSON file.
     * @param filePath The path to th json file.
     * @param formatted If the jsonString is formatted with line breaks and tabs then this should be set to true.
     * @return The JSON object representation of the provided JSON file as a string.
     * @see #stringToJSON(String, String, boolean)
     */
    public static JSON fromFile(Path filePath, boolean formatted) {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileReadingException("Failed to read file properly: "+e.getMessage());
        }
        StringBuilder path = new StringBuilder(filePath.getFileName().toString());
        String[] pathSegment = path.toString().split("\\.");
        path = new StringBuilder();
        for (int i=0; i<pathSegment.length; i++) {
            if (i==pathSegment.length-1) {
                break;
            }
            path.append(pathSegment[i]);
        }
        String jsonString = String.join("\n", lines);
        return stringToJSON(jsonString, path.toString(), formatted);
    }
    // properties
    /**
     * Holds the JSON object data.
     */
    private final Data<Map<String, Data<?>>> map;
    /**
     * The path to the directory that holds the JSON file,
     * where the said file is named after the provided JSON object name,
     * that represents this JSON object.
     */
    private String directory;

    // constructors

    /**
     * Creates an empty JSON object with the given name.
     * <br>
     * The given name must be the same as the JSON file's name
     * without the {@code .json} file extension.
     * @param name The name to give this JSON object.
     */
    public JSON(String name) {
        super(name, "This is a JSON Object.");
        this.map = new Data<>(new HashMap<>());
        this.directory = "";
    }
    /**
     * Creates a data filled JSON object with the given name.
     * <br>
     * The given name must be the same as the JSON file's name
     * without the {@code .json} file extension.
     * @param name The name to give this JSON object.
     * @param data The data to use as the JSON object.
     */
    public JSON(String name, Map<String, Data<?>> data) {
        super(name, "This is a JSON Object.");
        this.map = new Data<>(new HashMap<>());
        if (data != null) {
            update(data);
        }
        this.directory = "";
    }
    /**
     * Creates a copy of a JSON object that was provided.
     * @param json The JSON object to make a copy of.
     */
    public JSON(JSON json) {
        super(json.getName(), "This is a JSON Object.");
        this.map = new Data<>(json.map);
        this.directory = json.directory;
    }
    /**
     * Creates a copy of the provided JSON object with a new name.
     * <br>
     * The given name must be the same as the JSON file's name
     * without the {@code .json} file extension.
     * @param json The JSON object to make a copy of.
     * @param name The name to give this JSON object.
     */
    public JSON(JSON json, String name) {
        super(name, "This is a JSON Object.");
        this.map = json.map;
        this.directory = json.directory;
    }

    /**
     * Creates an empty unnamed JSON object.
     * @see #JSON(String)
     * @see #JSON(String, Map)
     * @see #JSON(JSON)
     * @see #JSON(JSON, String)
     */
    protected JSON() {
        super("Unnamed?", "This is a protected testing JSON Object.");
        this.map = new Data<>(new HashMap<>());
        this.directory = "";
    }

    // methods

    /**
     * Gets the name that was set to this JSON file.
     * @return The set name of the JSON file.
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Returns a {@link Set} of keys contained in this JSON object.
     * @return A set view of the keys contained in this JSON object.
     */
    public Set<String> getKeys() {
        return map.getValue().keySet();
    }

    /**
     * Get data from a path of keys.
     * <br>
     * {@code key} can be a single level or multiple levels.
     * The {@code key} path can be seperated by one or more of the following:
     * <br>
     * '.', '\', '/'
     * <br><br>
     * This means that those three characters can't be used in a key.
     * <br>
     * Here is an example:
     * <br>
     * <pre>
     *     Map<String, Object> jsonData = new HashMap<>();
     *     jsonData.put("test", new HashMap<>());
     *     jsonData.get("test").put("subTest", new HashMap<>());
     *     jsonData.get("test").get("subTest").put("subSubTest", "This is deep text.");
     *     jsonData.put("test2", new HashMap<>());
     *     jsonData.get("test2").put("subTest2", "This be text.");
     *     jsonData.put("test3", "Some Text");
     *     JSON jsonHolder = new JSON("holding", jsonData);
     *     System.out.println(jsonHolder.get("test.subTest.subSubTest")); // will print "This is deep text."
     * </pre>
     * @param key The key path to get.
     * @return A {@link Data} value that represents the given data of the {@code key}. If the given {@code key} path doesn't exist, then {@code null} will be returned.
     */
    public Data<?> get(String key) {
        if (key.contains(".") || key.contains("/") || key.contains("\\")) {
            String[] keys = key
                    .replace("\\", ".")
                    .replace("/", ".")
                    .split("\\.");
            return getNestedValue(keys, 0, this);
        } else {
            try {
                return this.map.getValue().get(key);
            } catch (NullPointerException e) {
                return null;
            }
        }
    }

    /**
     * This is the function that allows {@link #get(String)} to fetch data from nested levels.
     * @param keys The path of keys to the object that's being fetched.
     * @param index The current level in the search.
     * @param current The JSON object that the current level is.
     * @return A {@link Data} value that represents the given data of the key path. If the given {@code key} path doesn't exist, then {@code null} will be returned.
     * @see #get(String)
     */
    protected Data<?> getNestedValue(String[] keys, int index, JSON current) {
        try {
            Data<?> value = current.map.getValue().get(keys[index]);
            if (index == keys.length - 1 || !(value.dataClass().isInstance(current))) {
                return value;
            } else {
                return getNestedValue(keys, index + 1, (JSON) value.getValue());
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Replace the saved directory path with a new one.
     * @param path The directory path to change the current path with.
     */
    public void changeDirectory(String path) {
        this.directory = path;
    }

    /**
     * This method adds key-value pairs to the JSON object.
     * It will even change the values of the keys that already exist.
     * <br>
     * Note: The data passed to this method may be changed during it's addition to the JSON object.
     * @param data The data to add/update.
     */
    public void update(Map<String, Data<?>> data) {
        for (String key : data.keySet()) {
            if (data.get(key) == null) {
                data.replace(key, new Data<>(data.get(key)));
            }
            Data<?> value = data.get(key);
            if (value.getValue() instanceof Map) {
                Map<String, Data<?>> newVal = new HashMap<>();
                ((Map<?,?>) value.getValue()).forEach((key2, value2) -> {
                    newVal.put(key2.toString(), new Data<>(value2));
                });
                data.replace(key, new Data<>(new JSON(key, newVal)));
            }
            String[] keys;
            if (key.contains(".") || key.contains("/") || key.contains("\\")) {
                keys = key
                        .replace("\\", ".")
                        .replace("/", ".")
                        .split("\\.");
            } else {
                keys = new String[]{key};
            }
            JSON current = this;
            for (int i=0; i<keys.length; i++) {
                if (i == keys.length - 1) {
                    if (current.get(keys[i]).getValue() instanceof JSON) {
                        if (data.get(key).getValue() instanceof JSON) {
                            Map<String, Data<?>> newData = new HashMap<>(((JSON) data.get(key).getValue()).map.getValue());
                            current.update(newData);
                        } else {
                            current.put(keys[i], data.get(key));
                        }
                    } else {
                        current.put(keys[i], data.get(key));
                    }
                } else {
                    Data<?> nestedMap = current.get(keys[i]);
                    if (!(nestedMap.getValue() instanceof JSON)) {
                        nestedMap = new Data<>(new JSON(keys[i]));
                        current.put(keys[i], nestedMap);
                    }
                    current = (JSON) nestedMap.getValue();
                }
            }
        }
    }

    /**
     * Add/update data from a path of keys.
     * <br>
     * {@code key} can be a single level or multiple levels.
     * The {@code key} path can be separated by one or more of the following:
     * <br>
     * '.', '\', '/'
     * <br><br>
     * This means that those three characters can't be used in a key.
     * <br>
     * Here is an example:
     * <br>
     * <pre>
     *     JSON jsonHolder = new JSON("holding");
     *     jsonHolder.put("test.subTest.subSubTest", new Data<String>("This is deep text."));
     *     jsonHolder.put("test2.subTest2", new Data<String>("This be text."));
     *     jsonHolder.put("test3", new Data<String>("Some Text"));
     *     System.out.println(jsonHolder.get("test.subTest.subSubTest")); // will print "This is deep text."
     * </pre>
     * @param key The key path to add/update.
     */
    public void put(String key, Data<?> value) {
        if (key.contains(".") || key.contains("/") || key.contains("\\")) {
            String[] keys = key
                    .replace("\\", ".")
                    .replace("/", ".")
                    .split("\\.");
            putNestedValue(keys, 0, this, value);
        } else {
            this.map.getValue().put(key, value);
        }
    }
    /**
     * This is the method that allows {@link #put(String, Data)} to add/update data from nested levels.
     * @param keys The path of keys to the object that's being added/updated.
     * @param index The current level in the search.
     * @param current The JSON object that the current level is.
     * @see #put(String, Data)
     */
    protected void putNestedValue(String[] keys, int index, JSON current, Data<?> value) {
        if (index == keys.length - 1) {
            current.put(keys[index], value);
        } else {
            Data<?> nestedMap = current.get(keys[index]);
            if (nestedMap == null || !(nestedMap.getValue() instanceof JSON)) {
                nestedMap = new Data<>(new JSON(keys[index]));
                current.put(keys[index], nestedMap);
            }
            putNestedValue(keys, index + 1, (JSON) nestedMap.getValue(), value);
        }
    }

    /**
     * Creates/updates a file with the given directory path.
     * <br>
     * The directory path given mustn't include the file at the end, it'll be added by the method.
     * @param directory The path to the directory where this JSON file will be added to.
     * @return true if the JSON file with the given {@code name} of this JSON object, was created/updated. Otherwise, false if the file wasn't created or updated.
     */
    public boolean toFile(String directory) {
        if (getName().equals("Unnamed?")) {
            throw new FileUpdateException("Cannot create/update an unnamed JSON file.");
        }
        Path filePath = Paths.get(directory, getName()+".json");
        try {
            List<String> lines = Arrays.asList(toString().split("\n"));
            Files.write(filePath, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    /**
     * Creates/updates a file with the save directory path.
     * @return true if the JSON file with the given {@code name} of this JSON object, was created/updated. Otherwise, false if the file wasn't created or updated.
     */
    public boolean toFile() {
        return toFile(this.directory);
    }

    /**
     * Returns a formatted (ie with line breaks and tabs) String representation of this
     * JSON file.
     * @return A string with readability formatting that is a representation of this JSON file.
     * @see #toUnformattedString()
     * @see #stringToJSON(String, String, boolean)
     */
    @Override
    public String toString() {
        return toFormattedString(1);
    }

    protected String toFormattedString(int indent) {
        StringBuilder jsonFile = new StringBuilder();
        jsonFile.append('{');
        for (String key: getKeys()) {
            Data<?> value = get(key);
            jsonFile.append("\n")
                    .append("\t".repeat(indent))
                    .append('"');
            jsonFile.append(key);
            jsonFile.append("\": ");
            if (value.isPrimitive() || value.getValue() instanceof String) {
                jsonFile.append('"');
                jsonFile.append(value.dataClass().getSimpleName());
                jsonFile.append(" -;- ");
                jsonFile.append(value);
                jsonFile.append('"');
            } else {
                if (value.instance(new JSON())) {
                    jsonFile.append(((JSON) value.getValue()).toFormattedString(indent+1));
                } else {
                    jsonFile.append("\"Base64 Data Value | ");
                    jsonFile.append(value.formattedString());
                    jsonFile.append('"');
                }
            }
            jsonFile.append(',');
        }
        jsonFile.deleteCharAt(jsonFile.length()-1);
        jsonFile.append("\n");
        jsonFile.append("\t".repeat(indent-1));
        jsonFile.append('}');
        return jsonFile.toString();
    }

    /**
     * Returns an unformatted (ie no line breaks and tabs) String representation of this
     * JSON file.
     * @return A string without readable formatting that is a representation of this JSON file.
     * @see #toString()
     * @see #stringToJSON(String, String, boolean)
     */
    public String toUnformattedString() {
        StringBuilder jsonFile = new StringBuilder();
        jsonFile.append('{');
        for (String key: getKeys()) {
            Data<?> value = get(key);
            jsonFile.append('"');
            jsonFile.append(key);
            jsonFile.append("\":");
            if (value.isPrimitive()  || value.getValue() instanceof String) {
                jsonFile.append('"');
                jsonFile.append(value.dataClass().getSimpleName());
                jsonFile.append(" -;- ");
                jsonFile.append(value);
                jsonFile.append('"');
            } else {
                if (value.instance(new JSON())) {
                    jsonFile.append(value.getValue().toString());
                } else {
                    jsonFile.append("\"Base64 Data Value | ");
                    jsonFile.append(value.formattedString());
                    jsonFile.append('"');
                }
            }
            jsonFile.append(',');
        }
        jsonFile.deleteCharAt(jsonFile.length()-1);
        jsonFile.append('}');
        return jsonFile.toString();
    }

    /**
     * Translates this JSON file to base64 using {@link Data}
     * with it's {@code formattedString()} method.
     * @return A base64 representation of this JSON file.
     */
    public String toBase64() {
        return new Data<>(this).formattedString();
    }
}
