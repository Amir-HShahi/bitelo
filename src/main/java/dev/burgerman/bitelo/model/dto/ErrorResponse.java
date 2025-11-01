package dev.burgerman.bitelo.model.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String message;
    private final String path;
    private final String traceId;
    private Map<String, String> fieldErrors;
}