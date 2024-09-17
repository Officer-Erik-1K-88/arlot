package arlot.roleplay.info;

import arlot.error.GetValueException;
import arlot.math.Basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class Characters {
    // properties
    private final List<Character> characters;
    private final Map<String, Character> mappedCharacters;
    // constructors
    public Characters() {
        characters = new ArrayList<>();
        mappedCharacters = new HashMap<>();
    }
    public Characters(Collection<Character> listed) {
        characters = new ArrayList<>(listed);
        mappedCharacters = new HashMap<>();
    }
    public Characters(Map<String, Character> mapped) {
        characters = new ArrayList<>();
        mappedCharacters = new HashMap<>(mapped);
    }
    public Characters(Collection<Character> listed, Map<String, Character> mapped) {
        characters = new ArrayList<>(listed);
        mappedCharacters = new HashMap<>(mapped);
    }
    // methods
    public Character add(Character character) {
        this.characters.add(character);
        return character;
    }
    public Character add(String name, String description, Map<String, Object> data) {
        Character character = new Character(name, description, data);
        return add(character);
    }
    public Character add(String key, Character character) throws Exception {
        catchKey(key);
        this.mappedCharacters.put(key, character);
        return character;
    }
    public Character add(String key, String name, String description, Map<String, Object> data) throws Exception {
        catchKey(key);
        Character character = new Character(name, description, data);
        return add(key, character);
    }

    public Character get(int index) throws Exception {
        index = Math.toIntExact(Basic.nortic((long) index, (long) characters.size()));
        if (index>characters.size()-1) {
            throw new GetValueException("An unexpected error caused the index to be greater than amount of characters stored: "+index);
        }
        return characters.get(index);
    }
    public Character get(String key) throws Exception {
        if (!containsKey(key)) {
            throw new GetValueException("No character is defined by the provided key: "+key);
        }
        return mappedCharacters.get(key);
    }

    public Character remove(int index) throws Exception {
        index = Math.toIntExact(Basic.nortic((long) index, (long) characters.size()));
        Character ret = characters.get(index);
        characters.remove(index);
        return ret;
    }
    public Character remove(String key) throws Exception {
        Character ret = get(key);
        mappedCharacters.remove(key);
        return ret;
    }

    public boolean containsKey(String key) {
        return mappedCharacters.containsKey(key);
    }
    protected void catchKey(String key) throws Exception {
        if (containsKey(key)) {
            throw new Exception("Cannot override characters stored. The provided key already exists: "+key);
        }
    }
}
