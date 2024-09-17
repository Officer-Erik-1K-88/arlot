// UserInput program for data input validation by Erik Kountzman
package arlot.user;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

/**
 * This class can help with user input validation.
 * <br>
 * Can deal with integers, doubles, Strings, booleans, and Files.
 */
public class UserInput implements Closeable {
    // private methods/values:
    /**
     * Is the {@link Scanner} that gets data input.
     */
    private Scanner sc;
    /**
     * The {@link InputStream} used by {@link #sc}.
     */
    private InputStream in;
    /**
     * The {@link PrintStream} used by this UserInput for printing.
     */
    private PrintStream out;
    /**
     * Is the data holder for the statement of whether this UserInput was closed.
     */
    private boolean closed = false;

    // constructors
    /**
     * Builds a new UserInput with a new {@link Scanner} using {@code System.in} as the source.
     * As well as having a {@link PrintStream} using {@code System.out} as the printable surface.
     */
    public UserInput() {
        this.in = System.in;
        this.sc = new Scanner(this.in);
        this.out = System.out;
    }
    /**
     * Builds a new UserInput with the provided {@link Scanner}.
     * As well as having a {@link PrintStream} using {@code System.out} as the printable surface.
     * @param scanner The {@link Scanner} to be used as user input fetching.
     */
    public UserInput(Scanner scanner) {
        this.in = null;
        this.sc = scanner;
        this.out = System.out;
        validate();
    }
    /**
     * Builds a new UserInput with the provided {@link Scanner}.
     * @param scanner The {@link Scanner} to be used as user input fetching.
     * @param out The {@link PrintStream} that text will be printed to.
     */
    public UserInput(Scanner scanner, PrintStream out) {
        this.in = null;
        this.sc = scanner;
        this.out = out;
        validate();
    }
    /**
     * Builds a new UserInput with the provided {@link InputStream}.
     * As well as having a {@link PrintStream} using {@code System.out} as the printable surface.
     * @param in The {@link InputStream} to be used as the source of the {@link Scanner}.
     */
    public UserInput(InputStream in) {
        this.in = in;
        this.sc = null;
        this.out = System.out;
        validate();
    }
    /**
     * Builds a new UserInput with the provided {@link InputStream}.
     * @param in The {@link InputStream} to be used as the source of the {@link Scanner}.
     * @param out The {@link PrintStream} that text will be printed to.
     */
    public UserInput(InputStream in, PrintStream out) {
        this.in = in;
        this.sc = null;
        this.out = out;
        validate();
    }
    /**
     * Builds a new UserInput with a new {@link Scanner} using {@code System.in} as the source.
     * @param out The {@link PrintStream} that text will be printed to.
     */
    public UserInput(PrintStream out) {
        this.in = System.in;
        this.sc = new Scanner(this.in);
        this.out = out;
        validate();
    }

    /**
     * Copies the provided {@link UserInput} given that it hasn't been closed.
     * <br>
     * Will make a new instance of the {@link InputStream} given that {@link #in()} doesn't return null.
     * If the {@link InputStream} is copied, then a new {@link Scanner} object will be created.
     * Otherwise, no new instances of either {@link InputStream} or {@link Scanner} will be created.
     * @param userInput The {@link UserInput} that will be copied.
     * @throws IllegalStateException If the provided {@link UserInput} is closed.
     */
    public UserInput(UserInput userInput) {
        userInput.closeCheck();
        if (userInput.in() == null) {
            this.in = null;
        } else {
            // Cloning InputStream
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                userInput.in().transferTo(baos);
                byte[] data = baos.toByteArray();
                this.in = new ByteArrayInputStream(data);
            } catch (IOException e) {
                this.in = null;
            }
        }
        if (this.in != null) {
            this.sc = new Scanner(this.in);
        } else {
            this.sc = userInput.scanner();
        }
        this.out = userInput.out();
        this.closed = userInput.isClosed();
    }

    // getters

    /**
     * Gets the {@link Scanner} that this UserInput is using for input fetching.
     * @return The {@link Scanner} that input data is being fetched from.
     */
    public Scanner scanner() {
        return this.sc;
    }

    /**
     * Gets the {@link InputStream} that the {@link Scanner} is using for fetching input data.
     * <br>
     * if this UserInput object was initialized with a given {@link Scanner},
     * then this method will return {@code null}.
     * @return The {@link InputStream} that the {@link Scanner} is using for input data.
     */
    protected InputStream in() {
        return this.in;
    }

    /**
     * Gets the {@link PrintStream} that is being used to printout data when needed.
     * @return The {@link PrintStream} that text is printed to.
     */
    public PrintStream out() {
        return this.out;
    }

    /**
     * Returns true if this UserInput is closed and can no longer search the {@link Scanner}.
     * @return true if this UserInput has been closed.
     */
    public boolean isClosed() {
        return closed;
    }

    // checkers

    /**
     * Checks to see if this UserInput is closed.
     * If closed will throw {@link IllegalStateException}.
     * <br>
     * If this UserInput isn't closed, but the {@link #scanner()} is closed,
     * then this UserInput will be closed and an {@link IllegalStateException} is thrown.
     * @throws IllegalStateException If this UserInput is closed.
     */
    protected void closeCheck() {
        if (closed) {
            throw new IllegalStateException("UserInput closed");
        }
        try {
            boolean ignored = sc.hasNext();
        } catch (IllegalStateException e) {
            close();
            throw new IllegalStateException("UserInput newly closed due to `scanner()` being closed.");
        }
    }

    /**
     * Validates the given inputs at construction of this UserInput.
     */
    private void validate() {
        if (sc == null) {
            if (in == null) {
                in = System.in;
            }
            sc = new Scanner(in);
        }
        if (out == null) {
            out = System.out;
        }
    }

    // methods that primarily handle given data.

    /**
     * Checks if the {@code value} is with in the bounds of {@code min} and {@code max}.
     * <br>
     * If the {@code value} is not a number then the length of {@code value} will be checked.
     * @param isNum Should be true if {@code value} is a number.
     * @param value The value to check if is in between {@code min} and {@code max}.
     * @param min The smallest allowed number.
     * @param max The largest allowed number.
     * @param errmsg The message to be printed out if the {@code value} is out of bounds.
     * @return The {@code value} and whether the value is in bound. Formatted: "[value],[value in bounds?]"
     */
    protected String minMaxVals(boolean isNum, String value, Double min, Double max, String[] errmsg) {
        double v;
        boolean goodval = true;
        if (isNum) {
            try {
                v = Double.parseDouble(value);
            } catch (Exception e) {
                v = value.length();
            }
        } else {
            v = value.length();
        }
        if (min != null && v < min) {
            goodval = false;
            this.out.println(errmsg[0]);
        } else if (max != null && v > max) {
            goodval = false;
            this.out.println(errmsg[1]);
        }
        return value + "," + goodval;
    }
    /**
     * Checks if the {@code value}'s length is with in the bounds of {@code min} and {@code max}.
     * @param value The value to check if it's length is in between {@code min} and {@code max}.
     * @param min The smallest allowed number.
     * @param max The largest allowed number.
     * @param errmsg The message to be printed out if the {@code value}'s length is out of bounds.
     * @return The {@code value} and whether the {@code value} is in bound. Formatted: "[value],[value in bounds?]"
     */
    protected String minMaxVals(String value, Double min, Double max, String[] errmsg) {
        return minMaxVals(false, value, min, max, errmsg);
    }
    /**
     * Checks if the {@code value} is with in the bounds of {@code min} and {@code max}.
     * @param value The {@code int} value to check if is in between {@code min} and {@code max}.
     * @param min The smallest allowed number.
     * @param max The largest allowed number.
     * @param errmsg The message to be printed out if the {@code value} is out of bounds.
     * @return The {@code value} and whether the {@code value} is in bound. Formatted: "[value],[value in bounds?]"
     */
    protected String minMaxVals(Integer value, Double min, Double max, String[] errmsg) {
        return minMaxVals(true, String.valueOf(value), min, max, errmsg);
    }
    /**
     * Checks if the {@code value} is with in the bounds of {@code min} and {@code max}.
     * @param value The {@code double} value to check if is in between {@code min} and {@code max}.
     * @param min The smallest allowed number.
     * @param max The largest allowed number.
     * @param errmsg The message to be printed out if the {@code value} is out of bounds.
     * @return The {@code value} and whether the {@code value} is in bound. Formatted: "[value],[value in bounds?]"
     */
    protected String minMaxVals(Double value, Double min, Double max, String[] errmsg) {
        return minMaxVals(true, String.valueOf(value), min, max, errmsg);
    }
    // end of minMaxVals

    /**
     *
     * @param value The string value to check if it contains any stated values.
     * @param contains An array of strings that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that it only takes up a single item placement within this
     *                 parameter, where it's recommended to be placed as the first item in the
     *                 provided array, as it must follow the following formatting specifications:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 (and allow for the normal operation and usage of the following parameters: contains and doesntcontains)
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @param inverse Reverses it, instead of checking if it exists and returning true, it'll check if it doesn't exist and return true.
     * @return A comma separated string:
     * <ul>
     *     <li>0: the string value given after processing value</li>
     *     <li>1: boolean value that represents whether the value is valid</li>
     *     <li>2: boolean value that represents that the starting characters of value were checked</li>
     *     <li>3: boolean value that represents whether the full string value provided was returned, there is a chance that the returned value is the original even if this value is false</li>
     *     <li>4: The value given to the method</li>
     * </ul>
     */
    protected String contains(String value, String[] contains, boolean inverse) {
        String v = value;
        boolean goodval = true;
        boolean isastart = false;
        boolean givefull = true;
        try {
            ArrayList<String> contain = null;
            if (contains != null) {
                contain = new ArrayList<>(List.of(contains));
            }
            if (contains != null && (contain.get(0).startsWith("start:") || contain.get(0).startsWith("give full with start:"))) {
                isastart = true;
                String ret = "";
                String[] cont;
                boolean innergoodval = true;
                if (contain.get(0).startsWith("give full with start:")) {
                    givefull = true;
                    cont = contain.get(0).replaceFirst("give full with start:", "").split(",");
                    contain.remove(0);
                } else {
                    givefull = false;
                    cont = contain.get(0).replaceFirst("start:", "").split(",");
                }
                for (int a=0; a<cont.length; a++) {
                    boolean startedWith = false;
                    if (cont[a].startsWith("lower:")) {
                        cont[a] = cont[a].replaceFirst("lower:", "");
                        startedWith = v.toLowerCase().startsWith(cont[a]);
                    } else if (cont[a].startsWith("upper:")) {
                        cont[a] = cont[a].replaceFirst("upper:", "");
                        startedWith = v.toUpperCase().startsWith(cont[a]);
                    } else {
                        startedWith = v.startsWith(cont[a]);
                    }
                    if (startedWith) {
                        if (inverse) {
                            innergoodval = false;
                            goodval = false;
                        } else {
                            innergoodval = true;
                            goodval = true;
                            if (givefull) {
                                ret = v;
                            } else {
                                ret = cont[a];
                            }
                        }
                        break;
                    } else {
                        innergoodval = inverse;
                        goodval = inverse;
                    }
                }
                if (ret.isEmpty()) {
                    ret = v;
                }
                if (!innergoodval) {
                    if (inverse) {
                        this.out.println("Cannot understand the input: "+v);
                        this.out.println("The response must not start with any of the following:");
                        this.out.println(Arrays.toString(cont)
                                .replace(", ", "\n\t")
                                .replace("[", "\t")
                                .replace("]", ""));
                    } else {
                        this.out.println("Cannot understand the input: "+v);
                        this.out.println("The response must start with one of the following:");
                        this.out.println(Arrays.toString(cont)
                                .replace(", ", "\n\t")
                                .replace("[", "\t")
                                .replace("]", ""));
                    }
                }
                v = ret;
            }
            if (contains != null && (!isastart || givefull)) {
                String togiveonerror = contain.toString()
                        .replace(", ", "\n\t")
                        .replace("[", "\t")
                        .replace("]", "");
                for (int i=0; i<contain.toArray().length; i++) {
                    if (inverse) {
                        if (v.contains(contain.get(i))) {
                            goodval = false;
                            this.out.println("The response must not contain any of the of the following:");
                            this.out.println(togiveonerror);
                            break;
                        }
                    } else {
                        if (!v.contains(contain.get(i))) {
                            goodval = false;
                            this.out.println("The response must contain all of the following:");
                            this.out.println(togiveonerror);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            goodval = false;
        }
        return v + "," + goodval + "," + isastart + "," + givefull + "," + value;
    }
    /**
     *
     * @param value The string value to check if it contains any stated values.
     * @param contains An array of strings that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that it only takes up a single item placement within this
     *                 parameter, where it's recommended to be placed as the first item in the
     *                 provided array, as it must follow the following formatting specifications:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 (and allow for the normal operation and usage of the following parameters: contains and doesntcontains)
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return A comma separated string:
     * <ul>
     *     <li>0: the string value given after processing value</li>
     *     <li>1: boolean value that represents whether the value is valid</li>
     *     <li>2: boolean value that represents that the starting characters of value were checked</li>
     *     <li>3: boolean value that represents whether the full string value provided was returned, there is a chance that the returned value is the original even if this value is false</li>
     *     <li>4: The value given to the method</li>
     * </ul>
     */
    protected String contains(String value, String[] contains) {
        return contains(value, contains, false);
    }
    /**
     *
     * @param value The string value to check if it contains any stated values.
     * @param contains A string that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that the String is formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @param inverse Reverses it, instead of checking if it exists and returning true, it'll check if it doesn't exist and return true.
     * @return A comma separated string:
     * <ul>
     *     <li>0: the string value given after processing value</li>
     *     <li>1: boolean value that represents whether the value is valid</li>
     *     <li>2: boolean value that represents that the starting characters of value were checked</li>
     *     <li>3: boolean value that represents whether the full string value provided was returned, there is a chance that the returned value is the original even if this value is false</li>
     *     <li>4: The value given to the method</li>
     * </ul>
     */
    protected String contains(String value, String contains, boolean inverse) {
        return contains(value, new String[]{contains}, inverse);
    }
    /**
     *
     * @param value The string value to check if it contains any stated values.
     * @param contains A string that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that the String is formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return A comma separated string:
     * <ul>
     *     <li>0: the string value given after processing value</li>
     *     <li>1: boolean value that represents whether the value is valid</li>
     *     <li>2: boolean value that represents that the starting characters of value were checked</li>
     *     <li>3: boolean value that represents whether the full string value provided was returned, there is a chance that the returned value is the original even if this value is false</li>
     *     <li>4: The value given to the method</li>
     * </ul>
     */
    protected String contains(String value, String contains) {
        return contains(value, new String[]{contains}, false);
    }
    // end of contains

    // methods for getting user provided data
    /**
     * This method takes a prompt and reads an integer from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @param ceiling The maximum value allowed
     * @param exclude An array of integers that will be prohibited from being selected.
     * @return The integer entered by the user
     */
    public int getInt(String prompt, Integer floor, Integer ceiling, Integer[] exclude) {
        closeCheck();
        String[] hold;
        int v = 0;
        boolean goodval;
        do {
            try {
                this.out.print(prompt);
                v = Integer.parseInt(this.sc.nextLine());
                hold = minMaxVals(v,
                        (floor==null?null:Double.parseDouble(String.valueOf(floor))),
                        (ceiling==null?null:Double.parseDouble(String.valueOf(ceiling))),
                        new String[]{
                                "Input of " + v + " is below the floor of " + floor + ".",
                                "Input of " + v + " is beyond the ceiling of " + ceiling + "."}
                ).split(",");
                v = Integer.parseInt(hold[0]);
                goodval = Boolean.parseBoolean(hold[1]);
                if (exclude != null) {
                    for (Integer integer : exclude) {
                        if (v == integer) {
                            goodval = false;
                            break;
                        }
                    }
                    if (!goodval) {
                        this.out.println("Prohibited Integer: "+v);
                        this.out.println("The integer response must not be one of the following:");
                        this.out.println(Arrays.toString(exclude)
                                .replace(", ", "\n\t")
                                .replace("[", "\t")
                                .replace("]", ""));
                    }
                }
            } catch (Exception e) {
                this.out.println("Illegal input: not an integer.");
                goodval = false;
            }
        } while (!goodval);
        return v;
    } // end of getInt
    /**
     * This method takes a prompt and reads an integer from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @param ceiling The maximum value allowed
     * @return The integer entered by the user
     */
    public int getInt(String prompt, Integer floor, Integer ceiling) {
        return getInt(prompt, floor, ceiling, null);
    }
    /**
     * This method takes a prompt and reads an integer from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @param exclude An array of integers that will be prohibited from being selected.
     * @return The integer entered by the user
     */
    public int getInt(String prompt, Integer floor, Integer[] exclude) {
        return getInt(prompt, floor, null, exclude);
    }
    /**
     * This method takes a prompt and reads an integer from the user.
     * @param prompt The prompt to display to the user
     * @param exclude An array of integers that will be prohibited from being selected.
     * @return The integer entered by the user
     */
    public int getInt(String prompt, Integer[] exclude) {
        return getInt(prompt, null, null, exclude);
    }
    /**
     * This method takes a prompt and reads an integer from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @return The integer entered by the user
     */
    public int getInt(String prompt, Integer floor) {
        return getInt(prompt, floor, null, null);
    }
    /**
     * This method takes a prompt and reads an integer from the user.
     * @param prompt The prompt to display to the user
     * @return The integer entered by the user
     */
    public int getInt(String prompt) {
        return getInt(prompt, null, null, null);
    }
    // end of getInt

    /**
     * This method takes a prompt and reads a double from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @param ceiling The maximum value allowed
     * @param exclude An array of doubles that will be prohibited from being selected.
     * @return The double entered by the user
     */
    public double getDouble(String prompt, Double floor, Double ceiling, Double[] exclude) {
        closeCheck();
        String[] hold;
        double v = 0.0;
        boolean goodval;
        do {
            try {
                this.out.print(prompt);
                v = Double.parseDouble(this.sc.nextLine());
                hold = minMaxVals(v,
                        floor,
                        ceiling,
                        new String[]{
                                "Input of " + v + " is below the floor of " + floor + ".",
                                "Input of " + v + " is beyond the ceiling of " + ceiling + "."}
                ).split(",");
                v = Double.parseDouble(hold[0]);
                goodval = Boolean.parseBoolean(hold[1]);
                if (exclude != null) {
                    for (Double dble : exclude) {
                        if (v == dble) {
                            goodval = false;
                            break;
                        }
                    }
                    if (!goodval) {
                        this.out.println("Prohibited Double: "+v);
                        this.out.println("The double response must not be one of the following:");
                        this.out.println(Arrays.toString(exclude)
                                .replace(", ", "\n\t")
                                .replace("[", "\t")
                                .replace("]", ""));
                    }
                }
            } catch (Exception e) {
                this.out.println("Illegal input: not a double.");
                goodval = false;
            }
        } while (!goodval);
        return v;
    } // end of getDouble
    /**
     * This method takes a prompt and reads a double from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @param ceiling The maximum value allowed
     * @return The double entered by the user
     */
    public double getDouble(String prompt, Double floor, Double ceiling) {
        return getDouble(prompt, floor, ceiling, null);
    }
    /**
     * This method takes a prompt and reads a double from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @param exclude An array of doubles that will be prohibited from being selected.
     * @return The double entered by the user
     */
    public double getDouble(String prompt, Double floor, Double[] exclude) {
        return getDouble(prompt, floor, null, exclude);
    }
    /**
     * This method takes a prompt and reads a double from the user.
     * @param prompt The prompt to display to the user
     * @param exclude An array of doubles that will be prohibited from being selected.
     * @return The double entered by the user
     */
    public double getDouble(String prompt, Double[] exclude) {
        return getDouble(prompt, null, null, exclude);
    }
    /**
     * This method takes a prompt and reads a double from the user,
     * ensuring that the input is within the specified range.
     * @param prompt The prompt to display to the user
     * @param floor The minimum value allowed
     * @return The double entered by the user
     */
    public double getDouble(String prompt, Double floor) {
        return getDouble(prompt, floor, null, null);
    }
    /**
     * This method takes a prompt and reads a double from the user.
     * @param prompt The prompt to display to the user
     * @return The double entered by the user
     */
    public double getDouble(String prompt) {
        return getDouble(prompt, null, null, null);
    }
    // end of getDouble

    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input is filtered to fit within the parameters.
     * @param prompt The prompt to display to the user
     * @param min The smallest amount of characters the user may input
     * @param max The most amount of characters the user may input
     * @param contains An array of strings that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that it only takes up a single item placement within this
     *                 parameter, where it's recommended to be placed as the first item in the
     *                 provided array, as it must follow the following formatting specifications:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 (and allow for the normal operation and usage of the following parameters: contains and doesntcontains)
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @param doesntcontains An array of strings that the user must not include in their response, uses the same setup as `contains`.
     * @return The string the user responds with
     */
    public String getString(String prompt, Integer min, Integer max, String[] contains, String[] doesntcontains) {
        closeCheck();
        String[] hold;
        String v = "";
        boolean goodval;
        boolean isastart = false;
        boolean givefull = true;
        do {
            this.out.print(prompt);
            hold = minMaxVals(this.sc.nextLine(),
                    (min==null?null:Double.parseDouble(String.valueOf(min))),
                    (max==null?null:Double.parseDouble(String.valueOf(max))),
                    new String[]{
                            "The response must not go below "+min+" characters.",
                            "The response must not exceed "+max+" characters."}
            ).split(",");
            goodval = Boolean.parseBoolean(hold[1]);
            v = hold[0];
            if (contains != null) {
                hold = contains(v, contains, false).split(",");
                v = hold[0];
                goodval = Boolean.parseBoolean(hold[1]);
                isastart = Boolean.parseBoolean(hold[2]);
                givefull = Boolean.parseBoolean(hold[3]);
            }
            if (doesntcontains != null && (!isastart || givefull)) {
                hold = contains(v, doesntcontains, true).split(",");
                v = hold[0];
                goodval = Boolean.parseBoolean(hold[1]);
            }
        } while(!goodval);
        return v;
    } // end of getString
    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input is filtered to fit within the parameters.
     * @param prompt The prompt to display to the user
     * @param min The smallest amount of characters the user may input
     * @param max The most amount of characters the user may input
     * @param contains An array of strings that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that it only takes up a single item placement within this
     *                 parameter, where it's recommended to be placed as the first item in the
     *                 provided array, as it must follow the following formatting specifications:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 (and allow for the normal operation and usage of the following parameters: contains and doesntcontains)
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The string the user responds with
     */
    public String getString(String prompt, Integer min, Integer max, String[] contains) {
        return getString(prompt, min, max, contains, null);
    }
    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input is filtered to fit within the parameters.
     * @param prompt The prompt to display to the user
     * @param min The smallest amount of characters the user may input
     * @param max The most amount of characters the user may input
     * @param contains A string that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that the String is formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The string the user responds with
     */
    public String getString(String prompt, Integer min, Integer max, String contains) {
        return getString(prompt, min, max, (contains!=null?new String[]{contains}:null), null);
    }
    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input's length fits within the margins.
     * @param prompt The prompt to display to the user
     * @param min The smallest amount of characters the user may input
     * @param max The most amount of characters the user may input
     * @return The string the user responds with
     */
    public String getString(String prompt, Integer min, Integer max) {
        return getString(prompt, min, max, null, null);
    }
    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input is at least x amount in length.
     * @param prompt The prompt to display to the user
     * @param min The smallest amount of characters the user may input
     * @return The string the user responds with
     */
    public String getString(String prompt, Integer min) {
        return getString(prompt, min, null, null, null);
    }
    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input is filtered to fit within the parameters.
     * @param prompt The prompt to display to the user
     * @param contains An array of strings that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that it only takes up a single item placement within this
     *                 parameter, where it's recommended to be placed as the first item in the
     *                 provided array, as it must follow the following formatting specifications:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 (and allow for the normal operation and usage of the following parameters: contains and doesntcontains)
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The string the user responds with
     */
    public String getString(String prompt, String[] contains) {
        return getString(prompt, null, null, contains, null);
    }
    /**
     * This method takes a prompt and reads a string from the user,
     * ensuring that the input is filtered to fit within the parameters.
     * @param prompt The prompt to display to the user
     * @param contains A string that must be within the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that the String is formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..."
     *                 or to get the full content provided by user, use:
     *                 "give full with start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The string the user responds with
     */
    public String getString(String prompt, String contains) {
        return getString(prompt, null, null, (contains!=null?new String[]{contains}:null), null);
    }
    /**
     * This method takes a prompt and reads a string from the user.
     * @param prompt The prompt to display to the user
     * @return The string the user responds with
     */
    public String getString(String prompt) {
        return getString(prompt, null, null, null, null);
    }
    // end of getString

    /**
     * This method takes a prompt and reads a 'Yes' or 'No' response from the user.
     * @param prompt The prompt to display to the user.
     * @param invert If true, will flip 'y' or 'Y' to a return of false.
     * @return true if the first character in the user's response is a 'y' or 'Y' otherwise it's false.
     */
    public boolean getYN(String prompt, boolean invert) {
        closeCheck();
        boolean v = false;
        boolean goodval;
        do {
            try {
                goodval = true;
                if (!prompt.toUpperCase().contains("(Y/N)")) {
                    prompt += (prompt.endsWith(" ")
                            ||prompt.endsWith("\n")
                            ||prompt.endsWith("\t")
                            ?"":" ")+"(Y/N): ";
                }
                v = getString(prompt, "start:upper:Y,upper:N").startsWith("Y");
                if (invert) {
                    v = !v;
                }
            } catch (Exception e) {
                this.out.println("Unexpected Error: An unexpected error occurred.\n\t"+e.getMessage());
                goodval = false;
            }
        } while(!goodval);
        return v;
    } // end of getYN
    /**
     * This method takes a prompt and reads a 'Yes' or 'No' response from the user.
     * @param prompt The prompt to display to the user.
     * @return true if the first character in the user's response is a 'y' or 'Y' otherwise it's false.
     */
    public boolean getYN(String prompt) {
        return getYN(prompt, false);
    }
    // end of getYN

    /**
     * Gets a file provided by the user that fits with in the given parameters.
     * @param prompt The prompt to display to the user.
     * @param allowHidden The statement of accepting files that the user's operating system recognizes as hidden.
     * @param doesntcontains An array of strings that cannot be within the user's response.
     *                 For checking of what the file path starts with then the first String of
     *                 this parameter must be formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The {@link File} instance created from the user given file path.
     */
    public File getFile(String prompt, boolean allowHidden, String[] doesntcontains) {
        closeCheck();
        if (doesntcontains != null) {
            if (doesntcontains[0].startsWith("start:")) {
                doesntcontains[0] = "give full with "+doesntcontains[0];
            }
        }
        File v = null;
        boolean goodval;
        do {
            goodval = true;
            try {

                String filePath = getString(prompt, null, null, null, doesntcontains);
                v = new File(filePath);
                if (!v.exists()) {
                    goodval = false;
                    this.out.println("The file at the provided path doesn't exist.");
                }
                if (v.isDirectory()) {
                    goodval = false;
                    this.out.println("The provided path isn't a file.");
                }
                if (!allowHidden && v.isHidden()) {
                    goodval = false;
                    this.out.println("The provided file is hidden, it must not be.");
                }
            } catch (Exception e) {
                goodval = false;
                this.out.println("Unexpected Error: An unexpected error occurred.\n\t"+e.getMessage());
            }
        } while (!goodval);
        return v;
    } // end of getFile
    /**
     * Gets a file provided by the user that fits with in the given parameters.
     * @param prompt The prompt to display to the user.
     * @param allowHidden The statement of accepting files that the user's operating system recognizes as hidden.
     * @param doesntcontains A string that cannot be in the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that the String is formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The {@link File} instance created from the user given file path.
     */
    public File getFile(String prompt, boolean allowHidden, String doesntcontains) {
        return getFile(prompt, allowHidden, new String[]{doesntcontains});
    }
    /**
     * Gets a file provided by the user that fits with in the given parameters.
     * @param prompt The prompt to display to the user.
     * @param doesntcontains An array of strings that cannot be within the user's response.
     *                 For checking of what the file path starts with then the first String of
     *                 this parameter must be formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The {@link File} instance created from the user given file path.
     */
    public File getFile(String prompt, String[] doesntcontains) {
        return getFile(prompt, true, doesntcontains);
    }
    /**
     * Gets a file provided by the user that fits with in the given parameters.
     * @param prompt The prompt to display to the user.
     * @param doesntcontains A string that cannot be in the user's response.
     *                 May also be used to return what the given string starts with,
     *                 given that the String is formatted as follows:
     *                 "start:Your String startsWith 1,Your String startsWith 2,..." ;
     *                 may also have each comma separated value begin with "lower:" to lower the user given value
     *                 or "upper:" to uppercase the user given value.
     * @return The {@link File} instance created from the user given file path.
     */
    public File getFile(String prompt, String doesntcontains) {
        return getFile(prompt, true, doesntcontains);
    }
    /**
     * Gets a file provided by the user that fits with in the given parameters.
     * @param prompt The prompt to display to the user.
     * @return The {@link File} instance created from the user given file path.
     */
    public File getFile(String prompt) {
        return getFile(prompt, true, (String[]) null);
    }
    // end of getFile

    // Other methods
    /**
     * Closes this UserInput.
     * <br>
     * This will call the close method provided by the {@link Scanner}
     * and (given that one was provided) {@link InputStream}.
     * <br>
     * After closing, if a prompt is attempted, then an {@link IllegalStateException}
     * will be called, this is the only exception that should be called from the {@link UserInput} class.
     * @see #closeALL()
     */
    @Override
    public void close() {
        try {
            this.sc.close();
            if (this.in != null) {
                this.in.close();
            }
        } catch (IOException ignore) {}
        this.closed = true;
    } // end of close

    /**
     * Like the {@link #close()}, this method also closes this UserInput.
     * <br>
     * However, unlike {@link #close()},
     * this method will also call the close method provided by {@link PrintStream}.
     * <br>
     * After closing, if a prompt is attempted, then an {@link IllegalStateException}
     * will be called, this is the only exception that should be called from the {@link UserInput} class.
     * @see #close()
     */
    public void closeALL() {
        close();
        this.out.close();
    } // end of closeALL
} // end of program
