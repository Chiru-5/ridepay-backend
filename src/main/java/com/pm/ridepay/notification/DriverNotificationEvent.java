package com.pm.ridepay.notification;

public record DriverNotificationEvent(Long driverId, String title, String body) {
}
