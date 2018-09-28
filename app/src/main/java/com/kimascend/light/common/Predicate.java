package com.kimascend.light.common;

@FunctionalInterface
public interface Predicate<T> {

    boolean test(T t);

}
