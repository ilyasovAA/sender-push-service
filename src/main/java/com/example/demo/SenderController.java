package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.FcmOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.HashMap;

/**
 * @author albert
 * @since 05.03.2020.
 */
@RestController
@RequestMapping("/sender")
public class SenderController {


    private String keyPath = "eta24-test-firebase-adminsdk-1d430-dca759764e.json";

    private String dbUrl = "https://eta24-test.firebaseio.com";

    private HashMap<String, DeviceStatus> devices = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {

        InputStream serviceAccount = new ClassPathResource(keyPath).getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(dbUrl)
                .build();

        FirebaseApp.initializeApp(options);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getTest() {
        return "Hello";
    }

    @RequestMapping(method = RequestMethod.POST, path = "registerDevice")
    public void registerDevice(@RequestBody DeviceDto deviceDto) {
        devices.put(deviceDto.getImei(), new DeviceStatus(deviceDto.getImei(), deviceDto.getFcmToken(), false));
    }

    @RequestMapping(method = RequestMethod.POST, path = "starSendPush")
    public void starSendPush(@RequestBody DeviceDto deviceDto) {
        DeviceStatus deviceStatus = devices.get(deviceDto.getImei());
        if (deviceStatus == null) {
            return;
        }

        deviceStatus.setSend(true);
    }

    @RequestMapping(method = RequestMethod.POST, path = "stopSendPush")
    public void stopSendPush(@RequestBody DeviceDto deviceDto) {
        DeviceStatus deviceStatus = devices.get(deviceDto.getImei());
        if (deviceStatus == null) {
            return;
        }

        deviceStatus.setSend(false);
    }

    @Scheduled(fixedRate = 10000)
    private void internalStarSendPush() {
        System.out.println("internalStarSendPush");
        devices.forEach((imei, device) -> {
            if (device.isSend()) {
                sendPush(device.fcmToken);
            }
        });
    }

    public void sendPush(String fcmToken) {
        Message message = Message.builder()
                .putData("action", "testAction").setToken(fcmToken).build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

    }
}


