package br.com.staroski.obdjrp.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public final class LinkedPrintStream extends PrintStream {

	private final PrintStream chain;

	public LinkedPrintStream(PrintStream a, PrintStream b) throws FileNotFoundException {
		super(a);
		chain = b;
	}

	@Override
	public PrintStream append(char c) {
		super.append(c);
		chain.append(c);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		super.append(csq);
		chain.append(csq);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		super.append(csq, start, end);
		chain.append(csq, start, end);
		return this;
	}

	@Override
	public boolean checkError() {
		return super.checkError() || chain.checkError();
	}

	@Override
	public void close() {
		super.close();
		chain.close();
	}

	@Override
	public void flush() {
		super.flush();
		chain.flush();
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		super.format(l, format, args);
		chain.format(l, format, args);
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		super.format(format, args);
		chain.format(format, args);
		return this;
	}

	@Override
	public void print(boolean b) {
		super.print(b);
		chain.print(b);
	}

	@Override
	public void print(char c) {
		super.print(c);
		chain.print(c);
	}

	@Override
	public void print(char[] s) {
		super.print(s);
		chain.print(s);
	}

	@Override
	public void print(double d) {
		super.print(d);
		chain.print(d);
	}

	@Override
	public void print(float f) {
		super.print(f);
		chain.print(f);
	}

	@Override
	public void print(int i) {
		super.print(i);
		chain.print(i);
	}

	@Override
	public void print(long l) {
		super.print(l);
		chain.print(l);
	}

	@Override
	public void print(Object obj) {
		super.print(obj);
		chain.print(obj);
	}

	@Override
	public void print(String s) {
		super.print(s);
		chain.print(s);
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		super.printf(l, format, args);
		chain.printf(l, format, args);
		return this;
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		super.printf(format, args);
		chain.printf(format, args);
		return this;
	}

	@Override
	public void println() {
		super.println();
		chain.println();
	}

	@Override
	public void println(boolean b) {
		super.println(b);
		chain.println(b);
	}

	@Override
	public void println(char c) {
		super.println(c);
		chain.println(c);
	}

	@Override
	public void println(char[] c) {
		super.println(c);
		chain.println(c);
	}

	@Override
	public void println(double d) {
		super.println(d);
		chain.println(d);
	}

	@Override
	public void println(float f) {
		super.println(f);
		chain.println(f);
	}

	@Override
	public void println(int i) {
		super.println(i);
		chain.println(i);
	}

	@Override
	public void println(long l) {
		super.println(l);
		chain.println(l);
	}

	@Override
	public void println(Object o) {
		super.println(o);
		chain.println(o);
	}

	@Override
	public void println(String s) {
		super.println(s);
		chain.println(s);
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
		chain.write(b);
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
		chain.write(buf, off, len);
	}

	@Override
	public void write(int b) {
		super.write(b);
		chain.write(b);
	}
}
