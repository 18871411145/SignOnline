package cn.lxbest.wb2020.checkonline;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lxbest.wb2020.checkonline.TestData.TestData1;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.signonline.R;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Login_activity extends AppCompatActivity implements View.OnClickListener {

    EditText edit_phone;//手机号
    EditText edit_yzm;//验证码

    Button btn_getyzm;//获取验证码
    Button btn_login;//登录
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Funcs.setMyActionBar(this,"登录");

        edit_phone =findViewById(R.id.phone_EditText);
        edit_yzm =findViewById(R.id.yzm_EditText);
        btn_getyzm =findViewById(R.id.button1);
        btn_login =findViewById(R.id.button2);


        btn_getyzm.setOnClickListener(this);
        btn_login.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.button1:
                //获取验证码
                setenable(false);
                getYAM();
                break;
            case R.id.button2:
                setenable(false);
                postData();
                break;
        }
    }

    //设置不可点击（防止未返回数据就请求现象）
    private void setenable(boolean b){
        btn_getyzm.setEnabled(b);
         btn_login.setEnabled(b);
    }

    void getYAM(){
        String phone = edit_phone.getText().toString().trim();

        if(phone.length()==0){
            Funcs.showtoast(this,"手机号不能为空");
            return;
        }

        String url= Funcs.servUrlWQ(Const.Key_Resp_Path.login,"step="+1);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Field_Table_User.phone, phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpEntity entity=new StringEntity(jsonObject.toString(), HTTP.UTF_8);

        App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject1=Funcs.bytetojson(responseBody);
                if(jsonObject1!=null){
                parsePhone(jsonObject1);
                }else{
                    setenable(true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject1= TestData1.getOK();
                    parsePhone(jsonObject1);
                }else{
                    Funcs.showtoast(Login_activity.this,"发送失败");
                }

                setenable(true);
            }
        });
    }

    void parsePhone(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                Funcs.showtoast(Login_activity.this,"发送成功");
            }else
                Funcs.showtoast(Login_activity.this,"错误代码");

            setenable(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void postData(){
        String phone = edit_phone.getText().toString().trim();
        String yzm = edit_yzm.getText().toString().trim();

        if(phone.length()==0|| yzm.length()==0){
            Funcs.showtoast(this,"手机号或验证码不能为空");
            return;
        }

        String url= Funcs.servUrlWQ(Const.Key_Resp_Path.login,"step="+2);
       JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.YZM, yzm);
            jsonObject.put(Const.Field_Table_User.phone, phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpEntity entity=new StringEntity(jsonObject.toString(), HTTP.UTF_8);

        App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject1=Funcs.bytetojson(responseBody);
                if(jsonObject1!=null){
                parseData(jsonObject1);
                }else {
                    setenable(true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject1= TestData1.getUserData();
                    parseData(jsonObject1);
                }else{
                    Funcs.showtoast(Login_activity.this,"上传失败");
                }

                setenable(true);
            }
        });
    }

    void parseData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                //修改user
                JSONObject data=jsonObject.getJSONObject(Const.Key_Resp.Data);
                App.putJsonToUser(data);
                App.putUserToPreference();
                //前往主页
                Intent intent=new Intent(this, ZhuanLi_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Funcs.showtoast(this,"上传失败");
            }

            setenable(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
