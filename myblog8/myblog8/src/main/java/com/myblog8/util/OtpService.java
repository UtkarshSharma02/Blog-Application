package com.myblog8.util;

import com.myblog8.config.TwilioConfiguration;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Service
public class OtpService {
    @Autowired
    private TwilioConfiguration twilioConfiguration;

    private Map<String, String> otpMap = new HashMap<>();
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public OtpService() {
        // Schedule a task to clean up expired OTPs every minute
        executorService.scheduleAtFixedRate(this::cleanupExpiredOtps, 1, 1, TimeUnit.MINUTES);
    }

    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        otpMap.put(phoneNumber, otp + "," + System.currentTimeMillis()); // Store creation time

        Twilio.init(twilioConfiguration.getAccountSid(), twilioConfiguration.getAuthToken());

        Message message = Message.creator(
                        new PhoneNumber(phoneNumber),
                        new PhoneNumber(twilioConfiguration.getPhoneNumber()),
                        "Your OTP is: " + otp)
                .create();

        System.out.println("OTP sent to " + phoneNumber);
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        if (otpMap.containsKey(phoneNumber)) {
            String[] otpParts = otpMap.get(phoneNumber).split(",");
            String storedOtp = otpParts[0];
            long otpCreationTime = Long.parseLong(otpParts[1]);
            long currentTimeMillis = System.currentTimeMillis();

            // Check if OTP is expired
            if (isOtpExpired(phoneNumber)) {
                otpMap.remove(phoneNumber);
                return false; // OTP has expired
            }

            return storedOtp.equals(otp);
        }
        return false; // OTP doesn't exist
    }

    public boolean isOtpExpired(String phoneNumber) {
        if (otpMap.containsKey(phoneNumber)) {
            String[] otpParts = otpMap.get(phoneNumber).split(",");
            long otpCreationTime = Long.parseLong(otpParts[1]);
            long currentTimeMillis = System.currentTimeMillis();
            // Adjust the expiration time as needed (5 minutes in this example)
            return (currentTimeMillis - otpCreationTime) > TimeUnit.MINUTES.toMillis(5);
        }
        return true; // OTP doesn't exist, consider it expired
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void cleanupExpiredOtps() {
        long currentTimeMillis = System.currentTimeMillis();
        otpMap.entrySet().removeIf(entry -> {
            String[] otpParts = entry.getValue().split(",");
            long otpCreationTime = Long.parseLong(otpParts[1]);
            // Remove OTPs that are older than 5 minutes (adjust as needed)
            return (currentTimeMillis - otpCreationTime) > TimeUnit.MINUTES.toMillis(1);
        });
    }
}