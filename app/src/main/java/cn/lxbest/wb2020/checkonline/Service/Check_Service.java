package cn.lxbest.wb2020.checkonline.Service;


import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cn.lxbest.wb2020.checkonline.App;
import cn.lxbest.wb2020.checkonline.TestData.TestData1;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cz.msebera.android.httpclient.Header;


public class Check_Service extends Service {

    private WifiManager wifiManager;

    public static String wifi=null;

    public static RecallInterface recallInterface;


    Handler handler=new Handler();

    public interface RecallInterface{
        void recall(boolean b);
    }

    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        runnable=new Runnable() {
            @Override
            public void run() {
                getWifi();
                handler.postDelayed(this,60*1000*2);
            }
        };
        handler.post(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getWifi();
        return super.onStartCommand(intent, flags, startId);
    }

    //得到wifi名称发送给服务器
   public  void getWifi(){
        wifiManager= (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info=wifiManager.getConnectionInfo();
        wifi=info.getSSID().replace("\"","");
        Log.i("wifi:",wifi);

       Calendar now = Calendar.getInstance();
       int minute=now.get(Calendar.MINUTE);
       int second = now.get(Calendar.SECOND);

        String url= Funcs.servUrlWQ(Const.Key_Resp_Path.sendwifi,"wifi="+wifi+"&uid="+App.user.uid+"&m="+minute+"&s="+second);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null)
                parseData(jsonObject);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.getOK();
                    parseData(jsonObject);
                }else{
                    //上报失败
                    if(recallInterface!=null) recallInterface.recall(false);
                }
            }
        });
    }

    void parseData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                if(recallInterface!=null) recallInterface.recall(true);
            }else{
                if(recallInterface!=null) recallInterface.recall(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
