package cn.lxbest.wb2020.checkonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lxbest.wb2020.checkonline.TestData.TestData1;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.signonline.R;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class AddZLActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_zlh,et_zlmc,et_zlxq;

    TextView tv_sxrq,tv_jsrq, tv_zllx, tv_flzt;
    TextView tv_temp;//临时储存标量

    ImageView image1,image2,image3;
    ImageView image_temp;//临时储存标量
    int index=1;

    //存放从qn返回的qnid
    Map<String,String> qnids=new HashMap<>();

    Button button2;

    TimePickerView timePickerView;//时间选择器
    OptionsPickerView optionsPickerView;//字符选择器

    private int state=1;//选择开关
    List<String> list_zllx;//专利类型
    List<String> list_flzt;//法律状态

    private int zid=0;//判断是否是实现修改功能

    AlertDialog alertDialog;//确认修改对话框

    //初始化控件
    private void init(){
        et_zlh=findViewById(R.id.et_zlh);//专利号
        et_zlmc=findViewById(R.id.et_zlmc);//专利名称
        et_zlxq=findViewById(R.id.et_zlxq);//专利详情

        tv_zllx =findViewById(R.id.tv_zllx);//专利类型
        tv_flzt =findViewById(R.id.tv_flzt);//法律状态
        tv_sxrq=findViewById(R.id.tv_sxrq);//专利生效日期
        tv_jsrq=findViewById(R.id.tv_jsrq);//专利结束日期
        tv_temp=tv_sxrq;

        image1=findViewById(R.id.image1);//添加的第1张图
        image2=findViewById(R.id.image2);//添加的第2张图
        image3=findViewById(R.id.image3);//添加的第3张图
        image_temp=image1;

        button2=findViewById(R.id.button2);//提交发布

        tv_zllx.setOnClickListener(this);
        tv_flzt.setOnClickListener(this);
        tv_sxrq.setOnClickListener(this);
        tv_jsrq.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        button2.setOnClickListener(this);

        list_zllx=new ArrayList<String>(){{
            add("发明专利");
            add("实用新型专利");
            add("外观专利");
        }};

        list_flzt=new ArrayList<String>(){{
            add("申请中");
            add("未申请");
            add("已下证");
        }};

        alertDialog=App.getAlterDialog(this, "是否提交", "", new Funcs.CallbackInterface() {
            @Override
            public void onCallback(Object obj) {
                postData();
            }
        });


    }

    boolean[] nyr={true,true,true,false,false,false};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addzl_activity);
        Funcs.setMyActionBar(this,"专利添加");

        init();

        Intent intent=getIntent();
        if(intent!=null){
            zid=intent.getIntExtra("zid",0);
        }

        if(zid!=0){
            //根据zid获取专利详情
            getZLData();
        }

        timePickerView=new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_temp.setText(Funcs.simpleDateFormat1.format(date));
            }
        }).setType(nyr).build();



        optionsPickerView=new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String param=null;
                if(state==1){
                    param=list_zllx.get(options1);
                }else{
                    param=list_flzt.get(options1);
                }
                tv_temp.setText(param);
            }
        }).build();


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();

        switch (id){
            case R.id.tv_sxrq:
                tv_temp=tv_sxrq;
                timePickerView.show();
                break;
            case R.id.tv_jsrq:
                tv_temp=tv_jsrq;
                timePickerView.show();
                break;
            case R.id.tv_zllx:
                state=1;
                tv_temp=tv_zllx;
                optionsPickerView.setPicker(list_zllx);
                optionsPickerView.show();
                break;
            case R.id.tv_flzt:
                state=2;
                tv_temp=tv_flzt;
                optionsPickerView.setPicker(list_flzt);
                optionsPickerView.show();
                break;
            case R.id.image1:
                image_temp=image1;
                index=1;
                getImage();
                break;
            case R.id.image2:
                image_temp=image2;
                index=2;
                getImage();
                break;
            case R.id.image3:
                image_temp=image3;
                index=3;
                getImage();
                break;
            case R.id.button2:
                //提交专利信息
                alertDialog.show();
                break;
        }

    }

    //得到专利详情
    private void getZLData(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.zhuanli,"zid="+zid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseZLData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    //模拟数据
                    JSONObject jsonObject= TestData1.ZhuanLiXQ();
                    parseData(jsonObject);
                }else{
                    Funcs.showtoast(AddZLActivity.this,"连接服务器失败");
                }
            }
        });
    }

    void parseZLData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONObject data=jsonObject.getJSONObject(Const.Key_Resp.Data);
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zlh)) et_zlh.setText(data.getString(Const.Field_zhuangli_Table.zlh));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zname))et_zlmc.setText(data.getString(Const.Field_zhuangli_Table.zname));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zlxq)) et_zlxq.setText(data.getString(Const.Field_zhuangli_Table.zlxq));

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zlfl)) tv_zllx.setText(data.getString(Const.Field_zhuangli_Table.zlfl));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.flzt)) tv_flzt.setText(data.getString(Const.Field_zhuangli_Table.flzt));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.sxrq)) tv_sxrq.setText(data.getString(Const.Field_zhuangli_Table.sxrq));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.jsrq)) tv_jsrq.setText(data.getString(Const.Field_zhuangli_Table.jsrq));

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.qnid1)){
                    String qnid=data.getString(Const.Field_zhuangli_Table.qnid1);
                    if(qnid!=null) {
                        Picasso.with(this).load(Funcs.qnUrl(qnid)).into(image1);
                        qnids.put("image1",qnid);
                    }
                }

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.qnid2)){
                    String qnid=data.getString(Const.Field_zhuangli_Table.qnid2);
                    if(qnid!=null){
                        Picasso.with(this).load(Funcs.qnUrl(qnid)).into(image2);
                        qnids.put("image2",qnid);
                    }
                }

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.qnid3)){
                    String qnid=data.getString(Const.Field_zhuangli_Table.qnid3);
                    if(qnid!=null){
                        Picasso.with(this).load(Funcs.qnUrl(qnid)).into(image3);
                        qnids.put("image3",qnid);
                    }
                }

            }else{
                Funcs.showtoast(AddZLActivity.this,"错误代码");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //提交专利信息
    private void postData(){
        button2.setEnabled(false);
        String zlh=et_zlh.getText().toString().trim();

        String mc=et_zlmc.getText().toString().trim();
        if(mc.length()==0){
            Funcs.showtoast(this,"专利名称不能为空");
            return;
        }
        String lx= tv_zllx.getText().toString().trim();
        if(lx.length()==0){
            Funcs.showtoast(this,"专利类型不能为空");
            return;
        }
        String flzt= tv_flzt.getText().toString().trim();

        if(flzt.length()==0){
            Funcs.showtoast(this,"法律状态不能为空");
            return;
        }

        String sxrq=tv_sxrq.getText().toString().trim();

        String jsrq=tv_jsrq.getText().toString().trim();

        String xq=et_zlxq.getText().toString().trim();
        if(xq.length()==0){
            Funcs.showtoast(this,"专利描述不能为空");
            return;
        }

        String url=null;
        if(zid!=0){
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.postzl,"zid="+zid);
        }else{
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.postzl,"uid="+App.user.uid);
        }

        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject.put(Const.Field_zhuangli_Table.zname,mc);
            jsonObject.put(Const.Field_zhuangli_Table.zlh,zlh);
            jsonObject.put(Const.Field_zhuangli_Table.zlfl,lx);
            jsonObject.put(Const.Field_zhuangli_Table.flzt,flzt);
            jsonObject.put(Const.Field_zhuangli_Table.sxrq,sxrq);
            jsonObject.put(Const.Field_zhuangli_Table.jsrq,jsrq);
            jsonObject.put(Const.Field_zhuangli_Table.zlxq,xq);

            jsonObject.put("qnids",new JSONObject(qnids));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpEntity entity=new StringEntity(jsonObject.toString(), HTTP.UTF_8);

        App.http.post(this, url, entity, Const.contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject js=Funcs.bytetojson(responseBody);
                if(js!=null){
                parseData(js);
                }

                button2.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject js= TestData1.getOK();
                    parseData(js);
                }else{
                    Funcs.showtoast(AddZLActivity.this,"上传失败");
                }

                button2.setEnabled(true);
            }
        });


    }

    void parseData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                Funcs.showtoast(this,"上传成功");
            }else{
                Funcs.showtoast(this,"上传失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //向相册拿图片
    private void getImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"上传图片"),22);
        App.showLoadingMask(this);
    }


    //从相册返回数据流
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==22&&resultCode== Activity.RESULT_OK&&data!=null){
            Uri uri=data.getData();
            try {
                InputStream is=this.getContentResolver().openInputStream(uri);
                Bitmap bitmap= BitmapFactory.decodeStream(is);
                image_temp.setImageBitmap(bitmap);
                InputStream inputStream=this.getContentResolver().openInputStream(uri);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                byte[] bytes=output.toByteArray();
                inputStream.close();
                output.close();
                //将读出的字节传到qn服务器
                App.postImgToQnServer(bytes, new Funcs.CallbackInterface() {
                    @Override
                    public void onCallback(Object obj) {
                        if(obj==null){
                            //上传失败
                            App.hideLoadingMask(AddZLActivity.this);
                        }else{
                            String qnid= (String) obj;
                            qnids.put("image"+index,qnid);
                            App.hideLoadingMask(AddZLActivity.this);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            App.hideLoadingMask(AddZLActivity.this);
        }
    }


}
