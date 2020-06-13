package cn.lxbest.wb2020.checkonline;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lxbest.wb2020.checkonline.TestData.TestData1;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.signonline.R;
import cz.msebera.android.httpclient.Header;

public class ZhuanLiXQActivity extends AppCompatActivity implements View.OnClickListener {

    int zid;
    int state=0;
    TextView tv_mc,tv_zlh,tv_zlfl,tv_fbsj,tv_yxqx,tv_flzt,tv_gsmc,tv_zlxq;
    SliderLayout sliderLayout;

    ConstraintLayout csl;//从我的专利进入专有布局 修改 删除功能
    Button btn_xg;//修改按钮
    Button btn_sc;//删除按钮

    AlertDialog alertDialog;//确认删除对话框

    //初始化控件
    private void init(){
        tv_mc=findViewById(R.id.tv_mc);//专利名称
        tv_zlh=findViewById(R.id.tv_zlh);//专利号
        tv_zlfl=findViewById(R.id.tv_zlfl);//专利分类
        tv_fbsj=findViewById(R.id.tv_fbsj);//发布时间
        tv_yxqx=findViewById(R.id.tv_yxqx);//有效期限
        tv_flzt=findViewById(R.id.tv_flzt);//法律状态
        tv_gsmc=findViewById(R.id.tv_gsmc);//公司名称
        tv_zlxq=findViewById(R.id.tv_zlxq);//专利详情

        csl=findViewById(R.id.ConstraintLayout2);//修改 删除功能
        btn_xg=findViewById(R.id.btn_xg);//修改按钮
        btn_sc=findViewById(R.id.btn_sc);//删除按钮

        sliderLayout =findViewById(R.id.image_head);//三张照片的slider

        alertDialog=App.getAlterDialog(this, "是否删除此专利", "", new Funcs.CallbackInterface() {
            @Override
            public void onCallback(Object obj) {
                deleteZL();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuanlixq_activity);

        Funcs.setMyActionBar(this,"专利详情");

        init();

        Intent intent=getIntent();
        if(intent!=null){
        zid=intent.getIntExtra("id",0);
        state=intent.getIntExtra("state",0);
        }

        if(state==1&&App.user.role>=990){
            csl.setVisibility(View.VISIBLE);
        }else{
            csl.setVisibility(View.GONE);
        }

        getData();

        btn_xg.setOnClickListener(this);
        btn_sc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.btn_xg:
                //修改专利内容
                Intent intent=new Intent(this,AddZLActivity.class);
                intent.putExtra("zid",zid);
                startActivity(intent);
                break;
            case R.id.btn_sc:
                alertDialog.show();
                break;
        }
    }

    //删除此专利
    private void deleteZL(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.wdzhuanli,"zid="+zid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseDeleteData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){

                }else{
                    Funcs.showtoast(ZhuanLiXQActivity.this,"连接服务器失败");
                }
            }
        });
    }

    private void parseDeleteData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                Funcs.showtoast(this,"删除成功");
            }else{
                Funcs.showtoast(this,"删除失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getData(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.zhuanli,"zid="+zid);

        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                parseData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    //模拟数据
                    JSONObject jsonObject= TestData1.ZhuanLiXQ();
                    parseData(jsonObject);
                }else{
                    Funcs.showtoast(ZhuanLiXQActivity.this,"连接服务器失败");
                }
            }
        });
    }

    private void parseData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONObject data=jsonObject.getJSONObject(Const.Key_Resp.Data);

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zname)) tv_mc.setText("专利名称: "+data.getString(Const.Field_zhuangli_Table.zname));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zlh)) tv_zlh.setText("专利号: "+data.getString(Const.Field_zhuangli_Table.zlh));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zlfl)) tv_zlfl.setText("专利分类: "+data.getString(Const.Field_zhuangli_Table.zlfl));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.fbsj)) tv_fbsj.setText("发布时间: "+data.getString(Const.Field_zhuangli_Table.fbsj));
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.flzt)) tv_flzt.setText("法律状态: "+data.getString(Const.Field_zhuangli_Table.flzt));
                if(Funcs.jsonItemValid(data,Const.Field_Table_User.gsmc)) tv_gsmc.setText("公司名称: "+data.getString(Const.Field_Table_User.gsmc));

                String sxrq,jsrq;
                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.sxrq)&&Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.jsrq)){
                    sxrq=data.getString(Const.Field_zhuangli_Table.sxrq);
                    jsrq=data.getString(Const.Field_zhuangli_Table.jsrq);
                    tv_yxqx.setText("有效期限: "+sxrq+" - "+jsrq);
                }

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.zlxq)) tv_zlxq.setText(data.getString(Const.Field_zhuangli_Table.zlxq));

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.qnid1)){
                    TextSliderView sliderView=new TextSliderView(this);
                    sliderView.image(Funcs.qnUrl(data.getString(Const.Field_zhuangli_Table.qnid1)));
                    sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });

                    sliderLayout.addSlider(sliderView);
                }

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.qnid2)){
                    TextSliderView sliderView=new TextSliderView(this);
                    sliderView.image(Funcs.qnUrl(data.getString(Const.Field_zhuangli_Table.qnid2)));
                    sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });
                    sliderLayout.addSlider(sliderView);
                }

                if(Funcs.jsonItemValid(data,Const.Field_zhuangli_Table.qnid3)){
                    TextSliderView sliderView=new TextSliderView(this);
                    sliderView.image(Funcs.qnUrl(data.getString(Const.Field_zhuangli_Table.qnid3)));
                    sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });
                    sliderLayout.addSlider(sliderView);
                }
                //切换时间
                     sliderLayout.setDuration(3000);

            }else{
                Funcs.showtoast(ZhuanLiXQActivity.this,"返回错误信息");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
