package cn.lxbest.wb2020.checkonline.TestData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lxbest.wb2020.checkonline.Tool.Const;

public class TestData1 {



    //登录成功得到用户数据
    public static JSONObject getUserData(){
        JSONObject jsonObject=new JSONObject();
        JSONObject data=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
            data.put(Const.Field_Table_User.uid,"12121212");
            data.put(Const.Field_Table_User.name,"张三");
            data.put(Const.Field_Table_User.gsmc,"贝壳瑞晟");
            data.put(Const.Field_Table_User.phone,"1312345678");
            data.put(Const.Field_Table_User.role,"990");
            jsonObject.put(Const.Key_Resp.Data,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }




    //验证码时候ok WiFi连接是否符合要求
    public static JSONObject getOK() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static JSONObject zhuanlis() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);

            List<HashMap<String,Object>> list=new ArrayList<>();

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_zhuangli_Table.zname,"一种液压装置");
                put(Const.Field_zhuangli_Table.zlxq,"自己设计的一种液压装置，非常好用.....");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_zhuangli_Table.zname,"一种液压装置");
                put(Const.Field_zhuangli_Table.zlxq,"自己设计的一种液压装置，非常好用.....");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_zhuangli_Table.zname,"一种液压装置");
                put(Const.Field_zhuangli_Table.zlxq,"自己设计的一种液压装置，非常好用.....");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_zhuangli_Table.zname,"一种液压装置");
                put(Const.Field_zhuangli_Table.zlxq,"自己设计的一种液压装置，非常好用.....");
            }});

            list.add(new HashMap<String, Object>(){{
                put(Const.Field_zhuangli_Table.zname,"一种液压装置");
                put(Const.Field_zhuangli_Table.zlxq,"自己设计的一种液压装置，非常好用.....");
            }});

            jsonObject.put(Const.Key_Resp.Data,new JSONArray(list));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    //专利详情
    public static JSONObject ZhuanLiXQ() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.Key_Resp.Code,200);
            JSONObject data=new JSONObject();
            data.put(Const.Field_zhuangli_Table.zname,"一种压装置");
            data.put(Const.Field_zhuangli_Table.zlh,"1231asd1231da");
            data.put(Const.Field_zhuangli_Table.zlfl,"科技发明");
            data.put(Const.Field_zhuangli_Table.fbsj,"2020-2-23");
            data.put(Const.Field_zhuangli_Table.flzt,"已下证");
            data.put(Const.Field_zhuangli_Table.sxrq,"2020-2-23");
            data.put(Const.Field_zhuangli_Table.jsrq,"2020-2-25");
            data.put(Const.Field_zhuangli_Table.zlxq,"自己设计的一种液压装置，非常好用.....");

            jsonObject.put(Const.Key_Resp.Data,data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
