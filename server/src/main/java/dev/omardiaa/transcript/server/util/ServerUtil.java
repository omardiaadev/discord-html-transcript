package dev.omardiaa.transcript.server.util;

import dev.omardiaa.transcript.core.util.Check;
import dev.omardiaa.transcript.server.config.ServerConfig;
import dev.omardiaa.transcript.server.exception.MismatchedVersionException;
import dev.omardiaa.transcript.server.model.SemVer;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * A utility class for validating Javalin requests.
 */
@NullMarked
public final class ServerUtil {
  private ServerUtil() {}

  /**
   * Parses the {@code Server-Version} header and compares it with {@link ServerConfig#getVersion()}.
   * <p>
   * Compatibility:
   * <ul>
   * <li>Pre-release versions must match exactly.</li>
   * <li>Major versions must match exactly.</li>
   * <li>Server minor version must be greater than or equal to Client minor version.</li>
   * </ul>
   *
   * @param ctx
   *   the Javalin {@link Context}.
   *
   * @throws MismatchedVersionException
   *   if any of the compatibility rules are violated.
   */
  public static void validateVersion(Context ctx) {
    SemVer clientVersion = SemVer.parse(ctx.header("Server-Version"));
    SemVer serverVersion = ServerConfig.getVersion();

    if (serverVersion.equals(clientVersion)) {
      return;
    }

    if (serverVersion.isPreRelease() || clientVersion.isPreRelease()) {
      throw new MismatchedVersionException("Pre-release versions must match exactly.", clientVersion.toString());
    }

    if (serverVersion.major() != clientVersion.major()) {
      throw new MismatchedVersionException("Major versions must match exactly.", clientVersion.toString());
    }

    if (serverVersion.minor() < clientVersion.minor()) {
      throw new MismatchedVersionException(
        "Server minor version must be greater than or equal to Client minor version.",
        clientVersion.toString()
      );
    }
  }

  /**
   * Parses the {@code Authorization} header and compares it with {@link ServerConfig#getApiKey()}.
   *
   * @param ctx
   *   the Javalin {@link Context}.
   *
   * @throws UnauthorizedResponse
   *   if the {@code Authorization} header provided key does not match {@link ServerConfig#getApiKey()}.
   */
  public static void validateApiKey(Context ctx) {
    String authHeader = ctx.header("Authorization");
    String authTokenType = "Bearer ";

    if (Check.isBlank(authHeader)) {
      throw new UnauthorizedResponse("Missing Authorization header");
    }

    if (!authHeader.startsWith(authTokenType)) {
      throw new UnauthorizedResponse("Authorization header must start with \"Bearer \"");
    }

    if (!Objects.equals(authHeader.substring(authTokenType.length()), ServerConfig.getApiKey())) {
      throw new UnauthorizedResponse("Invalid API Key");
    }
  }
}
