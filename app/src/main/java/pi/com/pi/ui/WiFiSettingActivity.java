package pi.com.pi.ui;

import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.fsix.mqtt.util.Logc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pi.com.pi.R;
import pi.com.pi.adpter.WiFiAdpter;
import pi.com.pi.base.BaseActivity;
import pi.com.pi.bean.WiFiBean;
import pi.com.pi.wifi.WiFiTool;
import pi.com.pi.wifi.WifiUtils;
import pi.com.pi.wifi.callback.WiFiConnCallback;

public class WiFiSettingActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private EditText ssid,pass;
    private WiFiTool wifiConnect;
    private WiFiAdpter wiFiAdpter;
    private ListView listView;
    private TextView title;
    List<ScanResult> list;
    private Map<String,WiFiBean> wifiList = new HashMap<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_wi_fi_setting;
    }

    @Override
    protected void onView() {
        ssid = (EditText) findViewById(R.id.ssid_id);
        pass = (EditText) findViewById(R.id.pass_id);
        title = (TextView) findViewById(R.id.title);
        title.setText("当前WiFi："+WifiUtils.getSSid(this));
        Object o = getValue("WiFiKey");
        if (o instanceof Map){
            wifiList = (Map<String, WiFiBean>) o;
            if (wifiList!=null&&wifiList.size()>0){
                WiFiBean wiFiBean = (WiFiBean) wifiList.values().toArray()[0];
                if (wiFiBean!=null) {
                    ssid.setText(wiFiBean.getSsid());
                    pass.setText(wiFiBean.getPassword());
                }
            }
        }

        wiFiAdpter = new WiFiAdpter(this);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(wiFiAdpter);
        listView.setOnItemClickListener(this);

    }

    @Override
    protected void onDataInit() {
        wifiConnect = new WiFiTool(this);
        onScanWiFi(null);
    }

    public void onConnWiFi(View view) {
        String name = ssid.getText().toString();
        String word = pass.getText().toString();
        if (TextUtils.isEmpty(name)) {
            tips("WiFi名称不能为空");
            return;
        }
        connWiFi(name,word);
        showLoading("连接WiFi中...");
        wifiList.put(name,new WiFiBean(name,word));
        savaValue("WiFiKey",wifiList);
    }

    public void onScanWiFi(View view) {
        wifiConnect.scan(new WiFiTool.IScanWuliWiFi() {
            @Override
            public void onResult(List<ScanResult> l) {
                list = l;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        wiFiAdpter.setData(list);
                    }
                });
            }
        });
    }


    private void connWiFi(String ssid,String pass){
        wifiConnect.conn(ssid, pass, new WiFiConnCallback() {

            @Override
            public int onWiFiConnected(final String ssid, String password) {
                Logc.i("onWiFiConnected " + ssid + " " + password);
                return 0;
            }

            @Override
            public boolean onInternetConnected(final String ip) {
                Logc.i("onInternetConnected " + ip);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText("当前WiFi："+ip);
                    }
                });
                hideLoading();
                tips("连接成功");
                return true;
            }

            @Override
            public void onFailed(String msg) {
                tips("连接失败");
                Logc.i("onFailed " + msg);
                hideLoading();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list==null||list.size()==0)
            return;
        ScanResult item = list.get(position);
        ssid.setText(item.SSID);
        WiFiBean wifi = wifiList.get(item.SSID);
        pass.setText(wifi==null?"":wifi.getPassword());
    }
}
