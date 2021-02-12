package com.app.yoursingleradio;

public class Config {

    //Ingrese las Url de su Streaming de Audio y Video
    public static final String RADIO_STREAM_URL = "https://eu2.serviaudio.com:10839";
    public static final String TV_STREAM_URL ="https://www.webmedialive.org:1936/FAMILYTVHD/FAMILYTVHD/playlist.m3u8";

    //Acrivar el Uso de Panel de Control
    public static final boolean ENABLE_ADMIN_PANEL = false;
    //Cololar la URL del Panel de Control
    public static final String ADMIN_PANEL_URL = "none";

    //ads configuration
    public static final boolean ENABLE_ADMOB_BANNER_ADS = false;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ADS_ON_LOAD = false;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ADS_ON_DRAWER_SELECTION = false;
    public static final boolean ENABLE_ADMOB_INTERSTITIAL_ON_PLAY = false;
    public static final int ADMOB_INTERSTITIAL_ON_PLAY_INTERVAL = 30;

    //auto play function
    public static final boolean ENABLE_AUTO_PLAY = true;

    //layout customization
    public static final boolean ENABLE_SOCIAL_MENU = true;

    //album art configuration
    public static final boolean ENABLE_ALBUM_ART = true;
    public static final boolean ENABLE_CIRCULAR_IMAGE_ALBUM_ART = true;
    public static final int ALBUM_ART_BORDER_WIDTH = 8;
    public static final int ALBUM_ART_CORNER_RADIUS = 30;

    //when it enabled, the radio will be reloaded and the metadata will immediately be updated
    //there may be a slight delay on play audio streaming when the metadata is updated
    public static final boolean FORCE_UPDATE_METADATA_ON_RESUME = true;

    //splash screen duration in millisecond
    public static final int SPLASH_SCREEN_DURATION = 3000;

}