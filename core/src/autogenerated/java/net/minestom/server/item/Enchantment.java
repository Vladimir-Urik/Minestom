package net.minestom.server.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.registry.Registries;
import net.minestom.server.utils.NamespaceID;

/**
 * //==============================
 * //  AUTOGENERATED BY EnumGenerator
 * //==============================
 */
@SuppressWarnings({"deprecation"})
public enum Enchantment implements Keyed {
    PROTECTION("minecraft:protection"),

    FIRE_PROTECTION("minecraft:fire_protection"),

    FEATHER_FALLING("minecraft:feather_falling"),

    BLAST_PROTECTION("minecraft:blast_protection"),

    PROJECTILE_PROTECTION("minecraft:projectile_protection"),

    RESPIRATION("minecraft:respiration"),

    AQUA_AFFINITY("minecraft:aqua_affinity"),

    THORNS("minecraft:thorns"),

    DEPTH_STRIDER("minecraft:depth_strider"),

    FROST_WALKER("minecraft:frost_walker"),

    BINDING_CURSE("minecraft:binding_curse"),

    SOUL_SPEED("minecraft:soul_speed"),

    SHARPNESS("minecraft:sharpness"),

    SMITE("minecraft:smite"),

    BANE_OF_ARTHROPODS("minecraft:bane_of_arthropods"),

    KNOCKBACK("minecraft:knockback"),

    FIRE_ASPECT("minecraft:fire_aspect"),

    LOOTING("minecraft:looting"),

    SWEEPING("minecraft:sweeping"),

    EFFICIENCY("minecraft:efficiency"),

    SILK_TOUCH("minecraft:silk_touch"),

    UNBREAKING("minecraft:unbreaking"),

    FORTUNE("minecraft:fortune"),

    POWER("minecraft:power"),

    PUNCH("minecraft:punch"),

    FLAME("minecraft:flame"),

    INFINITY("minecraft:infinity"),

    LUCK_OF_THE_SEA("minecraft:luck_of_the_sea"),

    LURE("minecraft:lure"),

    LOYALTY("minecraft:loyalty"),

    IMPALING("minecraft:impaling"),

    RIPTIDE("minecraft:riptide"),

    CHANNELING("minecraft:channeling"),

    MULTISHOT("minecraft:multishot"),

    QUICK_CHARGE("minecraft:quick_charge"),

    PIERCING("minecraft:piercing"),

    MENDING("minecraft:mending"),

    VANISHING_CURSE("minecraft:vanishing_curse");

    private final String namespaceID;

    private final Key key;

    Enchantment(String namespaceID) {
        this.namespaceID = namespaceID;
        Registries.enchantments.put(NamespaceID.from(namespaceID), this);
        this.key = Key.key(this.namespaceID);
    }

    public int getId() {
        return ordinal();
    }

    public String getNamespaceID() {
        return namespaceID;
    }

    public Key key() {
        return this.key;
    }

    public static Enchantment fromId(int id) {
        if (id >= 0 && id < values().length) {
            return values()[id];
        }
        return null;
    }
}