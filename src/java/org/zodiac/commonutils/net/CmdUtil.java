package org.zodiac.commonutils.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CmdUtil {

	public static void execute(File directory, ArrayList<String> cmds) {
		execute(directory, cmds, inputStream -> {
			BufferedReader bs = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			try {
				while ((line = bs.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static void execute(File directory, ArrayList<String> cmds, InputStreamHandler handler) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(directory).command(cmds).redirectErrorStream(true);
		try {
			Process p = processBuilder.start();
			InputStream stream = p.getInputStream();
			handler.handle(stream);
			int exitCode = p.waitFor();
			if (exitCode != 0) {
				return;
			}
			if (p.isAlive()) {
				p.destroyForcibly();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
