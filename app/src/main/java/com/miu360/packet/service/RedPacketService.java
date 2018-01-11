package com.miu360.packet.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.miu360.packet.data.BroadCastReceiverConstant;
import com.miu360.packet.data.WechatConstant;
import com.miu360.packet.util.RedPacketUtil;

/**
 * 作者：wanglei on 2018/1/8.
 * 邮箱：forwlwork@gmail.com
 */

public class RedPacketService extends AccessibilityService {
    private static final String TAG = "service";

    @Override
    protected void onServiceConnected() {
        Log.e(TAG, "onServiceConnected");

        Intent intent = new Intent(BroadCastReceiverConstant.SERVICE_STOP_RECEIVER_ACTION);
        intent.putExtra(BroadCastReceiverConstant.SERVICE_IS_CONNECTED_KEY, true);
        sendBroadcast(intent);

        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {

            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.e(TAG, "notification");

                if (RedPacketUtil.isRedPacket(this, event.getText())) {
                    Log.e(TAG, "receive red packet.");

                    if (event.getParcelableData() != null
                            && event.getParcelableData() instanceof Notification) {
                        Log.e(TAG, "open wechat.");

                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent intent = notification.contentIntent;

                        try {
                            intent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();

                Log.e(TAG, "window state change , the class name is " + className);

                switch (className) {
                    case WechatConstant.LAUNCHER_UI:
                        RedPacketUtil.findRedPacket(getRootInActiveWindow());
                        break;

                    case WechatConstant.LUCKY_MONEY_RECEIVE_UI:
                        RedPacketUtil.openRedPacket(getRootInActiveWindow());
                        break;

                    case WechatConstant.LUCKY_MONEY_DETAIL_UI:
                        RedPacketUtil.closeRedPacketDetailById(getRootInActiveWindow());
                        break;

                    default:
                        break;
                }

                break;

            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.e(TAG, "window content scrolled.");
                if (event.getSource() != null) {
                    Log.e(TAG, "source is " + event.getSource().toString());
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt()");
    }

    @Override
    public boolean onUnbind(Intent i) {
        Log.e(TAG, "onUnbind()");

        Intent intent = new Intent(BroadCastReceiverConstant.SERVICE_STOP_RECEIVER_ACTION);
        intent.putExtra(BroadCastReceiverConstant.SERVICE_IS_CONNECTED_KEY, false);
        sendBroadcast(intent);

        return super.onUnbind(i);
    }
}
