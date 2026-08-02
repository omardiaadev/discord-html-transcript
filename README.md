<h1 align="center">discord-html-transcript</h1>

<p align="center"><strong>Generate natively styled Discord chat logs</strong></p>

<div align="center">
    <a href="https://central.sonatype.com/artifact/dev.omardiaa/discord-html-transcript"><img alt="Maven Version" src="https://img.shields.io/maven-central/v/dev.omardiaa/discord-html-transcript?style=flat-square&label=Maven&color=0088FF"></a>
    <a href="https://github.com/omardiaadev/discord-html-transcript/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/github/license/omardiaadev/discord-html-transcript?style=flat-square&label=License&color=0088FF"></a>
    <a href="https://discord.omardiaa.dev"><img alt="Discord" src="https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=FFF&color=5865F2"></a>
</div>

## About

**discord-html-transcript** is a microservice for generating Discord chat logs as a singular offline HTML file that
matches the look and feel of the Discord desktop client.

> [!IMPORTANT]
> discord-html-transcript is not affiliated with Discord Inc.

### Features

- **Discord UI:** Modern styling that replicates the look and feel of the Discord desktop client,
- **Portable:** 100% offline HTML file without compromising any styling.
- **ComponentsV2:** Containers, Media Galleries, Sections, and more.
- **Markdown:** Standard Markup, Mentions, Custom Emojis, and more.

### Example

<a title="Example" href="https://htmlpreview.github.io/?https://github.com/omardiaadev/discord-html-transcript/blob/main/examples/transcript.html">
    <img alt="discord-html-transcript" src="https://res.cloudinary.com/omardiaadev/image/upload/discord-transcript_aldbto.png">
</a>

## Usage

### 1. Library (Recommended)

You can use the suitable library if you're using any of the following Discord APIs:

<table>
    <thead>
        <tr>
            <th>Discord API</th>
            <th>Library</th>
        </tr>
    </thead>
    <tbody>
        <tr align="center">
            <td>
                <img alt="JDA" src="https://avatars.githubusercontent.com/u/103134607" height="64">
                <p>Java Discord API</p>
            </td>
            <td><a href="https://github.com/omardiaadev/discord-html-transcript-jda">discord-html-transcript-jda</a></td>
        </tr>
        <tr align="center">
            <td>
                <img alt="discord.js" src="https://avatars.githubusercontent.com/u/26492485" height="64">
                <p>discord.js</p>
            </td>
            <td><a href="https://github.com/omardiaadev/discord-html-transcript-discordjs">discord-html-transcript-discordjs</a></td>
        </tr>
        <tr align="center">
            <td>
                <img alt="discord.py" src="https://discordpy.readthedocs.io/en/latest/_static/discord_py_logo.ico" height="64">
                <p>discord.py</p>
            </td>
            <td><code>WIP</code></td>
        </tr>
    </tbody>
</table>

### 2. Installation

You can install the core library directly via Maven or Gradle.

#### Prerequisites

- **Java 17** or higher

##### Maven

```xml

<dependency>
  <groupId>dev.omardiaa</groupId>
  <artifactId>discord-html-transcript-core</artifactId>
  <version>0.1.0-beta.7</version>
</dependency>
```

##### Gradle

```kts
implementation("dev.omardiaa:discord-html-transcript-core:0.1.0-beta.7")
```

### 3. Standalone API

You can [download](https://github.com/omardiaadev/discord-html-transcript/releases) as an executable standalone web
server to self-host.

#### Configuration

##### Environment Variables

<table width="100%">
    <tr>
        <th width="20%">Variable</th>
        <th width="80%">Description</th>
    </tr>
    <tr>
        <td><code>DISCORD_TRANSCRIPT_HOST</code></td>
        <td>
            Specifies a custom host for the server.
            <br>
            (default: <code>"127.0.0.1"</code>)
        </td>
    </tr>
    <tr>
        <td><code>DISCORD_TRANSCRIPT_PORT</code></td>
        <td>
            Specifies a custom port for the server.
            <br>
            (default: <code>7000</code>)
        </td>
    </tr>
    <tr>
        <td><code>DISCORD_TRANSCRIPT_API_KEY</code></td>
        <td>Specifies a secret key to authenticate client requests.</td>
    </tr>
    <tr>
        <td><code>DISCORD_TRANSCRIPT_LOG_LEVEL</code></td>
        <td>
            Specifies log level.
            <br>
            (default: <code>"INFO"</code>)
        </td>
    </tr>
</table>

#### Usage

##### Headers

<table>
    <tr>
        <th>Header</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>Server-Version</code></td>
        <td>
            Validates the server version required by the client against the actual server version.
            <br>
            Required.
        </td>
    </tr>
    <tr>
        <td><code>Authorization</code></td>
        <td>
            Authenticates client requests.
            <br>
            Required, if <code>DISCORD_TRANSCRIPT_API_KEY</code> is set.
            <br>
            (format: <code>Bearer &lt;DISCORD_TRANSCRIPT_API_KEY&gt;</code>)
        </td>
    </tr>
</table>

##### Endpoints

<table>
    <tr>
        <th>Endpoint</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>GET /health</code></td>
        <td>Retrieves basic server information.</td>
    </tr>
    <tr>
        <td><code>POST /transcript</code></td>
        <td>
            Accepts a transcript Payload and asynchronously generates an HTML byte stream.
        </td>
    </tr>
</table>

##### Versioning

The server validates the [`Server-Version`](#headers) header using the following rules:

- Pre-release versions must match exactly.
- Major versions must match exactly.
- Server minor version must be greater than or equal to `Server-Version` minor version.

> [!TIP]
> Request examples can be found [here](examples/requests.http).

##### Request Payload Example

You can specify the `options` object to configure the generator's options:

```json5
{
  "guild": {},
  "channel": {},
  "messages": [],
  "options": {
    "attachment": {
      /*
      - If set to "true", the generator will download
        attachments and encode them into the file.
      - default: false
      */
      "save_images": true
    },
    "style": {
      /*
      - If set, the generator will use "path/to/style.css"
        instead of using inline styles.
      - default: null
      */
      "path": "path/to/style.css"
    }
  }
}
```

## Contributing

**If you found `discord-html-transcript` useful, please consider giving it a 🌟!**

Need help? [Ask the Community](https://discord.omardiaa.dev)!

<div align="center">
    <p>Made With 💙 By <a href="https://github.com/omardiaadev"><b>Omar Diaa</b></a></p>
    <a href="https://fiverr.omardiaa.dev"><img alt="Fiverr" src="https://img.shields.io/badge/-1DBF73?style=for-the-badge&logo=fiverr&logoColor=FFF&logoSize=auto"></a>
    <a href="https://ko-fi.com/omardiaadev"><img alt="Ko-fi" src="https://img.shields.io/badge/ko--fi-FF6433?style=for-the-badge&logo=kofi&logoColor=FFF"></a>
</div>
