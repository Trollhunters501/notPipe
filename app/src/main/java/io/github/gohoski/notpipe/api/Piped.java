package io.github.gohoski.notpipe.api;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.nnproject.json.JSON;
import cc.nnproject.json.JSONArray;
import cc.nnproject.json.JSONException;
import cc.nnproject.json.JSONObject;
import io.github.gohoski.notpipe.Utils;
import io.github.gohoski.notpipe.data.Channel;
import io.github.gohoski.notpipe.data.Comment;
import io.github.gohoski.notpipe.data.Video;
import io.github.gohoski.notpipe.data.VideoInfo;
import io.github.gohoski.notpipe.http.HttpClient;
import io.github.gohoski.notpipe.http.HttpRequest;

/**
 * Created by Gleb on 05.06.2026.
 */
class Piped implements Metadata, VideoStream {
    private String baseUrl, proxyUrl;
    Piped(String baseUrl, String proxyUrl) {
        this.baseUrl = baseUrl;
        this.proxyUrl = proxyUrl;
    }

    public String getName() { return "Piped"; }
    public String getHost() {
        return replaceAll(replaceAll(baseUrl, "https://", ""), "http://", "");
    }

    /**
     * Retrieves YouTube Hyped videos via playlists
     */
    @Override
    public List<VideoInfo> getPopularVideos() throws IOException {
        HttpRequest req = new HttpRequest(baseUrl, "/playlists/" + Utils.getHypePlaylist());
        JSONArray arr = JSON.getObject(HttpClient.executeToString(req)).getArray("relatedStreams");
        List<VideoInfo> videos = new ArrayList<VideoInfo>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject j = arr.getObject(i);
            String videoId = j.getString("url").substring(9);
            videos.add(new VideoInfo(videoId, j.getString("title"),
                    proxyUrl + "/vi/" + videoId + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false", j.getString("uploaderName"),
                    "", j.getString("uploaderUrl").substring(9), Utils.formatDuration(j.getInt("duration")),
                    j.getLong("views"), new Date(j.getLong("uploaded"))));
        }
        return videos;
    }


    @Override
    public List<VideoInfo> search(String q) throws IOException {
        HttpRequest req = new HttpRequest.Builder(baseUrl, "/search").addParam("q", q).addParam("filter", "videos").build();
        JSONArray arr = JSON.getObject(HttpClient.executeToString(req)).getArray("items");
        List<VideoInfo> videos = new ArrayList<VideoInfo>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject j = arr.getObject(i);
            String videoId = j.getString("url").substring(9);
            videos.add(new VideoInfo(videoId, j.getString("title"),
                    proxyUrl + "/vi/" + videoId + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false", j.getString("uploaderName"),
                    Utils.parseUrl(proxyUrl, replaceAll(j.getString("uploaderAvatar"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false"),
                    j.getString("uploaderUrl").substring(9), Utils.formatDuration(j.getInt("duration")),
                    j.getLong("views"), new Date(j.getLong("uploaded"))));
        }
        return videos;
    }

    @Override
    public List<String> searchSuggestions(String q) throws IOException {
        HttpRequest req = new HttpRequest.Builder(baseUrl, "/suggestions").addParam("query", q).build();
        JSONArray arr = JSON.getArray(HttpClient.executeToString(req));
        List<String> s = new ArrayList<String>();
        for (int i = 0; i < arr.size(); i++) {
            s.add(arr.getString(i));
        } return s;
    }

    @Override
    public Video getVideo(String id) throws IOException {
        HttpRequest req = new HttpRequest(baseUrl, "/streams/" + id);
        JSONObject json = JSON.getObject(HttpClient.executeToString(req));
        try {
            String error = json.getString("error"), message = json.getString("message");
            Log.e("Piped", error);
            if (error.length() > 0 && !(message.contains("bot") || message.contains("protect") || message.contains("page"))) {
                throw new ContentUnavailableException(message);
            }
        } catch (JSONException ignored) {}
        List<VideoInfo> related = new ArrayList<VideoInfo>();
        JSONArray arr = json.getArray("relatedStreams");
        for (int i = 0; i < arr.size(); i++) {
            JSONObject j = arr.getObject(i);
            String videoId = j.getString("url").substring(9);
            related.add(new VideoInfo(videoId, j.getString("title"),
                    proxyUrl + "/vi/" + videoId + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false", j.getString("uploaderName"),
                    Utils.parseUrl(proxyUrl, replaceAll(j.getString("uploaderAvatar"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false"),
                    j.getString("uploaderUrl").substring(9), Utils.formatDuration(j.getInt("duration")),
                    j.getLong("views"), new Date(j.getLong("uploaded"))));
        }
        JSONArray videoStreams = json.getArray("videoStreams");
        return new Video(id, json.getString("title"), proxyUrl + "/vi/" + id + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false",
                json.getString("uploader"), Utils.parseUrl(proxyUrl, replaceAll(json.getString("uploaderAvatar"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false"),
                json.getString("uploaderUrl").substring(9), json.getInt("duration"), json.getLong("views"),
                new Date(json.getLong("uploaded")), cleanHtml(json.getString("description")), json.getInt("likes"), json.getInt("uploaderSubscriberCount"),
                Utils.parseUrl(proxyUrl, videoStreams.getObject(videoStreams.size()-1).getString("url")), related, null);
    }

    @Override
    public String getVideoUrl(String id, String ignored, int ignored2) throws IOException {
        HttpRequest req = new HttpRequest(baseUrl, "/streams/" + id);
        JSONArray videoStreams = JSON.getObject(HttpClient.executeToString(req)).getArray("videoStreams");
        return Utils.parseUrl(proxyUrl, videoStreams.getObject(videoStreams.size()-1).getString("url"));
    }

    @Override
    public List<Comment> getComments(String id) throws IOException {
        HttpRequest req = new HttpRequest(baseUrl, "/comments/" + id);
        JSONArray arr = JSON.getObject(HttpClient.executeToString(req)).getArray("comments");
        List<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject j = arr.getObject(i);
            comments.add(new Comment(j.getString("author"), Utils.parseUrl(proxyUrl, replaceAll(j.getString("thumbnail"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false"),
                    cleanHtml(j.getString("commentText")), Utils.parseRelativeDate(j.getString("commentedTime")), j.getString("commentorUrl").substring(9)));
        }
        return comments;
    }

    @Override
    public List<VideoInfo> getRelated(String id) throws IOException {
        HttpRequest req = new HttpRequest(baseUrl, "/streams/" + id);
        JSONObject json = JSON.getObject(HttpClient.executeToString(req));
        List<VideoInfo> related = new ArrayList<VideoInfo>();
        JSONArray arr = json.getArray("relatedStreams");
        for (int i = 0; i < arr.size(); i++) {
            JSONObject j = arr.getObject(i);
            String videoId = j.getString("url").substring(9);
            related.add(new VideoInfo(videoId, j.getString("title"),
                    proxyUrl + "/vi/" + videoId + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false", j.getString("uploaderName"),
                    Utils.parseUrl(proxyUrl, replaceAll(j.getString("uploaderAvatar"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false"),
                    j.getString("uploaderUrl").substring(9), Utils.formatDuration(j.getInt("duration")),
                    j.getLong("views"), new Date(j.getLong("uploaded"))));
        }
        return related;
    }

    @Override
    public String getThumbnail(String id) {
        return proxyUrl + "/vi/" + id + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false";
    }

    @Override
    public Channel getChannel(String id) throws IOException {
        if (!id.startsWith("UC")) {
            id = JSON.getArray(HttpClient.executeToString(
                    new HttpRequest.Builder(baseUrl, "/search")
                            .addParam("q", id)
                            .addParam("filter", "channels").build()
            ))
                    .getObject(0).getString("url").substring(9);
        }
        HttpRequest req = new HttpRequest(baseUrl, "/channel/" + id);
        JSONObject json = JSON.getObject(HttpClient.executeToString(req));
        try {
            if (json.getString("error").length() > 0)
                throw new ContentUnavailableException(json.getString("message"));
        } catch (JSONException ignored) {}
        List<VideoInfo> videos = new ArrayList<VideoInfo>();
        JSONArray arr = json.getArray("relatedStreams");
        String thumbnail = Utils.parseUrl(proxyUrl, replaceAll(json.getString("avatarUrl"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false");
        for (int i = 0; i < arr.size(); i++) {
            try {
                JSONObject j = arr.getObject(i);
                String videoId = j.getString("url").substring(9);
                videos.add(new VideoInfo(videoId, j.getString("title"),
                        proxyUrl + "/vi/" + videoId + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false", j.getString("uploaderName"),
                        thumbnail, id, Utils.formatDuration(j.getInt("duration")),
                        j.getLong("views"), new Date(j.getLong("uploaded"))));
            } catch(Exception e) { e.printStackTrace(); }
        }
        return new Channel(json.getString("id"), json.getString("name"), thumbnail,
                Utils.parseUrl(proxyUrl, replaceAll(replaceAll(json.getString("bannerUrl"), "w2560", "w900"), "-rw?host=", "-rj?host=") + "&rewrite=false"),
                json.getString("description"), json.getInt("subscriberCount"), videos);
    }

    @Override
    public String getChannelIcon(String id) throws IOException {
        HttpRequest req = new HttpRequest(baseUrl, "/channel/"+id);
        return Utils.parseUrl(proxyUrl, replaceAll(JSON.getObject(HttpClient.executeToString(req)).getString("avatarUrl"), "-no-rw?host=", "-no-rj?host=") + "&rewrite=false");
    }

    @Override
    public List<VideoInfo> getChannelVideos(String id, int sort) throws IOException {
        List<VideoInfo> videos = new ArrayList<VideoInfo>();
        if (sort != 0) // We can get only latest channel videos
            return videos;
        HttpRequest req = new HttpRequest(baseUrl, "/playlists/UU" + id.substring(2));
        JSONObject json = JSON.getObject(HttpClient.executeToString(req));
        JSONArray arr = json.getArray("relatedStreams");
        for (int i = 0; i < arr.size(); i++) {
            try {
                JSONObject j = arr.getObject(i);
                String videoId = j.getString("url").substring(9);
                videos.add(new VideoInfo(videoId, j.getString("title"),
                        proxyUrl + "/vi/" + videoId + "/mqdefault.jpg?host=i.ytimg.com&rewrite=false", null, null, null,
                        Utils.formatDuration(j.getInt("duration")), j.getLong("views"), new Date(j.getLong("uploaded"))));
            } catch(Exception e) { e.printStackTrace(); }
        }
        return videos;
    }

    /**
     * Helper to parse Piped's HTML description safely and allocation-free.
     */
    private static String cleanHtml(String html) {
        if (html == null) return null;

        StringBuilder sb = new StringBuilder();
        int i = 0;
        int len = html.length();

        while (i < len) {
            int tagStart = html.indexOf('<', i);
            if (tagStart == -1) {
                sb.append(html, i, len); // Efficient append without substring allocation
                break;
            }

            sb.append(html, i, tagStart);

            int tagEnd = html.indexOf('>', tagStart);
            if (tagEnd == -1) {
                sb.append(html, tagStart, len);
                break;
            }

            // Case-insensitive comparisons matching starting tags directly within parent string to avoid allocations
            if (html.regionMatches(true, tagStart, "<br", 0, 3)) {
                sb.append("\n");
                i = tagEnd + 1;
            } else if (html.regionMatches(true, tagStart, "<a", 0, 2)) {
                int closeTagStart = indexOfIgnoreCase(html, "</a>", tagEnd + 1, len);
                if (closeTagStart == -1) {
                    i = tagEnd + 1;
                    continue;
                }

                String href = extractAttribute(html, tagStart, tagEnd + 1, "href");
                String innerText = html.substring(tagEnd + 1, closeTagStart);

                // Recursively clean inner text
                innerText = cleanHtml(innerText);

                if (href != null && href.length() > 0) {
                    href = decodeHtmlEntities(href);

                    boolean isHashtag = href.contains("/hashtag/");
                    boolean isTimestamp = (href.contains("youtube.com") || href.contains("youtu.be"))
                            && (href.contains("t=") || href.contains("time_continue="));

                    if (isHashtag || isTimestamp) {
                        sb.append(innerText);
                    } else {
                        String cleanInnerText = innerText.trim();
                        if (isUrlOrTruncated(cleanInnerText)) {
                            sb.append(href);
                        } else {
                            sb.append(cleanInnerText).append(" (").append(href).append(")");
                        }
                    }
                } else {
                    sb.append(innerText);
                }

                i = closeTagStart + 4; // Skip past </a>
            } else {
                i = tagEnd + 1; // Strip other html tags
            }
        }

        return decodeHtmlEntities(sb.toString());
    }

    /**
     * Extracts an attribute from within a bounded HTML tag string, allocation-free.
     */
    private static String extractAttribute(String html, int start, int end, String attr) {
        String attrMatch = attr + "=";
        int idx = indexOfIgnoreCase(html, attrMatch, start, end);
        if (idx == -1) return null;

        int valStart = idx + attrMatch.length();
        if (valStart >= end) return null;

        char quote = html.charAt(valStart);
        if (quote == '"' || quote == '\'') {
            int valEnd = -1;
            for (int j = valStart + 1; j < end; j++) {
                if (html.charAt(j) == quote) {
                    valEnd = j;
                    break;
                }
            }
            if (valEnd != -1) {
                return html.substring(valStart + 1, valEnd);
            }
        } else {
            int valEnd = valStart;
            while (valEnd < end) {
                char c = html.charAt(valEnd);
                if (c == ' ' || c == '>') {
                    break;
                }
                valEnd++;
            }
            return html.substring(valStart, valEnd);
        }
        return null;
    }

    /**
     * Determines if the anchor's inner text is a literal representation of a URL.
     */
    private static boolean isUrlOrTruncated(String text) {
        String lower = text.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("www.")) {
            return true;
        }
        if (text.contains(".") && !text.contains(" ")) {
            return true;
        }
        if (text.contains("...")) {
            int dotDotDot = text.indexOf("...");
            String prefix = lower.substring(0, dotDotDot);
            if (prefix.contains(".") || prefix.contains("/")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Safe HTML entity decoding using highly efficient custom replaceAll implementation.
     */
    private static String decodeHtmlEntities(String text) {
        if (text == null) return null;
        text = replaceAll(text, "&amp;", "&");
        text = replaceAll(text, "&nbsp;", " ");
        text = replaceAll(text, "&lt;", "<");
        text = replaceAll(text, "&gt;", ">");
        text = replaceAll(text, "&apos;", "'");
        text = replaceAll(text, "&quot;", "\"");
        return text;
    }

    /**
     * High-performance, memory-friendly non-regex replacement engine for Java 1.5+.
     * Resolves major performance penalties encountered in legacy standard String.replace.
     */
    private static String replaceAll(String src, String target, String replacement) {
        if (src == null || target == null || replacement == null || target.length() == 0) {
            return src;
        }
        int idx = src.indexOf(target);
        if (idx == -1) {
            return src; // Match not found: prevents instantiation of unnecessary StringBuilder
        }
        StringBuilder sb = new StringBuilder(src.length());
        int len = target.length();
        int prev = 0;
        while (idx != -1) {
            sb.append(src, prev, idx); // Appends characters directly without creating substring objects
            sb.append(replacement);
            prev = idx + len;
            idx = src.indexOf(target, prev);
        }
        sb.append(src, prev, src.length());
        return sb.toString();
    }

    /**
     * Allocation-free Case-Insensitive search utility within specified boundaries.
     */
    private static int indexOfIgnoreCase(String src, String target, int start, int end) {
        int targetLen = target.length();
        int max = end - targetLen;
        for (int i = start; i <= max; i++) {
            if (src.regionMatches(true, i, target, 0, targetLen)) {
                return i;
            }
        }
        return -1;
    }
}