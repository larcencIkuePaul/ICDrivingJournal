package com.sanjetco.ad10cht.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PaulLee on 2016/5/10.
 */
public class ChtGpsDataPack implements Parcelable {

    public double altitude;
    public float bearing;
    public float hdop;
    public double lat;
    public double lon;
    public float pdop;
    public float speed;
    public long time;
    public float vdop;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(altitude);
        dest.writeFloat(bearing);
        dest.writeFloat(hdop);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeFloat(pdop);
        dest.writeFloat(speed);
        dest.writeLong(time);
        dest.writeFloat(vdop);
    }

    public static final Parcelable.Creator<ChtGpsDataPack> CREATOR;

    static {
        CREATOR = new Creator<ChtGpsDataPack>() {

            public ChtGpsDataPack createFromParcel(Parcel source) {
                ChtGpsDataPack pack = new ChtGpsDataPack();

                pack.altitude = source.readDouble();
                pack.bearing = source.readFloat();
                pack.hdop = source.readFloat();
                pack.lat = source.readDouble();
                pack.lon = source.readDouble();
                pack.pdop = source.readFloat();
                pack.speed = source.readFloat();
                pack.time = source.readLong();
                pack.vdop = source.readFloat();

                return pack;
            }

            public ChtGpsDataPack[] newArray(int size) {
                return new ChtGpsDataPack[size];
            }
        };
    }
}
