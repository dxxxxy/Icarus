# Icarus

GUI and config library for Minecraft mods. A Minecraft-free core plus one thin adapter per Minecraft version and loader. User docs live in the [wiki](https://github.com/DxxxxY/Icarus/wiki); the README is kept short on purpose.

## Layout

- `core/`: everything that isn't Minecraft-specific (API, settings, configs, menu, themes), plain Java 8. Most changes belong here.
- `testmod/`: the example mod shared by every target, with one page set per theme (`test/skeet`, `test/fatality`).
- `platforms/`: one adapter per target, each implementing `Platform` and `Renderer`. There are 11 targets:
  - Forge 1.7.10, 1.8.9 and 1.12.2;
  - Fabric and Forge 1.16.5 and 1.20.1;
  - Fabric and NeoForge 1.21.1 and 26.3.
- `platforms/mc-26.3`: a separate included build on Unimined 2.0.2. Every other target uses Unimined 1.4.1. Its `buildSrc` workarounds are each marked for deletion once fixed upstream.

## Commands

- `./gradlew build`: builds and tests everything. Needs JDK 17+ to run Gradle; toolchains fetch JDK 8, 17, 21 and 25.
- `./gradlew :core:test`: the fast check for core changes.
- `./gradlew :testmod:snapshot -Pargs="<theme> <scale> <tab> <out.png>"`: renders a menu headlessly.
  - Optional trailing args: `[mx my] [clicks]`.
  - Env vars: `SNAPSHOT_TYPE` (text to type after the clicks) and `SNAPSHOT_TOAST="title|message"`.
- Dev clients: run from the target's directory.
  - Forge and NeoForge: `runClient`.
  - Fabric 1.16.5–1.21.1: `testmodRunClient`.
  - Fabric 26.3: `runTestmodClient`.
  - `ICARUS_TEST_THEME=fatality` picks the fatality theme; `ICARUS_TEST_OPEN=1` opens the menu on the title screen with a test toast.

## Traps

- **Forge dev runs can hang at startup in a log4j rollover.** The cause is a 0-byte `<date>-1.log.gz`. Move `run/**/logs` aside first.
- **The NeoForge 26.3 dev client doesn't start.** FML fails with "version is null", a Unimined 2.0.2 bug. The jar still builds; the target is untested in game.
- **26.x input is SDL-based.** Typed characters only arrive while `textInputManager().startTextInput(...)` is active. `IcarusScreen` toggles it from `menu.isTyping()`.
- **26.x rendering is retained-mode.** Each submit needs fresh `GuiElementRenderState` objects. Several draw lists are submitted per frame: HUD, menu, and toasts over the menu.
- **Testmod state persists** in `run/.../config/testmod`. Rename that folder aside when you need reproducible screen coordinates.
- **Fonts are system fonts:**
  - skeet uses Verdana;
  - fatality uses Segoe UI Semibold, Segoe UI and Bahnschrift.

  Snapshots and pixel comparisons are only meaningful where these are installed (Windows).

## Conventions

- Keep core on Java 8 and free of Minecraft classes.
- Use `studio.dreamys.icarus.input.Keys`, which are LWJGL2 codes; adapters translate GLFW and SDL codes to them.
- Themes do all the layout and drawing. Keep a steady frame allocation-free and the menu a single draw call from the shared atlas.
- The theme recreations are measured from reference footage (local `references/`, gitignored). Don't change geometry or colours by eye.
- Releases: pushing a `vX.Y.Z` tag runs `.github/workflows/release.yml`.
  - Release notes go in `.github/release-notes/<tag>.md`.
  - It needs the `MAVEN_REPO_DEPLOY_KEY` secret.
