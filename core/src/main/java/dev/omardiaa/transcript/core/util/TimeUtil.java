package dev.omardiaa.transcript.core.util;

import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * A helper class for formatting timestamps.
 */
@NullMarked
public final class TimeUtil {
  private static final DateTimeFormatter DATE_FULL = DateTimeFormatter.ofPattern("eeee, MMMM M, y 'at' h:mm a '(UTC)'");
  private static final DateTimeFormatter DATE_LONG = DateTimeFormatter.ofPattern("MMMM d, y");
  private static final DateTimeFormatter DATE_SHORT = DateTimeFormatter.ofPattern("M/d/yy, h:mm a '(UTC)'");
  private static final DateTimeFormatter TIME_SHORT = DateTimeFormatter.ofPattern("h:mm a");
  private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("M/d/y h:mm a '(UTC)'");

  private TimeUtil() {}

  public static String formatDateFull(OffsetDateTime timestamp) {
    return timestamp.format(DATE_FULL);
  }

  public static String formatDateLong(OffsetDateTime timestamp) {
    return timestamp.format(DATE_LONG);
  }

  public static String formatDateShort(OffsetDateTime timestamp) {
    return timestamp.format(DATE_SHORT);
  }

  public static String formatTimeShort(OffsetDateTime timestamp) {
    return timestamp.format(TIME_SHORT);
  }

  /**
   * Formats the specified {@code epochSecond} as a Discord Timestamp.
   *
   * @param epochSecond
   *   the epoch to format.
   *
   * @return {@code eg. 1/1/30 12:00 AM (UTC)}
   *
   * @see <a href="https://docs.discord.com/developers/reference#message-formatting">Message Formatting</a>
   */
  public static String formatTimestamp(String epochSecond) {
    return Instant.ofEpochSecond(Integer.parseInt(epochSecond)).atOffset(ZoneOffset.UTC).format(TIMESTAMP);
  }
}
