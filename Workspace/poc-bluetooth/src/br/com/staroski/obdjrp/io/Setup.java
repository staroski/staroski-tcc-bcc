package br.com.staroski.obdjrp.io;

import java.util.Arrays;
import java.util.Iterator;

final class Setup implements Settings {

	private final Iterator<?> iterator;

	@SafeVarargs
	<T> Setup(T... params) {
		iterator = Arrays.asList(params).iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T next() {
		return (T) iterator.next();
	}
}
