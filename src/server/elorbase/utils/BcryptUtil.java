package server.elorbase.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BcryptUtil {
	
	public static boolean verifyPassword(String password, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash.toCharArray());
        return result.verified;
    }
	
	public static String getHashedPass(String password) {
		return BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(12, password.toCharArray());
	}

}
