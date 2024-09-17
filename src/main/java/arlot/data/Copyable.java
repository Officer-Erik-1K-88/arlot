package arlot.data;

import arlot.error.NotCloneableException;
import arlot.error.NotDuplicatableException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 *
 * Classes that extend a class that implements the {@code Copyable} interface
 * must implement the {@link Cloneable} interface.
 *
 * @param <V> The type of data that can be copied.
 */
public interface Copyable<V> extends Cloneable {
    /**
     * Gets the actual value that is copyable.
     *
     * <br>
     *
     * In most cases, this should be equal to the Object that implements this
     * interface.
     *
     * @return The object to be copied.
     */
    V that();

    /**
     * Uses {@link Copier} to make a copy of this {@code Copyable} Object.
     *
     * @return A clone of a {@code Copyable} Object.
     * @throws NotCloneableException If {@link Copier#clone()} cannot duplicate
     * this {@code Copyable} Object.
     */
    default Copyable<V> selfCopy() throws NotCloneableException {
        Copier<Copyable<V>> copier = new Copier<>(this);
        Copyable<V> cloned = copier.clone();
        if (!copier.isDuplicatable()) {
            throw new NotCloneableException("Cloning of "+selfDef()+" failed.");
        }
        return cloned;
    }

    /**
     * Creates and returns a copy of an object. The precise meaning
     * of "copy" may depend on the class of the object.
     *
     * @implSpec
     * The method {@code clone} for interface {@code Copyable} performs a
     * specific cloning operation on {@link #that()}.
     * This method also should not incorporate either
     * {@link #copy()} or {@link #duplicate()}, as doing so may have unintended
     * effects.
     *
     *
     * @return The cloned Object.
     * @see Object#clone()
     */
    V clone();

    /**
     * Will attempt to {@link #duplicate() duplicate} the Object,
     * if not possible, then will create a {@link #clone() clone} of the Object.
     *
     * @return A copy of an Object.
     */
    default V copy() {
        try {
            return duplicate();
        } catch (NotDuplicatableException e) {
            return clone();
        }
    }

    /**
     * Makes a duplicate of an Object that is separate from the derived Object.
     * If the Object cannot be duplicated then {@code NotDuplicatableException} is thrown.
     * Otherwise, it is known as duplicatable if the Object can be duplicated.
     *
     * <br><br>
     *
     * This method cannot make a copy of all value types. However, the types that
     * this method can copy are as follows:
     * <ul>
     *     <li>
     *         <b>Null</b> -
     *         <span>If {@link #that()} is null, then null is returned.</span>
     *     </li>
     *     <li>
     *         <b>Primitives</b> -
     *         <span>All primitive data types can be duplicated easily.</span>
     *     </li>
     *     <li>
     *         <b>Char Sequences</b> -
     *         <span>Any class that implements {@code CharSequence},
     *         including {@code String}s, can also be duplicated.</span>
     *     </li>
     *     <li>
     *         <b>Arrays</b> -
     *         <span>As long as the data type is duplicatable via {@link Copier}, then
     *         the array can be duplicated.</span>
     *     </li>
     *     <li>
     *         <b>Collections</b> -
     *         <span>Same as arrays, as long as the data type is duplicatable
     *         via {@link Copier}, then the collection can be duplicated.</span>
     *     </li>
     *     <li>
     *         <b>Maps</b> -
     *         <span>Maps can be duplicated as long as the data type of both
     *         the keys and values are duplicatable via {@link Copier}.</span>
     *     </li>
     *     <li>
     *         <b>Cloneable</b> -
     *         <span>If <code>{@link #that()} != this</code> is {@code true} and
     *         if the Object implements {@code Cloneable},
     *         then the Object is also duplicatable.</span>
     *     </li>
     * </ul>
     *
     * @apiNote It is advisable to not override this method.
     *
     * @return A clone of the Object.
     * @throws NotDuplicatableException If the Object cannot be duplicated.
     */
    @SuppressWarnings("unchecked")
    default V duplicate() throws NotDuplicatableException {
        if (that() == null) {
            return null;
        }
        if (that().getClass().isPrimitive()) {
            return that();
        }
        if (that() instanceof CharSequence) {
            if (that() instanceof String) return (V) new String((String) that());
            try {
                return (V) that().getClass()
                        .getConstructor(CharSequence.class)
                        .newInstance((CharSequence) that());
            } catch (InstantiationException |
                     IllegalAccessException |
                     InvocationTargetException |
                     NoSuchMethodException e) {
                throw noDup(e.getMessage());
            }
        }
        if (that().getClass().isArray()) {
            Object[] objs = (Object[]) that();
            Object[] objects = (Object[]) Array.newInstance(objs.getClass(), objs.length);
            int io = 0;
            for (Object obj : objs) {
                Copier<?> copier = new Copier<>(obj);

                objects[io] = copier.clone();

                if (!copier.isDuplicatable()) {
                    throw noDup();
                }
                io++;
            }
            return (V) objects;
        }
        if (that() instanceof Collection) {
            Constructor<?> constructor = getDefaultConstructor();
            Collection<Object> collection;
            try {
                if (constructor != null) {
                    collection = (Collection<Object>) constructor.newInstance();
                    for (Object o : ((Collection<?>) that())) {
                        Copier<?> copier = new Copier<>(o);

                        collection.add(copier.clone());

                        if (!copier.isDuplicatable()) {
                            throw noDup();
                        }
                    }
                } else {
                    throw noDup();
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw noDup(e.getMessage());
            }
            return (V) collection;
        }
        if (that() instanceof Map) {
            Constructor<?> constructor = getDefaultConstructor();
            Map<Object, Object> map;
            try {
                if (constructor != null) {
                    map = (Map<Object, Object>) constructor.newInstance();
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) that()).entrySet()) {
                        Copier<?> copier1 = new Copier<>(entry.getKey());
                        Copier<?> copier2 = new Copier<>(entry.getValue());

                        map.put(copier1.clone(), copier2.clone());

                        if (!copier1.isDuplicatable() || !copier2.isDuplicatable()) {
                            throw noDup();
                        }
                    }
                } else {
                    throw noDup();
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw noDup(e.getMessage());
            }
            return (V) map;
        }
        if (that() != this) {
            if (that() instanceof Cloneable) {
                try {
                    Method cloneMethod = that().getClass().getMethod("clone");
                    return (V) cloneMethod.invoke(that());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw noDup(e.getMessage());
                }
            }
        }
        throw noDup();
    }

    private NotDuplicatableException noDup() {
        return noDup(null);
    }

    private NotDuplicatableException noDup(String invokedMessage) {
        return new NotDuplicatableException("The Object "+
                selfDef()+
                " cannot be duplicated."+
                (invokedMessage!=null?"\nInvoked by: "+invokedMessage:""));
    }

    /**
     * Used for a definitive definition of this {@code Copyable} Object's stored value,
     * this method must be unique from each instance of this {@code Copyable} Object,
     * the only time two or more instances of this {@code Copyable} Object to
     * have the same {@code selfDef} is given if the {@code Copyable} Objects'
     * stored values are equal.
     *
     * @implSpec
     * The {@code selfDef} method for class {@code Copyable} returns a string consisting of
     * the name of the class of which the {@link #that()} method returns,
     * the at-sign character '@',
     * and the unsigned hexadecimal representation of the hash code of
     * the {@link #that()} method returns.
     * In other words, this method returns a string equal to the value of:
     * <blockquote>
     * <pre>
     * that().getClass().getName() + '@' + Integer.toHexString( that().hashCode() )
     * </pre>
     * </blockquote>
     *
     *
     * @return A string representation of this {@code Copyable} Objects stored value.
     */
    default String selfDef() {
        return that().getClass().getName() + '@' +
                Integer.toHexString(that().hashCode());
    }

    /**
     * Gets and returns the default constructor (the constructor that has no parameters)
     * of the {@link #that() stored value}.
     *
     * @return The constructor of {@link #that()}.
     */
    private Constructor<?> getDefaultConstructor() {
        Constructor<?>[] constructors = that().getClass().getConstructors();
        Constructor<?> constructor = null;
        for (Constructor<?> construct : constructors) {
            if (construct.getParameterTypes().length == 0) {
                constructor = construct;
                break;
            }
        }
        return constructor;
    }

    /**
     * The base copy class that is used for {@link #duplicate() dulication}.
     *
     * @param <T> The value type that is being copied.
     */
    final class Copier<T> implements Copyable<T> {
        private final T value;
        private boolean duplicatable = true;

        /**
         * Creates a new Copier to clone the provided value.
         *
         * @param value The value to be stored for cloning.
         */
        public Copier(T value) {
            this.value = value;
        }

        /**
         * Makes a clone of the stored value of another copier.
         * The copied value can be accessed by using {@link #that()}.
         *
         * @param c The copier to make a clone of.
         */
        @SuppressWarnings("unchecked")
        public Copier(Copier<T> c) {
            T value1;
            try {
                value1 = c.duplicate();
            } catch (NotDuplicatableException e) {
                if (c.that() instanceof Cloneable && !(c.that() instanceof Copyable)) {
                    try {
                        Method cloneMethod = c.that().getClass().getMethod("clone");
                        value1 = (T) cloneMethod.invoke(c.that());
                    } catch (NoSuchMethodException |
                             InvocationTargetException |
                             IllegalAccessException e2) {
                        value1 = null;
                        duplicatable = false;
                    }
                } else {
                    value1 = null;
                    duplicatable = false;
                }
            }
            this.value = value1;
        }

        /**
         * Gets whether the cloning was successful.
         *
         * <br>
         *
         * A call to {@link #duplicate()} that throws an error will not
         * affect the returned boolean of this method.
         *
         * @return true if {@link #clone()} couldn't clone the object.
         */
        public boolean isDuplicatable() {
            return duplicatable;
        }

        @Override
        public T that() {
            return value;
        }

        /**
         * Creates a copy of the value that is stored in this copier.
         * <br>
         * This method uses {@link #duplicate()} to make a clone.
         * If the stored Object is not duplicatable, then {@link #isDuplicatable()}
         * will return {@code false}.
         *
         * @return A copy of the stored value.
         */
        @Override
        @SuppressWarnings("unchecked")
        public T clone() {
            Copier<T> c = this;
            try {
                c = (Copier<T>) super.clone();
            } catch (CloneNotSupportedException ignored) {}
            Copier<T> copier = new Copier<>(c);
            duplicatable = copier.isDuplicatable();
            return copier.that();
        }

        /**
         * The same as {@link #clone()}.
         *
         * @return A copy of the stored value.
         */
        @Override
        public T copy() {
            return clone();
        }
    }
}
