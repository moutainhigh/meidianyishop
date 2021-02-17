package com.meidianyi.shop.support;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 *
 * @author 新国
 *
 */
public class PemToPkcs12 {

	/**
	 * X.509 pem format to pkcs12 format
     *
	 * @param privateKeyStr
	 * @param certificateStr
	 * @return
	 * @throws Exception
	 */
	public static byte[] pemToPkcs12(String privateKeyStr, String certificateStr) throws Exception {
        return pemToPkcs12(privateKeyStr, certificateStr, StringUtils.EMPTY.toCharArray(), "alias");
	}

	/**
	 * X.509 pem format to pkcs12 format
     *
	 * @param privateKeyStr
	 * @param certificateStr
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static byte[] pemToPkcs12(String privateKeyStr, String certificateStr, char[] password) throws Exception {
		return pemToPkcs12(privateKeyStr, certificateStr, password, "alias");
	}

	/**
	 * X.509 pem format to pkcs12 format
     *
	 * @param privateKeyStr
	 * @param certificateStr
	 * @param password
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	public static byte[] pemToPkcs12(String privateKeyStr, String certificateStr, char[] password,
			String alias) throws Exception {
		byte[] privateKeyData = privateKeyStr.getBytes();
		byte[] certificateData = certificateStr.getBytes();

		// Remove PEM header, footer and \n
		String privateKeyPem = new String(privateKeyData, StandardCharsets.UTF_8);
		privateKeyPem = privateKeyPem.replace(
				"-----BEGIN PRIVATE KEY-----\n", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\n", "");
		byte[] privateKeyDer = Base64.getDecoder().decode(privateKeyPem);

		// Used to read User_privkey.pem file to get private key
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyDer);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(spec);

		// Used to read user certificate
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		Certificate cert = factory.generateCertificate(new ByteArrayInputStream(certificateData));

		// Create keystore, add entry with the provided alias and save
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(null);
		ks.setKeyEntry(alias, privateKey, password, new Certificate[] { cert });
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ks.store(out, password);
		byte[] result = out.toByteArray();
		out.close();
		return result;
	}
}
