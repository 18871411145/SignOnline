package cn.lxbest.wb2020.checkonline.Modle;

import org.json.JSONObject;

import cn.lxbest.wb2020.checkonline.Tool.Const;
import cn.lxbest.wb2020.checkonline.Tool.Funcs;

public class ZhuanLi implements Comparable<ZhuanLi>{

    public int zid;
    public String zname;//专利名称
    public String zlh;//专利号
    public String zlfl;//专利分类  软件。。。

    public String fbsj;//专利发布时间
    public String flzt;//法律状态  已下证

    public String sxrq;//生效日期  2020/2/23-2030/2/23
    public String jsrq;//结束日期

    public String zlxq;//专利详情

    public String qnid1;
    public String qnid2;
    public String qnid3;

    public ZhuanLi(JSONObject jsonObject) {
        try{
            if(Funcs.jsonItemValid(jsonObject,Const.Field_zhuangli_Table.zid)) zid=jsonObject.getInt(Const.Field_zhuangli_Table.zid);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.zname)) zname=jsonObject.getString(Const.Field_zhuangli_Table.zname);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.zlh)) zlh=jsonObject.getString(Const.Field_zhuangli_Table.zlh);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.zlfl)) zlfl=jsonObject.getString(Const.Field_zhuangli_Table.zlfl);

            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.fbsj)) fbsj=jsonObject.getString(Const.Field_zhuangli_Table.fbsj);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.flzt)) flzt=jsonObject.getString(Const.Field_zhuangli_Table.flzt);

            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.sxrq)) sxrq=jsonObject.getString(Const.Field_zhuangli_Table.sxrq);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.jsrq)) jsrq=jsonObject.getString(Const.Field_zhuangli_Table.jsrq);

            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.zlxq)) zlxq=jsonObject.getString(Const.Field_zhuangli_Table.zlxq);

            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.qnid1)) qnid1=jsonObject.getString(Const.Field_zhuangli_Table.qnid1);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.qnid2)) qnid2=jsonObject.getString(Const.Field_zhuangli_Table.qnid2);
            if(Funcs.jsonItemValid(jsonObject, Const.Field_zhuangli_Table.qnid3)) qnid3=jsonObject.getString(Const.Field_zhuangli_Table.qnid3);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(ZhuanLi o) {
        int i=o.zid-this.zid;
        return i;
    }
}
