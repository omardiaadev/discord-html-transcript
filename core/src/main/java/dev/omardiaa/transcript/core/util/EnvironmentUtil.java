package dev.omardiaa.transcript.core.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A helper class for environment variables.
 */
@NullMarked
public final class EnvironmentUtil {
  private EnvironmentUtil() {}

  /**
   * @param key
   *   the variable name.
   * @param defaultValue
   *   the value to return if {@link #get(String)} returns {@code null}.
   *
   * @return {@link #get(String)}, or {@code defaultValue} if {@link #get(String)} is null.
   */
  public static String get(String key, String defaultValue) {
    String value = get(key);
    return value != null ? value : defaultValue;
  }

  /**
   * @param key
   *   the variable name.
   * @param defaultValue
   *   the value to return if {@link #get(String)} returns {@code null}.
   *
   * @return {@link #get(String)} as {@code int}, or {@code defaultValue} if {@link #get(String)} is null.
   *
   * @throws IllegalArgumentException
   *   if the {@link #get(String)} returns a value that's not an {@code int}.
   */
  public static int get(String key, int defaultValue) {
    String value = get(key);

    try {
      return value != null ? Integer.parseInt(value) : defaultValue;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
        "Invalid value '" + value + "' for environment variable '" + key + "'. Expected an integer."
      );
    }
  }

  /**
   * @param key
   *   the variable name.
   * @param defaultValue
   *   the value to return if {@link #get(String)} returns {@code null}.
   *
   * @return {@link #get(String)} as {@code boolean}, or {@code defaultValue} if {@link #get(String)} is null.
   */
  public static boolean get(String key, boolean defaultValue) {
    String value = get(key);
    return value != null ? Boolean.parseBoolean(value) : defaultValue;
  }

  /**
   * @param key
   *   the variable name.
   *
   * @return {@link System#getenv(String)}.
   */
  public static @Nullable String get(String key) {
    return System.getenv(key);
  }
}
