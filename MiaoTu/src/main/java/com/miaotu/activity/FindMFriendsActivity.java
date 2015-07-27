package com.miaotu.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miaotu.R;
import com.miaotu.adapter.ContactListAdapter;
import com.miaotu.adapter.RecommendListAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.Address;
import com.miaotu.model.Recommend;
import com.miaotu.result.AddressListResult;
import com.miaotu.result.BaseResult;
import com.miaotu.result.RecommendListResult;
import com.miaotu.result.WeiboResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class FindMFriendsActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llQQ, llWX, llWeibo, llAddress;
    private RecyclerView rvFriends, rvRecommend;
    /**
     * 获取库Phone表字段*
     */
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};

    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    private ArrayList<String> mContactsName = new ArrayList<String>();
    private ArrayList<String> mContactsNumber = new ArrayList<String>();
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
    private List<Address> contactlist;
    private List<Recommend> recommendList;
    private ContactListAdapter contactsadapter;
    private RecommendListAdapter recommendadapter;
    private TextView tvChange;
    private TextView tvLeft,tvTitle, tvRight;
    private LinearLayout llFriend,llShareArea;
    private TextView tvFriendCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mfriends);

        initView();
        bindView();
        initData();
    }

    private void initView() {
        llShareArea = (LinearLayout) this.findViewById(R.id.ll_share_area);
        tvLeft = (TextView) this.findViewById(R.id.tv_left);
        tvRight = (TextView) this.findViewById(R.id.tv_right);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
        llFriend = (LinearLayout) this.findViewById(R.id.ll_friend);
        tvFriendCount = (TextView) this.findViewById(R.id.tv_friend_count);
        llAddress = (LinearLayout) this.findViewById(R.id.ll_address);
        llQQ = (LinearLayout) this.findViewById(R.id.ll_qq);
        llWeibo = (LinearLayout) this.findViewById(R.id.ll_weibo);
        llWX = (LinearLayout) this.findViewById(R.id.ll_wx);
        rvFriends = (RecyclerView) this.findViewById(R.id.rv_friends);
        rvRecommend = (RecyclerView) this.findViewById(R.id.rv_recommend);
        tvChange = (TextView) this.findViewById(R.id.tv_change);
    }

    private void bindView() {
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        llWeibo.setOnClickListener(this);
        llWX.setOnClickListener(this);
        llQQ.setOnClickListener(this);
        llAddress.setOnClickListener(this);
        tvChange.setOnClickListener(this);
    }

    private void initData() {
        if ("register".equals(getIntent().getStringExtra("register"))){
            tvTitle.setText("添加手机好友");
            tvRight.setText("跳过");
            llShareArea.setVisibility(View.GONE);
            tvLeft.setVisibility(View.GONE);
        }else {
            tvTitle.setText("寻找妙友");
            tvRight.setVisibility(View.GONE);
        }
        contactlist = new ArrayList<>();
        recommendList = new ArrayList<>();
        contactsadapter = new ContactListAdapter(this, contactlist);
        recommendadapter = new RecommendListAdapter(this, recommendList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFriends.setLayoutManager(linearLayoutManager);
        rvFriends.setAdapter(contactsadapter);
        rvRecommend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecommend.setAdapter(recommendadapter);
        if ("qq".equals(readPreference("logintype"))){
            llFriend.setVisibility(View.GONE);
            rvFriends.setVisibility(View.GONE);
        }else if("wx".equals(readPreference("logintype"))) {
            llFriend.setVisibility(View.GONE);
            rvFriends.setVisibility(View.GONE);
        }else if ("weibo".equals(readPreference("logintype"))){
            llFriend.setVisibility(View.GONE);
            rvFriends.setVisibility(View.GONE);
        }else {
            llFriend.setVisibility(View.VISIBLE);
            rvFriends.setVisibility(View.VISIBLE);
            getPhoneContacts();
            if (mContactsNumber.size() > 1){
                String phones = "";
                for (String phone:mContactsNumber){
                    phones+=phone+",";
                }
                matchPhoneList(phones.substring(0, phones.length() - 1));
            }else {
                showToastMsg("手机没有通讯录");
                llFriend.setVisibility(View.GONE);
            }
        }
        getRecommendList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_change:
                getRecommendList();
                break;
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_right:
                Intent intent = new Intent();
                intent.setClass(FindMFriendsActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_address:
                Intent addressIntent = new Intent();
                addressIntent.setClass(FindMFriendsActivity.this,
                        PhoneAddressActivity.class);
                startActivity(addressIntent);
                break;
            case R.id.ll_weibo:
                getFollowList(readPreference("weibo_token"),
                        readPreference("weibo_id"),readPreference("weibo_name"));
                break;
            case R.id.ll_wx:
                ShareSDK.initSDK(this);
                Wechat.ShareParams wcsp = new Wechat.ShareParams();
                String headurl = "";
                if (StringUtil.isBlank(headurl)) {
                    wcsp.setShareType(Platform.SHARE_TEXT);
                } else {
//                    wcsp.setShareType(Platform.SHARE_WEBPAGE);
//                    wcsp.setImageUrl(headurl + "200x200");
//                    wcsp.setUrl("http://m.miaotu.com/ShareLine/?yid=" + yid);
                }
                wcsp.setTitle("妙途");
                wcsp.setText("妙途" + "\n http://m.miaotu.com/");
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(new PlatFormListener());
                wechat.share(wcsp);
                break;
            case R.id.ll_qq:
                ShareSDK.initSDK(this);
                QQ.ShareParams qqsp = new QQ.ShareParams();
                qqsp.setTitle("妙途");
                qqsp.setTitleUrl("http://m.miaotu.com/");
                qqsp.setText("http://m.miaotu.com/");
//                qqsp.setImageUrl("");
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(new PlatFormListener());
                qq.share(qqsp);
                break;
        }
    }

    /**
     * 得到手机通讯录联系人信息*
     */
    private void getPhoneContacts() {
        ContentResolver resolver = this.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_head);
                }

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsPhonto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    /**
     * 匹配手机通讯录
     */
    private void matchPhoneList(final String phones){
        new BaseHttpAsyncTask<Void, Void, AddressListResult>(this, true){

            @Override
            protected void onCompleteTask(AddressListResult addressListResult) {
                if (addressListResult.getCode() == BaseResult.SUCCESS){
                    if (rvFriends == null){
                        return;
                    }
                    if (addressListResult.getAddressList().size() < 1){

                        llFriend.setVisibility(View.GONE);
                        rvFriends.setVisibility(View.GONE);
                        return;
                    }
                    contactlist.clear();
                    contactlist.addAll(addressListResult.getAddressList());
                    contactsadapter.notifyItemChanged(contactlist.size() - 1);
                    setAdapterHeght(contactlist.size(), 60, rvFriends);
                    tvFriendCount.setText("您有"+contactlist.size()+"个QQ的好友已经注册了妙途\\n关注后和TA们一起，看更大的世界");
                }else {
                    if (StringUtil.isBlank(addressListResult.getMsg())){
                        showToastMsg("匹配通讯录失败");
                    }else {
                        showToastMsg(addressListResult.getMsg());
                    }
                }
            }

            @Override
            protected AddressListResult run(Void... params) {
                return HttpRequestUtil.getInstance().matchPhones(readPreference("token"), phones);
            }
        }.execute();
    }

    /**
     * 设置recyclerview的高度
     * @param count item个数
     * @param base 每个item高度
     */
    private void setAdapterHeght(int count, int base, RecyclerView view){
        int height = count*Util.dip2px(this, base);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.setMargins(60,0,0,0);
        view.setLayoutParams(params);
    }

    /**
     * 获取寻找妙友的推荐好友
     */
    private void getRecommendList(){
        new BaseHttpAsyncTask<Void, Void, RecommendListResult>(this, true){

            @Override
            protected void onCompleteTask(RecommendListResult recommendListResult) {
                if (recommendListResult.getCode() == BaseResult.SUCCESS){
                    if (rvRecommend == null){
                        return;
                    }
                    recommendList.clear();
                    recommendList.addAll(recommendListResult.getRecommendList());
                    recommendadapter.notifyItemChanged(recommendList.size() - 1);
                    setAdapterHeght(recommendList.size(), 60, rvRecommend);
                }else {
                    if (StringUtil.isBlank(recommendListResult.getMsg())){
                        showToastMsg("推荐好友获取失败");
                    }else {
                        showToastMsg(recommendListResult.getMsg());
                    }
                }
            }

            @Override
            protected RecommendListResult run(Void... params) {
                return HttpRequestUtil.getInstance().getRecommendList(readPreference("token"));
            }
        }.execute();
    }

    private void getFollowList(final String access_token, final String uid, final String screen_name){
        new BaseHttpAsyncTask<Void, Void, WeiboResult>(this, true){

            @Override
            protected void onCompleteTask(WeiboResult weiboResult) {
                Log.e("ERROR", "总数/"+weiboResult.getTotal_number());
                showToastMsg("总数/"+weiboResult.getTotal_number());
            }

            @Override
            protected WeiboResult run(Void... params) {
                return HttpRequestUtil.getInstance().getWeiboList(access_token, uid, screen_name);
            }
        }.execute();
    }

    private class PlatFormListener implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            showToastMsg("发送完成");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            showToastMsg("发送失败");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            showToastMsg("取消发送");
        }
    }
}
