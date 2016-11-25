package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ListVehicles implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> vehicles = new ArrayList<>();
		File dir = ObdJrpWeb.getDataDir();
		for (File f : dir.listFiles()) {
			vehicles.add(f.getName());
		}
		request.setAttribute("vehicles", vehicles);
		return "vehicle-list.jsp";
	}
}
