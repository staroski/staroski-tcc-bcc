package br.com.staroski.obdjrp.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Command {

	public static final Command NULL = new Command() {

		@Override
		public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			return null;
		}
	};

	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
