/**
 * 
 */
package com.royalstone.security;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author BaiJian
 */
public class Code {
	static private Color getRandColor( int min, int max ) { // 随机产生指定区域内的RGB颜色
		Random random1 = new Random();
		if (min >= 255)
			min = 255;
		if (max >= 255)
			max = 255;
		int r = min + random1.nextInt(max - min);
		int g = min + random1.nextInt(max - min);
		int b = min + random1.nextInt(max - min);
		return new Color(r, g, b);
	}

	static public void sendCode( HttpServletResponse response, HttpServletRequest request ) throws IOException {
		HttpSession session = request.getSession();

		// 禁止页面缓冲
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		// 在缓存中创建图形对象，然后输出
		int width = 80, height = 22; // 输出图片的大小
		BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 指定缓冲的图片和颜色结构
		Graphics g = buff.getGraphics();
		Random rand = new Random();
		// 背景色,200-250色段
//		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设置字体
		g.setFont(new Font( "", Font.BOLD , 20 ));
		// 画出30条随机干扰线，160-255色段
		g.setColor(getRandColor(160, 200)); // 在循环外面干扰线颜色一样
		for (int i = 1; i <= 30; i++) {
			int x = rand.nextInt(width); // 线条的起始位置
			int y = rand.nextInt(height);
			int tx = rand.nextInt(12);
			int ty = rand.nextInt(12);
			g.drawLine(x, y, x + tx, y + ty);
		}
		// 随机产生4个验证码
		String coding = ""; // 保存得到的验证码字符串
		for (int i = 0; i < 4; i++) {
			int idx = rand.nextInt(codemap.length);
			if(idx>codemap.length) idx=0;
			String temp = String.valueOf(codemap[idx]); // 0-31的数字
			coding += temp;
			// 显示验证码,10-100色段
			g.setColor(getRandColor(20, 100));
			g.drawString(temp, 16 * i + 10, 18);
		}
		// 信息存入session
		session.setAttribute("code", coding);
		// 图象生成，显示到页面
		g.dispose();
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(buff, "jpeg", sos);
		sos.flush(); // 强行将缓冲区的内容输入到页面
		sos.close();
		sos = null;
		response.flushBuffer();
	}
	
	static public void sendCode2( HttpServletResponse response, HttpServletRequest request ) throws IOException {
		HttpSession session = request.getSession();

		// 禁止页面缓冲
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		// 在缓存中创建图形对象，然后输出
		int width = 80, height = 22; // 输出图片的大小
		BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 指定缓冲的图片和颜色结构
		Graphics g = buff.getGraphics();
		Random rand = new Random();
		// 背景色,200-250色段
//		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设置字体
		g.setFont(new Font( "", Font.BOLD , 20 ));
		// 画出30条随机干扰线，160-255色段
		g.setColor(getRandColor(160, 200)); // 在循环外面干扰线颜色一样
		for (int i = 1; i <= 30; i++) {
			int x = rand.nextInt(width); // 线条的起始位置
			int y = rand.nextInt(height);
			int tx = rand.nextInt(12);
			int ty = rand.nextInt(12);
			g.drawLine(x, y, x + tx, y + ty);
		}
		// 随机产生算术
		String coding = ""; // 保存得到的验证码字符串
		String code = "";
		int a = rand.nextInt(10);
		int b = rand.nextInt(10);
		int c = rand.nextInt(3);
		if(a==0 || a>9) a=1;
		if(b==0 || b>9) b=1;
		if(c>2) c=2;
		if(a<b){
			b=a;
			a=10-b;
		}
		String[] ss = {"+","-","×"};
		if(c==0){
			coding = String.valueOf(a+b);
		}else if(c==1){
			coding = String.valueOf(a-b);
		}else if(c==2){
			coding = String.valueOf(a*b);
		}else{
			coding = String.valueOf(a+b);
		}
		code = a+ss[c]+b+"=";
		char[] ch = code.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			g.setColor(getRandColor(20, 100));
			g.drawString(String.valueOf(ch[i]), 16 * i + 10, 18);
		}
		// 信息存入session
		session.setAttribute("code", coding);
		// 图象生成，显示到页面
		g.dispose();
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(buff, "jpeg", sos);
		sos.flush(); // 强行将缓冲区的内容输入到页面
		sos.close();
		sos = null;
		response.flushBuffer();
	}
	
	final static private char[] codemap = {
		'A', 'B', 'C', 'D', 'E', '#', 'C', 'H', 'J', 'K', 
		'L', 'M', 'N', 'P', 'Q', 'R', '4', 'T', 'Q', 'V', 
		'M', 'X', 'Y', '2', '3', '4', 'X', '6', '7', 
		'8', '9' };
}
