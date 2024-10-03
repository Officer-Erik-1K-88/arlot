package arlot.data.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class File extends java.io.File {

    private AccessFileChannel channel = null;

    /**
     * Creates a new {@code File} instance by converting an
     * {@link AccessFileChannel} into a {@code java.io.File}
     *
     * @param channel The {@link AccessFileChannel} to make into a {@code File}.
     */
    public File(AccessFileChannel channel) {
        super(channel.toFile().getPath());
        this.channel = channel;
    }

    /**
     * Creates a new {@code File} instance by converting {@link java.io.File}
     * into a {@code arlot.data.file.File}.
     *
     * @param file The {@code java.io.File} to make into a {@code arlot.data.file.File}
     */
    public File(java.io.File file) {
        super(file.getPath());
    }

    /**
     * Creates a new {@code File} instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the {@code pathname} argument is {@code null}
     */
    public File(String pathname) {
        super(pathname);
    }

    /**
     * Creates a new {@code File} instance from a parent pathname string
     * and a child pathname string.
     *
     * <p> If {@code parent} is {@code null} then the new
     * {@code File} instance is created as if by invoking the
     * single-argument {@code File} constructor on the given
     * {@code child} pathname string.
     *
     * <p> Otherwise the {@code parent} pathname string is taken to denote
     * a directory, and the {@code child} pathname string is taken to
     * denote either a directory or a file.  If the {@code child} pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If {@code parent} is the empty string then
     * the new {@code File} instance is created by converting
     * {@code child} into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     *
     * @param parent The parent pathname string
     * @param child  The child pathname string
     * @throws NullPointerException If {@code child} is {@code null}
     */
    public File(String parent, String child) {
        super(parent, child);
    }

    /**
     * Creates a new {@code File} instance from a parent abstract
     * pathname and a child pathname string.
     *
     * <p> If {@code parent} is {@code null} then the new
     * {@code File} instance is created as if by invoking the
     * single-argument {@code File} constructor on the given
     * {@code child} pathname string.
     *
     * <p> Otherwise the {@code parent} abstract pathname is taken to
     * denote a directory, and the {@code child} pathname string is taken
     * to denote either a directory or a file.  If the {@code child}
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If {@code parent} is the empty
     * abstract pathname then the new {@code File} instance is created by
     * converting {@code child} into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @param parent The parent abstract pathname
     * @param child  The child pathname string
     * @throws NullPointerException If {@code child} is {@code null}
     */
    public File(java.io.File parent, String child) {
        super(parent, child);
    }

    /**
     * Creates a new {@code File} instance by converting the given
     * {@code file:} URI into an abstract pathname.
     *
     * <p> The exact form of a {@code file:} URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     *
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     *
     * <blockquote><code>
     * new File(</code><i>&nbsp;f</i><code>.{@link #toURI()
     * toURI}()).equals(</code><i>&nbsp;f</i><code>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </code></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a {@code file:} URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            {@code "file"}, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If {@code uri} is {@code null}
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see #toURI()
     * @see URI
     */
    public File(URI uri) {
        super(uri);
    }

    /**
     *
     * @return A {@code BufferedInputStream}.
     * Or <code>null</code> if the file doesn't exist.
     */
    public BufferedInputStream newInputStream() {
        try {
            return new BufferedInputStream(new FileInputStream(this));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     *
     * @return A {@code BufferedReader}.
     * Or <code>null</code> if the file doesn't exist.
     */
    public BufferedReader newReader() {
        try {
            return new BufferedReader(new FileReader(this));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     *
     * @return A {@code BufferedOutputStream}.
     * Or <code>null</code> if the file doesn't exist.
     */
    public BufferedOutputStream newOutputStream() {
        try {
            return new BufferedOutputStream(new FileOutputStream(this));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     *
     * @return A {@code BufferedWriter}.
     * Or <code>null</code> if the file doesn't exist.
     */
    public BufferedWriter newWriter() {
        try {
            return new BufferedWriter(new FileWriter(this));
        } catch (IOException e) {
            return null;
        }
    }

    public AccessFileChannel getChannel() {
        AccessFileChannel fc = this.channel;
        if (fc == null) {
            synchronized (this) {
                try {
                    this.channel = fc = new AccessFileChannel(this);
                } catch (FileNotFoundException e) {
                    this.channel = fc = null;
                }
                if (fc != null && !fc.isOpen()) {
                    try {
                        fc.close();
                    } catch (IOException ioe) {
                        throw new InternalError(ioe); // should not happen
                    }
                    this.channel = fc = null;
                }
            }
        }
        return fc;
    }


    /**
     * Will create this file or directory.
     *
     * @return {@code true} if the path was successfully created;
     *          {@code false} if the path already exists
     *          or an {@link IOException} would have been thrown.
     * @see #createNewFile()
     * @see #mkdirs()
     * @see #mkdir()
     */
    public boolean create() {
        try {
            // Create parent directories if they don't exist
            if (getParentFile() != null && !getParentFile().exists()) {
                getParentFile().mkdirs();
            }

            if (Files.isDirectory(toPath()) || toPath().endsWith("/") || toPath().endsWith("\\")) {
                // If it's a directory or the path ends with a file separator, create directories
                return mkdirs();  // Create the directory (including parent directories)
            } else {
                // If it's a file, create the file
                return createNewFile();  // Create the file if it doesn't exist
            }
        } catch (IOException ignored) {}
        return false;
    }
}
