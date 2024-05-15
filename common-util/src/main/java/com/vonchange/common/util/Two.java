package com.vonchange.common.util;

public final class Two<A, B> {

	private   A first;
	private   B second;

	public Two(){

	}
	private Two(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Creates a new {@link Two} for the given elements.
	 *
	 * @param first must not be {@literal null}.
	 * @param second must not be {@literal null}.
	 * @return
	 */
	public static <A, B> Two<A, B> of(A first, B second) {
		return new Two<>(first, second);
	}

	/**
	 * Returns the first element of the {@link Two}.
	 *
	 * @return
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * Returns the second element of the {@link Two}.
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