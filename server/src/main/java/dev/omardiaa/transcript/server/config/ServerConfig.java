package dev.omardiaa.transcript.server.config;

import dev.omardiaa.transcript.core.util.EnvironmentUtil;
import dev.omardiaa.transcript.server.Server;
import dev.omardiaa.transcript.server.model.SemVer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A helper class for initializing {@link Server} configuration.
 */
@NullMarked
public final class ServerConfig {
  private static final @Nullable String API_KEY = EnvironmentUtil.get("DISCORD_TRANSCRIPT_API_KEY").orElse(null);
  private static final String LOG_LEVEL = EnvironmentUtil.get("DISCORD_TRANSCRIPT_LOG_LEVEL").orElse("INFO");
  private static final String HOST = EnvironmentUtil.get("DISCORD_TRANSCRIPT_HOST", "127.0.0.1");
  private static final int PORT = EnvironmentUtil.get("DISCORD_TRANSCRIPT_PORT", 7000);
  private static final SemVer VERSION = SemVer.parse("0.1.0-beta.7");

  private ServerConfig() {}

  /**
   * @return {@code DISCORD_TRANSCRIPT_API_KEY} value, or {@code null} if variable is not specified.
   */
  public static @Nullable String getApiKey() {
    return API_KEY;
  }

  /**
   * @return {@code DISCORD_TRANSCRIPT_LOG_LEVEL} value, or {@code "WARN"} if variable is not specified.
   */
  public static String getLogLevel() {
    return LOG_LEVEL;
  }

  /**
   * @return {@code DISCORD_TRANSCRIPT_HOST} value, or {@code "127.0.0.1"} if variable is not specified.
   */
  public static String getHost() {
    return HOST;
  }

  /**
   * @return {@code DISCORD_TRANSCRIPT_PORT} value, or {@code 7000} if variable is not specified.
   */
  public static int getPort() {
    return PORT;
  }

  /**
   * @return current server {@link SemVer}.
   */
  public static SemVer getVersion() {
    return VERSION;
  }
}
