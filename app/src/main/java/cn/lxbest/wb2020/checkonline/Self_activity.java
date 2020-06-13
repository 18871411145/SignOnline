package cn.lxbest.wb2020.checkonline;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.signonline.R;


public class Self_activity extends AppCompatActivity {

    EditText edit_gsmc;//公司名称
    EditText edit_name;//员工ID

    Button btn_out;//删除信息并推出
    Button btn_qd;//前往签到界面
    Button btn_zl;//前往我的专利页面
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_activity);

        getSupportActionBar().setTitle("个人");

        edit_gsmc=findViewById(R.id.phone_EditText);
        edit_name =findViewById(R.id.ygid_EditText);
        btn_out=findViewById(R.id.button2);
        btn_qd=findViewById(R.id.button3);
        btn_zl=findViewById(R.id.button4);

        edit_gsmc.setText(App.user.gsmc);
        edit_name.setText(App.user.name);

        edit_gsmc.setEnabled(false);
        edit_name.setEnabled(false);

        if(App.user.role!= Const.Role.Admin){
            btn_zl.setVisibility(View.INVISIBLE);
        }


        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.logout(Self_activity.this);
            }
        });

        btn_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Self_activity.this,QianDaoActivity.class);
                startActivity(intent);
            }
        });

        btn_zl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Self_activity.this,WodeZhuanLi_Activity.class);
                startActivity(intent);
            }
        });
    }



}
