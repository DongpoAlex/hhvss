package com.efutre.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.annotation.processing.FilerException;

public class Zip {
	public static void main(String args[]) {
		try {
			//zip("X:\\aaa", "X:\\abc.zip");
			unzip("X:\\SEE20111125000010.zip","X:\\");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void zipAFile(File sourceFile,File zipFile){
		
	}

	public static void zip(String fileDir, String zipPatch) throws IOException {
		File file = new File(fileDir);
		if(!file.exists()) throw new FilerException(fileDir+"不存在");
		ArrayList<File> fileList = getFiles(file);
		//如果是空目录直接跳出
		if(fileList.size()==0){
			MyLog.info("备份目录"+fileDir+"为空，直接删除!");
			return;
		}
		zip(fileList, zipPatch);
	}

	/**
	 * 获取目录下的所有文件
	 * @param fileDir 
	 * @return
	 */
	public static ArrayList<File> getFiles(File fileDir) {
		ArrayList list = new ArrayList<File>();
		File[] files = fileDir.listFiles();
		for (File tmpFile : files) {
			if (tmpFile==null) continue;
			if(tmpFile.isDirectory()){
				list.addAll(getFiles(tmpFile));
			}else{
				list.add(tmpFile);
			}
		}
		return list;
	}

	/**
	 * 将指定的文件集合压缩到参数2指定的文件
	 * @param fileList 文件List集合
	 * @param zipPatch 压缩文件路径及文件名
	 * @throws IOException 
	 */
	public static void zip(ArrayList<File> fileList, String zipPatch) throws IOException {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPatch));

			for (File file : fileList) {
				out.putNextEntry(new ZipEntry(file.getName()));
				FileInputStream in = new FileInputStream(file);
//				int b;
//				while ((b = in.read()) != -1) {
//					out.write(b);
//				}
				byte[] b = new byte[1024];
				int len = in.read(b);
				while (len > 0) {
					out.write(b, 0, len);
					len = in.read(b);
				}
				
				in.close();
			}

			out.close();
	}
	
	/**
	 * 解压文件
	 * @param zipFile zip文件绝对路径
	 * @param filePath 解压到绝对路径文件夹
	 * @throws IOException
	 */
	public static void unzip(String zipFile,String filePath) throws IOException{
		ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry z = null;
		while ((z = in.getNextEntry()) != null) {
			if (z.isDirectory()) {
				File f = new File(filePath + z.getName() + "\\");
				f.mkdirs();
			} else {
				File f = new File(filePath + z.getName());
				OutputStream out = new FileOutputStream(f);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				out.close();
			}
		}
		// 记得关闭
		in.close();
	}
	

	// 文件转zip文件，这里模拟一个文件夹和文件压缩
	private static void file2zip(String filePath) {
		try {
			File zipFile = new File(filePath);
			zipFile.getParentFile().mkdirs();
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(filePath));

			// 写一个文件
			File f = new File("C:\\1.txt");
			// 添加一个ZipEntry对象
			out.putNextEntry(new ZipEntry("copy1.txt"));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
			// 在压缩文件中创建一个文件夹
			out.putNextEntry(new ZipEntry("abc/"));
			// 向这个压缩中的文件夹写入一个文件
			File f1 = new File("C:\\1.txt");
			// 注意这里是文件在压缩文件中的位置，在解压的时候它的那么也是这个
			out.putNextEntry(new ZipEntry("abc/copy2.txt"));
			FileInputStream in1 = new FileInputStream(f1);
			int b1;
			while ((b1 = in1.read()) != -1) {
				out.write(b1);
			}
			in1.close();
			System.out.println("压缩完成");
			// 没有关闭的话文件会存在异常
			out.close();
		}
		catch (Exception e) {}
	}

	// 压缩文件转回文件形式
	private static void zip2file(String zipPatch) {
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zipPatch));
			ZipEntry z = null;
			while ((z = in.getNextEntry()) != null) {
				if (z.isDirectory()) {
					File f = new File("C:\\123\\" + z.getName() + "\\");
					// 这里将文件夹创建，压缩文件夹中的文件也会当做一个个ZipEntry类，文件夹也是一个ZipEntry类。
					f.mkdirs();
				} else {
					File f = new File("C:\\123\\" + z.getName());
					OutputStream out = new FileOutputStream(f);
					int b;
					while ((b = in.read()) != -1) {
						out.write(b);
					}
					out.close();
				}
			}
			// 记得关闭
			in.close();

		}
		catch (Exception e) {
			// 这里最好写点东西，要不出错也不知道
			e.printStackTrace();
		}
	}
}
