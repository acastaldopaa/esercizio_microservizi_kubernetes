package it.paa.ordini.response;

import java.time.Instant;

public abstract class ApiResponse {
    public String timestamp;

    public ApiResponse() {
        this.timestamp = Instant.now().toString();
    }
}