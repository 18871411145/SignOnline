package cn.lxbest.wb2020.checkonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import cn.lxbest.wb2020.signonline.R;
import cn.lxbest.wb2020.checkonline.Service.Check_Service;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.checkonline.OverWriteClass.MyValueFormatter;
import cz.msebera.android.httpclient.Header;

public class QianDaoActivity extends AppCompatActivity implements Check_Service.RecallInterface {

    TextView text_name;//员工姓名
    TextView text_gs;//所在公司
    TextView text_type;//是否签到
    TextView text_signInTime;//签到时间
    TextView text_state;//是否在工作区域

    PieChart pieChart1;//显示出勤率
    ArrayList<PieEntry> entries = new ArrayList<>();

    BarChart barChart1;//显示每月出勤情况
    ArrayList<BarEntry> values = new ArrayList<>();

    //判断数据是否获取完毕，并关闭loading界面.
    boolean boo_day=false;
    boolean boo_mon=false;
    boolean boo_mons=false;


    void initPieChart(){

        pieChart1.setUsePercentValues(true);//是否显示百分比
        pieChart1.getDescription().setEnabled(false);//得到图标描述文本

        pieChart1.setDrawCenterText(true);
        pieChart1.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        pieChart1.setCenterText("本月出勤情况 ");
        pieChart1.setCenterTextSize(18);

        //内圆
        pieChart1.setDrawHoleEnabled(true);
        pieChart1.setHoleColor(Color.WHITE);
        pieChart1.setHoleRadius(45);

        //修饰外圆（白边）
        pieChart1.setTransparentCircleRadius(48);
        pieChart1.setTransparentCircleColor(Color.WHITE);
        pieChart1.setTransparentCircleAlpha(110);//透明度



        pieChart1.setRotationAngle(0);
        pieChart1.setRotationEnabled(false);


        pieChart1.animateY(1400, Easing.EaseInOutQuad);

        Legend l = pieChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        l.setEnabled(false);

        pieChart1.setDrawEntryLabels(true);
        pieChart1.setEntryLabelColor(Color.WHITE);
        pieChart1.setEntryLabelTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        pieChart1.setEntryLabelTextSize(12f);
    }

    void initBarChart(){
        barChart1.setScaleEnabled(false);//不能缩放
        barChart1.getDescription().setEnabled(false);

        barChart1.setDrawGridBackground(false);//是否设置单元格背景

        ValueFormatter xAxisFormatter = new MyValueFormatter("月");

        XAxis xAxis = barChart1.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1); // 设置x坐标显示间隔
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("天");

        YAxis leftAxis = barChart1.getAxisLeft();
        leftAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0);//设置其实坐标值

//        barChart1.getAxisLeft().setDrawGridLines(false);

        YAxis rightAxis = barChart1.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = barChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setEnabled(false);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("考勤签到");
        text_name=findViewById(R.id.name_text);
        text_gs=findViewById(R.id.gs_text);
        text_type=findViewById(R.id.type_TextView);
        text_signInTime=findViewById(R.id.signInTime_TextView);
        text_state=findViewById(R.id.state_TextView);

        pieChart1=findViewById(R.id.pieChart1);
        barChart1=findViewById(R.id.barChart1);

        initPieChart();
        initBarChart();

        text_name.setText(App.user.name);
        text_gs.setText(App.user.gsmc);


        Intent intent=new Intent(this,Check_Service.class);
        startService(intent);

        Check_Service.recallInterface=this;

        //用页面挡住加载前不完整界面
        App.showLoadingMask(this);
    }

    @Override
    public void recall(boolean b){
        if(b){
            text_state.setTextColor(getResources().getColor(R.color.green));
            text_state.setText("已在工作区域内");
        }else{
            text_state.setTextColor(getResources().getColor(R.color.red));
            text_state.setText("未在工作区域内");
        }

        getData();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent=new Intent(this,Check_Service.class);
        startService(intent);
    }

    //得到当天出勤情况
    private void getCQ(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.daykq,"uid="+App.user.uid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseCQData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(QianDaoActivity.this,"获取当日出勤失败");
                boo_day=true;
                closeLoading();
            }
        });
    }


    private void parseCQData(JSONObject jsonObject){
        boolean sfqd=false;
        String qdsj=null;
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONObject data=jsonObject.getJSONObject(Const.Key_Resp.Data);
                if(Funcs.jsonItemValid(data,Const.Field_Table_KaoQin.sfqd)) sfqd=data.getBoolean(Const.Field_Table_KaoQin.sfqd);
                if(Funcs.jsonItemValid(data,Const.Field_Table_KaoQin.qdsj)) qdsj=data.getString(Const.Field_Table_KaoQin.qdsj);
                //今天是否签到
                if(sfqd){
                    //如果已经签到就显示签到时间
                    text_type.setText("已签到");
                    text_signInTime.setText("签到时间:"+qdsj);
                    text_signInTime.setVisibility(View.VISIBLE);
                }
            }else{
//                Funcs.showtoast(QianDaoActivity.this,"请连接工作WiFi,");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            boo_day=true;
            closeLoading();
        }
    }


    //得到当月出勤情况
    private void getMCQ(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.monkq,"uid="+App.user.uid+"&m="+1);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseMCQData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(QianDaoActivity.this,"获取当月出勤失败");
                boo_mon=true;
                closeLoading();
            }
        });
    }

    private void parseMCQData(JSONObject jsonObject){
        entries=new ArrayList<>();
        int cqts=0;
        int qqts=0;
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONObject data=jsonObject.getJSONObject(Const.Key_Resp.Data);
                if(Funcs.jsonItemValid(data,Const.Field_Table_KaoQin.cqts)) cqts=data.getInt(Const.Field_Table_KaoQin.cqts);
                if(Funcs.jsonItemValid(data,Const.Field_Table_KaoQin.qqts)) qqts=data.getInt(Const.Field_Table_KaoQin.qqts);

                entries.add(new PieEntry(cqts,"出勤"+cqts+"天"));
                entries.add(new PieEntry(qqts,"缺勤"+qqts+"天"));

                addDataToPie();
            }else{
                Funcs.showtoast(QianDaoActivity.this,"获取当月出勤失败");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            boo_mon=true;
            closeLoading();
        }
    }

    //得到每个月出勤情况
    private void getMCQs(){
        String url=Funcs.servUrlWQ(Const.Key_Resp_Path.monkq,"uid="+App.user.uid+"&m="+2);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseMSCQData(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Funcs.showtoast(QianDaoActivity.this,"获取每月出勤失败");
                boo_mons=true;
                closeLoading();
            }
        });
    }

    private void parseMSCQData(JSONObject jsonObject){
        values=new ArrayList<>();
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray data=jsonObject.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<data.length();i++){
                    JSONObject js=data.getJSONObject(i);
                    int month=0;
                    int days=0;
                    if(Funcs.jsonItemValid(js,Const.Field_Table_KaoQin.month)) month=js.getInt(Const.Field_Table_KaoQin.month);
                    if(Funcs.jsonItemValid(js,Const.Field_Table_KaoQin.days)) days=js.getInt(Const.Field_Table_KaoQin.days);
                    values.add(new BarEntry(month,days));
                }
                addDataToBar();

            }else{
                Funcs.showtoast(QianDaoActivity.this,"获取每月出勤失败");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            boo_mons=true;
            closeLoading();
        }
    }

    void getData(){
       getCQ();
       getMCQ();
       getMCQs();
    }

    private void closeLoading(){
        //同时满足情况时
        if(boo_day&&boo_mon&&boo_mons){
            App.hideLoadingMask(this);
        }
    }


    void addDataToBar(){
        BarDataSet set1;

            set1 = new BarDataSet(values, "月");
            set1.setColors(ColorTemplate.PASTEL_COLORS);
            set1.setDrawValues(false);//柱状图上方显示数值


            BarData data = new BarData(set1);
            barChart1.setData(data);


        barChart1.invalidate();
    }

    //将数据放入chart中
    void addDataToPie(){
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setSelectionShift(0);
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart1));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf"));
        pieChart1.setData(data);

        pieChart1.highlightValues(null);

        pieChart1.invalidate();


    }


}
