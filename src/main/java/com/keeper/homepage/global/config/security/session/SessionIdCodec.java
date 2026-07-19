package com.keeper.homepage.global.config.security.session;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class SessionIdCodec {

  private static final int SESSION_ID_BYTES = 32;
  private static final Pattern SESSION_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]{43}");

  public Optional<String> toRedisKey(String sessionId) {
    if (!isCanonicalSessionId(sessionId)) {
      return Optional.empty();
    }
    return Optional.of(SessionPolicy.REDIS_KEY_PREFIX + sha256Hex(sessionId));
  }

  public boolean isCanonicalSessionId(String sessionId) {
    if (sessionId == null || !SESSION_ID_PATTERN.matcher(sessionId).matches()) {
      return false;
    }
    try {
      byte[] decoded = Base64.getUrlDecoder().decode(sessionId);
      return decoded.length == SESSION_ID_BYTES
          && Base64.getUrlEncoder().withoutPadding().encodeToString(decoded).equals(sessionId);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static String sha256Hex(String sessionId) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(sessionId.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", e);
    }
  }
}
