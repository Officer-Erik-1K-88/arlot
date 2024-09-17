package arlot.roleplay;

import arlot.data.tag.Tag;
import arlot.data.tag.TagHolder;

import arlot.roleplay.info.Character;
import arlot.roleplay.info.Characters;
import arlot.roleplay.info.Item;
import arlot.roleplay.info.Items;


public class RolePlay {
    private final TagHolder tags = Tag.all();
    private final Items items;
    private final Characters characters;
    public RolePlay() {
        items = new Items();
        characters = new Characters();
        addTag("character", "Character", "Defines that some data is a playable character.");
        addTag("item", "Item", "Defines that some data is an item that a character can obtain.");
    }

    public void addTag(String name, String title, String definition) {
        tags.add(name, title, definition);
    }
    public Tag getTag(String name) {
        return tags.get(name);
    }
    public String[] getAllTags() {
        return tags.getAllTags();
    }

    public Items getItems() {
        return items;
    }

    public Item addItem(Item item) {
        return items.add(item);
    }
    public Item addItem(String key, Item item) throws Exception {
        return items.add(key, item);
    }

    public Characters getCharacters() {
        return characters;
    }
    public Character getCharacter(String key) throws Exception {
        return characters.get(key);
    }

    public Character addCharacter(Character character) {
        return characters.add(character);
    }
    public Character addCharacter(String key, Character character) throws Exception {
        return characters.add(key, character);
    }
}
