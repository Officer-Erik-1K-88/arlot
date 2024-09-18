package arlot.protect;

import arlot.math.*;
import arlot.math.Number;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Encryption {
    public static final char[] VALIDKEYCHARS = new char[]{
            '.',',','?','!','-','+','=','<','>','/','\\','|','~','&','$','%','^','*', // special characters
            '0','1','2','3','4','5','6','7','8','9', // numbers 0-9
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z', // letters a-z, lower case
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' // letters A-Z, upper case
    };
    public static final char[] VALID_SECUREKEY_ADDED_CHARS = new char[]{
            '@','#','(',')','[',']','{','}','`','"','\'','_',':',';'
    };
    public static final int PASSMINLENGTH = 8;
    public static final long PASSMAXLENGTH = 2861946L;
    public static final int MAXCHAR = 65535;

    private static final String KEYSTORE_FILE = "EncriptionPasswords.jks";
    protected static final Encryption BASE_ENCRYPTION;
    private boolean isBase = false;
    static {
        try {
            String key = "6684294889!!Rudie&rjf2294I:PFtwGbtiPS@AET*ALIASasPassword";
            String alias = "TaxedEncriptionAlias@SytemProtectionIsToBeGuaranteedWithTheFollowingPad:6684294889!!Rudie&rjf2294I";
            BASE_ENCRYPTION = new Encryption(true, key.toCharArray(), alias.toCharArray());
            BASE_ENCRYPTION.isBase = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSecure = false;
    private KeyStore.PasswordProtection key, password, alias;
    private StringBuilder errormsg;

    private List<String[]> encrypted_data = Collections.synchronizedList(new ArrayList<String[]>());

    /**
     * Checks if a given key is valid.
     * @param key The encryption key, can be a secure key given that isSecure is true.
     * @param isSecure Is the statement of if the given key is a secure key.
     * @return Whether the key is valid.
     */
    public boolean isValidKey(char[] key, boolean isSecure) {
        boolean good = true;
        String validChars = String.valueOf(VALIDKEYCHARS);
        if (isSecure) {
            validChars += String.valueOf(VALID_SECUREKEY_ADDED_CHARS);
        }
        for (char c : key) {
            if (!validChars.contains(String.valueOf(c))) {
                good = false;
                break;
            }
        }
        return good;
    }

    /**
     * Checks if a normal encryption key is valid.
     * @param key The encryption key.
     * @return Whether the key is valid.
     */
    public boolean isValidKey(char[] key) {
        return isValidKey(key, false);
    }

    /**
     * Validates that the password is with in the allowed length parameters.
     * @return
     */
    private boolean isValid() {
        this.errormsg = new StringBuilder();
        if (this.password.getPassword().length < PASSMINLENGTH) {
            this.errormsg.append("\n\t")
                    .append("The password length must be at least ")
                    .append(PASSMINLENGTH)
                    .append(" characters long.");
        } else if (this.password.getPassword().length > PASSMAXLENGTH) {
            this.errormsg.append("\n\t")
                    .append("The password length must not exceed ")
                    .append(PASSMAXLENGTH)
                    .append(" characters long.");
        }
        return this.errormsg.isEmpty();
    } // end of isValid

    /**
     * Builds the key based on the provided password.
     * @throws Exception
     */
    private void build() throws Exception {
        char[] charpass = this.password.getPassword();
        Number numkey = new Number();
        // Using the ASCII to calculate the number is as follows:
        // The first two and last two chars are multiplied together
        // The first two and last two chars are then removed from the array of chars
        // Then all the rest are added to the number key
        // Then the number key is multiplied by the length of the password
        // And finally the number key is divided by two
        // The final number must be rounded down.
        numkey.add((int) charpass[0])
                .multiply((int) charpass[1])
                .multiply((int) charpass[charpass.length-1])
                .multiply((int) charpass[charpass.length-2]);
        charpass = Arrays.copyOfRange(charpass, 2, charpass.length-2);
        for (char c : charpass) {
            numkey.add((int) c);
        }
        numkey.multiply(this.password.getPassword().length)
                .divide(2)
                .floor();
        // turning the number key into the actual key:
        // modulo number key by half the length of valid key chars
        // use the remainder to get a char value from the valid key chars
        // then subtract number key by two
        // then add the char to the key
        StringBuilder keyhold = new StringBuilder();
        while (numkey.symbols("gt", "0")) {
            keyhold.append(VALIDKEYCHARS[(int) Basic.nortic(
                    Long.parseLong(numkey.mod(VALIDKEYCHARS.length/2).toString()),
                    (long) VALIDKEYCHARS.length)]);
            numkey.divide(VALIDKEYCHARS.length/2).floor().subtract(2);
            //System.out.println(numkey);
        }
        this.key = new KeyStore.PasswordProtection(keyhold.toString().toCharArray());
        //System.out.println(this.key.getPassword());
    } // end of build
    private KeyStore.PasswordProtection secureKeyToPass(char[] secureKey) {
        StringBuilder password = new StringBuilder();
        Number numkey = new Number();
        String validChars = String.valueOf(VALIDKEYCHARS)+String.valueOf(VALID_SECUREKEY_ADDED_CHARS);
        for (char c : secureKey) {
            numkey.add(validChars.indexOf(c)).add((int) c).divide(2).floor();
            password.append((char) Integer.parseInt(numkey.toString()));
            numkey.add(MAXCHAR).divide(6000).floor();
        }
        return new KeyStore.PasswordProtection(password.toString().toCharArray());
    } // end of secureKeyToPassAndKey

    // Constructors
    public Encryption() {
        this.password = new KeyStore.PasswordProtection(null);
        this.key = new KeyStore.PasswordProtection(null);
        this.alias = new KeyStore.PasswordProtection("Doesn't Exist, Currently.".toCharArray());
    }
    public Encryption(char[] password) throws Exception {
        this.password = new KeyStore.PasswordProtection(password);
        this.key = new KeyStore.PasswordProtection(null);
        this.alias = new KeyStore.PasswordProtection("Doesn't Exist, Currently.".toCharArray());
        if (isValid()) {
            build();
        } else {
            throw new SecurityException("Invalid password provided:"+this.errormsg);
        }
    }
    public Encryption(boolean isSecure, char[] secureKey, char[] alias) throws Exception {
        this.isSecure = isSecure;
        if (isValidKey(secureKey, true)) {
            this.password = secureKeyToPass(secureKey);
            this.key = new KeyStore.PasswordProtection(null);
            this.alias = new KeyStore.PasswordProtection(alias);
            build();
        } else {
            throw new SecurityException("Invalid secureKey provided.");
        }
        //this.key = new KeyStore.PasswordProtection(key.toCharArray());
    }
    public Encryption(boolean isSecure, char[] secureKey) throws Exception {
        new Encryption(isSecure, secureKey, "Doesn't Exist, Currently.".toCharArray());
    }
    public Encryption(KeyStore.PasswordProtection password) throws Exception {
        new Encryption(password.getPassword());
    }

    /**
     * Used to encrypt data based on the password provided to the Encryption instance.
     * @param data The data to be encoded, is a String array.
     * @return The encoded data.
     */
    public String[] encode(String[] data) {
        StringBuilder[] holder = new StringBuilder[data.length];
        Arrays.fill(holder, new StringBuilder());
        String[] finalhold = new String[data.length];
        for (int i=0; i<data.length; i++) {
            char[] datalist = data[i].toCharArray();
            int count = 1, overcount = 0;
            for (char c: datalist) {
                if (count == this.key.getPassword().length+1) {
                    count = 1;
                }
                int a = this.key.getPassword()[count-1];
                int digchar = (
                        (((int) c * count) + a)
                                +((overcount + a) * count));
                // max char = 65535
                //System.out.println((int) c+" : "+(int) this.key.getPassword()[count-1]+" : "+count+" : "+digchar);
                holder[i].append((char) digchar);
                count += 1;
                overcount += 1;
            }
            finalhold[i] = holder[i].toString();
        }
        return finalhold;
    } // end of encode

    /**
     * Used to decode the encrypted data created by the `encode`.
     * @param password The password set to the Encryption, is just for verification.
     * @param data The data to be decoded, is a String array.
     * @return The decoded data.
     */
    public String[] decode(String password, String[] data) {
        if (!Arrays.equals(password.toCharArray(), this.password.getPassword())) {
            throw new SecurityException("The incorrect password or alias was given to Encryption.decode");
        } else {
            StringBuilder[] holder = new StringBuilder[data.length];
            Arrays.fill(holder, new StringBuilder());
            String[] finalhold = new String[data.length];
            for (int i=0; i<data.length; i++) {
                char[] datalist = data[i].toCharArray();
                int count = 1, overcount = 0;
                for (char c: datalist) {
                    if (count == this.key.getPassword().length+1) {
                        count = 1;
                    }
                    int a = this.key.getPassword()[count-1];
                    int digchar = ((((int) c - (
                            (overcount + a) * count)
                    ) - a) / count);
                    holder[i].append((char) digchar);
                    count += 1;
                    overcount += 1;
                }
                finalhold[i] = holder[i].toString();
            }
            return finalhold;
        }
    } // end of decode
    /**
     * Used to decode the encrypted data created by the `encode`.
     * Only available to secure encryption processes.
     * @param alias The alias set to the Encryption, is for verification.
     * @param data The data to be decoded, is a String array.
     * @return The decoded data.
     */
    public String[] decode(char[] alias, String[] data) {
        if (isBase) {
            throw new SecurityException("Cannot decode base encryption's via alias.");
        }
        if (isSecure && !Arrays.equals(this.alias.getPassword(), "Doesn't Exist, Currently.".toCharArray())) {
            if (!Arrays.equals(alias, this.alias.getPassword())) {
                throw new SecurityException("The provided alias is incorrect.");
            } else {
                return decode(this.password.getPassword().toString(), data);
            }
        } else {
            throw new SecurityException("This Encryption process is not secure or doesn't hava an alias.");
        }
    }

    public void saveEncryptedData(String[] data) {
        this.encrypted_data.add(data);
    }

    public void save(String[] data, boolean isEncrypted) {
        if (!isEncrypted) {
            data = encode(data);
        }
        saveEncryptedData(data);
    }

    public String[] getEncryptedData(int index) throws Exception {
        index = Math.toIntExact(Basic.nortic((long) index, (long) this.encrypted_data.size()));
        return this.encrypted_data.get(index);
    }

    // setters

    /**
     * Is used to change the password of the current Encryption handler.
     * @param password The char array to replace the current password with.
     * @throws Exception When trying to change a secure password.
     */
    public void setPassword(char[] password) throws Exception {
        if (isSecure) {
            throw new SecurityException("Cannot change a secure password");
        }
        this.password = new KeyStore.PasswordProtection(password);
        this.key = new KeyStore.PasswordProtection(null);
        if (isValid()) {
            build();
        } else {
            throw new SecurityException("Invalid password provided:"+this.errormsg);
        }
    }
    /**
     * Is used to change the alias of the current Encryption handler.
     * @param alias The char array to replace the current alias with.
     * @throws Exception When trying to change a secure alias.
     */
    public void setAlias(char[] alias) throws Exception {
        if (isSecure) {
            throw new SecurityException("Cannot change a secure alias");
        }
        this.alias = new KeyStore.PasswordProtection(alias);
    }
    // getters

    /**
     * Uses a protected base encryption to encrypt the returned password.
     * @return The encrypted password of the current Encryption.
     */
    public String getPassword() {
        if (isSecure) {
            throw new SecurityException("Cannot provide a secure password.");
        }
        return BASE_ENCRYPTION.encode(new String[]{String.valueOf(this.password.getPassword())})[0];
    }

    /**
     * This is only really useful for the secure encryption processes.
     * @return The alias of the encryption.
     */
    public String getAlias() {
        return this.alias.getPassword().toString();
    }
} // end of encription program
