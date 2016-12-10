package br.com.staroski.obdjrp.utils;

public final class Print {

	public static String message(Throwable error) {
		if (error == null) {
			System.out.println("null");
			return "null";
		}
		String raised = error.getClass().getSimpleName();
		String message = error.getMessage();
		String text = String.format("%s: %s", raised, message);
		System.out.println(text);
		return text;
	}

	private Print() {}
}
