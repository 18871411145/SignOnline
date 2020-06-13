package cn.lxbest.wb2020.checkonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.lxbest.wb2020.checkonline.Modle.User;
import cn.lxbest.wb2020.checkonline.Service.Check_Service;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.signonline.R;
import cz.msebera.android.httpclient.Header;

public class App extends Application {
    public static AsyncHttpClient http;

    public static int env= Const.Env.DEV_OK;

    public static int screenWidth, screenHeight;

    public static Context context;

    public static User user;

    public static UploadManager uploadManager;
    public static HashMap<String, Object> qnToken = new HashMap<>(); //token, time

    public App() {
        http=new AsyncHttpClient();
        http.addHeader("dataType", "json");
        http.addHeader("User-Agent", "jpark");
        http.setResponseTimeout(1000*60);
        user=new User();
    }



    @Override
    public void onCreate() {
        super.onCreate();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        context=getApplicationContext();
    }

    //得到储存类SharedPreferences
    public  static SharedPreferences sharedPreferences(String tag) {
        return context.getSharedPreferences(Const.domain+"."+tag, Context.MODE_PRIVATE);
    }

    //将json解析放入user
    public static void putJsonToUser(JSONObject data) {
        try{
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.uid)) App.user.uid=data.getInt(Const.Field_Table_User.uid);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.name)) App.user.name=data.getString(Const.Field_Table_User.name);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.phone)) App.user.phone=data.getString(Const.Field_Table_User.phone);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.gsmc)) App.user.gsmc=data.getString(Const.Field_Table_User.gsmc);
            if(Funcs.jsonItemValid(data,Const.Field_Table_User.role)) App.user.role=data.getInt(Const.Field_Table_User.role);
        }catch (Exception e){
            Log.i("错误","解析出错!");
        }
    }
    //将user里的信息存到preference里以备下次登录直接使用
    public static void putUserToPreference(){
        SharedPreferences.Editor editor=sharedPreferences(Const.Key_SharedPref.Account).edit();
        editor.putInt(Const.Field_Table_User.uid,user.uid);
        editor.putString(Const.Field_Table_User.name,user.name);
        editor.putString(Const.Field_Table_User.phone,user.phone);
        editor.putString(Const.Field_Table_User.gsmc,user.gsmc);
        editor.putInt(Const.Field_Table_User.role,user.role);
        editor.commit();
    }

    //清除preference
    public static void cleanUserPref() {
        sharedPreferences(Const.Key_SharedPref.Account).edit().clear().commit();
    }

    //向preference拿数据放到user(是否需要重新填写信息)
    public static boolean refreshUserFromPreference(){
        SharedPreferences sharedPreferences=sharedPreferences(Const.Key_SharedPref.Account);
        if(sharedPreferences.getInt(Const.Field_Table_User.uid,0)!=0){
            user.uid=sharedPreferences.getInt(Const.Field_Table_User.uid,0);
            user.name=sharedPreferences.getString(Const.Field_Table_User.name,null);
            user.gsmc=sharedPreferences.getString(Const.Field_Table_User.gsmc,null);
            user.phone=sharedPreferences.getString(Const.Field_Table_User.phone,null);
            user.role=sharedPreferences.getInt(Const.Field_Table_User.role,0);
            return false;
        }else{
            return true;
        }

    }

    //推出登录
    public static void logout(Context context){
        cleanUserPref();
        //注销服务
        Intent intent1=new Intent(context, Check_Service.class);
        context.stopService(intent1);
        //返回登陆界面
        Intent intent=new Intent(context,Login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    //判断现在的qntoken是否可用
    public static boolean qnTokenLegal() {
        return !(Funcs.isNull(qnToken.get("token")) || (new Date().getTime() - (long) qnToken.get("time") >= 60 * 1000));
    }

    //如果不存在qntoken或者token过期就重新向服务器拿
    public static void getQnTokenIfExpired(final Funcs.CallbackInterface callbackInterface) {
        if (!qnTokenLegal()) {
            http.get(Funcs.servUrl(Const.Key_Resp_Path.QnToken), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String tk = null;
                    try {
                        JSONObject jo = Funcs.bytetojson(responseBody);
                        if (jo != null) {
                            tk = jo.getString("tk");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    qnToken.put("token", tk);
                    qnToken.put("time", new Date().getTime());
                    if (callbackInterface != null) callbackInterface.onCallback(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (callbackInterface != null) callbackInterface.onCallback(null);
                }
            });
        } else if (callbackInterface != null) callbackInterface.onCallback(null);
    }


    //向七牛传图片
    public static void postImgToQnServer(final byte[] bytes, final Funcs.CallbackInterface callbackInterface) {
        if (uploadManager==null) uploadManager = new UploadManager();
        getQnTokenIfExpired(new Funcs.CallbackInterface() {
            @Override
            public void onCallback(Object obj) {
                if (!App.qnTokenLegal()){
                    Funcs.showtoast(context,"Token 错误，请重启APP重试");
                    if (callbackInterface != null) callbackInterface.onCallback(null);
                }else {
                    uploadManager.put(bytes, null, App.qnToken.get("token").toString(), new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()){
                                Funcs.showtoast(context,"上传成功!");
                                String qnid = null;
                                try {
                                    qnid=info.response.getString("key");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (callbackInterface != null) callbackInterface.onCallback(qnid);
                            }else {
                                if (callbackInterface != null) callbackInterface.onCallback(null);
                                Funcs.showtoast(context,"上传失败，请刷新重试");
                            }
                        }
                    }, null);
                }
            }
        });
    }


//全屏显示组件

    public static Activity getActivity(Context context){
        while(!(context instanceof  Activity) && context instanceof ContextWrapper){
            context = ((ContextWrapper)context).getBaseContext();
        }
        return (Activity)context;
    }
    public static ViewGroup getRootView(Activity act) {
        return act.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public static void showLoadingMask(ViewGroup parent) {
        if (parent==null) return;
        ViewGroup v = (ViewGroup) App.getActivity(parent.getContext()).getLayoutInflater().inflate(R.layout.view_loading_mask, null);
        v.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
//        v.setTop(0); v.setLeft(0);
        parent.addView(v);
    }

    //弹出读取loading界面
    public static void showLoadingMask(Activity act) {
        if (act==null) return;
        ViewGroup p = getRootView(act);
        showLoadingMask(p);
    }

    //取消读取loading界面
    public static void hideLoadingMask(Activity act) {
        if (act==null) return;
        ViewGroup p = getRootView(act);
        hideLoadingMask(p);
    }
    public static void hideLoadingMask(ViewGroup parent) {
        if (parent==null) return;
        parent.removeView(parent.findViewById(R.id.cl_loading_mask));
    }

    //对话框

    public static AlertDialog getAlterDialog(Context context, String title, String message, final Funcs.CallbackInterface callbackInterface){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        AlertDialog dialog=builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callbackInterface!=null) callbackInterface.onCallback(null);

                    }
                }).create();

        return  dialog;
    }


}
