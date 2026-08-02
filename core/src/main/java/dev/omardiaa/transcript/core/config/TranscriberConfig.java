package dev.omardiaa.transcript.core.config;

import dev.omardiaa.transcript.core.service.Transcriber;
import dev.omardiaa.transcript.core.util.EnvironmentUtil;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class for configuring the {@link Transcriber}.
 */
@NullMarked
public final class TranscriberConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(TranscriberConfig.class);
  private static final boolean JTE_DEV = EnvironmentUtil.get("JTE_DEV", false);
  private static final ExecutorService EXECUTOR = Executors
    .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
  private static final JsonMapper JSON_MAPPER = JsonMapper
    .builder()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
    .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    .build();

  private static final TemplateEngine TEMPLATE_ENGINE;

  private TranscriberConfig() {}

  static {
    if (JTE_DEV) {
      TEMPLATE_ENGINE = TemplateEngine.create(new ResourceCodeResolver("jte"), ContentType.Html);
      TEMPLATE_ENGINE.setBinaryStaticContent(true);
      LOGGER.warn("Java Template Engine is running in development mode");
    } else {
      TEMPLATE_ENGINE = TemplateEngine.createPrecompiled(ContentType.Html);
    }
  }

  /**
   * @return the {@link ExecutorService} for transcription.
   */
  public static ExecutorService getExecutor() {
    return EXECUTOR;
  }

  /**
   * @return the default {@link JsonMapper}.
   */
  public static JsonMapper getJsonMapper() {
    return JSON_MAPPER;
  }

  /**
   * @return the default {@link TemplateEngine}.
   */
  public static TemplateEngine getTemplateEngine() {
    return TEMPLATE_ENGINE;
  }

  /**
   * Gracefully shuts down the {@link #EXECUTOR}.
   */
  public static void shutdownExecutor() {
    if (EXECUTOR.isShutdown()) {
      return;
    }

    LOGGER.debug("Shutting down executor...");

    EXECUTOR.shutdown();

    try {
      if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
        LOGGER.warn("Forcing shutdown due to executor taking too long to shutdown");
        EXECUTOR.shutdownNow();
      }
    } catch (InterruptedException e) {
      LOGGER.error("Forcing shutdown due to interruption");
      EXECUTOR.shutdownNow();
      Thread.currentThread().interrupt();
    }

    LOGGER.debug("Executor shutdown.");
  }
}
