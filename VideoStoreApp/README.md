# Rewind Video Club

A native Android app that turns your Plex library into a 90s video rental store:
movies and shows sit on wood-grain shelves as VHS tapes, each tape's case flips
over to show its plot/cast/rating like a real VHS back cover, and hitting play
slides the tape into a drawn VCR before cutting to an in-app "TV" that streams
the video from your Plex server.

## Features

- **Sign in with Plex** using the official PIN/"link" OAuth flow (plex.tv/link)
  — no manual token copy-pasting, and the account token is stored in
  `EncryptedSharedPreferences`.
- **Server discovery** via `plex.tv/api/v2/resources`, so it finds every Plex
  Media Server on your account and lets you pick one.
- **Shelves**: each library section (Movies, TV Shows) is broken into
  genre shelves, built from each item's real Plex genre metadata.
- **VHS cases**: poster art pulled straight from your Plex server; tap a case
  to flip it and read the plot summary, cast, genres, content rating, runtime,
  and studio — all real Plex metadata, not placeholders.
- **Shows**: seasons and episodes are browsable inside the show's case, same
  as a real box set insert.
- **Playback**: tapping Play animates the tape loading into a drawn VCR deck,
  then shows a CRT-style TV frame with scanline overlay playing the actual
  video (via Plex's universal HLS transcode endpoint through Media3/ExoPlayer).

## Project layout

```
app/src/main/java/com/rewindvideo/plex/
  data/            Plex API models, Retrofit interfaces, repository, token storage
  ui/theme/        90s color palette + typography
  ui/components/   VhsCaseTile, VideoShelf, FlippableVhsCase, VcrInsertAnimation, RetroTvFrame, CrtScanlineOverlay
  ui/screens/      login, browse, detail, player screens + ViewModels
  navigation/      Compose Navigation graph
```

No dependency injection framework is used — `RewindApplication` is a tiny
manual service locator holding the one `PlexRepository` instance.

## Building

Open the `VideoStoreApp/` folder in Android Studio (Koala or newer) and hit
Run. It targets `compileSdk = 35`, `minSdk = 26`.

**A note on this sandbox**: the project was authored and reviewed here, but
this remote environment's network policy blocks `dl.google.com`, which is
where the Android Gradle Plugin and every `androidx`/Compose artifact live —
so a real `./gradlew build` could not be run in this session (it fails at
dependency resolution, not at compiling this app's code). The Gradle wrapper
is committed and everything was hand-reviewed for import/API correctness, but
please do a build in Android Studio on your machine (where `dl.google.com`
is reachable) as the first real compile check, and expect to fix anything
that review missed.

## Design choices worth knowing about

- **Streaming** uses Plex's "Universal Transcode" HLS endpoint
  (`/video/:/transcode/universal/start.m3u8`) so playback works regardless of
  the source file's codec/container, at the cost of always transcoding rather
  than direct-playing compatible files. If you want direct play for files
  your device can already handle, that's a follow-up (check `Media[0].Part[0].key`
  and stream that path directly when the container/codec is device-compatible).
- **Cleartext traffic is allowed** (`usesCleartextTraffic="true"`) because most
  home Plex servers are only reachable over `http://` on the LAN.
- **Fonts**: the 90s look currently comes from color, layout, and heavy/wide
  system-font styling rather than a bundled display font. Drop a retro `.ttf`
  into `res/font` and wire it into `ui/theme/Type.kt` for the full look.
- **Icons**: shelves/cases/VCR/TV are all drawn with Compose `Canvas` and
  layered `Box`es rather than bitmap art, so there's nothing to source or
  license — it also means you can restyle the whole look by editing
  `ui/theme/Color.kt` and the composables in `ui/components/`.

## Unrelated heads-up

`index.html` at the repo root (unrelated to this app) has a live OpenAI API
key hardcoded in plain text, committed to git history. Revoke that key and
scrub it from history when you get a chance — it's a real, exploitable secret
sitting in a public-ish place.
