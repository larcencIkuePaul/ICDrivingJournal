package com.sanjetco.ad10cht.ui;

import android.app.Activity;
import android.widget.TextView;

import com.sanjetco.ad10cht.R;

/**
 * Created by PaulLee on 2016/4/26.
 */
public class UpdateUI {

    Activity mActivity;
    TextView mXAxisValue, mYAxisValue, mZAxisValue;
    TextView mSysEventMsg;

    public UpdateUI(Activity activity) {
        mActivity = activity;
    }

    public void initUI() {
        mXAxisValue = (TextView) mActivity.findViewById(R.id.x_axis_value);
        mYAxisValue = (TextView) mActivity.findViewById(R.id.y_axis_value);
        mZAxisValue = (TextView) mActivity.findViewById(R.id.z_axis_value);
        mSysEventMsg = (TextView) mActivity.findViewById(R.id.system_event_message);
    }

    public void updateAccelValue(float[] data) {
        mXAxisValue.setText(String.format("%+.2f", data[0]));
        mYAxisValue.setText(String.format("%+.2f", data[1]));
        mZAxisValue.setText(String.format("%+.2f", data[2]));
    }

    public void updateSystemMessage(String message) {
        //mSysEventMsg.setText(message);
        mSysEventMsg.append("\r\n" + message);
    }
}
