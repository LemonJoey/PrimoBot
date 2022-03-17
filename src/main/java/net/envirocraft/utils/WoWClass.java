package net.envirocraft.utils;

import java.awt.*;

public enum WoWClass {
    Death_Knight(new Color(196, 30, 58)),
    Demon_Hunter(new Color(163, 48, 201)),
    Druid(new Color(255, 124, 10)),
    Hunter(new Color(170, 211, 144)),
    Mage(new Color(63, 199, 235)),
    Monk(new Color(0, 255, 152)),
    Paladin(new Color(244, 140, 186)),
    Priest(new Color(255, 255, 255)),
    Rogue(new Color(255, 244, 104)),
    Shaman(new Color(0, 112, 221)),
    Warlock(new Color(135, 136, 238)),
    Warrior(new Color(198, 155, 109));

    private final Color color;

    WoWClass(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
