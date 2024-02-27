package com.vonchange.common.util;

public class Triplet <A,B,C>{
    private final  A first;
    private final  B second;

    private final  C third;

    private Triplet(A first, B second,C third) {
        this.first = first;
        this.second = second;
        this.third=third;
    }

    public static <A, B,C> Triplet<A, B,C> of(A first, B second,C third) {
        return new Triplet<>(first, second,third);
    }


    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
    public C getThird() {
        return third;
    }

}
