package arlot.data.file;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import jdk.internal.access.JavaIOFileDescriptorAccess;
import jdk.internal.access.SharedSecrets;
import jdk.internal.ref.CleanerFactory;
import sun.nio.ch.FileChannelImpl;

import java.io.UncheckedIOException;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import java.io.FileNotFoundException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;

public class AccessFileChannel extends FileChannel {
    // Access to FileDescriptor internals
    private static final JavaIOFileDescriptorAccess fdAccess =
            SharedSecrets.getJavaIOFileDescriptorAccess();

    private final String mode;
    private final boolean readable;
    private final boolean writable;

    private final String path;
    private File file = null;

    private final RandomAccessFile raFile;

    private final Closeable parent;
    private final Cleaner.Cleanable closer;
    private boolean closed = false;

    private boolean linkedRA;

    private FileChannel channel;

    public AccessFileChannel(File file) throws FileNotFoundException {
        this(file, "rw", false, null);
    }

    public AccessFileChannel(String name) throws FileNotFoundException {
        this(name, "rw", false, null);
    }

    protected AccessFileChannel(Object object, String mode, boolean linkRAFile, Closeable parent) throws FileNotFoundException {
        switch (mode) {
            case "rw" -> {
                this.mode = "rwd";
                this.readable = true;
                this.writable = true;
            }
            case "w" -> {
                this.mode = "rwd";
                this.readable = false;
                this.writable = true;
                linkRAFile = false;
            }
            case "r" -> {
                this.mode = mode;
                this.readable = true;
                this.writable = false;
            }
            default -> throw new IllegalArgumentException("Mode must only be `rw`, `r`, or `w`.");
        }
        switch (object) {
            case File f -> {
                this.file = f;
                this.raFile = new RandomAccessFile(file, this.mode);
                this.path = file.getPath();
            }
            case String s -> {
                this.raFile = new RandomAccessFile(s, this.mode);
                this.path = s;
            }
            case null, default -> throw new IllegalArgumentException("Object type not allowed.");
        }
        this.linkedRA = linkRAFile;
        getChannel();

        this.parent = parent;
        try {
            this.closer = parent != null ? null :
                    CleanerFactory.cleaner().register(this, new Closer(this.raFile.getFD()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new {@code AccessFileChannel} that can be used to access the
     * contents of a file.
     *
     * @param file Can be either a {@link File} or {@link String} that
     *               represents the file to be accessed.
     * @param readable The state of whether to allow for reading functionality.
     * @param writable The state of whether to allow for writing functionality.
     * @param linkedRA Whether to use the constructed {@link RandomAccessFile}
     *                 to create the file channel. This parameter can only be true
     *                 if {@code readable} is true, otherwise will be forced to false.
     * @return a {@code AccessFileChannel} that can be used to read/write to the provided file.
     * @throws FileNotFoundException If the {@code file} is not a file.
     * @throws IllegalArgumentException If the {@code file} is not a supported type.
     * Or if both {@code readable} and {@code writable} are false.
     */
    public static AccessFileChannel open(Object file, boolean readable, boolean writable,
                                         boolean linkedRA, Closeable parent) throws FileNotFoundException {
        String mode = "";
        if (readable) {
            mode += "r";
        }
        if (writable) {
            mode += "w";
        }
        if (mode.isBlank()) {
            throw new IllegalArgumentException("Both readable and writable are false, that is prohibited.");
        }
        return new AccessFileChannel(file, mode, linkedRA, parent);
    }

    public String getMode() {
        return mode;
    }

    /**
     * Gets whether this {@code AccessFileChannel} has a file channel that is
     * congruent with the construction method used to construct the file channel for
     * {@link RandomAccessFile}.
     *
     * @return True if the {@link #getChannel()} was constructed using
     * {@link RandomAccessFile}.
     */
    public boolean isLinkedRA() {
        return linkedRA;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public boolean isClosed() {
        return closed && !isOpen();
    }

    /**
     * Gets the {@link RandomAccessFile} that was constructed by this
     * {@code AccessFileChannel}.
     *
     * @return A wrapped {@link RandomAccessFile}.
     */
    public RAFile getRaFile() {
        return new RAFile(this.raFile, this);
    }

    /**
     * Returns the unique {@link java.nio.channels.FileChannel FileChannel}
     * object associated with this {@code AccessFileChannel}.
     *
     * <br>
     *
     * The functionality of the returned channel is the driving force of this
     * {@code AccessFileChannel}, this in change means that this {@code AccessFileChannel}
     * is synchronized with the returned channel.
     *
     * @return the file channel associated with this access file channel
     */
    public FileChannel getChannel() {
        FileChannel fc = this.channel;
        if (fc == null) {
            synchronized (this) {
                fc = this.channel;
                if (fc == null) {
                    if (this.linkedRA) {
                        this.channel = fc = this.raFile.getChannel();
                    } else {
                        try {
                            this.channel = fc = FileChannelImpl.open(this.raFile.getFD(),
                                    this.path, this.readable, this.writable,
                                    false, this);
                            this.linkedRA = false;
                        } catch (IOException e) {
                            this.channel = fc = this.raFile.getChannel();
                            this.linkedRA = true;
                        }
                    }
                    if (fc != null && !isOpen()) {
                        try {
                            fc.close();
                        } catch (IOException ioe) {
                            throw new InternalError(ioe); // should not happen
                        }
                    }
                }
            }
        }
        return fc;
    }

    public File toFile() {
        if (this.file == null) {
            this.file = new File(this.path);
        }
        return this.file;
    }

    /**
     * Makes sure that this {@code AccessFileChannel} is fully open before allowing
     * most methods from executing. If not fully open then an {@link IOException}
     * is thrown.
     *
     * @throws ClosedChannelException If this {@code AccessFileChannel} is closed.
     * @throws AsynchronousCloseException If this {@code AccessFileChannel} is
     * both open and closed simultaneously.
     * @throws IOException For any other reasons.
     */
    private void ensureOpen() throws IOException {
        if (isClosed())
            throw new ClosedChannelException();
        if (!isOpen() || this.closed) {
            throw new AsynchronousCloseException();
        }
        if (!readable && !writable) {
            throw new IOException("Can't do anything; both readable and writable are false.");
        }
    }

    /**
     * Reads a sequence of bytes from this channel into the given buffer.
     *
     * <p> Bytes are read starting at this channel's current file position, and
     * then the file position is updated with the number of bytes actually
     * read.  Otherwise this method behaves exactly as specified in the {@link
     * ReadableByteChannel} interface. </p>
     *
     * @param dst
     * @throws ClosedChannelException      {@inheritDoc}
     * @throws AsynchronousCloseException  {@inheritDoc}
     * @throws ClosedByInterruptException  {@inheritDoc}
     * @throws NonReadableChannelException {@inheritDoc}
     */
    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen();
        return this.channel.read(dst);
    }

    /**
     * Reads a sequence of bytes from this channel into a subsequence of the
     * given buffers.
     *
     * <p> Bytes are read starting at this channel's current file position, and
     * then the file position is updated with the number of bytes actually
     * read.  Otherwise this method behaves exactly as specified in the {@link
     * java.nio.channels.ScatteringByteChannel} interface.  </p>
     *
     * @param dsts
     * @param offset
     * @param length
     * @throws ClosedChannelException      {@inheritDoc}
     * @throws AsynchronousCloseException  {@inheritDoc}
     * @throws ClosedByInterruptException  {@inheritDoc}
     * @throws NonReadableChannelException {@inheritDoc}
     */
    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        ensureOpen();
        return this.channel.read(dsts, offset, length);
    }

    /**
     * Reads a sequence of bytes from this channel into the given buffer,
     * starting at the given file position.
     *
     * <p> This method works in the same manner as the {@link
     * #read(ByteBuffer)} method, except that bytes are read starting at the
     * given file position rather than at the channel's current position.  This
     * method does not modify this channel's position.  If the given position
     * is greater than or equal to the file's current size then no bytes are
     * read.  </p>
     *
     * @param dst      The buffer into which bytes are to be transferred
     * @param position The file position at which the transfer is to begin;
     *                 must be non-negative
     * @return The number of bytes read, possibly zero, or {@code -1} if the
     * given position is greater than or equal to the file's current
     * size
     * @throws IllegalArgumentException    If the position is negative or the buffer is read-only
     * @throws NonReadableChannelException If this channel was not opened for reading
     * @throws ClosedChannelException      If this channel is closed
     * @throws AsynchronousCloseException  If another thread closes this channel
     *                                     while the read operation is in progress
     * @throws ClosedByInterruptException  If another thread interrupts the current thread
     *                                     while the read operation is in progress, thereby
     *                                     closing the channel and setting the current thread's
     *                                     interrupt status
     * @throws IOException                 If some other I/O error occurs
     */
    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        ensureOpen();
        return this.channel.read(dst, position);
    }

    /**
     * Writes a sequence of bytes to this channel from the given buffer.
     *
     * <p> Bytes are written starting at this channel's current file position
     * unless the channel is in append mode, in which case the position is
     * first advanced to the end of the file.  The file is grown, if necessary,
     * to accommodate the written bytes, and then the file position is updated
     * with the number of bytes actually written.  Otherwise this method
     * behaves exactly as specified by the {@link WritableByteChannel}
     * interface. </p>
     *
     * @param src
     * @throws ClosedChannelException      {@inheritDoc}
     * @throws AsynchronousCloseException  {@inheritDoc}
     * @throws ClosedByInterruptException  {@inheritDoc}
     * @throws NonWritableChannelException {@inheritDoc}
     */
    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureOpen();
        return this.channel.write(src);
    }

    /**
     * Writes a sequence of bytes to this channel from a subsequence of the
     * given buffers.
     *
     * <p> Bytes are written starting at this channel's current file position
     * unless the channel is in append mode, in which case the position is
     * first advanced to the end of the file.  The file is grown, if necessary,
     * to accommodate the written bytes, and then the file position is updated
     * with the number of bytes actually written.  Otherwise this method
     * behaves exactly as specified in the {@link java.nio.channels.GatheringByteChannel}
     * interface.  </p>
     *
     * @param srcs
     * @param offset
     * @param length
     * @throws ClosedChannelException      {@inheritDoc}
     * @throws AsynchronousCloseException  {@inheritDoc}
     * @throws ClosedByInterruptException  {@inheritDoc}
     * @throws NonWritableChannelException {@inheritDoc}
     */
    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        ensureOpen();
        return this.channel.write(srcs, offset, length);
    }

    /**
     * Writes a sequence of bytes to this channel from the given buffer,
     * starting at the given file position.
     *
     * <p> This method works in the same manner as the {@link
     * #write(ByteBuffer)} method, except that bytes are written starting at
     * the given file position rather than at the channel's current position.
     * This method does not modify this channel's position.  If the given
     * position is greater than or equal to the file's current size then the
     * file will be grown to accommodate the new bytes; the values of any bytes
     * between the previous end-of-file and the newly-written bytes are
     * unspecified.  </p>
     *
     * <p> If the file is open in <a href="#append-mode">append mode</a>, then
     * the effect of invoking this method is unspecified.
     *
     * @param src      The buffer from which bytes are to be transferred
     * @param position The file position at which the transfer is to begin;
     *                 must be non-negative
     * @return The number of bytes written, possibly zero
     * @throws IllegalArgumentException    If the position is negative
     * @throws NonWritableChannelException If this channel was not opened for writing
     * @throws ClosedChannelException      If this channel is closed
     * @throws AsynchronousCloseException  If another thread closes this channel
     *                                     while the write operation is in progress
     * @throws ClosedByInterruptException  If another thread interrupts the current thread
     *                                     while the write operation is in progress, thereby
     *                                     closing the channel and setting the current thread's
     *                                     interrupt status
     * @throws IOException                 If some other I/O error occurs
     */
    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        ensureOpen();
        return this.channel.write(src, position);
    }

    /**
     * Returns this channel's file position.
     *
     * @return This channel's file position,
     * a non-negative integer counting the number of bytes
     * from the beginning of the file to the current position
     * @throws ClosedChannelException If this channel is closed
     * @throws IOException            If some other I/O error occurs
     */
    @Override
    public long position() throws IOException {
        ensureOpen();
        return this.channel.position();
    }

    /**
     * Sets this channel's file position.
     *
     * <p> Setting the position to a value that is greater than the file's
     * current size is legal but does not change the size of the file.  A later
     * attempt to read bytes at such a position will immediately return an
     * end-of-file indication.  A later attempt to write bytes at such a
     * position will cause the file to be grown to accommodate the new bytes;
     * the values of any bytes between the previous end-of-file and the
     * newly-written bytes are unspecified.  </p>
     *
     * @param newPosition The new position, a non-negative integer counting
     *                    the number of bytes from the beginning of the file
     * @return This file channel
     * @throws ClosedChannelException   If this channel is closed
     * @throws IllegalArgumentException If the new position is negative
     * @throws IOException              If some other I/O error occurs
     */
    @Override
    public FileChannel position(long newPosition) throws IOException {
        ensureOpen();
        return this.channel.position(newPosition);
    }

    /**
     * Returns the current size of this channel's file.
     *
     * @return The current size of this channel's file,
     * measured in bytes
     * @throws ClosedChannelException If this channel is closed
     * @throws IOException            If some other I/O error occurs
     */
    @Override
    public long size() throws IOException {
        ensureOpen();
        return this.channel.size();
    }

    /**
     * Truncates this channel's file to the given size.
     *
     * <p> If the given size is less than the file's current size then the file
     * is truncated, discarding any bytes beyond the new end of the file.  If
     * the given size is greater than or equal to the file's current size then
     * the file is not modified.  In either case, if this channel's file
     * position is greater than the given size then it is set to that size.
     * </p>
     *
     * @param size The new size, a non-negative byte count
     * @return This file channel
     * @throws NonWritableChannelException If this channel was not opened for writing
     * @throws ClosedChannelException      If this channel is closed
     * @throws IllegalArgumentException    If the new size is negative
     * @throws IOException                 If some other I/O error occurs
     */
    @Override
    public FileChannel truncate(long size) throws IOException {
        ensureOpen();
        FileChannel actual = this.channel.truncate(size);
        if (actual == null) {
            return null;
        }
        return this;
    }

    /**
     * Forces any updates to this channel's file to be written to the storage
     * device that contains it.
     *
     * <p> If this channel's file resides on a local storage device then when
     * this method returns it is guaranteed that all changes made to the file
     * since this channel was created, or since this method was last invoked,
     * will have been written to that device.  This is useful for ensuring that
     * critical information is not lost in the event of a system crash.
     *
     * <p> If the file does not reside on a local device then no such guarantee
     * is made.
     *
     * <p> The {@code metaData} parameter can be used to limit the number of
     * I/O operations that this method is required to perform.  Passing
     * {@code false} for this parameter indicates that only updates to the
     * file's content need be written to storage; passing {@code true}
     * indicates that updates to both the file's content and metadata must be
     * written, which generally requires at least one more I/O operation.
     * Whether this parameter actually has any effect is dependent upon the
     * underlying operating system and is therefore unspecified.
     *
     * <p> Invoking this method may cause an I/O operation to occur even if the
     * channel was only opened for reading.  Some operating systems, for
     * example, maintain a last-access time as part of a file's metadata, and
     * this time is updated whenever the file is read.  Whether or not this is
     * actually done is system-dependent and is therefore unspecified.
     *
     * <p> This method is only guaranteed to force changes that were made to
     * this channel's file via the methods defined in this class, or the methods
     * defined by {@link FileOutputStream} or
     * {@link RandomAccessFile} when the channel was obtained with the
     * {@code getChannel} method. It may or may not force changes that were made
     * by modifying the content of a
     * {@link MappedByteBuffer <i>mapped byte buffer</i>} obtained by
     * invoking the {@link #map map} method.  Invoking the {@link
     * MappedByteBuffer#force force} method of the mapped byte buffer will
     * force changes made to the buffer's content to be written.  </p>
     *
     * @param metaData If {@code true} then this method is required to force changes
     *                 to both the file's content and metadata to be written to
     *                 storage; otherwise, it need only force content changes to be
     *                 written
     * @throws ClosedChannelException If this channel is closed
     * @throws IOException            If some other I/O error occurs
     */
    @Override
    public void force(boolean metaData) throws IOException {
        ensureOpen();
        this.channel.force(metaData);
    }

    /**
     * Transfers bytes from this channel's file to the given writable byte
     * channel.
     *
     * <p> An attempt is made to read up to {@code count} bytes starting at
     * the given {@code position} in this channel's file and write them to the
     * target channel.  An invocation of this method may or may not transfer
     * all of the requested bytes; whether or not it does so depends upon the
     * natures and states of the channels.  Fewer than the requested number of
     * bytes are transferred if this channel's file contains fewer than
     * {@code count} bytes starting at the given {@code position}, or if the
     * target channel is non-blocking and it has fewer than {@code count}
     * bytes free in its output buffer.
     *
     * <p> This method does not modify this channel's position.  If the given
     * position is greater than or equal to the file's current size then no
     * bytes are transferred.  If the target channel has a position then bytes
     * are written starting at that position and then the position
     * is incremented by the number of bytes written.
     *
     * <p> This method is potentially much more efficient than a simple loop
     * that reads from this channel and writes to the target channel.  Many
     * operating systems can transfer bytes directly from the filesystem cache
     * to the target channel without actually copying them.  </p>
     *
     * @param position The position within the file at which the transfer is to begin;
     *                 must be non-negative
     * @param count    The maximum number of bytes to be transferred; must be
     *                 non-negative
     * @param target   The target channel
     * @return The number of bytes, possibly zero,
     * that were actually transferred
     * @throws IllegalArgumentException    If the preconditions on the parameters do not hold
     * @throws NonReadableChannelException If this channel was not opened for reading
     * @throws NonWritableChannelException If the target channel was not opened for writing
     * @throws ClosedChannelException      If either this channel or the target channel is closed
     * @throws AsynchronousCloseException  If another thread closes either channel
     *                                     while the transfer is in progress
     * @throws ClosedByInterruptException  If another thread interrupts the current thread while the
     *                                     transfer is in progress, thereby closing both channels and
     *                                     setting the current thread's interrupt status
     * @throws IOException                 If some other I/O error occurs
     */
    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        ensureOpen();
        return this.channel.transferTo(position, count, target);
    }

    /**
     * Transfers bytes into this channel's file from the given readable byte
     * channel.
     *
     * <p> An attempt is made to read up to {@code count} bytes from the
     * source channel and write them to this channel's file starting at the
     * given {@code position}.  An invocation of this method may or may not
     * transfer all of the requested bytes; whether or not it does so depends
     * upon the natures and states of the channels.  Fewer than the requested
     * number of bytes will be transferred if the source channel has fewer than
     * {@code count} bytes remaining, or if the source channel is non-blocking
     * and has fewer than {@code count} bytes immediately available in its
     * input buffer. No bytes are transferred, and zero is returned, if the
     * source has reached end-of-stream.
     *
     * <p> This method does not modify this channel's position.  If the given
     * position is greater than or equal to the file's current size then the
     * file will be grown to accommodate the new bytes; the values of any bytes
     * between the previous end-of-file and the newly-written bytes are
     * unspecified.  If the source channel has a position then bytes are read
     * starting at that position and then the position is incremented by the
     * number of bytes read.
     *
     * <p> This method is potentially much more efficient than a simple loop
     * that reads from the source channel and writes to this channel.  Many
     * operating systems can transfer bytes directly from the source channel
     * into the filesystem cache without actually copying them.  </p>
     *
     * @param src      The source channel
     * @param position The file position at which the transfer is to begin;
     *                 must be non-negative
     * @param count    The maximum number of bytes to be transferred; must be
     *                 non-negative
     * @return The number of bytes, possibly zero,
     * that were actually transferred
     * @throws IllegalArgumentException    If the preconditions on the parameters do not hold
     * @throws NonReadableChannelException If the source channel was not opened for reading
     * @throws NonWritableChannelException If this channel was not opened for writing
     * @throws ClosedChannelException      If either this channel or the source channel is closed
     * @throws AsynchronousCloseException  If another thread closes either channel
     *                                     while the transfer is in progress
     * @throws ClosedByInterruptException  If another thread interrupts the current thread while the
     *                                     transfer is in progress, thereby closing both channels and
     *                                     setting the current thread's interrupt status
     * @throws IOException                 If some other I/O error occurs
     */
    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        ensureOpen();
        return this.channel.transferFrom(src, position, count);
    }

    /**
     * Maps a region of this channel's file directly into memory.
     *
     * <p> The {@code mode} parameter specifies how the region of the file is
     * mapped and may be one of the following modes:
     *
     * <ul>
     *
     *   <li><p> <i>Read-only:</i> Any attempt to modify the resulting buffer
     *   will cause a {@link ReadOnlyBufferException} to be thrown.
     *   ({@link MapMode#READ_ONLY MapMode.READ_ONLY}) </p></li>
     *
     *   <li><p> <i>Read/write:</i> Changes made to the resulting buffer will
     *   eventually be propagated to the file; they may or may not be made
     *   visible to other programs that have mapped the same file.  ({@link
     *   MapMode#READ_WRITE MapMode.READ_WRITE}) </p></li>
     *
     *   <li><p> <i>Private:</i> Changes made to the resulting buffer will not
     *   be propagated to the file and will not be visible to other programs
     *   that have mapped the same file; instead, they will cause private
     *   copies of the modified portions of the buffer to be created.  ({@link
     *   MapMode#PRIVATE MapMode.PRIVATE}) </p></li>
     *
     * </ul>
     *
     * <p> An implementation may support additional map modes.
     *
     * <p> For a read-only mapping, this channel must have been opened for
     * reading; for a read/write or private mapping, this channel must have
     * been opened for both reading and writing.
     *
     * <p> The {@link MappedByteBuffer <i>mapped byte buffer</i>}
     * returned by this method will have a position of zero and a limit and
     * capacity of {@code size}; its mark will be undefined.  The buffer and
     * the mapping that it represents will remain valid until the buffer itself
     * is garbage-collected.
     *
     * <p> A mapping, once established, is not dependent upon the file channel
     * that was used to create it.  Closing the channel, in particular, has no
     * effect upon the validity of the mapping.
     *
     * <p> Many of the details of memory-mapped files are inherently dependent
     * upon the underlying operating system and are therefore unspecified.  The
     * behavior of this method when the requested region is not completely
     * contained within this channel's file is unspecified.  Whether changes
     * made to the content or size of the underlying file, by this program or
     * another, are propagated to the buffer is unspecified.  The rate at which
     * changes to the buffer are propagated to the file is unspecified.
     *
     * <p> For most operating systems, mapping a file into memory is more
     * expensive than reading or writing a few tens of kilobytes of data via
     * the usual {@link #read read} and {@link #write write} methods.  From the
     * standpoint of performance it is generally only worth mapping relatively
     * large files into memory.  </p>
     *
     * @param mode     One of the constants {@link MapMode#READ_ONLY READ_ONLY}, {@link
     *                 MapMode#READ_WRITE READ_WRITE}, or {@link MapMode#PRIVATE
     *                 PRIVATE} defined in the {@link MapMode} class, according to
     *                 whether the file is to be mapped read-only, read/write, or
     *                 privately (copy-on-write), respectively, or an implementation
     *                 specific map mode
     * @param position The position within the file at which the mapped region
     *                 is to start; must be non-negative
     * @param size     The size of the region to be mapped; must be non-negative and
     *                 no greater than {@link Integer#MAX_VALUE}
     * @return The mapped byte buffer
     * @throws NonReadableChannelException   If the {@code mode} is {@link MapMode#READ_ONLY READ_ONLY} or
     *                                       an implementation specific map mode requiring read access,
     *                                       but this channel was not opened for reading
     * @throws NonWritableChannelException   If the {@code mode} is {@link MapMode#READ_WRITE READ_WRITE},
     *                                       {@link MapMode#PRIVATE PRIVATE} or an implementation specific
     *                                       map mode requiring write access, but this channel was not
     *                                       opened for both reading and writing
     * @throws IllegalArgumentException      If the preconditions on the parameters do not hold
     * @throws UnsupportedOperationException If an unsupported map mode is specified
     * @throws IOException                   If some other I/O error occurs
     * @see MapMode
     * @see MappedByteBuffer
     */
    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        ensureOpen();
        return this.channel.map(mode, position, size);
    }

    /**
     * Acquires a lock on the given region of this channel's file.
     *
     * <p> An invocation of this method will block until the region can be
     * locked, this channel is closed, or the invoking thread is interrupted,
     * whichever comes first.
     *
     * <p> If this channel is closed by another thread during an invocation of
     * this method then an {@link AsynchronousCloseException} will be thrown.
     *
     * <p> If the invoking thread is interrupted while waiting to acquire the
     * lock then its interrupt status will be set and a {@link
     * FileLockInterruptionException} will be thrown.  If the invoker's
     * interrupt status is set when this method is invoked then that exception
     * will be thrown immediately; the thread's interrupt status will not be
     * changed.
     *
     * <p> The region specified by the {@code position} and {@code size}
     * parameters need not be contained within, or even overlap, the actual
     * underlying file.  Lock regions are fixed in size; if a locked region
     * initially contains the end of the file and the file grows beyond the
     * region then the new portion of the file will not be covered by the lock.
     * If a file is expected to grow in size and a lock on the entire file is
     * required then a region starting at zero, and no smaller than the
     * expected maximum size of the file, should be locked.  The zero-argument
     * {@link #lock()} method simply locks a region of size {@link
     * Long#MAX_VALUE}.  If the {@code position} is non-negative and the
     * {@code size} is zero, then a lock of size
     * {@code Long.MAX_VALUE - position} is returned.
     *
     * <p> Some operating systems do not support shared locks, in which case a
     * request for a shared lock is automatically converted into a request for
     * an exclusive lock.  Whether the newly-acquired lock is shared or
     * exclusive may be tested by invoking the resulting lock object's {@link
     * FileLock#isShared() isShared} method.
     *
     * <p> File locks are held on behalf of the entire Java virtual machine.
     * They are not suitable for controlling access to a file by multiple
     * threads within the same virtual machine.  </p>
     *
     * @param position The position at which the locked region is to start; must be
     *                 non-negative
     * @param size     The size of the locked region; must be non-negative, and the sum
     *                 {@code position}&nbsp;+&nbsp;{@code size} must be non-negative.
     *                 A value of zero means to lock all bytes from the specified
     *                 starting position to the end of the file, regardless of whether
     *                 the file is subsequently extended or truncated
     * @param shared   {@code true} to request a shared lock, in which case this
     *                 channel must be open for reading (and possibly writing);
     *                 {@code false} to request an exclusive lock, in which case this
     *                 channel must be open for writing (and possibly reading)
     * @return A lock object representing the newly-acquired lock
     * @throws IllegalArgumentException      If the preconditions on the parameters do not hold
     * @throws ClosedChannelException        If this channel is closed
     * @throws AsynchronousCloseException    If another thread closes this channel while the invoking
     *                                       thread is blocked in this method
     * @throws FileLockInterruptionException If the invoking thread is interrupted while blocked in this
     *                                       method
     * @throws OverlappingFileLockException  If a lock that overlaps the requested region is already held by
     *                                       this Java virtual machine, or if another thread is already
     *                                       blocked in this method and is attempting to lock an overlapping
     *                                       region
     * @throws NonReadableChannelException   If {@code shared} is {@code true} but this channel was not
     *                                       opened for reading
     * @throws NonWritableChannelException   If {@code shared} is {@code false} but this channel was not
     *                                       opened for writing
     * @throws IOException                   If some other I/O error occurs
     * @see #lock()
     * @see #tryLock()
     * @see #tryLock(long, long, boolean)
     */
    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        ensureOpen();
        return this.channel.lock(position, size, shared);
    }

    /**
     * Attempts to acquire a lock on the given region of this channel's file.
     *
     * <p> This method does not block.  An invocation always returns
     * immediately, either having acquired a lock on the requested region or
     * having failed to do so.  If it fails to acquire a lock because an
     * overlapping lock is held by another program then it returns
     * {@code null}.  If it fails to acquire a lock for any other reason then
     * an appropriate exception is thrown.
     *
     * <p> The region specified by the {@code position} and {@code size}
     * parameters need not be contained within, or even overlap, the actual
     * underlying file.  Lock regions are fixed in size; if a locked region
     * initially contains the end of the file and the file grows beyond the
     * region then the new portion of the file will not be covered by the lock.
     * If a file is expected to grow in size and a lock on the entire file is
     * required then a region starting at zero, and no smaller than the
     * expected maximum size of the file, should be locked.  The zero-argument
     * {@link #tryLock()} method simply locks a region of size {@link
     * Long#MAX_VALUE}.  If the {@code position} is non-negative and the
     * {@code size} is zero, then a lock of size
     * {@code Long.MAX_VALUE - position} is returned.
     *
     * <p> Some operating systems do not support shared locks, in which case a
     * request for a shared lock is automatically converted into a request for
     * an exclusive lock.  Whether the newly-acquired lock is shared or
     * exclusive may be tested by invoking the resulting lock object's {@link
     * FileLock#isShared() isShared} method.
     *
     * <p> File locks are held on behalf of the entire Java virtual machine.
     * They are not suitable for controlling access to a file by multiple
     * threads within the same virtual machine.  </p>
     *
     * @param position The position at which the locked region is to start; must be
     *                 non-negative
     * @param size     The size of the locked region; must be non-negative, and the sum
     *                 {@code position}&nbsp;+&nbsp;{@code size} must be non-negative.
     *                 A value of zero means to lock all bytes from the specified
     *                 starting position to the end of the file, regardless of whether
     *                 the file is subsequently extended or truncated
     * @param shared   {@code true} to request a shared lock,
     *                 {@code false} to request an exclusive lock
     * @return A lock object representing the newly-acquired lock,
     * or {@code null} if the lock could not be acquired
     * because another program holds an overlapping lock
     * @throws IllegalArgumentException     If the preconditions on the parameters do not hold
     * @throws ClosedChannelException       If this channel is closed
     * @throws OverlappingFileLockException If a lock that overlaps the requested region is already held by
     *                                      this Java virtual machine, or if another thread is already
     *                                      blocked in this method and is attempting to lock an overlapping
     *                                      region of the same file
     * @throws NonReadableChannelException  If {@code shared} is {@code true} but this channel was not
     *                                      opened for reading
     * @throws NonWritableChannelException  If {@code shared} is {@code false} but this channel was not
     *                                      opened for writing
     * @throws IOException                  If some other I/O error occurs
     * @see #lock()
     * @see #lock(long, long, boolean)
     * @see #tryLock()
     */
    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        ensureOpen();
        return this.channel.tryLock(position, size, shared);
    }

    private final Object closeLock = new Object();

    /**
     * Closes this channel.
     *
     * <p> This method is invoked by the {@link #close close} method in order
     * to perform the actual work of closing the channel.  This method is only
     * invoked if the channel has not yet been closed, and it is never invoked
     * more than once.
     *
     * <p> An implementation of this method must arrange for any other thread
     * that is blocked in an I/O operation upon this channel to return
     * immediately, either by throwing an exception or by returning normally.
     * </p>
     *
     * @throws IOException If an I/O error occurs while closing the channel
     */
    @Override
    protected void implCloseChannel() throws IOException {
        if (closed) {
            return;
        }
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }

        if (parent != null) {
            //
            // Close the fd via the parent stream's close method. The parent
            // will reinvoke our close method, which is defined in the
            // superclass AbstractInterruptibleChannel, but the isOpen logic in
            // that method will prevent this method from being reinvoked.
            //
            parent.close();
        } else { // parent == null hence closer != null
            //
            // Perform the cleaning action so it is not redone when
            // this channel becomes phantom reachable.
            //
            try {
                closer.clean();
            } catch (UncheckedIOException uioe) {
                throw uioe.getCause();
            }
        }

        this.raFile.close();
        this.channel.close();
    }

    private static class Closer implements Runnable {
        private final FileDescriptor fd;
        private final Closeable[] closeables;

        Closer(FileDescriptor fd, Closeable... closeables) {
            this.fd = fd;
            this.closeables = closeables;
        }

        public void run() {
            try {
                fdAccess.close(fd);
                for (Closeable c : closeables) {
                    c.close();
                }
            } catch (IOException ioe) {
                // Rethrow as unchecked so the exception can be propagated as needed
                throw new UncheckedIOException("close", ioe);
            }
        }
    }

    /**
     * This is a wrapper class of {@link RandomAccessFile} to give a
     * {@link AccessFileChannel parent class} to it.
     *
     * <br><br>
     *
     * All methods in {@code RandomAccessFile} can be used with this method,
     * however, {@link #getChannel()} and {@link #close()} have different functionalities
     * from what is defined by {@code RandomAccessFile}.
     *
     * @see RandomAccessFile
     */
    public static class RAFile implements DataOutput, DataInput, Closeable {
        private final RandomAccessFile randomAccessFile;
        private final AccessFileChannel parent;
        public RAFile(RandomAccessFile randomAccessFile, AccessFileChannel parent) {
            this.randomAccessFile = randomAccessFile;
            this.parent = parent;
        }

        /**
         * @see RandomAccessFile#getFD() Matched in RandomAccessFile.
         */
        public final FileDescriptor getFD() throws IOException {
            return randomAccessFile.getFD();
        }

        /**
         * Unlike {@link RandomAccessFile#getChannel()},
         * this one will return the {@link AccessFileChannel parent} of this
         * {@code RandomAccessFile} wrapper.
         *
         * @return a {@code AccessFileChannel} that is the parent of
         * this {@code RandomAccessFile} wrapper.
         */
        public final FileChannel getChannel() {
            return parent;
        }

        /**
         * @see RandomAccessFile#read() Matched in RandomAccessFile.
         */
        public int read() throws IOException {
            return randomAccessFile.read();
        }

        /**
         * @see RandomAccessFile#read(byte[], int, int) Matched in RandomAccessFile.
         */
        public int read(byte[] b, int off, int len) throws IOException {
            return randomAccessFile.read(b, off, len);
        }

        /**
         * @see RandomAccessFile#read(byte[]) Matched in RandomAccessFile.
         */
        public int read(byte[] b) throws IOException {
            return randomAccessFile.read(b);
        }

        /**
         * @see RandomAccessFile#readFully(byte[]) Matched in RandomAccessFile.
         */
        @Override
        public void readFully(byte[] b) throws IOException {
            randomAccessFile.readFully(b);
        }

        /**
         * @see RandomAccessFile#readFully(byte[], int, int) Matched in RandomAccessFile.
         */
        @Override
        public void readFully(byte[] b, int off, int len) throws IOException {
            randomAccessFile.readFully(b, off, len);
        }

        /**
         * @see RandomAccessFile#skipBytes(int) Matched in RandomAccessFile.
         */
        @Override
        public int skipBytes(int n) throws IOException {
            return randomAccessFile.skipBytes(n);
        }

        /**
         * @see RandomAccessFile#readBoolean() Matched in RandomAccessFile.
         */
        @Override
        public boolean readBoolean() throws IOException {
            return randomAccessFile.readBoolean();
        }

        /**
         * @see RandomAccessFile#readByte() Matched in RandomAccessFile.
         */
        @Override
        public byte readByte() throws IOException {
            return randomAccessFile.readByte();
        }

        /**
         * @see RandomAccessFile#readUnsignedByte() Matched in RandomAccessFile.
         */
        @Override
        public int readUnsignedByte() throws IOException {
            return randomAccessFile.readUnsignedByte();
        }

        /**
         * @see RandomAccessFile#readShort() Matched in RandomAccessFile.
         */
        @Override
        public short readShort() throws IOException {
            return randomAccessFile.readShort();
        }

        /**
         * @see RandomAccessFile#readUnsignedShort() Matched in RandomAccessFile.
         */
        @Override
        public int readUnsignedShort() throws IOException {
            return randomAccessFile.readUnsignedShort();
        }

        /**
         * @see RandomAccessFile#readChar() Matched in RandomAccessFile.
         */
        @Override
        public char readChar() throws IOException {
            return randomAccessFile.readChar();
        }

        /**
         * @see RandomAccessFile#readInt() Matched in RandomAccessFile.
         */
        @Override
        public int readInt() throws IOException {
            return randomAccessFile.readInt();
        }

        /**
         * @see RandomAccessFile#readLong() Matched in RandomAccessFile.
         */
        @Override
        public long readLong() throws IOException {
            return randomAccessFile.readLong();
        }

        /**
         * @see RandomAccessFile#readFloat() Matched in RandomAccessFile.
         */
        @Override
        public float readFloat() throws IOException {
            return randomAccessFile.readFloat();
        }

        /**
         * @see RandomAccessFile#readDouble() Matched in RandomAccessFile.
         */
        @Override
        public double readDouble() throws IOException {
            return randomAccessFile.readDouble();
        }

        /**
         * @see RandomAccessFile#readLine() Matched in RandomAccessFile.
         */
        @Override
        public String readLine() throws IOException {
            return randomAccessFile.readLine();
        }

        /**
         * @see RandomAccessFile#readUTF() Matched in RandomAccessFile.
         */
        @Override
        public String readUTF() throws IOException {
            return randomAccessFile.readUTF();
        }

        /**
         * @see RandomAccessFile#write(int) Matched in RandomAccessFile.
         */
        @Override
        public void write(int b) throws IOException {
            randomAccessFile.write(b);
        }

        /**
         * @see RandomAccessFile#write(byte[]) Matched in RandomAccessFile.
         */
        @Override
        public void write(byte[] b) throws IOException {
            randomAccessFile.write(b);
        }

        /**
         * @see RandomAccessFile#write(byte[], int, int) Matched in RandomAccessFile.
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            randomAccessFile.write(b, off, len);
        }

        /**
         * @see RandomAccessFile#writeBoolean(boolean) Matched in RandomAccessFile.
         */
        @Override
        public void writeBoolean(boolean v) throws IOException {
            randomAccessFile.writeBoolean(v);
        }

        /**
         * @see RandomAccessFile#writeByte(int) Matched in RandomAccessFile.
         */
        @Override
        public void writeByte(int v) throws IOException {
            randomAccessFile.writeByte(v);
        }

        /**
         * @see RandomAccessFile#writeShort(int) Matched in RandomAccessFile.
         */
        @Override
        public void writeShort(int v) throws IOException {
            randomAccessFile.writeShort(v);
        }

        /**
         * @see RandomAccessFile#writeChar(int) Matched in RandomAccessFile.
         */
        @Override
        public void writeChar(int v) throws IOException {
            randomAccessFile.writeChar(v);
        }

        /**
         * @see RandomAccessFile#writeInt(int) Matched in RandomAccessFile.
         */
        @Override
        public void writeInt(int v) throws IOException {
            randomAccessFile.writeInt(v);
        }

        /**
         * @see RandomAccessFile#writeLong(long) Matched in RandomAccessFile.
         */
        @Override
        public void writeLong(long v) throws IOException {
            randomAccessFile.writeLong(v);
        }

        /**
         * @see RandomAccessFile#writeFloat(float) Matched in RandomAccessFile.
         */
        @Override
        public void writeFloat(float v) throws IOException {
            randomAccessFile.writeFloat(v);
        }

        /**
         * @see RandomAccessFile#writeDouble(double) Matched in RandomAccessFile.
         */
        @Override
        public void writeDouble(double v) throws IOException {
            randomAccessFile.writeDouble(v);
        }

        /**
         * @see RandomAccessFile#writeBytes(String) Matched in RandomAccessFile.
         */
        @Override
        public void writeBytes(String s) throws IOException {
            randomAccessFile.writeBytes(s);
        }

        /**
         * @see RandomAccessFile#writeChars(String) Matched in RandomAccessFile.
         */
        @Override
        public void writeChars(String s) throws IOException {
            randomAccessFile.writeChars(s);
        }

        /**
         * @see RandomAccessFile#writeUTF(String) Matched in RandomAccessFile.
         */
        @Override
        public void writeUTF(String s) throws IOException {
            randomAccessFile.writeUTF(s);
        }

        /**
         * @see RandomAccessFile#getFilePointer() Matched in RandomAccessFile.
         */
        public long getFilePointer() throws IOException {
            return randomAccessFile.getFilePointer();
        }

        /**
         * @see RandomAccessFile#seek(long) Matched in RandomAccessFile.
         */
        public void seek(long pos) throws IOException {
            randomAccessFile.seek(pos);
        }

        /**
         * @see RandomAccessFile#length() Matched in RandomAccessFile.
         */
        public long length() throws IOException {
            return randomAccessFile.length();
        }

        /**
         * @see RandomAccessFile#setLength(long) Matched in RandomAccessFile.
         */
        public void setLength(long newLength) throws IOException {
            randomAccessFile.setLength(newLength);
        }

        /**
         * Doesn't call the {@link RandomAccessFile#close()}, but will
         * call the {@code AccessFileChannel} that is the parent of this
         * {@code RandomAccessFile} wrapper.
         *
         * @throws IOException if {@link AccessFileChannel#close()} throws one.
         */
        @Override
        public void close() throws IOException {
            parent.close();
        }
    }
}
