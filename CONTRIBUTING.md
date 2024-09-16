#### Contribute

During the commit process, both commit-msg and lint checks are automatically executed. Committing triggers the lint task. If any issues arise, you can manually trigger the lint check with the following command:

```
./gradlew ktlintFormat
```
Ensure your commit message follows this format:
```
[DEV]||[FIX]|[REFACTOR] [commit message]
```

Git hooks are set up to enforce linting and commit message checks. If you encounter any issues with the hooks, you can reinstall them by running:

```
./gradlew installGitHook
```

If you make any changes in the <b>explode</b> module, don’t forget to run
```
cd explode && flutter build aar
```
