package dev.omardiaa.transcript.server;

import dev.omardiaa.transcript.core.config.TranscriberConfig;
import dev.omardiaa.transcript.core.exception.TranscriberException;
import dev.omardiaa.transcript.core.model.Payload;
import dev.omardiaa.transcript.core.service.Transcriber;
import dev.omardiaa.transcript.server.config.ServerConfig;
import dev.omardiaa.transcript.server.config.ServerRequestLogger;
import dev.omardiaa.transcript.server.exception.GlobalExceptionHandler;
import dev.omardiaa.transcript.server.exception.MismatchedVersionException;
import dev.omardiaa.transcript.server.util.ServerUtil;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.json.JavalinJackson3;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.exc.MismatchedInputException;

import java.util.Map;

/**
 * A singleton class for initializing, configuring, and running the Javalin server.
 */
@NullMarked
public final class Server {
  private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

  private static final Server INSTANCE = new Server();

  private final Javalin javalin;
  private final Transcriber transcriber;

  private Server() {
    this.transcriber = new Transcriber();
    this.javalin = Javalin
      .create(config -> {
        config.startup.showJavalinBanner = false;
        config.startup.showOldJavalinVersionWarning = false;

        config.jetty.host = ServerConfig.getHost();
        config.jetty.port = ServerConfig.getPort();

        config.jsonMapper(new JavalinJackson3(TranscriberConfig.getJsonMapper(), false));

        config.routes
          .get("/health", this::healthHandler)
          .post("/transcript", this::transcriptHandler)
          .beforeMatched(ServerUtil::validateVersion)
          .exception(TranscriberException.class, GlobalExceptionHandler::handleTranscriber)
          .exception(MismatchedInputException.class, GlobalExceptionHandler::handleMismatchedInput)
          .exception(MismatchedVersionException.class, GlobalExceptionHandler::handleMismatchedVersion)
          .exception(UnauthorizedResponse.class, GlobalExceptionHandler::handleUnauthorized)
          .exception(Exception.class, GlobalExceptionHandler::handleException);

        config.requestLogger.http(new ServerRequestLogger());

        config.events.serverStopped(TranscriberConfig::shutdownExecutor);

        if (ServerConfig.getApiKey() != null) {
          config.routes.before(ServerUtil::validateApiKey);
        }
      });
  }

  /**
   * @return the singleton {@link Server} instance.
   */
  public static Server getInstance() {
    return INSTANCE;
  }

  /**
   * Starts the Javalin server.
   */
  public void start() {
    javalin.start();
    LOGGER.info("Started discord-transcript {}", ServerConfig.getVersion());
  }

  /**
   * Stops the Javalin server.
   */
  public void stop() {
    javalin.stop();
    LOGGER.info("Stopped discord-transcript");
  }

  /**
   * {@code GET /health} route handler.
   *
   * @param ctx
   *   the Javalin {@link Context}.
   */
  private void healthHandler(Context ctx) {
    ctx.status(HttpStatus.OK)
       .json(Map.of("version", ServerConfig.getVersion().toString()));
  }

  /**
   * {@code POST /transcript} route handler.
   *
   * @param ctx
   *   the Javalin {@link Context}.
   */
  private void transcriptHandler(Context ctx) {
    Payload payload = ctx.bodyStreamAsClass(Payload.class);

    ctx.future(() -> transcriber
      .transcribe(payload)
      .thenAccept(output -> ctx
        .status(HttpStatus.OK)
        .contentType(ContentType.HTML)
        .result(output.toByteArray())));
  }
}
