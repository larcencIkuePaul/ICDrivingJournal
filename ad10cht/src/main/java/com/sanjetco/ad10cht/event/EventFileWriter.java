package com.sanjetco.ad10cht.event;

import android.util.Log;

import com.sanjetco.ad10cht.common.ChtGpsDataPack;
import com.sanjetco.ad10cht.common.DatabaseCommon;
import com.sanjetco.ad10cht.common.MainCommon;
import com.sanjetco.ad10cht.log.LogFileWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by PaulLee on 2016/5/11.
 */
public class EventFileWriter implements 
        MainCommon, 
        DatabaseCommon
{
    static LogFileWriter mLogFileWriter = new LogFileWriter();
    
    public EventFileWriter() {

    }

    public void writeGsensorTrackInfo(String fileName, float[] data, long startTime) {
        try {
            File dir = new File(GSENSOR_PATH);
            if ( !dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Create directory " + GSENSOR_PATH);
            }

            File file = new File(GSENSOR_PATH, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            JSONObject gstrackToJson = new JSONObject();
            gstrackToJson.put("time", String.valueOf(startTime));
            gstrackToJson.put("x_acc", String.valueOf(data[0]));
            gstrackToJson.put("y_acc", String.valueOf(data[1]));
            gstrackToJson.put("z_acc", String.valueOf(data[2]));

            Log.d(TAG, gstrackToJson.toString());

            fos.write(gstrackToJson.toString().concat(",").getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + fileName);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
    
    public void writeAccelerationEvent(String fileName, long startTime, long endTime, float velocity) {
        try {
            File dir = new File(GSENSOR_PATH);
            if ( !dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Create directory " + GSENSOR_PATH);
            }
            
            File file = new File(GSENSOR_PATH, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            JSONObject toJsonObj = new JSONObject();
            toJsonObj.put("continueSeconds", String.valueOf(endTime - startTime));
            toJsonObj.put("startTime", String.valueOf(startTime));
            toJsonObj.put("y_accMax", String.valueOf(velocity));

            Log.d(TAG, toJsonObj.toString());

            fos.write(toJsonObj.toString().concat(",").getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + fileName);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void writeBrakingEvent(String fileName, long startTime, long endTime, float velocity) {
        try {
            File dir = new File(GSENSOR_PATH);
            if ( !dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Create directory " + GSENSOR_PATH);
            }

            File file = new File(GSENSOR_PATH, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            JSONObject toJsonObj = new JSONObject();
            toJsonObj.put("continueSeconds", String.valueOf(endTime - startTime));
            toJsonObj.put("startTime", String.valueOf(startTime));
            toJsonObj.put("y_accMin", String.valueOf(velocity));

            Log.d(TAG, toJsonObj.toString());

            fos.write(toJsonObj.toString().concat(",").getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + fileName);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
    
    public void writeReverseEvent(String fileName, long startTime, long endTime, float velocity) {
        try {
            File dir = new File(GSENSOR_PATH);
            if ( !dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Create directory " + GSENSOR_PATH);
            }

            File file = new File(GSENSOR_PATH, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            JSONObject toJsonObj = new JSONObject();
            toJsonObj.put("continueSeconds", String.valueOf(endTime - startTime));
            toJsonObj.put("startTime", String.valueOf(startTime));
            toJsonObj.put("z_accMin", String.valueOf(velocity));

            Log.d(TAG, toJsonObj.toString());

            fos.write(toJsonObj.toString().concat(",").getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + fileName);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
    
    public void writeSwerveEvent(String fileName, long startTime, long endTime, float velocity) {
        try {
            File dir = new File(GSENSOR_PATH);
            if ( !dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "Create directory " + GSENSOR_PATH);
            }

            File file = new File(GSENSOR_PATH, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            JSONObject toJsonObj = new JSONObject();
            toJsonObj.put("continueSeconds", String.valueOf(endTime - startTime));
            toJsonObj.put("startTime", String.valueOf(startTime));
            toJsonObj.put("x_acc", String.valueOf(velocity));

            Log.d(TAG, toJsonObj.toString());

            fos.write(toJsonObj.toString().concat(",").getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + fileName);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void writeGsensorJournal(String gsensorJournalFileName, String imeiId, String tripId, long startTime, String gsensorTrackFileName, String[] eventFileName, int[] eventCount) {
        JSONObject toJsonObj = new JSONObject();

        try {

            File ofile = new File(GSENSOR_PATH, gsensorJournalFileName);
            FileOutputStream fos = new FileOutputStream(ofile);

            toJsonObj.put("deviceId", imeiId);
            toJsonObj.put("endTime", String.valueOf(System.currentTimeMillis() / 1000));
            toJsonObj.put("gsensorTrack", readGsensorTrackInfoFromFile(gsensorTrackFileName));
            toJsonObj.put("startTime",  String.valueOf(startTime));
            toJsonObj.put("suddenAcceleration", readEventDataFromFile(eventFileName[0], eventCount[0]));
            toJsonObj.put("suddenBraking", readEventDataFromFile(eventFileName[1], eventCount[1]));
            toJsonObj.put("suddenReverse", readEventDataFromFile(eventFileName[2], eventCount[2]));
            toJsonObj.put("suddenSwerve", readEventDataFromFile(eventFileName[3], eventCount[3]));
            toJsonObj.put("tripId", tripId);

            fos.write(toJsonObj.toString().getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + " | " + gsensorJournalFileName + " writeGsensorJournal");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    protected JSONObject readEventDataFromFile(String fileName, int eventCount) {
        JSONObject toJsonObj = new JSONObject();
        try {
            File ifile = new File(GSENSOR_PATH, fileName);
            if (ifile.exists()) {
                FileInputStream fis = new FileInputStream(ifile);
                String jsonArrayString;

                int length = fis.available();
                byte[] buffer = new byte[length];
                fis.read(buffer);
                fis.close();

                jsonArrayString = new String(buffer, "UTF-8");
                Log.d(TAG, "before | " + jsonArrayString);
                // Remove the last "," from jsonArrayString to avoid null object
                jsonArrayString = jsonArrayString.substring(0, jsonArrayString.length() - 1);
                // Add "[ ]" to jsonArrayString to format a legal JSONArray string
                jsonArrayString = "[" + jsonArrayString + "]";
                Log.d(TAG, "after | " + jsonArrayString);

                toJsonObj.put("count", String.valueOf(eventCount));
                toJsonObj.put("data", new JSONArray(jsonArrayString));
            } else {
                toJsonObj.put("count", String.valueOf(0));
                toJsonObj.put("data", new JSONArray());
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + " | " + fileName + " readEventDataFromFile");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return toJsonObj;
    }
    
    protected JSONArray readGsensorTrackInfoFromFile(String fileName) {
        JSONArray toJsonArray = new JSONArray();
        try {
            File ifile = new File(GSENSOR_PATH, fileName);
            if (ifile.exists()) {
                FileInputStream fis = new FileInputStream(ifile);
                String jsonArrayString;

                int length = fis.available();
                byte[] buffer = new byte[length];
                fis.read(buffer);
                fis.close();

                jsonArrayString = new String(buffer, "UTF-8");
                Log.d(TAG, "before | "+ jsonArrayString);
                // Remove the last "," from jsonArrayString to avoid null object
                jsonArrayString = jsonArrayString.substring(0, jsonArrayString.length() - 1);
                // Add "[ ]" to jsonArrayString to format a legal JSONArray string
                jsonArrayString = "[" + jsonArrayString + "]";
                Log.d(TAG, "after | " + jsonArrayString);

                toJsonArray = new JSONArray(jsonArrayString);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + " | " + fileName + " readGsensorTrackInfoFromFile");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return toJsonArray;
    }
    
    public void writeGpsTrackInfo(String fileName, ChtGpsDataPack pack) {
        try {
            File dir = new File(GPS_PATH);
            if ( !dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(GPS_PATH, fileName);
            FileOutputStream fos = new FileOutputStream(file, true);

            JSONObject gpsToJson = new JSONObject();
            gpsToJson.put("altitude", pack.altitude);
            gpsToJson.put("bearing", pack.bearing);
            gpsToJson.put("hdop", pack.hdop);
            gpsToJson.put("lat", pack.lat);
            gpsToJson.put("lon", pack.lon);
            gpsToJson.put("pdop", pack.pdop);
            gpsToJson.put("speed", pack.speed);
            gpsToJson.put("time", pack.time);
            gpsToJson.put("vdop", pack.vdop);

            Log.d(TAG, gpsToJson.toString());

            fos.write(gpsToJson.toString().concat(",").getBytes());
            fos.close();
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + " | " + fileName + " writeGpsTrack");
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }
    
    public boolean writeGpsJournal(String gpsJournalFileName, String gpsTrackFileName, String imeiId, String tripId, long startTime) {
        JSONObject toJsonObj = new JSONObject();
        boolean bGpsTrackExist = false;
        try {
            File ifile = new File(GPS_PATH, gpsTrackFileName);
            if (ifile.exists()) {
                bGpsTrackExist = true;
                File ofile = new File(GPS_PATH, gpsJournalFileName);
                FileInputStream fis = new FileInputStream(ifile);
                FileOutputStream fos = new FileOutputStream(ofile);
                String jsonArrayString;

                int length = fis.available();
                byte[] buffer = new byte[length];
                fis.read(buffer);
                fis.close();

                jsonArrayString = new String(buffer, "UTF-8");
                Log.d(TAG, "before: " + jsonArrayString);
                // Remove the last "," from jsonArrayString to avoid null object
                jsonArrayString = jsonArrayString.substring(0, jsonArrayString.length() - 1);
                // Add "[ ]" to jsonArrayString to format a legal JSONArray string
                jsonArrayString = "[" + jsonArrayString + "]";
                Log.d(TAG, "after: " + jsonArrayString);

                toJsonObj.put("deviceId", imeiId);
                toJsonObj.put("endTime", String.valueOf(System.currentTimeMillis() / 1000));
                toJsonObj.put("gpsTrack", new JSONArray(jsonArrayString));
                toJsonObj.put("segmentId", "end");
                toJsonObj.put("startTime", String.valueOf(startTime));
                toJsonObj.put("tripId", tripId);

                fos.write(toJsonObj.toString().getBytes());
                fos.close();
            } else {
                Log.d(TAG, "No GPS Track file !");
                mLogFileWriter.appendLog("No GPS Track file !");
                bGpsTrackExist = false;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage() + " | " + gpsJournalFileName + " writeGpsJournal");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return bGpsTrackExist;
    }
}