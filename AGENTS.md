# Fan Remote — contributor guide

## Orient and scope

Read the README and affected code before editing. This is one Android-only Kotlin/Compose application module, one fan profile and a dark-only UI. Use the project’s command vocabulary. Research platform facts; ask only about unresolved product choices.

Apply YAGNI: preserve correctness and useful refactoring; introduce abstractions for demonstrated complexity, not anticipated growth. Keep routine changes local. Delegate only independent review/research or genuinely parallel work, with explicit file ownership. Audits produce findings; authorized implementation includes relevant verification.

## Ownership and conventions

- `ir/` owns commands, pure pulse encoding and Android hardware access. `IrProtocol` must remain independent of Android APIs.
- `ui/` owns composables, semantic theme colors and lifecycle-owned screen state. Use Material 3 controls and Android string resources. Keep the main screen to recognizable keys and minimal labels; no enclosing remote-shaped panel, decorative header or routine status prose. Errors appear only when needed. Respect system insets, font scaling and 48 dp minimum touch targets.
- The ViewModel accepts taps on the main thread and rejects overlaps. Hardware work stays off the main thread. Never transmit during composition, initialization, restoration or intent handling. Do not present an inferred fan state as confirmed.
- Kotlin files use PascalCase matching their main type or responsibility; Android resources use snake_case. Use descriptive names and explicit imports. Run the configured KotlinLang formatter instead of hand-maintaining formatting rules.
- Comments explain non-obvious rationale, invariants or external constraints. Use `//` sentences near the relevant code; use KDoc only for API contracts worth documenting. Start with a capital and finish prose with punctuation. Do not narrate obvious code or append task history. Update comments when behavior changes.

## Verification and runtime ownership

Use the checked-in Gradle wrapper with JDK 17. Run formatting checks, affected unit tests, debug build and Android lint. Test changes to command values, frame counts, bit order and timing against independent expected values. Physical fan behavior requires user observation; build success and completed API calls are different evidence. Keep meaningful regression tests in the repository.

Keep one-worker/in-process Kotlin limits in `gradle.properties`. Use the README’s 1536 MiB systemd ceiling with swap disabled on this workstation. Do not start emulators or parallel builds by default. Diagnose memory failures before increasing limits. Stop only captured processes or named services owned by the task. Device testing is coordinated with the user; never trigger an unexpected fan command.

## Documentation and Git

Keep setup, commands, compatibility and release instructions in the README; local protocol rationale belongs beside the code. Avoid redundant documents and automatic memory stores. Report inspected, built, tested and physically observed behavior separately, including material limitations. No CI or hooks are configured; local checks are required, and absent CI is not passing CI.

Keep SDK paths, keys, passwords, caches and generated artifacts untracked. Check ignore rules before storing secrets. Do not remove third-party notices. Do not add a license or publish without the owner’s decision.

Branches use `<type>/<short-kebab-slug>`. Commit and PR titles use `type(scope): imperative summary`, lowercase, no trailing period. Types: `feat`, `fix`, `refactor`, `perf`, `docs`, `chore`, `test`, `build`, `ci`. Prefer one coherent change per commit/PR. Describe why behavior matters, then relevant validation. Humans merge; agents do not push to main or rewrite shared history without explicit authorization.
