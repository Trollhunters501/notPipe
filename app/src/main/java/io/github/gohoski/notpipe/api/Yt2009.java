package io.github.gohoski.notpipe.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.gohoski.notpipe.Utils;
import io.github.gohoski.notpipe.data.VideoInfo;
import io.github.gohoski.notpipe.http.HttpClient;
import io.github.gohoski.notpipe.http.HttpRequest;

/**
 * Implementation of yt2009's video endpoints
 * yt2009 isn't quite designed to be used as an API—it's more a frontend, however the video endpoints and HTML are straightforward
 */
 class Yt2009 implements VideoStream, Conversion, ChannelApi {
    private String baseUrl;

    Yt2009(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getName() { return "yt2009"; }
    public String getHost() {
        return baseUrl.replace("https://", "").replace("http://", "");
    }

    @Override
    public String getVideoUrl(String id, String quality, int timeout) throws IOException {
        String url;
        switch(quality) {
            case "480":
                url = "/get_480?video_id=" + id; break;
            case "720":
                url = "/exp_hd?video_id=" + id; break;
            case "1080":
                url = "/exp_hd?video_id=" + id + "&fhd=1"; break;
            default:
                url = "/channel_fh264_getvideo?v=" + id;
        }
        // We are requesting and getting the redirect URL manually to use our own User-Agent, since yt2009
        // tries to convert videos to H.264 Contrained Baseline for Android <3.0 devices based on the User-Agent,
        // which we don't actually want as it leads to 1. slowness
        // 2. useless conversion to Contrained Baseline since user's either device supports Main or video is converted using getConvUrl
        // 3. Contrained Baseline doesn't actually fix the codec problems on most Android devices with them
        // 4. This also automatically checks if the instance is dead
        return HttpClient.getRedirectUrl(baseUrl, url, timeout);
    }

    @Override
    public String getConvUrl(String id, int codec) throws IOException {
        return HttpClient.getRedirectUrl(baseUrl, (codec == 1 ? "/http_3gp?v=" : "/http_mpeg4?v=") + id, HttpClient.CONVERSION_TIMEOUT);
    }

    @Override
    public List<VideoInfo> getChannelVideos(String id, int sort) throws IOException {
        HttpRequest req = new HttpRequest.Builder(baseUrl, "/channel_sort")
                .addHeader("source",id)
                .addHeader("sort", sort == 1 ? "popularity" : "date").build();
        String html = HttpClient.executeToString(req);
        List<VideoInfo> videos = new ArrayList<VideoInfo>();
        Pattern pattern = Pattern.compile(
                "<div\\s+class=\"playnav-item playnav-video\\s*\"\\s+id=\"playnav-video-([a-zA-Z0-9_-]+)\".*?" +
                        "<span\\s+class=\"video-title-[a-zA-Z0-9_-]+\">([^<]+)</span>.*?" +
                        "<div\\s+class=\"metadata video-meta-[a-zA-Z0-9_-]+\">([^<]+)</div>",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String videoId = matcher.group(1);
            String title = matcher.group(2);
            String rawMetadata = matcher.group(3);

            long views = -1;
            Date date = null;

            if (rawMetadata != null) {
                int delimiterIndex = rawMetadata.indexOf(" - ");
                if (delimiterIndex != -1) {
                    views = Long.parseLong(rawMetadata.substring(0, delimiterIndex).replaceAll("[^0-9]", ""));
                    date = Utils.parseRelativeDate(rawMetadata.substring(delimiterIndex + 3));
                } else {
                    views = Long.parseLong(rawMetadata.replaceAll("[^0-9]", ""));
                }
            }

            videos.add(new VideoInfo(videoId, title.trim(), baseUrl + "/thumb_proxy?v=" + videoId, "", "", "", null, views, date));
        }
        return videos;
    }
}
