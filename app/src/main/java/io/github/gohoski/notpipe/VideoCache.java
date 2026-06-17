package io.github.gohoski.notpipe;

import io.github.gohoski.notpipe.api.Metadata;
import io.github.gohoski.notpipe.api.VideoStream;
import io.github.gohoski.notpipe.data.Video;

/**
 * Created by Gleb on 08.06.2026.
 */

class VideoCache {
    private static String cachedVideoId;
    private static Video cachedVideo;
    private static Metadata cachedMetadataInstance;
    private static String cachedStreamUrl;
    private static String cachedQuality;
    private static VideoStream cachedVideoStreamInstance;

    public static void put(String id, Video video, Metadata metadata) {
        if (id == null) return;
        // If the user navigates to a different video, discard the old stream cache
        if (!id.equals(cachedVideoId)) clearStream();
        cachedVideoId = id;
        cachedVideo = video;
        cachedMetadataInstance = metadata;
    }

    public static void putStream(String id, String url, String quality, VideoStream streamInstance) {
        if (id != null && id.equals(cachedVideoId)) {
            cachedStreamUrl = url;
            cachedQuality = quality;
            cachedVideoStreamInstance = streamInstance;
        }
    }

    public static boolean hasValidMetadata(String id) {
        return id != null && id.equals(cachedVideoId) && cachedVideo != null;
    }

    public static boolean hasValidStream(String id, String quality) {
        return id != null && id.equals(cachedVideoId) &&
                cachedStreamUrl != null && quality != null && quality.equals(cachedQuality);
    }

    public static Video getVideo() { return cachedVideo; }
    public static Metadata getMetadataInstance() { return cachedMetadataInstance; }
    public static String getStreamUrl() { return cachedStreamUrl; }
    public static VideoStream getVideoStreamInstance() { return cachedVideoStreamInstance; }

    public static void clearStream() {
        cachedStreamUrl = null;
        cachedQuality = null;
        cachedVideoStreamInstance = null;
    }

    public static void clearAll() {
        cachedVideoId = null;
        cachedVideo = null;
        cachedMetadataInstance = null;
        clearStream();
    }
}