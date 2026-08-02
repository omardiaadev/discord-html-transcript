package dev.omardiaa.transcript.core.service;

import dev.omardiaa.transcript.core.config.TranscriberConfig;
import dev.omardiaa.transcript.core.model.Payload;
import dev.omardiaa.transcript.core.model.payload.Channel;
import dev.omardiaa.transcript.core.model.payload.Guild;
import dev.omardiaa.transcript.core.model.payload.Message;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A class for fetching the required {@link Payload}.
 */
@NullMarked
class TranscriberFetcher {
  private static final TypeReference<Guild> GUILD_TYPE = new TypeReference<>() {};
  private static final TypeReference<Channel> CHANNEL_TYPE = new TypeReference<>() {};
  private static final TypeReference<List<Message>> MESSAGE_LIST_TYPE = new TypeReference<>() {};

  private final HttpClient httpClient;
  private final String token;

  TranscriberFetcher(String token) {
    this.token = token;
    this.httpClient = HttpClient.newHttpClient();
  }

  public CompletableFuture<Guild> getGuild(String guildId) {
    return fetch("/guilds/" + guildId, GUILD_TYPE);
  }

  public CompletableFuture<Channel> getChannel(String channelId) {
    return fetch("/channels/" + channelId, CHANNEL_TYPE);
  }

  public CompletableFuture<List<Message>> getMessages(String channelId) {
    return getMessages(channelId, new ArrayList<>(), null)
      .thenApply(messages -> {
        Collections.reverse(messages);
        return messages;
      });
  }

  private CompletableFuture<List<Message>> getMessages(
    String channelId, List<Message> messages, @Nullable String lastMessageId
  ) {
    String path = "/channels/" + channelId + "/messages?limit=100";

    if (lastMessageId != null) {
      path += "&before=" + lastMessageId;
    }

    return fetch(path, MESSAGE_LIST_TYPE)
      .thenCompose(batch -> {
        messages.addAll(batch);

        if (batch.size() < 100) {
          return CompletableFuture.completedStage(messages);
        }

        return getMessages(channelId, messages, batch.get(batch.size() - 1).id());
      });
  }

  private <T> CompletableFuture<T> fetch(String path, TypeReference<T> responseType) {
    HttpRequest request = HttpRequest
      .newBuilder()
      .uri(URI.create("https://discord.com/api/v10" + path))
      .header("Authorization", "Bot " + token)
      .build();

    return httpClient
      .sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(response -> {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
          throw new RuntimeException("Discord API Error: " + response.statusCode() + " - " + response.body());
        }

        try {
          return TranscriberConfig.getJsonMapper().readValue(response.body(), responseType);
        } catch (Exception e) {
          throw new RuntimeException("Failed to deserialize response", e);
        }
      });
  }
}
