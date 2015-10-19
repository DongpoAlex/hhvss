package com.royalstone.util.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DES_Encrypt {
	private static String keyData = "2351412351251235"; //密钥

	private static SecretKey key;

	private static Cipher enCipher;

	private static DESKeySpec spec = null;

	private static SecretKeyFactory kf = null;

	static {
		try {
			spec = new DESKeySpec(keyData.getBytes());
			kf = SecretKeyFactory.getInstance("DES");
			key = kf.generateSecret(spec);
			enCipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");
			enCipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加密方法   source 源数据
	 */
	public static String encrypt(String source) throws Exception {
		/**  */
		enCipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = source.getBytes();
		/** 执行加密操作 */
		byte[] b1 = enCipher.doFinal(b);
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(b1);
	}

	public static void main(String[] args) throws Exception {
		String source = "Hello World!";// 要加密的字符串
		String cryptograph = encrypt(source);// 生成的密文
		System.out.println(cryptograph);
	}
}
