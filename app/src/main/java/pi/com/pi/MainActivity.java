package pi.com.pi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pi.com.pi.base.BaseActivity;
import pi.com.pi.ui.WiFiSettingActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onView() {

    }

    @Override
    protected void onDataInit() {

    }


    public void onConnWiFi(View view) {
        jumpToTarget(WiFiSettingActivity.class);
    }

    public void onScanLan(View view) {
    }


    protected void showDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog,null);
        final EditText ssid = (EditText) view.findViewById(R.id.ssid_id);
        final EditText pass = (EditText) view.findViewById(R.id.pass_id);
        new  AlertDialog.Builder(this)
                .setTitle("WiFi连接")
                .setView(view)
                .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("取消" ,  null )
                .setCancelable(false)
                .show();
    }

}
