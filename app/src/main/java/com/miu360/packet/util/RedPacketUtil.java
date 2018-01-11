package com.miu360.packet.util;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.miu360.packet.R;
import com.miu360.packet.base.App;
import com.miu360.packet.data.WechatConstant;
import com.miu360.packet.service.RedPacketService;

import java.util.List;

/**
 * 作者：wanglei on 2018/1/8.
 * 邮箱：forwlwork@gmail.com
 */

public class RedPacketUtil {
    private static final String TAG = "RedPacketUtil";

    /**
     * 检查通知栏的通知是否包含有 '[微信红包]' 字符串，以此来确定该通知是否为红包通知
     *
     * @param context 上下文
     * @param content 通知栏的内容
     * @return 是否为微信红包
     */
    public static boolean isRedPacket(Context context, List<CharSequence> content) {
        for (CharSequence charSequence : content) {
            Log.e(TAG, charSequence.toString());

            if (!TextUtils.isEmpty(charSequence)
                    && charSequence.toString().contains(context.getString(R.string.wechat_red_packet))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 找到微信红包所在的LinearLayout并点击
     *
     * @param rootInfo 微信红包所在页面的根节点信息
     */
    public static void findRedPacket(AccessibilityNodeInfo rootInfo) {
        Log.e(TAG, rootInfo.toString());

        List<AccessibilityNodeInfo> infos = rootInfo
                .findAccessibilityNodeInfosByViewId(WechatConstant.RECEIVE_RED_PACKET_CLICK_LINEARLAYOUT_ID);

        if (infos != null) {
            //找到红包
            for (AccessibilityNodeInfo info : infos) {
                Log.e(TAG, "the red packet view message is " + info.toString());

                if (redPacketEnable(info)) {
                    //点击领取红包
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    /**
     * 判断该红包是否可以拆
     * 过期红包和已领取的红包不用拆
     *
     * @param linearLayoutNodeInfo 红包所在的LinearLayout节点
     * @return 红包是否可拆
     */
    private static boolean redPacketEnable(AccessibilityNodeInfo linearLayoutNodeInfo) {
        List<AccessibilityNodeInfo> infos = linearLayoutNodeInfo
                .findAccessibilityNodeInfosByText(App.self.getString(R.string.receive_red_packet));

        return infos != null && infos.size() > 0;
    }

    /**
     * 拆红包
     *
     * @param rootInfo 拆红包所在页面的根节点信息
     */
    public static void openRedPacket(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> infos = rootInfo
                .findAccessibilityNodeInfosByViewId(WechatConstant.OPEN_RED_PACKET_CLICK_BUTTON_ID);

        if (infos != null) {
            for (AccessibilityNodeInfo info : infos) {
                if (info.isClickable()) {
                    //拆红包
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    /**
     * 关闭领取红包过后的红包详情界面
     * 通过控件层级来找到页面来关闭
     *
     * @param rootInfo 红包详情界面的根节点
     * @deprecated 最好通过id来找到控件并关闭
     * {@link com.miu360.packet.util.RedPacketUtil#closeRedPacketDetailById(AccessibilityNodeInfo)}
     */
    public static void closeRedPacketDetail(AccessibilityNodeInfo rootInfo) {
        AccessibilityNodeInfo fristNodeInfo = rootInfo.getChild(0);

        if (fristNodeInfo != null && fristNodeInfo.getChildCount() > 0) {
            for (int i = 0; i < fristNodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo itemNodeInfo = fristNodeInfo.getChild(i);

                if (itemNodeInfo.getClassName().equals("android.widget.LinearLayout")
                        && itemNodeInfo.isClickable()) {
                    //退出红包详情页面
                    itemNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    /**
     * 关闭领取红包过后的红包详情界面
     * 通过控件id来找到控件再关闭页面
     *
     * @param rootInfo 红包详情界面的根节点
     */
    public static void closeRedPacketDetailById(AccessibilityNodeInfo rootInfo) {
        List<AccessibilityNodeInfo> infos = rootInfo
                .findAccessibilityNodeInfosByViewId(WechatConstant.RED_DETAIL_CLICK_BACK_LINEARLAYOUT_ID);

        if (infos != null) {
            for (AccessibilityNodeInfo info : infos) {
                if (info.isEnabled() && info.isClickable()) {
                    //退出红包详情页面
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
    }

    /**
     * 判断抢红包的辅助服务是否开启
     *
     * @param context 上下文
     * @return true 已开启 , false 未开启
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int enabled = 0;

        String redPacketServicePath = context.getPackageName()
                + "/"
                + RedPacketService.class.getCanonicalName();

        Log.e(TAG, "service path is " + redPacketServicePath);

        try {
            enabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver()
                    , Settings.Secure.ACCESSIBILITY_ENABLED);

            Log.e(TAG, "enabled is " + enabled);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');

        if (enabled == 1) {
            Log.e(TAG, "accessibility service enabled.");

            String value = Settings.Secure.getString(context.getApplicationContext().getContentResolver()
                    , Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (!TextUtils.isEmpty(value)) {
                splitter.setString(value);

                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();

                    Log.e(TAG, "all accessibility  service is " + accessibilityService);

                    if (redPacketServicePath.equals(accessibilityService)) {
                        return true;
                    }
                }
            }
        } else {
            Log.e(TAG, "accessibility service not enabled.");
        }

        return false;
    }
}
