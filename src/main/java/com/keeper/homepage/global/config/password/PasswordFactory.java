package com.keeper.homepage.global.config.password;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordFactory {

  public static PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }

  private static final PasswordEncoder passwordEncoder = new PasswordEncoder() {
    @Override
    public String encode(CharSequence rawPassword) {
      return createDelegatingPasswordEncoder().encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
      return createDelegatingPasswordEncoder().matches(rawPassword, encodedPassword)
          || matchesWithPBKDF2SHA256(rawPassword.toString(), encodedPassword)
          || matchesWithMD5(rawPassword.toString(), encodedPassword);
    }
  };

  private static boolean matchesWithPBKDF2SHA256(String password, String hashedPassword) {
    try {
      String[] parts = hashedPassword.split(":");
      if (parts.length != 4) {
        return false;
      }
      int iterations = Integer.parseInt(parts[1]);
      String salt = parts[2];
      String hash = encodeWithPBKDF2SHA256(password, salt, iterations);
      String[] passwordParts = hash.split(":");
      return passwordParts[3].substring(0, 32).equals(parts[3]);
    } catch (Exception ignore) {
      return false;
    }
  }

  private static String encodeWithPBKDF2SHA256(String password, String salt, int iterations)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String hash = getEncodedHashWithPBKDF2SHA256(password, salt, iterations);
    return "%s:%d:%s:%s".formatted("pbkdf2_sha256", iterations, salt, hash);
  }

  private static String getEncodedHashWithPBKDF2SHA256(String password, String salt, int iterations)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8),
        iterations, 256);
    SecretKey secret = keyFactory.generateSecret(keySpec);

    assert secret != null;
    byte[] rawHash = secret.getEncoded();
    byte[] hashBase64 = Base64.getEncoder().encode(rawHash);

    return new String(hashBase64);
  }

  private static boolean matchesWithMD5(String password, String hashedPassword) {
    try {
      return hashedPassword.equals(encodeWithMD5(password));
    } catch (Exception ignore) {
      return false;
    }
  }

  private static String encodeWithMD5(String password) throws NoSuchAlgorithmException {
    return getEncodedHashWithMD5(password);
  }

  private static String getEncodedHashWithMD5(String pwd) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(pwd.getBytes());
    byte[] byteData = md.digest();
    StringBuilder sb = new StringBuilder();
    for (byte byteDatum : byteData) {
      sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }
}
