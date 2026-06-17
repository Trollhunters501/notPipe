package io.github.gohoski.notpipe.api;

import java.io.IOException;

/**
 * Created by Gleb on 09.01.2026.
 */

public interface VideoStream {
    String getName(); String getHost();

    String getVideoUrl(String id, String quality, int timeout) throws IOException;
}
