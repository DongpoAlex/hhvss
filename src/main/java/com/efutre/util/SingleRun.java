package com.efutre.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class SingleRun {
	public static void init() {
		try {
			File file = new File("./.lock");
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			FileChannel fc = raf.getChannel();
			FileLock lock = fc.tryLock(0, 1, false);
			if (lock == null) {
				for (int i = 30; i > 0; i--) {
					MyLog.err("已有一个实例在运行，" + i + " 秒后自动退出！");
					Thread.sleep(1000);
				}
				System.exit(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
