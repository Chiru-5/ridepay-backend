package com.pm.ridepay.exception;

public class RideAlreadyActiveException extends RuntimeException {
    public RideAlreadyActiveException(String message) {
        super(message);
    }
}
