package com.sanjetco.ad10cht.httpclient;

import android.util.Log;

import com.sanjetco.ad10cht.authentication.ChtUbiAuthentication;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.log.LogFileWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by PaulLee on 2016/5/10.
 * CHT
 */
public class ChtHttpConnection implements MainCommon {

    static final String APIKEY = "rNbvc5kYs5wXa6QNv6aOsYSDGMN9WFdd";
    static final String SECKEY = "dAm97bcStXvQdnvz9Vh4pn85OWymVzAw";
    LogFileWriter mLogFileWriter = new LogFileWriter();

    public int sendDataToCht(byte[] data, String fullPath, String subPath) {

        int result = 0;
        String method = "POST";

        try {
            URL url = new URL(fullPath);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

            httpUrlConn.setConnectTimeout(3000);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setRequestMethod(method);
            httpUrlConn.setUseCaches(false);

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyy HH:mm:ss zzz");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date currentDate = new Date(System.currentTimeMillis());

            String XRestDate = sdf.format(currentDate);
            XRestDate = XRestDate.substring(0, XRestDate.length() - 6);
            //Log.d(TAG, "X-Rest-Date: " + XRestDate);

            String sigData = method+subPath+XRestDate;
            //Log.d(TAG, "sigData: " + sigData);
            String signature = new ChtUbiAuthentication().generateSignature(SECKEY, sigData);
            signature = APIKEY + ":" + signature;

            //Log.d(TAG, "Signature: " + signature);

            httpUrlConn.setRequestProperty("Content-Type", "application/json");
            httpUrlConn.setRequestProperty("Authorization", signature);
            httpUrlConn.addRequestProperty("X-Rest-Date", XRestDate);
            //Log.d(TAG, "Authorization: " + httpUrlConn.getRequestProperty("Authorization"));

            BufferedOutputStream bos = new BufferedOutputStream(httpUrlConn.getOutputStream());
            bos.write(data);
            bos.flush();
            Log.d(TAG, "Write out data OK");

            int responseCode = httpUrlConn.getResponseCode();
            result = responseCode;
            String responseMessage = readInputStreamToString(httpUrlConn, responseCode);

            Log.d(TAG, "Response Code: " + responseCode);
            Log.d(TAG, "Response Message: " + responseMessage);

            mLogFileWriter.appendLog("Response Code: " + responseCode);
            mLogFileWriter.appendLog("Response Message: " + responseMessage);

            if (bos != null)
                bos.close();

        } catch (MalformedURLException e) {
            Log.d(TAG, e.getMessage());
            mLogFileWriter.appendLog(e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            mLogFileWriter.appendLog(e.getMessage());
        }

        return result;
    }

    protected String readInputStreamToString(HttpURLConnection httpURLConnection, int responseCode) {

        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            if (responseCode >= 400) {
                is = new BufferedInputStream( httpURLConnection.getErrorStream() );
            } else {
                is = new BufferedInputStream( httpURLConnection.getInputStream() );
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            while (( inputLine = br.readLine() ) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();

        } catch (IOException e) {
            Log.d(TAG, "Error read InputStream");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.d(TAG, "Error closing InputStream");
                }
            }
        }
        return result;
    }
}
