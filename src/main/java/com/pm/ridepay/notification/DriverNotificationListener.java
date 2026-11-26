package com.pm.ridepay.notification;


import com.google.firebase.messaging.*;
import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverNotificationListener {
    private static final String CHANNEL_ID = "ridepay_payments";
    private final DriverRepository driverRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDriverNotification(DriverNotificationEvent event){
        Driver driver = driverRepository.findById(event.driverId()).orElse(null);
        if(driver == null || driver.getFcmToken() == null){
            return;
        }

        Message message= Message.builder()
                .setToken(driver.getFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(event.title())
                        .setBody(event.body())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setChannelId(CHANNEL_ID)
                                .build())
                        .build())
                .build();


        try {
            FirebaseMessaging.getInstance().send(message);
        }catch (FirebaseMessagingException e){
            if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED){
                // If driver logout the phone will delete the token
                driver.setFcmToken(null);
                driverRepository.save(driver);
            } else {
                log.warn("Push to driver {} failed", driver.getId(), e);
            }
        } catch (Exception e) {
            log.warn("Push to driver {} failed", driver.getId(), e);
        }
    }
}
