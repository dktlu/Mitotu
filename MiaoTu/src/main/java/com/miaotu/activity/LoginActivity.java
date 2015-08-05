package com.miaotu.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.miaotu.R;
import com.miaotu.adapter.LoginListAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.db.LoginDbManager;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.Account;
import com.miaotu.model.RegisterInfo;
import com.miaotu.result.BaseResult;
import com.miaotu.result.LoginResult;
import com.miaotu.util.LogUtil;
import com.miaotu.util.MD5;
import com.miaotu.util.StringUtil;
import com.miaotu.view.LoadingDlg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends BaseActivity implements View.OnClickListener,PlatformActionListener {
    private TextView tvLeft,tvRight,tvTitle,tvFindPassword;
    private Button btnLogin;
    private EditText etPassword;
    private ImageView ivWeibo,ivQQ,ivWechat;
    protected Dialog loadingDlg; // 加载对话框
    private LoginDbManager dbManager;
    private List<String> accounts;
    private AutoCompleteTextView tvAuto;
    private LoginListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        bindView();
        init();
    }
private void findView(){
    tvAuto = (AutoCompleteTextView) findViewById(R.id.tv_auto);
    tvRight = (TextView) findViewById(R.id.tv_right);
    tvLeft = (TextView) findViewById(R.id.tv_left);
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvFindPassword = (TextView) findViewById(R.id.tv_find_password);
    btnLogin = (Button) findViewById(R.id.btn_login);
    etPassword = (EditText) findViewById(R.id.et_password);
    ivWeibo = (ImageView) findViewById(R.id.iv_weibo);
    ivQQ = (ImageView) findViewById(R.id.iv_qq);
    ivWechat = (ImageView) findViewById(R.id.iv_wechat);
}
private void bindView(){
    tvRight.setOnClickListener(this);
    tvLeft.setOnClickListener(this);
    tvFindPassword.setOnClickListener(this);
    btnLogin.setOnClickListener(this);
    ivWeibo.setOnClickListener(this);
    ivQQ.setOnClickListener(this);
    ivWechat.setOnClickListener(this);
}

    private void init(){
        tvRight.setText("注册");
        tvTitle.setText("登录");
        ShareSDK.initSDK(this);
        dbManager = new LoginDbManager(this);
//        dbManager.delete();
        accounts = new ArrayList<>();
        accounts = dbManager.queryName();
        adapter = new LoginListAdapter(this, R.layout.item_phone, accounts);
        tvAuto.setAdapter(adapter);
}
    //登陆
    private void login(final RegisterInfo registerInfo, final boolean isTel) {
        new BaseHttpAsyncTask<Void, Void, LoginResult>(this, false) {
            @Override
            protected void onCompleteTask(LoginResult result) {
                if(btnLogin==null){
                    return;
                }
                if (result.getCode() == BaseResult.SUCCESS) {
//                    LoginActivity.this.showMyToast("登录成功！");
                    writePreference("uid", result.getLogin().getUid());
                    writePreference("id", result.getLogin().getId());
                    writePreference("token",result.getLogin().getToken());
                    writePreference("name",result.getLogin().getName());
                    writePreference("age",result.getLogin().getAge());
                    writePreference("gender",result.getLogin().getGender());
                    writePreference("address",result.getLogin().getAddress());
                    writePreference("emotion",result.getLogin().getMaritalstatus());
                    writePreference("wantgo",result.getLogin().getWantgo());
                    writePreference("tags",result.getLogin().getTags());
                    writePreference("headphoto",result.getLogin().getHeadPhoto());
                    writePreference("job",result.getLogin().getJob());
                    writePreference("fanscount", result.getLogin().getFanscount());
                    writePreference("followcount", result.getLogin().getFollowcount());
                    writePreference("luckmoney", result.getLogin().getLuckymoney());
                    writePreference("email", result.getLogin().getEmail());
                    writePreference("phone", result.getLogin().getPhone());
                    writePreference("login_status","in");
                    writePreference("workarea",result.getLogin().getWorkarea());
                    writePreference("school",result.getLogin().getSchool());
                    writePreference("freetime",result.getLogin().getFreetime());
                    writePreference("budget",result.getLogin().getBudget());
                    writePreference("home",result.getLogin().getHome());
                    writePreference("lifearea",result.getLogin().getLifearea());
                    writePreference("ordercount", "0");
                    writePreference("customcount", "0");
                    if (!StringUtil.isBlank(result.getLogin().getOrderCount())){
                        writePreference("ordercount", result.getLogin().getOrderCount());
                    }
                    if (!StringUtil.isBlank(result.getLogin().getCustomCount())) {
                        writePreference("customcount", result.getLogin().getCustomCount());
                    }

                    EMChatManager.getInstance().login(MD5.md5(readPreference("uid")), readPreference("token"),
                            new EMCallBack() {//回调
                                @Override
                                public void onSuccess() {
                                    new LoadIMInfoThread().start();
                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }

                                @Override
                                public void onError(int code, String message) {
                                    Log.d("main", "登录聊天服务器失败！");
                                }

                            });
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                    String sysDatetime = fmt.format(calendar.getTime())+readPreference("token");
                    if(readPreference("everyday").equals(sysDatetime)){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivityForResult(intent, 1);
                    }else{
                        Intent intent = new Intent(LoginActivity.this,EveryDayPicActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    if (!dbManager.isExit(tvAuto.getText().toString())){
                        dbManager.save(tvAuto.getText().toString(), etPassword.getText().toString());
                    }
                    setResult(1);
                    finish();
                } else {
                    if(isTel){
                        if(StringUtil.isEmpty(result.getMsg())){
                            LoginActivity.this.showMyToast("登录失败！");
                        }else{
                            LoginActivity.this.showMyToast(result.getMsg());
                        }
                    }/*else{
                        register(registerInfo);
                    }*/
                }
            }

            @Override
            protected LoginResult run(Void... params) {
                return HttpRequestUtil.getInstance().login(registerInfo);
            }

            @Override
            protected void finallyRun() {
                if(loadingDlg!=null){
                    loadingDlg.dismiss();
                }
            }
        }.execute();
    }
    class LoadIMInfoThread extends Thread{
        @Override
        public void run() {
            super.run();
            LogUtil.d("main", "登录聊天服务器成功！");
            long start = System.currentTimeMillis();
            EMChatManager.getInstance().loadAllConversations();
            try {
                EMGroupManager.getInstance().getGroupsFromServer();
            } catch (EaseMobException e) {
                e.printStackTrace();
            }
            EMGroupManager.getInstance().loadAllGroups();

//            long costTime = System.currentTimeMillis() - start;
//            //等待sleeptime时长
//            if (sleepTime - costTime > 0) {
//                try {
//                    Thread.sleep(sleepTime - costTime);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
    /**
     * 注册
     * @param registerInfo
     */
    private void register(final RegisterInfo registerInfo) {
        new BaseHttpAsyncTask<Void, Void, LoginResult>(this, true) {
            @Override
            protected void onCompleteTask(LoginResult result) {
                if(btnLogin==null){
                    return;
                }
                if (result.getCode() == BaseResult.SUCCESS) {
//                    login(registerInfo,false);
//                    LoginActivity.this.showMyToast("登录成功！");
                    writePreference("uid", result.getLogin().getUid());
                    writePreference("id", result.getLogin().getId());
                    writePreference("token",result.getLogin().getToken());
                    writePreference("name",result.getLogin().getName());
                    writePreference("age",result.getLogin().getAge());
                    writePreference("gender",result.getLogin().getGender());
                    writePreference("address",result.getLogin().getAddress());
                    writePreference("emotion",result.getLogin().getMaritalstatus());
                    writePreference("wantgo",result.getLogin().getWantgo());
                    writePreference("tags",result.getLogin().getTags());
                    writePreference("headphoto",result.getLogin().getHeadPhoto());
                    writePreference("job",result.getLogin().getJob());
                    writePreference("fanscount", result.getLogin().getFanscount());
                    writePreference("followcount", result.getLogin().getFollowcount());
//                    writePreference("wxid", result.getLogin().getWxunionid());
//                    writePreference("qqid", result.getLogin().getQqopenid());
//                    writePreference("sinaid", result.getLogin().getSinauid());
                    writePreference("luckmoney", result.getLogin().getLuckymoney());
//                    writePreference("status", result.getLogin().getStatus());   //1身份证验证 0未验证
                    writePreference("email", result.getLogin().getEmail());
                    writePreference("phone", result.getLogin().getPhone());
                    writePreference("login_status","in");
                    writePreference("workarea",result.getLogin().getWorkarea());
                    writePreference("school",result.getLogin().getSchool());
                    writePreference("freetime",result.getLogin().getFreetime());
                    writePreference("budget",result.getLogin().getBudget());
                    writePreference("home",result.getLogin().getHome());
                    writePreference("lifearea",result.getLogin().getLifearea());

                    EMChatManager.getInstance().login(MD5.md5(readPreference("uid")), readPreference("token"),
                            new EMCallBack() {//回调
                                @Override
                                public void onSuccess() {
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//
//                                        }
//                                    });
                                    new LoadIMInfoThread().start();
                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }

                                @Override
                                public void onError(int code, String message) {
                                    Log.d("main", "登录聊天服务器失败！");
                                }

                            });
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                    String sysDatetime = fmt.format(calendar.getTime())+readPreference("token");
                    if(readPreference("everyday").equals(sysDatetime)){
                        if (isEmpty(result)){
                            Intent intent = new Intent(LoginActivity.this,Register1Activity.class);
                            intent.putExtra("name", result.getLogin().getName());
                            intent.putExtra("gender", result.getLogin().getGender());
                            intent.putExtra("age", result.getLogin().getAge());
                            intent.putExtra("emotion", result.getLogin().getMaritalstatus());
                            intent.putExtra("wantgo", result.getLogin().getWantgo());
                            intent.putExtra("third", "third");
                            startActivityForResult(intent, 1);
                        }else {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivityForResult(intent, 1);
                        }
                    }else{
                        Intent intent = new Intent(LoginActivity.this,EveryDayPicActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    setResult(1);
                    finish();
                } else {
                    if(StringUtil.isEmpty(result.getMsg())){
                        LoginActivity.this.showMyToast("登陆失败！");
                    }else{
                        LoginActivity.this.showMyToast(result.getMsg());
                    }
                }
            }

            @Override
            protected LoginResult run(Void... params) {
                return HttpRequestUtil.getInstance().register(registerInfo);
            }

            @Override
            protected void finallyRun() {
                if(loadingDlg!=null){
                    loadingDlg.dismiss();
                }
            }

        }.execute();
    }
    private boolean validate(){
        if(StringUtil.isBlank(tvAuto.getText().toString())){
            LoginActivity.this.showMyToast("请输入您的手机号！");
            return false;
        }
        if(!StringUtil.isPhoneNumber(tvAuto.getText().toString())){
            LoginActivity.this.showMyToast("账号填写有误，请检查后重新填写");
            return false;
        }
        if(StringUtil.isBlank(etPassword.getText().toString())){
            LoginActivity.this.showMyToast("请输入您的登录密码！");
            return false;
        }
        return true;
    }
    //执行授权,获取用户信息
    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        loadingDlg = LoadingDlg.show(this);
        plat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==1){
            setResult(1);
            finish();
            //告诉上一个页面结束
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_right:
                Intent registerIntent = new Intent(LoginActivity.this, Register2Activity.class);
                startActivity(registerIntent);
                break;
            case R.id.tv_find_password:
                Intent intent = new Intent(LoginActivity.this,FindPasswordActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_login:
                if(validate()){
                    final RegisterInfo registerInfo = new RegisterInfo();
                    registerInfo.setPhone(tvAuto.getText().toString());
                    registerInfo.setPassword(etPassword.getText().toString());
                    login(registerInfo, true);
                }
                break;
            case R.id.iv_qq:
                Platform qq = ShareSDK.getPlatform(QZone.NAME);
                authorize(qq);
                break;
            case R.id.iv_weibo:
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(weibo);
                break;
            case R.id.iv_wechat:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);
                break;
        }
    }
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> res) {
        LogUtil.d("登陆成功！");

        if(platform.getName().equals(QZone.NAME)){
            writePreference("logintype", "qq");
            try{
                Iterator iter = res.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    LogUtil.d(key+":"+val);
                    }
                String info = "qq登陆信息：";
                //qq登陆
                info+=" id:"+platform.getDb().getUserId();
                info+=" 头像地址："+res.get("figureurl_qq_2").toString();
                info+=" 昵称："+res.get("nickname").toString();
                info+=" 城市："+res.get("city").toString();
                info+=" 省份："+res.get("province").toString();
                info+=" 性别："+res.get("gender").toString();
                LogUtil.d(info);
                final RegisterInfo registerInfo = new RegisterInfo();
                registerInfo.setOpenid(platform.getDb().getUserId());
                registerInfo.setHeadurl(res.get("figureurl_qq_2").toString());
                registerInfo.setNickname(res.get("nickname").toString());
                registerInfo.setCity(res.get("city").toString());
                registerInfo.setProvince(res.get("province").toString());
                registerInfo.setGender(res.get("gender").toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        register(registerInfo);
                    }
                });
            }catch (Exception e){e.printStackTrace();}
        }
        //微博
        if (platform.getName().equals(SinaWeibo.NAME)) {
            writePreference("logintype", "weibo");
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            platDB.getUserId();
            platDB.getToken();
            platDB.getUserGender();
            platDB.getUserIcon();
            platDB.getUserId();
            platDB.getUserName();
            writePreference("weibo_token,", platDB.getToken());
            writePreference("weibo_id", platDB.getUserId());
            writePreference("weibo_name", platDB.getUserName());
            LogUtil.d("微博信息    " + platDB.getToken() + "," + platDB.getUserGender() + "," + platDB.getUserIcon()
                    + "," + platDB.getUserId() + "," + platDB.getUserName());
            final RegisterInfo registerInfo = new RegisterInfo();
            registerInfo.setUsid(platDB.getUserId());
            if(platDB.getUserGender().equals("m")){
                registerInfo.setGender("男");
            }
            if(platDB.getUserGender().equals("w")){
                registerInfo.setGender("女");
            }
            registerInfo.setNickname(platDB.getUserName());
            registerInfo.setHeadurl(platDB.getUserIcon());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    register(registerInfo);
                }
            });

        }
        //微信
        if(platform.getName().equals(Wechat.NAME)){
            writePreference("logintype", "wx");
            try {
                Iterator iter = res.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    LogUtil.d("微信信息"+key + ":" + val);
                }
            }catch (Exception e){e.printStackTrace();}
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            platDB.getUserId();
            platDB.getToken();
            platDB.getUserGender();
            platDB.getUserIcon();
            platDB.getUserName();
            LogUtil.d("微信信息    " + platDB.getToken() + "," + platDB.getUserGender() + "," + platDB.getUserIcon()
                    + "," + platDB.getUserId() + "," + platDB.getUserName()+"////"+res.get("unionid").toString());
            final RegisterInfo registerInfo = new RegisterInfo();
//            registerInfo.setUnionid(platDB.getUserId());
            registerInfo.setUnionid(res.get("unionid").toString());
            if(platDB.getUserGender().equals("m")){
                registerInfo.setGender("男");
            }
            if(platDB.getUserGender().equals("w")){
                registerInfo.setGender("女");
            }
            registerInfo.setNickname(platDB.getUserName());
            registerInfo.setHeadurl(platDB.getUserIcon());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    register(registerInfo);
                }
            });
        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        if(loadingDlg!=null){
            loadingDlg.dismiss();
        }
    }

    @Override
    public void onCancel(Platform platform, int i) {
        if(loadingDlg!=null){
            loadingDlg.dismiss();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Exit");
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }

    private boolean isEmpty(LoginResult result){
        boolean flag = false;
        if (StringUtil.isBlank(result.getLogin().getName())||
                StringUtil.isBlank(result.getLogin().getGender())||
                StringUtil.isBlank(result.getLogin().getAge())||
                StringUtil.isBlank(result.getLogin().getMaritalstatus())||
                StringUtil.isBlank(result.getLogin().getWantgo())){
            flag = true;
        }
        return flag;
    }
}
