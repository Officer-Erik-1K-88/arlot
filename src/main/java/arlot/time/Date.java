package arlot.time;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.TimeZone;

public class Date extends DateFormat implements Cloneable {
    private java.util.Date heldDate;

    private String formatCalendar = "MM-dd-yyyy";
    private String formatClock = "hh:mm:ss";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatCalendar
            +" "+
            formatClock);

    public Date() {
        this.heldDate = new java.util.Date();
        this.numberFormat = simpleDateFormat.getNumberFormat();
    }

    public Date(java.util.Date heldDate) {
        this.heldDate = heldDate;
        this.numberFormat = simpleDateFormat.getNumberFormat();
        this.calendar = simpleDateFormat.getCalendar();
    }

    public Date(long date) {
        this.heldDate = new java.util.Date(date);
        this.numberFormat = simpleDateFormat.getNumberFormat();
        this.calendar = simpleDateFormat.getCalendar();
    }

    public Date(String date) throws ParseException {
        this.heldDate = simpleDateFormat.parse(date);
        this.numberFormat = simpleDateFormat.getNumberFormat();
        this.calendar = simpleDateFormat.getCalendar();
    }

    public Date(String formatCalendar, String formatClock) {
        setFormat(formatCalendar, formatClock);
        this.heldDate = new java.util.Date();
    }

    public Date(String formatCalendar, String formatClock, String date) throws ParseException {
        setFormat(formatCalendar, formatClock);
        this.heldDate = simpleDateFormat.parse(date);
    }

    public Date(Date date) {
        this.simpleDateFormat = (SimpleDateFormat) date.simpleDateFormat.clone();
        this.heldDate = (java.util.Date) date.heldDate.clone();
        this.formatCalendar = date.formatCalendar;
        this.formatClock = date.formatClock;
        this.numberFormat = simpleDateFormat.getNumberFormat();
        this.calendar = simpleDateFormat.getCalendar();
    }

    /**
     * Updates this {@code Date} with a new {@link java.util.Date}.
     * @param newDate The date to replace the {@link #heldDate}.
     * @return The old date held.
     */
    public java.util.Date update(java.util.Date newDate) {
        java.util.Date old = this.heldDate;
        this.heldDate = newDate;
        return old;
    }

    public void setFormat(String formatCalendar, String formatClock) {
        if (formatCalendar != null && formatClock != null) {
            simpleDateFormat = new SimpleDateFormat(formatCalendar
                +" "+
                formatClock);
        } else if (formatCalendar != null) {
            simpleDateFormat = new SimpleDateFormat(formatCalendar);
        } else if (formatClock != null) {
            simpleDateFormat = new SimpleDateFormat(formatClock);
        }
        if (formatCalendar != null || formatClock != null) {
            this.formatCalendar = formatCalendar;
            this.formatClock = formatClock;
        }
        this.numberFormat = simpleDateFormat.getNumberFormat();
        this.calendar = simpleDateFormat.getCalendar();
    }

    public String getFormatCalendar() {
        return formatCalendar;
    }

    public String getFormatClock() {
        return formatClock;
    }

    /**
     * Gets the full pattern used for formatting.
     * @return a pattern string describing this {@code Date}'s date format.
     */
    public String getFormat() {
        return simpleDateFormat.toPattern();
    }

    /**
     * Gets a {@link SimpleDateFormat} that is used for formatting this {@code Date}.
     * @return A copy of this {@code Date}'s {@link SimpleDateFormat}.
     */
    public SimpleDateFormat getFormatter() {
        return (SimpleDateFormat) simpleDateFormat.clone();
    }

    /**
     * Gets a {@link java.util.Date} that is used for holding this {@code Date}.
     * @return A copy of this {@code Date}'s {@link java.util.Date}.
     */
    public java.util.Date getDate() {
        return (java.util.Date) this.heldDate.clone();
    }

    /**
     * Formats a {@link java.util.Date} into a date-time string. The formatted
     * string is appended to the given {@code StringBuffer}.
     *
     * @param date          a Date to be formatted into a date-time string.
     * @param toAppendTo    the string buffer for the returning date-time string.
     * @param fieldPosition keeps track on the position of the field within
     *                      the returned string. For example, given a date-time text
     *                      {@code "1996.07.10 AD at 15:08:56 PDT"}, if the given {@code fieldPosition}
     *                      is {@link DateFormat#YEAR_FIELD}, the begin index and end index of
     *                      {@code fieldPosition} will be set to 0 and 4, respectively.
     *                      Notice that if the same date-time field appears more than once in a
     *                      pattern, the {@code fieldPosition} will be set for the first occurrence
     *                      of that date-time field. For instance, formatting a {@code Date} to the
     *                      date-time string {@code "1 PM PDT (Pacific Daylight Time)"} using the
     *                      pattern {@code "h a z (zzzz)"} and the alignment field
     *                      {@link DateFormat#TIMEZONE_FIELD}, the begin index and end index of
     *                      {@code fieldPosition} will be set to 5 and 8, respectively, for the
     *                      first occurrence of the timezone pattern character {@code 'z'}.
     * @return the string buffer passed in as {@code toAppendTo}, with formatted
     * text appended.
     */
    @Override
    public StringBuffer format(java.util.Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return simpleDateFormat.format(date, toAppendTo, fieldPosition);
    }

    /**
     * Parse a date/time string according to the given parse position.  For
     * example, a time text {@code "07/10/96 4:5 PM, PDT"} will be parsed into a {@code Date}
     * that is equivalent to {@code Date(837039900000L)}.
     *
     * <p> By default, parsing is lenient: If the input is not in the form used
     * by this object's format method but can still be parsed as a date, then
     * the parse succeeds.  Clients may insist on strict adherence to the
     * format by calling {@link #setLenient(boolean) setLenient(false)}.
     *
     * <p>This parsing operation uses the {@link #calendar} to produce
     * a {@code Date}. As a result, the {@code calendar}'s date-time
     * fields and the {@code TimeZone} value may have been
     * overwritten, depending on subclass implementations. Any {@code
     * TimeZone} value that has previously been set by a call to
     * {@link #setTimeZone(TimeZone) setTimeZone} may need
     * to be restored for further operations.
     *
     * @param source The date/time string to be parsed
     * @param pos    On input, the position at which to start parsing; on
     *               output, the position at which parsing terminated, or the
     *               start position if the parse failed.
     * @return A {@code Date}, or {@code null} if the input could not be parsed
     */
    @Override
    public java.util.Date parse(String source, ParsePosition pos) {
        return simpleDateFormat.parse(source, pos);
    }

    /**
     * Gets the string value of this {@code Date} using its formatter.
     * @return a string representation of this {@code Date}.
     */
    @Override
    public String toString() {
        return simpleDateFormat.format(heldDate);
    }

    /**
     * Converts this {@code Date} object to an Instant.
     * The conversion creates an {@link Instant} that represents the same point on the
     * time-line as this {@code Date}.
     * @return an instant representing the same point on the
     * time-line as this {@code Date} object.
     */
    public Instant toInstant() {
        return this.heldDate.toInstant();
    }

    /**
     * Overrides Cloneable
     */
    @Override
    public Object clone() {
        Date other = (Date) super.clone();
        other.simpleDateFormat = (SimpleDateFormat) simpleDateFormat.clone();
        other.heldDate = (java.util.Date) heldDate.clone();
        other.formatCalendar = formatCalendar;
        other.formatClock = formatClock;
        return other;
    }
}
