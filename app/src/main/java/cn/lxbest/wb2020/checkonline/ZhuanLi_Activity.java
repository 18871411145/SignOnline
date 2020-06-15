package cn.lxbest.wb2020.checkonline;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cn.lxbest.wb2020.checkonline.Modle.ZhuanLi;
import cn.lxbest.wb2020.checkonline.MyView.MySearchView;
import cn.lxbest.wb2020.checkonline.TestData.TestData1;
import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;
import cn.lxbest.wb2020.signonline.R;
import cz.msebera.android.httpclient.Header;

public class ZhuanLi_Activity extends AppCompatActivity implements MySearchView.MySearchInterface, OnRefreshListener, OnLoadMoreListener {

    RecyclerView recyclerView;
    RecyclerViewAdapter viewAdapter;
    RefreshLayout refreshLayout;//刷新list外围框架
    MySearchView searchView;//搜索框

    List<ZhuanLi> zhuanLis=new ArrayList<>(); //专利类集合

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuanli_activity);

        getSupportActionBar().setTitle("专利");
        recyclerView=findViewById(R.id.recycler_view);
        refreshLayout=findViewById(R.id.refresh);
        searchView=findViewById(R.id.searchview);

        viewAdapter=new RecyclerViewAdapter();

        recyclerView.setHasFixedSize(true);//自适应宽高

        StaggeredGridLayoutManager _sGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);


        recyclerView.setAdapter(viewAdapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        searchView.searchInterface = this;

        getData(null,false);

    }

    int have=0;
    //得到首页专利列表
    private void getData(String s,boolean more){
        zhuanLis=new ArrayList<>();

//如果重新设置适配器 要重新设置布局管理
//        StaggeredGridLayoutManager _sGridLayoutManager = new StaggeredGridLayoutManager(2,
//                StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(_sGridLayoutManager);

        String url=null;
        if(s==null){
          url= Funcs.servUrl(Const.Key_Resp_Path.zhuanli);
          if(more){
              url= Funcs.servUrlWQ(Const.Key_Resp_Path.zhuanli,"m="+have);
          }
        }else{
            url=Funcs.servUrlWQ(Const.Key_Resp_Path.zhuanli,"s="+s);
        }
        App.http.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject jsonObject=Funcs.bytetojson(responseBody);
                if(jsonObject!=null){
                parseData(jsonObject);
                }
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(App.env==Const.Env.DEV_TD){
                    JSONObject jsonObject= TestData1.zhuanlis();
                    parseData(jsonObject);
                }else{
                    Funcs.showtoast(ZhuanLi_Activity.this,"连接失败");
                }
                    refreshLayout.finishRefresh();
                    refreshLayout.finishLoadMore();
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
                have=zhuanLis.size();
                Collections.sort(zhuanLis);
                recyclerView.setAdapter(viewAdapter);
            }else{
                Funcs.showtoast(ZhuanLi_Activity.this,"数据获取失败失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //搜索框回调接口
    @Override
    public void search(String str) {
         getData(str,false);
    }

    //刷新列表
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getData(null,false);
    }

    //列表加载更多
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getData(null,true);
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_content;
        ImageView image_title;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.item_title);
            tv_content = itemView.findViewById(R.id.item_content);
            image_title=itemView.findViewById(R.id.item_image);
        }
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        public RecyclerViewAdapter() {

        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.zhuanli_card, null);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
           final ZhuanLi data = zhuanLis.get(position);
           holder.tv_title.setText(data.zname);
           holder.tv_content.setText(data.zlxq);
           Picasso.with(getBaseContext()).load(Funcs.qnUrl(data.qnid1)).placeholder(R.drawable.zhuanli1).into(holder.image_title);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), ZhuanLiXQActivity.class);
                    intent.putExtra("id", data.zid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return zhuanLis.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //判断该用户是否有添加权限
        if(App.user.role>=990){
            getMenuInflater().inflate(R.menu.main, menu);
        }else{
            getMenuInflater().inflate(R.menu.main1, menu);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.self_item:
                intent=new Intent(this,Self_activity.class);
                startActivity(intent);
                break;
            case R.id.addzl_item:
                intent=new Intent(this,AddZLActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
