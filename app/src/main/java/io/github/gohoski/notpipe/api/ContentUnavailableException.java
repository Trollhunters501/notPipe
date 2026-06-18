package io.github.gohoski.notpipe.api;

import java.io.IOException;

/**
 * Created by Gleb on 17.06.2026.
 * Exception thrown when requested content (video, channel, etc.) is unavailable.
 * Unlike network errors, this signals the content genuinely doesn't exist or is private,
 * so the Manager should not switch instances when this is thrown.
 */
public class ContentUnavailableException extends IOException {
    public ContentUnavailableException(String message) {
        super(message);
    }
}
