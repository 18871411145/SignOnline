package cn.lxbest.wb2020.checkonline.MyView;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import cn.lxbest.wb2020.signonline.R;


public class MySearchView extends ConstraintLayout implements View.OnClickListener {

    Context cx;
    EditText search;
    TextView btn;

    private int type=0;//0搜索 1清空

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id== R.id.button5){
            if(type==0){
                String sw=search.getText().toString().trim();
                if(sw.length()==0) {
                    return;
                }else {

                    btn.setText("清空");
                    type = 1;
                    if (searchInterface != null) searchInterface.search(sw);
                }
            }else {
                btn.setText("搜索");
                search.setText("");
                type=0;
                if(searchInterface!=null) searchInterface.search(null);
            }

        }
    }

    public MySearchInterface searchInterface;


    public interface  MySearchInterface{
        void search(String str);
    }

    public MySearchView(Context context) {
        super(context);
    }

    public MySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cx=context;
        inflate();
    }

    void inflate(){
        LayoutInflater.from(cx).inflate(R.layout.mysearchview, this, true);
        search=findViewById(R.id.editText);
        btn=findViewById(R.id.button5);
        btn.setOnClickListener(this);
    }

}
