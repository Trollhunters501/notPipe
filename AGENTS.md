Coding agent instructions for the notPipe project—a YouTube client for legacy Android (1.5+).

## Project Overview

notPipe is a YouTube client targeting Android 1.5 (API 3) and above. It uses multiple APIs (Invidious, yt2009, Piped, YtAPILegacy) with random instance selection to combat YouTube blocks. The app uses Java 1.5 for compatibility with Android <=2.2.

## Build & Test Commands
Please **do not try to use build commands** as they will not work correctly. The user will build the project by themselves.

### Build Environment
- Android Studio 2.3.2 (recommended for Android <2.2 development)
- Gradle 2.3.2, compileSdkVersion 25, targetSdkVersion 25, minSdkVersion 3 (Android 1.5)
- Build Tools 25.0.0

## Code Style Guidelines

### Java Version
- **Use Java 1.5 only** - no diamond operators, try-with-resources, or other Java 6+ features
- Use `ArrayList<Type>()` not `ArrayList<>()`
- Use `String.format()` for string formatting

### Naming Conventions
- **Packages**: `io.github.gohoski.notpipe.*` (lowercase)
- **Classes**: PascalCase (e.g., `VideoAdapter`, `HttpClient`)
- **Methods/Variables**: camelCase (e.g., `getVideoUrl`, `videoInstances`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `USER_AGENT`, `TIMEOUT`)
- **Interfaces**: PascalCase nouns (e.g., `Metadata`, `VideoStream`)

### Imports
- Android imports first (sorted alphabetically)
- Followed by `java.*` imports
- Followed by third-party imports (e.g., `cc.nnproject.json.*`)
- Followed by project imports (`io.github.gohoski.notpipe.*`)
- No wildcard imports

### Formatting
- Indent: 4 spaces (no tabs)
- Line length: ~120 characters
- Opening braces on same line
- Blank line between methods and between import groups

### Javadoc Comments
- Classes should have a Javadoc header:
  ```java
  /**
   * Created by [Name] on [Date].
   * [Description of class purpose]
   */
  ```
- Public methods should have Javadoc documenting parameters and return values
- No inline comments in method bodies unless absolutely necessary

### Error Handling
- Use checked exceptions (`IOException`) for network/IO operations
- Wrap exceptions in result objects for AsyncTask (see `SearchResult` pattern)
- Log errors with `e.printStackTrace()` in background threads
- Show user-friendly errors via `Toast.makeText()`
- Use `IllegalStateException` for programming errors (e.g., Manager not initialized)

### Memory Management
- Clear image caches on `onDestroy()` and `onLowMemory()`
- Call `System.gc()` before opening new activities when appropriate

### API Architecture
- All API implementations implement capability interfaces (`Metadata`, `VideoStream`)
- The `Manager` singleton provides random instance selection via capability
- Use the Manager to get API instances, never instantiate directly in activities

### Network Operations
- Always use `HttpClient` for HTTP requests
- Use `HttpRequest.Builder` for building requests with parameters/bodies
- All network operations must be on background threads (AsyncTask)
- Timeout is defined in `HttpClient.TIMEOUT`

### UI Conventions
- Activities extend `Activity` (not AppCompatActivity for legacy support)
- Use `findViewById()` with explicit casts: `(TextView) findViewById(R.id.view)`
- Use `R.layout.*` for layouts, `R.id.*` for view IDs

### JSON Handling
- Use the bundled NNJSON library (`cc.nnproject.json.*`)
- `JSON.getObject(String)` for parsing JSON objects
- `JSON.getArray(String)` for parsing JSON arrays

### ProGuard
- Release builds use ProGuard with optimization
- Configuration in `app/proguard-rules.pro`

## Project Structure

```
app/src/main/java/io/github/gohoski/notpipe/
├── api/           # API implementations (Invidious, Yt2009, YtApiLegacy)
│   ├── Manager.java    # Singleton managing API instances
│   ├── Metadata.java   # Interface for search/video/comments/popular videos
│   └── VideoStream.java # Interface for video URL retrieval
├── data/          # Data models (Video, VideoInfo, Comment)
├── http/          # HTTP client and utilities
├── ui/            # UI adapters and custom views
└── util/          # Utility classes (ImageLoader)
```

## Important Notes

- Never modify the `cc.nnproject.json` package - it's a bundled library
- SSL certificate verification is disabled for compatibility (`SSLDisabler`)
- The app uses HTTP for most requests to support older Android versions
- Use `NotPipe.SDK` for getting the SDK version of the device (since SDK_INT doesn't exist on 1.5)