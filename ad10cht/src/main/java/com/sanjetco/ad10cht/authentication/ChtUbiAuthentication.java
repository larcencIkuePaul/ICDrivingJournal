package com.sanjetco.ad10cht.authentication;

import android.util.Base64;
import android.util.Log;

import com.sanjetco.ad10cht.common.MainCommon;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by PaulLee on 2016/5/10.
 */
public class ChtUbiAuthentication implements MainCommon {

    public String generateSignature(String secretKey, String data) {
        String result = "";
        try {
            SecretKey signingKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = new String(Base64.encodeToString(rawHmac, Base64.DEFAULT));

        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage());
        } catch (InvalidKeyException e) {
            Log.d(TAG, e.getMessage());
        }
        return result;
    }
}
