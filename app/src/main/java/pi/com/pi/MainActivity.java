package pi.com.pi;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import pi.com.pi.base.BaseActivity;
import pi.com.pi.ui.WiFiSettingActivity;
import pi.com.pi.wifi.WiFiApManager;

public class MainActivity extends BaseActivity {
    private EditText device_typeid, device_subtypeid, device_code, ssid_id;
    private TextView logger;
    private ScrollView scrollView;
    private String ssid = "HET_00";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onView() {
        device_typeid = (EditText) findViewById(R.id.device_typeid);
        device_subtypeid = (EditText) findViewById(R.id.device_subtypeid);
        device_code = (EditText) findViewById(R.id.device_code);
        ssid_id = (EditText) findViewById(R.id.ssid_id);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        logger = (TextView) findViewById(R.id.logger);
    }

    @Override
    protected void onDataInit() {
    }

    private boolean createSsid(){
        String deviceTypeId = device_typeid.getText().toString();
        String deviceSubTypeId = device_subtypeid.getText().toString();
        String deviceCode = device_code.getText().toString();
        if (TextUtils.isEmpty(deviceTypeId)) {
            tips("请输入设备大类");
            return false;
        }
        if (TextUtils.isEmpty(deviceSubTypeId)) {
            tips("请输入设备小类");
            return false;
        }
        if (TextUtils.isEmpty(deviceCode)) {
            tips("请输入设备编码");
            return false;
        }

        String a = Integer.toHexString(Integer.parseInt(deviceTypeId));
        if (a.length() == 1) {
            a = "0" + a;
        }
        String b = Integer.toHexString(Integer.parseInt(deviceSubTypeId));
        if (b.length() == 1) {
            b = "0" + b;
        }
        ssid = "HET_00";
        ssid += a;
        ssid += b;
        ssid += "_1234";
        ssid_id.setText(ssid);
        return true;
    }
    public void onGenerateSSID(View view) {
        createSsid();
    }

    public void onStartMonitor(View view) {
        if (!createSsid())
            return;
        //注册handler
        WiFiApManager.getInstance().setWiFiApStateListener(new WiFiApManager.WiFiApStateListener() {

            @Override
            public void onApStateEnabled(final String ssid, final String password, final int security) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "热点设置成功，ssid:" + ssid + " pass：" + password + " security:" + security, Toast.LENGTH_LONG).show();
                        showLog("热点设置成功，ssid:" + ssid + " pass：" + password + " security:" + security);
                    }
                });
            }

            @Override
            public void onApStateDisabled() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onApStateDisabled", Toast.LENGTH_LONG).show();
                        showLog("onApStateDisabled");
                    }
                });
            }

            @Override
            public void onApStateFailed() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onApStateFailed", Toast.LENGTH_LONG).show();
                        showLog("onApStateFailed");
                    }
                });
            }
        });
        //开启wifi热点
        WiFiApManager.getInstance().turnOnWifiAp(this, ssid);
    }

    private void showLog(String text) {
        Message msg = Message.obtain();
        msg.obj = text;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "handler", Toast.LENGTH_LONG).show();
            logger.append(msg.obj.toString() + "\r\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    public void onSopMonitor(View view) {
        WiFiApManager.getInstance().closeWifiAp();
    }

    public void onSetting(View view) {
        jumpToTarget(WiFiSettingActivity.class);
    }
}
