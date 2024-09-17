package arlot.user;

import java.util.Locale;

public enum Colors {
    WHITE(255,255,255),
    BLACK(0,0,0),
    RED(255,0,0),
    LIME(0,255,0),
    GREEN(0,128,0),
    BLUE(0,0,255),
    YELLOW(255,255,0),
    CYAN(0,255,255),
    MAGENTA(255,0,255),
    SILVER(192,192,192),
    GRAY(128,128,128),
    PURPLE(128,0,128),
    TEAL(0,128,128),
    NAVY(0,0,128),
    MAROON(128,0,0),
    OLIVE(128,128,0),
    BROWN(150,75,0);

    private final Color color;
    Colors(double red, double green, double blue, double alpha) {
        color = new Color(red, green, blue, alpha, false);
        color.setName(this.name().toLowerCase());
    }

    Colors(double red, double green, double blue) {
        this(red, green, blue, 255);
    }

    /**
     * Returns the {@code Color Object} that represents the namesake of this enum.
     * <br>
     * The returned {@code Color Object} cannot be edited, if you wish to edit a
     * copy of this {@code Color} use {@link Color Color(Color, boolean)}.
     *
     * @return A non-editable {@code Color}
     */
    public Color color() {
        return color;
    }
}
