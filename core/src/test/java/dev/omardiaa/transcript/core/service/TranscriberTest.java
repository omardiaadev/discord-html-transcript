package dev.omardiaa.transcript.core.service;

import dev.omardiaa.transcript.core.config.TranscriberConfig;
import dev.omardiaa.transcript.core.model.Payload;
import dev.omardiaa.transcript.core.model.payload.Channel;
import dev.omardiaa.transcript.core.model.payload.Guild;
import dev.omardiaa.transcript.core.model.payload.Message;
import dev.omardiaa.transcript.core.model.payload.PayloadOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An integration test to verify transcript generation.
 * <br>
 * Additionally, this will generate a {@code transcript.html} and {@code transcript.json} of the specified channel
 * under {@code /target}.
 * <p>
 * This test will be skipped if {@link #DISCORD_BOT_TOKEN}, {@link #DISCORD_GUILD_ID}, {@link #DISCORD_CHANNEL_ID},
 * or {@code JTE_DEV} environment variables are not specified.
 */
@EnabledIfEnvironmentVariables(
  {
    @EnabledIfEnvironmentVariable(named = "DISCORD_BOT_TOKEN", matches = ".+"),
    @EnabledIfEnvironmentVariable(named = "DISCORD_GUILD_ID", matches = ".+"),
    @EnabledIfEnvironmentVariable(named = "DISCORD_CHANNEL_ID", matches = ".+"),
    @EnabledIfEnvironmentVariable(named = "JTE_DEV", matches = "true", disabledReason = "JTE_DEV must be set to: true")
  }
)
class TranscriberTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TranscriberTest.class);
  private static final String DISCORD_BOT_TOKEN = System.getenv("DISCORD_BOT_TOKEN");
  private static final String DISCORD_GUILD_ID = System.getenv("DISCORD_GUILD_ID");
  private static final String DISCORD_CHANNEL_ID = System.getenv("DISCORD_CHANNEL_ID");
  private static final String STYLE_PATH = System.getenv("DISCORD_HTML_TRANSCRIPT_STYLE_PATH");

  private static Transcriber transcriber;
  private static TranscriberFetcher fetcher;

  @BeforeAll
  static void beforeAll() {
    transcriber = new Transcriber();
    fetcher = new TranscriberFetcher(DISCORD_BOT_TOKEN);
  }

  @Test
  void shouldTranscribe() throws IOException {
    CompletableFuture<Guild> guildFuture = fetcher.getGuild(DISCORD_GUILD_ID);
    CompletableFuture<Channel> channelFuture = fetcher.getChannel(DISCORD_CHANNEL_ID);
    CompletableFuture<List<Message>> messagesFuture = fetcher.getMessages(DISCORD_CHANNEL_ID);

    Payload payload = Assertions.assertTimeoutPreemptively(
      Duration.ofSeconds(30),
      () -> CompletableFuture
        .allOf(guildFuture, channelFuture, messagesFuture)
        .thenApply(
          v -> new Payload(
            guildFuture.join(),
            channelFuture.join(),
            messagesFuture.join(),
            new PayloadOptions.Builder().path(STYLE_PATH).build()
          ))
        .join()
    );

    Path dir = Path.of("target");
    Files.createDirectories(dir);

    Path htmlPath = dir.resolve("transcript.html");
    Files.write(htmlPath, transcriber.transcribe(payload).join().toByteArray());
    LOGGER.info("Saved: file://{}", htmlPath.toAbsolutePath());

    Path jsonPath = dir.resolve("transcript.json");
    TranscriberConfig.getJsonMapper().writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), payload);
    LOGGER.info("Saved: file://{}", jsonPath.toAbsolutePath());
  }
}
