package cn.lxbest.wb2020.checkonline.Tool;


import java.util.ArrayList;
import java.util.List;

public class Const {

    public static final String domain = "cn.lxbest.wb2020.checkonline";
    public static final String server = "http://39.100.102.110";
    public static final String qnserver = "http://qnyeyimg.lxbest.cn";
    public static final String contentType="application/json";


    public static class Key_SharedPref {
        public static String Account = "acc"; //用于保存账户信息的key
    }

    public static class Env {
        /**
         * 开发环境+客户端假数据
         */
        public static final int DEV_TD = 1;
        /**
         * 开发环境+服务器ok
         */
        public static final int DEV_OK = 2;
        /**
         * 生产环境
         */
        public static final int PROD = 3;

    }

    //role
    public static class Role{
        public static int Admin=990;//管理
        public static int member=980;//普通员工

        public static String Admin_value="管理员";
        public static String member_value="普通员工";

        public static List<String> roles = new ArrayList<String>() {{
            add(Admin_value);
            add(member_value);
        }};

        public static List<Integer> roleCode = new ArrayList<Integer>() {{
            add(Admin);
            add(member);
        }};
    }



    public static class Key_Resp {

        public static String Code = "code";
        public static String Data = "data";


    }
    /**请求url*/
    public static class Key_Resp_Path{

        //向服务器拿qntoken
        public static String QnToken = "/qntoken";

       //登录/获取验证码请求
        public static String login ="login";

       //当天出勤情况
        public static String daykq="daykq";
        //月考勤情况
        public static String monkq ="monkq";
        //隔一段时间将自己的wifi传给服务器
        public static String sendwifi ="sendwifi";

        //得到所有专利列表
        public static String zhuanli="zhuanli";
        //得到我的专利
        public static String wdzhuanli="wdzhuanli";

        //提交专利
        public static String postzl="postzl";
    }


    //考勤相关字段
    public static class Field_Table_KaoQin{

        public static String sfqd="sfqd";//是否签到
        public static String qdsj="qdsj";//签到时间

        public static String month ="month";//考勤月份
        public static String days ="days";//这个月份出勤天数

        public static String cqts="cqts";//当月出勤天数
        public static String qqts="qqts";//当月缺勤天数
    }

    //用户字段
    public static class Field_Table_User{
        public static String uid ="uid";//用户id
        public static String name ="name";//员工姓名
        public static String phone ="phone";//电话号码
        public static String gsmc="company";//公司名称
        public static String role="role";//角色
    }

    public static String YZM="yzm";//验证码



    //专利
    public static class Field_zhuangli_Table{
        public static String zid="zid";
        public static String zname="zname";//名称
        public static String zlh="zlh";//专利号

        public static String zlfl="zlfl";//专利分类

        public static String fbsj="fbsj";//发布时间
        public static String flzt="flzt";//法律状态
        public static String sxrq="sxrq";//专利生效日期
        public static String jsrq="jsrq";//结束日期

        public static String zlxq="zlxq";//专利详情

        public static String qnid1="qnid1";//第1张图id
        public static String qnid2="qnid2";//第2张图id
        public static String qnid3="qnid3";//第3张图id

    }

}
