package cn.lxbest.wb2020.checkonline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.lxbest.wb2020.checkonline.Modle.ZhuanLi;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.signonline.R;
import cz.msebera.android.httpclient.Header;

public class WodeZhuanLi_Activity extends AppCompatActivity implements OnRefreshListener, OnLoadMoreListener {

    ListView listView;
    MyAdapter adapter;
    RefreshLayout refreshLayout;//刷新list外围框架
    //适配器list
    List<ZhuanLi>  zhuanLis=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wodezhuanli_activity);

        setTitle("我的专利");
        listView=findViewById(R.id.lv_zhuanli);
        refreshLayout=findViewById(R.id.refresh);
        adapter=new MyAdapter();
        listView.setAdapter(adapter);
        getData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int zid=zhuanLis.get(position).zid;
                Intent intent=new Intent(WodeZhuanLi_Activity.this,ZhuanLiXQActivity.class);
                intent.putExtra("id", zid);
                intent.putExtra("state", 1);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    private void getData(){
        zhuanLis=new ArrayList<>();
        String url= Funcs.servUrlWQ(Const.Key_Resp_Path.wdzhuanli,"uid="+App.user.uid);
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                    parseData(jsonObject);
                }

                refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){

                }else{
                    Funcs.showtoast(WodeZhuanLi_Activity.this,"连接失败");
                }
                refreshLayout.finishRefresh();
            }
        });

    }

    private void parseData(JSONObject jsonObject){
        try {
            int code=jsonObject.getInt(Const.Key_Resp.Code);
            if(code==200){
                JSONArray data=jsonObject.getJSONArray(Const.Key_Resp.Data);
                for(int i=0;i<data.length();i++){
                    zhuanLis.add(new ZhuanLi(data.getJSONObject(i)));
                }
                Collections.sort(zhuanLis);
                listView.setAdapter(adapter);
            }else{
                Funcs.showtoast(WodeZhuanLi_Activity.this,"数据获取失败失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //刷新列表
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getData();
    }

    //列表加载更多
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
    }


    class Container{
        ImageView iv_image;
        TextView tv_title,tv_content;
    }

    class MyAdapter extends BaseAdapter{
        Container container;

        @Override
        public int getCount() {
            return zhuanLis.size();
        }

        @Override
        public Object getItem(int position) {
            return zhuanLis.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view==null){
                container=new Container();
                view= LayoutInflater.from(getBaseContext()).inflate(R.layout.zhuanli_item,null);
                container.iv_image=view.findViewById(R.id.iv_image);
                container.tv_title=view.findViewById(R.id.tv_title);
                container.tv_content=view.findViewById(R.id.tv_content);
                view.setTag(container);
            }else container= (Container) view.getTag();

            ZhuanLi data=zhuanLis.get(position);
            Picasso.with(getBaseContext()).load(Funcs.qnUrl(data.qnid1)).placeholder(R.drawable.zhuanli1).into(container.iv_image);
            container.tv_title.setText(data.zname);
            container.tv_content.setText(data.zlxq);

            return view;
        }
    }

}
