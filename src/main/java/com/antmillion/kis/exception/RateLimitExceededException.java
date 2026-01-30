package com.antmillion.kis.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final long nanosToWait;
    private final long retryAfterSeconds;

    public RateLimitExceededException(long nanosToWait) {
        super("KIS API rate limit exceeded. Please retry after " + (nanosToWait / 1_000_000_000) + " seconds");
        this.nanosToWait = nanosToWait;
        this.retryAfterSeconds = Math.max(1, nanosToWait / 1_000_000_000);
    }
}
