package io.github.gohoski.notpipe.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by opencode on 15.02.2026.
 * Data class holding user configuration settings.
 * Uses Java 1.5 compatible getters/setters (no Lombok/data classes).
 */
public class Config {

    private List<String> invidiousInstances;
    private List<String> yt2009Instances;
    private List<String> pipedInstances;
    private List<String> ytApiLegacyInstances;
    private String preferredQuality;
    private boolean updateInstancesFromUrl;
    private String instancesUpdateUrl;
    private int updateFrequency;
    private boolean useExternalPlayer;
    private boolean streamPlayback;
    private long lastUpdate;
    private boolean convertVideos;
    private int convertCodec;
    private boolean asyncSetVideoUri;
    private boolean fullscreenRotateLandscape;

    Config() {
        invidiousInstances = new ArrayList<String>();
        yt2009Instances = new ArrayList<String>();
        pipedInstances = new ArrayList<String>();
        ytApiLegacyInstances = new ArrayList<String>();
        fullscreenRotateLandscape = true;
    }

    /**
     * Copy constructor for creating a defensive copy.
     */
    public Config(Config other) {
        invidiousInstances = new ArrayList<String>(other.invidiousInstances);
        yt2009Instances = new ArrayList<String>(other.yt2009Instances);
        pipedInstances = new ArrayList<String>(other.pipedInstances);
        ytApiLegacyInstances = new ArrayList<String>(other.ytApiLegacyInstances);
        preferredQuality = other.preferredQuality;
        updateInstancesFromUrl = other.updateInstancesFromUrl;
        instancesUpdateUrl = other.instancesUpdateUrl;
        updateFrequency = other.updateFrequency;
        useExternalPlayer = other.useExternalPlayer;
        streamPlayback = other.streamPlayback;
        lastUpdate = other.lastUpdate;
        convertVideos = other.convertVideos;
        convertCodec = other.convertCodec;
        asyncSetVideoUri = other.asyncSetVideoUri;
        fullscreenRotateLandscape = other.fullscreenRotateLandscape;
    }

    public List<String> getInvidiousInstances() {
        return invidiousInstances;
    }

    public void setInvidiousInstances(List<String> instances) {
        this.invidiousInstances = instances;
    }

    public List<String> getYt2009Instances() {
        return yt2009Instances;
    }

    public void setYt2009Instances(List<String> instances) {
        this.yt2009Instances = instances;
    }

    public List<String> getPipedInstances() {
        return pipedInstances;
    }

    public void setPipedInstances(List<String> instances) {
        this.pipedInstances = instances;
    }

    public List<String> getYtApiLegacyInstances() {
        return ytApiLegacyInstances;
    }

    public void setYtApiLegacyInstances(List<String> instances) {
        this.ytApiLegacyInstances = instances;
    }

    public String getPreferredQuality() {
        return preferredQuality;
    }

    public void setPreferredQuality(String quality) {
        this.preferredQuality = quality;
    }

    public boolean isUpdateInstancesFromUrl() {
        return updateInstancesFromUrl;
    }

    public void setUpdateInstancesFromUrl(boolean update) {
        this.updateInstancesFromUrl = update;
    }

    public String getInstancesUpdateUrl() {
        return instancesUpdateUrl;
    }

    public void setInstancesUpdateUrl(String url) {
        this.instancesUpdateUrl = url;
    }

    public int getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(int frequency) {
        this.updateFrequency = frequency;
    }

    public boolean isUseExternalPlayer() {
        return useExternalPlayer;
    }

    public void setUseExternalPlayer(boolean external) {
        this.useExternalPlayer = external;
    }

    public boolean isStreamPlayback() {
        return streamPlayback;
    }

    public void setStreamPlayback(boolean stream) {
        this.streamPlayback = stream;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isConvertVideos() {
        return convertVideos;
    }

    public void setConvertVideos(boolean convertVideos) {
        this.convertVideos = convertVideos;
    }

    public int getConvertCodec() {
        return convertCodec;
    }

    public void setConvertCodec(int convertCodec) {
        this.convertCodec = convertCodec;
    }

    public boolean isAsyncSetVideoUri() {
        return asyncSetVideoUri;
    }

    public void setAsyncSetVideoUri(boolean asyncSetVideoUri) {
        this.asyncSetVideoUri = asyncSetVideoUri;
    }

    public boolean isFullscreenRotateLandscape() {
        return fullscreenRotateLandscape;
    }

    public void setFullscreenRotateLandscape(boolean rotate) {
        this.fullscreenRotateLandscape = rotate;
    }

    /**
     * Add default instance. Called by ConfigManager.ensureInstancesConfigured()
     * when all instance lists are empty.
     */
    public void applyDefaults() {
        ytApiLegacyInstances.add("http://45.132.96.44:2823");

        updateInstancesFromUrl = true;
        instancesUpdateUrl = "http://144.31.189.129/notPipe.json";
        updateFrequency = 1;
        streamPlayback = true;
        fullscreenRotateLandscape = true;
    }
}