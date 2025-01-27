package server.elorbase.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import server.config.ServerConfig;

public class AESUtil {
	// Comando para regenerar la clave desde consola: openssl rand -out aes.key 32
	
	public static SecretKey loadKey() throws FileNotFoundException, IOException {
		SecretKey key = null;
		byte[] bytes = null;
		try {
			Path path = Paths.get(ServerConfig.AES_KEY);
			bytes = Files.readAllBytes(path);
			
			if (bytes.length == 0) {
                throw new IOException("El archivo de clave está vacío.");
            } else {
            	key = new SecretKeySpec(bytes, "AES");
            }

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		return key;
	}

	public static String encrypt(String data, SecretKey key) throws Exception {
		String encryptedString = null;
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
		byte[] encryptedData = cipher.doFinal(data.getBytes());
		encryptedString = Base64.getEncoder().encodeToString(encryptedData);
		
		return encryptedString;
	}
	
	public static String encryptObject(Object obj, SecretKey key) throws Exception {
        String json = JSONUtil.getSerializedString(obj);
        return encrypt(json, key);
    }
	
	public static String decrypt(String encryptedData, SecretKey key) throws Exception {
		String decryptedString = null;
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		byte[] decodedData = Base64.getDecoder().decode(encryptedData);
		byte[] decryptedData = cipher.doFinal(decodedData);
		decryptedString = new String(decryptedData);
		
		return decryptedString;
	}

}
