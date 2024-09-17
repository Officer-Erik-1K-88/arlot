package arlot.user;

import arlot.error.InvalidColorException;

import java.util.Arrays;
import java.util.Objects;

public final class Color implements Comparable<Color> {
    /**
     * Converts values of {@code HSLA} to a {@code Color Object}.
     *
     * @param h hue (0-360)
     * @param s saturation (0-1)
     * @param l luminance (0-1)
     * @param a alpha (0-255)
     * @return a {@code Color Object}
     */
    public static Color hslaToColor(double h, double s, double l, double a) {
        Color color = new Color();
        color.setAsHSLA(h, s, l, a);
        return color;
    }

    /**
     * Converts values of {@code HSL} to a {@code Color Object}.
     *
     * @param h hue (0-360)
     * @param s saturation (0-1)
     * @param l luminance (0-1)
     * @return a {@code Color Object}
     */
    public static Color hslToColor(double h, double s, double l) {
        return hslaToColor(h, s, l, 255);
    }

    private static double hueToRgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6.0) return p + (q - p) * 6.0 * t;
        if (t < 1.0 / 3.0) return q;
        if (t < 1.0 / 2.0) return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
        return p;
    }

    /**
     *
     * @param h hue (0-360)
     * @param s saturation (0-1)
     * @param l luminance (0-1)
     * @param a alpha (0-255)
     * @return a double array where 0=red, 1=green, 2=blue, and 3=alpha
     */
    private static double[] hslaToRgba(double h, double s, double l, double a) {
        double r, g, b;

        if (s == 0) {
            r = g = b = l; // achromatic
        } else {
            h = h/360.0;
            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0 / 3.0);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0 / 3.0);
        }

        return new double[] {r*255.0, g*255.0, b*255.0, a};
    }

    /**
     *
     * @param r red (0-255)
     * @param g green (0-255)
     * @param b blue (0-255)
     * @param a alpha (0-255)
     * @return a double array where 0=hue, 1=saturation, 2=luminance, and 3=alpha
     */
    private static double[] rgbaToHsla(double r, double g, double b, double a) {
        r = r / 255.0;
        g = g / 255.0;
        b = b / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double h, s, l;
        l = (max + min) / 2.0;

        if (max == min) {
            h = s = 0.0; // achromatic
        } else {
            double d = max - min;
            s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);

            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else {
                h = (r - g) / d + 4;
            }
            h /= 6;
        }
        return new double[] {h*360.0, s, l, a};
    }

    private String name = null;
    private final boolean editable;
    private boolean setted = false;
    private boolean nameSetted = false;

    private double red = 255;
    private double green = 255;
    private double blue = 255;
    private double hue = 360;
    private double saturation = 1;
    private double luminance = 1;
    private double alpha = 255; // transparency

    public Color() {
        this.editable = true;
    }

    public Color(double red, double green, double blue) {
        this(red, green, blue, 255);
    }

    public Color(double red, double green, double blue, double alpha) {
        this(red, green, blue, alpha, true);
    }

    public Color(double red, double green, double blue, double alpha, boolean isEditable) {
        setAsRGBA(red, green, blue, alpha);
        this.editable = isEditable;
    }

    public Color(Color color) {
        this(color, color.editable);
    }
    private Color(Color color, boolean isEditable) {
        this.red = color.red;
        this.green = color.green;
        this.blue = color.blue;
        this.hue = color.hue;
        this.saturation = color.saturation;
        this.luminance = color.luminance;
        this.alpha = color.alpha;
        this.name = color.name;
        this.editable = isEditable;
    }

    public void setAsHSLA(double hue, double saturation, double luminance, double alpha) {
        if (editable || !setted) {
            setted = true;
            this.hue = Math.max(0, Math.min(360, hue));
            this.saturation = Math.max(0, Math.min(1, saturation));
            this.luminance = Math.max(0, Math.min(1, luminance));
            this.alpha = Math.max(0, Math.min(255, alpha));
            double[] rgba = hslaToRgba(this.hue, this.saturation, this.luminance, this.alpha);
            this.red = rgba[0];
            this.green = rgba[1];
            this.blue = rgba[2];
            validate();
        }
    }
    public void setAsHSL(double hue, double saturation, double luminance) {
        setAsHSLA(hue, saturation, luminance, this.alpha);
    }

    public void setAsRGBA(double red, double green, double blue, double alpha) {
        if (editable || !setted) {
            setted = true;
            this.red = Math.max(0, Math.min(255, red));
            this.green = Math.max(0, Math.min(255, green));
            this.blue = Math.max(0, Math.min(255, blue));
            this.alpha = Math.max(0, Math.min(255, alpha));
            double[] hsla = rgbaToHsla(this.red, this.green, this.blue, this.alpha);
            this.hue = hsla[0];
            this.saturation = hsla[1];
            this.luminance = hsla[2];
            validate();
        }
    }
    public void setAsRGB(double red, double green, double blue) {
        setAsRGBA(red, green, blue, this.alpha);
    }

    // Hexadecimal conversion methods
    public void setAsHex(String hex) {
        if (editable || !setted) {
            setted = true;
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            if (hex.length() == 6 || hex.length() == 8) {
                try {
                    double r = Integer.parseInt(hex.substring(0, 2), 16);
                    double g = Integer.parseInt(hex.substring(2, 4), 16);
                    double b = Integer.parseInt(hex.substring(4, 6), 16);
                    double a;
                    if (hex.length() == 8) {
                        a = Integer.parseInt(hex.substring(6, 8), 16);
                    } else {
                        a = 255;
                    }
                    setAsRGBA(r, g, b, a);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid hex color format.");
                }
            } else {
                throw new IllegalArgumentException("Hex color must be 6 or 8 characters long.");
            }
        }
    }

    public String getHex() {
        int r = (int) red;
        int g = (int) green;
        int b = (int) blue;
        int a = (int) alpha;
        return String.format("#%02X%02X%02X%02X", r, g, b, a);
    }

    private void validate() {
        StringBuilder error = new StringBuilder();
        if (red < 0 || red > 255) {
            error.append("\tRed out of bounds of 0 and 255: ")
                    .append(red).append("\n");
        }
        if (green < 0 || green > 255) {
            error.append("\tGreen out of bounds of 0 and 255: ")
                    .append(green).append("\n");
        }
        if (blue < 0 || blue > 255) {
            error.append("\tBlue out of bounds of 0 and 255: ")
                    .append(blue).append("\n");
        }
        if (hue < 0 || hue > 360) {
            error.append("\tHue out of bounds of 0 and 360: ")
                    .append(hue).append("\n");
        }
        if (saturation < 0 || saturation > 1) {
            error.append("\tSaturation out of bounds of 0 and 1: ")
                    .append(saturation).append("\n");
        }
        if (luminance < 0 || luminance > 1) {
            error.append("\tluminance out of bounds of 0 and 1: ")
                    .append(luminance).append("\n");
        }
        if (alpha < 0 || alpha > 255) {
            error.append("\tAlpha out of bounds of 0 and 255: ")
                    .append(alpha).append("\n");
        }
        if (!error.isEmpty()) {
            error.insert(0, "This is not a valid color:\n");
            throw new InvalidColorException(error.toString());
        }
    }

    /**
     * Is the statement of this color been resettable after first time setting.
     *
     * @return true if at instantiation was set as editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Checks to see if this color has been set.
     *
     * @return true if any (other than {@code setAlpha}) setters have been called.
     */
    public boolean isSet() {
        return setted;
    }

    /**
     * Checks to see if this color has a name set to it.
     *
     * @return true if {@link #getName()} doesn't return null.
     */
    public boolean isNameSet() {
        return nameSetted;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this color to the provided String.
     *
     * @param name The String value to set this color's name to.
     * @throws NullPointerException if the provided name is null
     */
    public void setName(String name) {
        Objects.requireNonNull(name);
        if (editable || !nameSetted) {
            nameSetted = true;
            this.name = name;
        }
    }

    public double getAlpha() {
        return alpha;
    }
    public void setAlpha(double alpha) {
        if (editable) {
            this.alpha = Math.max(0, Math.min(255, alpha));
        }
    }

    public double getRed() {
        return red;
    }
    public void setRed(double red) {
        setAsRGB(red, green, blue);
    }

    public double getGreen() {
        return green;
    }
    public void setGreen(double green) {
        setAsRGB(red, green, blue);
    }

    public double getBlue() {
        return blue;
    }
    public void setBlue(double blue) {
        setAsRGB(red, green, blue);
    }

    public double getHue() {
        return hue;
    }
    public void setHue(double hue) {
        setAsHSL(hue, saturation, luminance);
    }

    public double getSaturation() {
        return saturation;
    }
    public void setSaturation(double saturation) {
        setAsHSL(hue, saturation, luminance);
    }

    public double getLuminance() {
        return luminance;
    }
    public void setLuminance(double luminance) {
        setAsHSL(hue, saturation, luminance);
    }

    public void changeBrightness(double factor) {
        if (factor < 0 || factor > 2) {
            throw new IllegalArgumentException("Brightness factor must be between 0 and 2.");
        }
        setLuminance(luminance * factor);
    }

    /**
     * Returns the name of this {@code Color}, or, if name is null or blank,
     * then the hexadecimal of this {@code Color} will be returned instead.
     *
     * @return either the color's name or hexadecimal
     */
    @Override
    public String toString() {
        if (name == null || name.isBlank()) {
            return getHex();
        }
        return getName();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <br><br>
     * This color and the provided color are only equal, if and only if,
     * the stored values of
     * {@code red}, {@code green}, {@code blue},
     * {@code hue}, {@code saturation}, {@code luminance},
     * and {@code alpha}
     * are the same.
     *
     * @param object The object to compare to this color.
     * @return {@code true} if this object is the same as the obj argument;
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (this == object) return true;
        if (!(object instanceof Color color)) return false;
        return Double.compare(getRed(), color.getRed()) == 0
                && Double.compare(getGreen(), color.getGreen()) == 0
                && Double.compare(getBlue(), color.getBlue()) == 0
                && Double.compare(getHue(), color.getHue()) == 0
                && Double.compare(getSaturation(), color.getSaturation()) == 0
                && Double.compare(getLuminance(), color.getLuminance()) == 0
                && Double.compare(getAlpha(), color.getAlpha()) == 0;
    }

    /**
     * Gets the hash code of this color.
     * <br><br>
     * The hash code of a {@code Color} is based on the stored values of
     * {@code red}, {@code green}, {@code blue},
     * {@code hue}, {@code saturation}, {@code luminance},
     * and {@code alpha}.
     *
     * @return a hash code value for this color.
     */
    @Override
    public int hashCode() {
        /* Another option to creating the hashCode
        return (int) ((getRed() + getGreen() + getBlue()
                + getHue() + getSaturation() + getLuminance()
                + getAlpha())*2.22486);
        */
        return Objects.hash(getRed(), getGreen(), getBlue(),
                getHue(), getSaturation(), getLuminance(),
                getAlpha());
    }

    /**
     * Compares this color with the specified color for order. Returns a
     * negative integer, zero, or a positive integer as this color is less
     * than, equal to, or greater than the specified color.
     * <br><br>
     * The color deemed as the larger of the two is the color closest to white.
     * <br>
     * If the two colors are equally close to white,
     * then the most opaque (the alpha value)
     * of the two will be the larger color.
     * <br><br>
     * Before the comparison of what color is whiter,
     * this method checks to see if they are truly the same color
     * using {@link #equals(Object)},
     * and only if they are different colors does the
     * comparison of what color is whiter is processed.
     *
     * @param color the color to be compared.
     * @return a negative integer, zero, or a positive integer as this color
     * is less than, equal to, or greater than the specified color.
     */
    @Override
    public int compareTo(Color color) {
        if (equals(color)) return 0;
        double distanceToWhite = Math.sqrt(Math.pow(255 - this.red, 2)
                + Math.pow(255 - this.green, 2)
                + Math.pow(255 - this.blue, 2));
        double otherDistanceToWhite = Math.sqrt(Math.pow(255- color.red, 2)
                + Math.pow(255 - color.green, 2)
                + Math.pow(255 - color.blue, 2));
        int compare = Double.compare(otherDistanceToWhite, distanceToWhite);
        if (compare == 0) {
            compare = Double.compare(this.getAlpha(), color.getAlpha());
        }
        return compare;
    }
}
