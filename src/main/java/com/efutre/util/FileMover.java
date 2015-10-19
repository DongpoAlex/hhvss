package com.efutre.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class FileMover {
	static Logger log = Logger.getLogger("FileMover");
	public static boolean move(String source, String dest) {
		return move(new File(source), new File(dest));
	}

	public static boolean move(File source, File dest) {
		if ((dest.exists()) && (dest.isDirectory())) {
			try {
				dest = new File(dest.getCanonicalPath(), source.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return internalMove(source, dest);
	}

	private static boolean internalMove(File source, File dest) {
		
		if (!source.exists()) {
		}

		if (source.length() == 0L) {
		}

		if (source.exists()) {
			if ((dest.isFile()) && (dest.exists())) {
				dest.delete();
			}

			return source.renameTo(dest);
		}
		return false;
	}
}

/*
 * Location: X:\efutDXSTrans.jar
 * Qualified Name: com.efuture.dxs.util.FileMover
 * JD-Core Version: 0.6.0
 */