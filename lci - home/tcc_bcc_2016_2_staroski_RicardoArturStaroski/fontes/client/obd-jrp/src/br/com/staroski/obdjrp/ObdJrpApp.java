package br.com.staroski.obdjrp;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import br.com.staroski.obdjrp.core.Config;
import br.com.staroski.obdjrp.utils.LinkedPrintStream;

abstract class ObdJrpApp {

	ObdJrpApp(String name) throws IOException {
		File dir = Config.get().dataDir();
		String instant = Config.get().formatted(new Date());
		String prefix = name + "_" + instant;
		PrintStream out = new PrintStream(new File(dir, prefix + ".out"));
		PrintStream err = new PrintStream(new File(dir, prefix + ".err"));
		System.setOut(new LinkedPrintStream(System.out, out));
		System.setErr(new LinkedPrintStream(System.err, err));
	}
}
