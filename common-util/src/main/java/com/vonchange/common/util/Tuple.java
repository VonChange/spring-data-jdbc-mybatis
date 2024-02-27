package com.vonchange.common.util;

public class Tuple<E> {
    private final E[] elements;
 
    public Tuple(E... elements) {
        this.elements = elements;
    }
 
    public E get(int index) {
        return elements[index];
    }
 
    public int size() {
        return elements.length;
    }
}