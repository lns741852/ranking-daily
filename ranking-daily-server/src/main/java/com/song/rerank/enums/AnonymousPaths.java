package com.song.rerank.enums;

public enum AnonymousPaths {

    HTML("/**.html"),
    CSS("/**.css"),
    JS("/**.js"),
    WEBSOCKET("/webSocket/**"),
    FILE("/file/**"),
    SWAGGER_UI("/swagger-ui.html"),
    SWAGGER_RESOURCES("/swagger-resources/**"),
    USER_LOGIN("/api/user/login");

    private final String path;

    AnonymousPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
