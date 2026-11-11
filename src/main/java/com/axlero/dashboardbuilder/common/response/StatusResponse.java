package com.axlero.dashboardbuilder.common.response;

import java.time.Instant;

/** Response body of the public status endpoint. */
public record StatusResponse(String application, String status, Instant timestamp) {
}