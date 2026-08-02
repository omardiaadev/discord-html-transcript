@import dev.omardiaa.transcript.core.model.Payload
@import dev.omardiaa.transcript.core.model.payload.Message
@import dev.omardiaa.transcript.core.util.TimeUtil

@param Payload payload
@param Message message
@param Message previousMessage

@if(message.type() != Message.Type.UNKNOWN)
  !{boolean showAuthor = message.showAuthor(previousMessage);}

  @if(message.showDivider(previousMessage))
    @template.discord.ui.divider(timestamp = message.timestamp())
  @endif

  <li
    id="${message.id()}"
    data-show-author="${showAuthor}"
    data-message-type="${message.type()}"
    class="message"
  >
    @if(message.type() == Message.Type.DEFAULT)
      @template.discord.ui.reference(message = message)

      <div class="author__avatar__container">
        @if(showAuthor)
          <img
            src="${message.author().avatarUrl()}"
            alt="Avatar"
            class="author__avatar"
          />
        @else
          <time class="message__timestamp">${TimeUtil.formatTimeShort(message.timestamp())}</time>
        @endif
      </div>

      <div>
        @if(showAuthor)
          <h3 class="author">
            @template.discord.message.user.name(author = message.author())
            @template.discord.message.user.tag(user = message.author())

            <div class="author__timestamp">
              @template.discord.ui.timestamp(timestamp = message.timestamp())
              <time>${TimeUtil.formatDateShort(message.timestamp())}</time>
            </div>
          </h3>
        @endif

        @template.discord.message.content(guild = payload.guild(), message = message, content = message.content())
        @template.discord.message.message-snapshots(guild = payload.guild(), message = message)

        @if(message.editedTimestamp() != null)
          <div class="message__edited">
            @template.discord.ui.timestamp(timestamp = message.editedTimestamp())
            <span>(edited)</span>
          </div>
        @endif

        @template.discord.message.accessories(payload = payload, message = message)
      </div>
    @elseif(message.type() == Message.Type.THREAD_CREATED)
      @template.discord.ui.reference(message = message)

      <div class="author__avatar__container">
        <svg
          viewBox="0 0 24 24"
          height="20"
          width="20"
        >
          <path d="M12 2.81a1 1 0 0 1 0-1.41l.36-.36a1 1 0 0 1 1.41 0l9.2 9.2a1 1 0 0 1 0 1.4l-.7.7a1 1 0 0 1-1.3.13l-9.54-6.72a1 1 0 0 1-.08-1.58l1-1L12 2.8ZM12 21.2a1 1 0 0 1 0 1.41l-.35.35a1 1 0 0 1-1.41 0l-9.2-9.19a1 1 0 0 1 0-1.41l.7-.7a1 1 0 0 1 1.3-.12l9.54 6.72a1 1 0 0 1 .07 1.58l-1 1 .35.36ZM15.66 16.8a1 1 0 0 1-1.38.28l-8.49-5.66A1 1 0 1 1 6.9 9.76l8.49 5.65a1 1 0 0 1 .27 1.39ZM17.1 14.25a1 1 0 1 0 1.11-1.66L9.73 6.93a1 1 0 0 0-1.11 1.66l8.49 5.66Z"/>
        </svg>
      </div>

      <div>
        <h3 class="author message__thread">
          @template.discord.message.user.name(author = message.author())

          <span>started a thread:</span>
          <span class="message__thread__name">${message.content()}.</span>

          <div class="author__timestamp">
            @template.discord.ui.timestamp(timestamp = message.timestamp())
            <time>${TimeUtil.formatDateShort(message.timestamp())}</time>
          </div>
        </h3>

        @template.discord.message.accessories(payload = payload, message = message)

        <div class="message__thread__container">
          <span class="message__thread__container__name">${message.content()}</span>
        </div>
      </div>
    @endif
  </li>
@endif
