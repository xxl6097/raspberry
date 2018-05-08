package pi.com.pi.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import pi.com.pi.util.AppTools;
import pi.com.pi.util.Base64;
import pi.com.pi.util.SpUtil;
import rx.functions.Action1;

public abstract class BaseActivity extends AppCompatActivity {

    protected RxPermissions rxPermissions;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
    };

    //声明进度条对话框
    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions();
        this.setContentView(this.getLayoutId());
        progressDialog = new ProgressDialog(this);
        this.onView();
        this.onDataInit();
    }

    private void getPermissions(){
        if (rxPermissions==null) {
            rxPermissions = new RxPermissions(this);
        }
        rxPermissions.request(permissions)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        tips("权限获取" + (aBoolean?"成功":"失败"));
                    }
                });
    }


    public void tips(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected <T> void savaValue(String key,T value){
        SpUtil.putString(this,key,Base64.objBase64Str(value));
    }

     protected <T> T getValue(String key){
         String base64 = SpUtil.getString(this,key);
         return Base64.strBase64Obj(base64);
     }


    public void jumpToTarget(Class<?> desActivity) {
        jumpToTarget(desActivity, false);
    }

    public void jumpToTarget(Class<?> desActivity, boolean isFinish) {
        AppTools.startForwardActivity(this, desActivity, isFinish);
    }

    public void jumpToTarget(Class<?> desActivity, Bundle bund) {
        AppTools.startForwardActivity(this, desActivity, bund, false);
    }

    public void jumpToForResultTarget(Class<?> desActivity, Bundle bund, int resultCode) {
        AppTools.startForResultActivity(this, desActivity, resultCode, bund, false);
    }

    protected void showLoading(){
        showLoading(null);
    }

    protected void showLoading(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(TextUtils.isEmpty(msg)?"加载中...":msg);
                progressDialog.show();
            }
        });
    }

    protected void hideLoading(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }


    protected abstract int getLayoutId();

    protected abstract void onView();

    protected abstract void onDataInit();
}
