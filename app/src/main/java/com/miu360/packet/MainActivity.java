package com.miu360.packet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.miu360.packet.data.BroadCastReceiverConstant;
import com.miu360.packet.util.RedPacketUtil;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener
        , View.OnClickListener {
    private static final String TAG = "activity";

    private static final int GET_SERVICE_STATE = 0x0001;

    private Switch switcher;
    private FrameLayout container;

    private MaterialDialog openDialog;
    private MaterialDialog stopDialog;

    private AccessbilityServiceReceiver receiver;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == GET_SERVICE_STATE) {
                if ((Boolean) msg.obj) {
                    switcher.setChecked(true);
                    openDialog.dismiss();
                } else {
                    switcher.setChecked(false);
                    stopDialog.dismiss();
                }
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switcher = (Switch) findViewById(R.id.switcher);
        container = (FrameLayout) findViewById(R.id.container);

        switcher.setOnCheckedChangeListener(this);
        switcher.setOnClickListener(this);

        IntentFilter filter = new IntentFilter(BroadCastReceiverConstant.SERVICE_STOP_RECEIVER_ACTION);
        receiver = new AccessbilityServiceReceiver();
        registerReceiver(receiver, filter);

        initDailog();
    }

    private void initDailog() {
        openDialog = new MaterialDialog(this);
        openDialog.setTitle(getString(R.string.dialog_hint));
        openDialog.setMessage(getString(R.string.dialog_open_message));
        openDialog.setCanceledOnTouchOutside(false);
        openDialog.setNegativeButton(getString(R.string.dialog_cancle), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
                switcher.setChecked(false);
            }
        });
        openDialog.setPositiveButton(getString(R.string.dialog_server_open), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        stopDialog = new MaterialDialog(this);
        stopDialog.setTitle(getString(R.string.dialog_hint));
        stopDialog.setMessage(getString(R.string.dialog_stop_message));
        stopDialog.setCanceledOnTouchOutside(false);
        stopDialog.setNegativeButton(getString(R.string.dialog_cancle), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDialog.dismiss();
                switcher.setChecked(true);
            }
        });
        stopDialog.setPositiveButton(getString(R.string.dialog_server_stop), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.e(TAG, "onCheckedChanged()");

        if (isChecked) {
            container.setBackgroundResource(R.color.color_bg_checked);
        } else {
            container.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == switcher) {
            Log.e(TAG, "onClick()");

            if (switcher.isChecked()) {
                if (!RedPacketUtil.isAccessibilitySettingsOn(this)) {
                    if (!isDestroyed() && !isFinishing()) {
                        openDialog.show();
                    }
                }
            } else {
                if (!isDestroyed() && !isFinishing()) {
                    stopDialog.show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private class AccessbilityServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "receive broadcast.");

            Message message = Message.obtain();

            message.what = GET_SERVICE_STATE;
            message.obj = intent
                    .getBooleanExtra(BroadCastReceiverConstant.SERVICE_IS_CONNECTED_KEY, false);

            handler.sendMessage(message);
        }
    }
}
