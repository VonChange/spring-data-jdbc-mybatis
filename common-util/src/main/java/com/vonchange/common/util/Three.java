package com.vonchange.common.util;

public class Three<A,B,C>{
    private final  A first;
    private final  B second;

    private final  C third;

    private Three(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third=third;
    }

    public static <A, B,C> Three<A, B,C> of(A first, B second, C third) {
        return new Three<>(first, second,third);
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
