package com.pm.ridepay.exception;

public class AlreadyOnboardedException extends RuntimeException {
    public AlreadyOnboardedException(String message) {
        super(message);
    }
}
