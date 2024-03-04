package com.vonchange.common.util;

public final class Pair<A, B> {

	private   A first;
	private   B second;

	public Pair(){

	}
	private Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Creates a new {@link Pair} for the given elements.
	 *
	 * @param first must not be {@literal null}.
	 * @param second must not be {@literal null}.
	 * @return
	 */
	public static <A, B> Pair<A, B> of(A first, B second) {
		return new Pair<>(first, second);
	}

	/**
	 * Returns the first element of the {@link Pair}.
	 *
	 * @return
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * Returns the second element of the {@link Pair}.
	 *
	 * @return
	 */
	public B getSecond() {
		return second;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}
}