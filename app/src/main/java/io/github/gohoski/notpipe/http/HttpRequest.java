package io.github.gohoski.notpipe.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpRequest {
    private final String baseUrl;
    private final String endpoint;
    private final String method;
    private final Map<String, String> headers;
    private String body = "";

    public HttpRequest(String baseUrl, String endpoint) {
        this(baseUrl, endpoint, "GET");
    }

    public HttpRequest(String urlStr) throws java.net.MalformedURLException {
        this(new URL(urlStr));
    }

    public HttpRequest(URL url) {
        this(
                url.getProtocol() + "://" + url.getAuthority(),
                url.getFile().length() < 1 ? "/" : url.getFile(),
                "GET"
        );
    }

    public HttpRequest(String baseUrl, String endpoint, String method) {
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.method = method;
        this.headers = new HashMap<String, String>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBaseUrl() { return baseUrl; }
    public String getEndpoint() { return endpoint; }
    public String getMethod() { return method; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }

    public static class Builder {
        private final String baseUrl;
        private final String endpoint;
        private final String method;
        private final Map<String, String> params = new HashMap<String, String>();
        private final Map<String, String> headers = new HashMap<String, String>();
        private String body = "";

        public Builder(String baseUrl, String endpoint) {
            this(baseUrl, endpoint, "GET");
        }

        public Builder(String baseUrl, String endpoint, String method) {
            this.baseUrl = baseUrl;
            this.endpoint = endpoint;
            this.method = method;
        }

        public Builder addParam(String key, String value) {
            if (value != null) params.put(key, value);
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            String fullEndpoint = endpoint;
            if (!params.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    queryString.append(first ? "?" : "&");
                    try {
                        queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        queryString.append("=");
                        queryString.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } catch (UnsupportedEncodingException ignored) {}
                    first = false;
                }
                fullEndpoint += queryString.toString();
            }
            HttpRequest request = new HttpRequest(baseUrl, fullEndpoint, method);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
            request.setBody(body);
            return request;
        }
    }
}