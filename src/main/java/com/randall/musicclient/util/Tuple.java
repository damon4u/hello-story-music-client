package com.randall.musicclient.util;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-20 14:32
 */
public class Tuple<T, U> {
    public final T first;
    public final U second;

    public Tuple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }

    public static <T, U> Tuple<T, U> newInstance(T first, U second) {
        return new Tuple(first, second);
    }

    public String toString() {
        return "Tuple [first=" + this.first + ", second=" + this.second + "]";
    }
}
