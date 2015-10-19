package com.royalstone.util;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/*
 * 加密/解密
 */

public class Secret {
	private static String Algorithm = "DES"; // 定义 加密算法,可用

	// DES,DESede,Blowfish

	private static byte[] key = { -84, -19, 0, 5, 115, 114, 0, 30, 99, 111,
			109, 46, 115, 117, 110, 46, 99, 114, 121, 112, 116, 111, 46, 112,
			114, 111, 118, 105, 100, 101, 114, 46, 68, 69, 83, 75, 101, 121,
			107, 52, -100, 53, -38, 21, 104, -104, 2, 0, 1, 91, 0, 3, 107, 101,
			121, 116, 0, 2, 91, 66, 120, 112, 117, 114, 0, 2, 91, 66, -84, -13,
			23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 0, 8, -118, 7, -50,
			-92, -63, 35, 56, 25 };

	private static Secret secret;

	private Secret() {
	}

	static {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		secret = new Secret();
	}

	// -----------------------------------------------------------------------------

	/**
	 * 加密字节数组
	 */
	public byte[] encrypt(byte[] b) throws Exception {
		ObjectInputStream ois = null;
		try {
			// 密钥
			ois = new ObjectInputStream(new ByteArrayInputStream(key));
			SecretKey deskey = (SecretKey) ois.readObject();

			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			byte[] en = c1.doFinal(b);

			// Base 64编码
			return Base64.encode(en).getBytes();
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}

	/**
	 * 加密字符串,返回加密的字符串
	 */
	public static String encrypt(String str) throws Exception {
		// Des加密
		return new String(secret.encrypt(str.getBytes("8859_1")), "8859_1");
		// Base64编码
	}

	// -----------------------------------------------------------------------------

	/**
	 * 解密字节数组
	 */
	public static byte[] decrypt(byte[] b) {
		try {
			// 密钥
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(key));
			try {
				SecretKey deskey = (SecretKey) ois.readObject();

				// Base 64编码
				byte[] de = Base64.decode(new String(b));

				// 解密
				Cipher c1 = Cipher.getInstance(Algorithm);
				c1.init(Cipher.DECRYPT_MODE, deskey);

				return c1.doFinal(de);
			} finally {
				ois.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 解密字符串,返回加密的字符串
	 */
	public static String decrypt(String str) {
		try {
			return new String(Secret.decrypt(str.getBytes("8859_1")), "8859_1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.err.println("Encrypt text not found!");
			System.exit(1);
		}

		System.out.println("The result is " + encrypt(args[0]));

	}

}