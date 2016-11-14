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

	public PrintStream append(char c) {
		super.append(c);
		chain.append(c);
		return this;
	}

	public PrintStream append(CharSequence csq) {
		super.append(csq);
		chain.append(csq);
		return this;
	}

	public PrintStream append(CharSequence csq, int start, int end) {
		super.append(csq, start, end);
		chain.append(csq, start, end);
		return this;
	}

	public boolean checkError() {
		return super.checkError() || chain.checkError();
	}

	public void close() {
		super.close();
		chain.close();
	}

	public void flush() {
		super.flush();
		chain.flush();
	}

	public PrintStream format(Locale l, String format, Object... args) {
		super.format(l, format, args);
		chain.format(l, format, args);
		return this;
	}

	public PrintStream format(String format, Object... args) {
		super.format(format, args);
		chain.format(format, args);
		return this;
	}

	public void print(boolean b) {
		super.print(b);
		chain.print(b);
	}

	public void print(char c) {
		super.print(c);
		chain.print(c);
	}

	public void print(char[] s) {
		super.print(s);
		chain.print(s);
	}

	public void print(double d) {
		super.print(d);
		chain.print(d);
	}

	public void print(float f) {
		super.print(f);
		chain.print(f);
	}

	public void print(int i) {
		super.print(i);
		chain.print(i);
	}

	public void print(long l) {
		super.print(l);
		chain.print(l);
	}

	public void print(Object obj) {
		super.print(obj);
		chain.print(obj);
	}

	public void print(String s) {
		super.print(s);
		chain.print(s);
	}

	public PrintStream printf(Locale l, String format, Object... args) {
		super.printf(l, format, args);
		chain.printf(l, format, args);
		return this;
	}

	public PrintStream printf(String format, Object... args) {
		super.printf(format, args);
		chain.printf(format, args);
		return this;
	}

	public void println() {
		super.println();
		chain.println();
	}

	public void println(boolean b) {
		super.println(b);
		chain.println(b);
	}

	public void println(char c) {
		super.println(c);
		chain.println(c);
	}

	public void println(char[] c) {
		super.println(c);
		chain.println(c);
	}

	public void println(double d) {
		super.println(d);
		chain.println(d);
	}

	public void println(float f) {
		super.println(f);
		chain.println(f);
	}

	public void println(int i) {
		super.println(i);
		chain.println(i);
	}

	public void println(long l) {
		super.println(l);
		chain.println(l);
	}

	public void println(Object o) {
		super.println(o);
		chain.println(o);
	}

	public void println(String s) {
		super.println(s);
		chain.println(s);
	}

	public void write(byte[] b) throws IOException {
		super.write(b);
		chain.write(b);
	}

	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
		chain.write(buf, off, len);
	}

	public void write(int b) {
		super.write(b);
		chain.write(b);
	}
}
