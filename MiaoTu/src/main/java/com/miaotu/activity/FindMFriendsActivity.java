package com.miaotu.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
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

import com.miaotu.R;
import com.miaotu.adapter.ContactListAdapter;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.Address;
import com.miaotu.result.AddressListResult;
import com.miaotu.result.BaseResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    private ContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_mfriends);

        initView();
        bindView();
        initData();
    }

    private void initView() {
        llAddress = (LinearLayout) this.findViewById(R.id.ll_address);
        llQQ = (LinearLayout) this.findViewById(R.id.ll_qq);
        llWeibo = (LinearLayout) this.findViewById(R.id.ll_weibo);
        llWX = (LinearLayout) this.findViewById(R.id.ll_wx);
        rvFriends = (RecyclerView) this.findViewById(R.id.rv_friends);
        rvRecommend = (RecyclerView) this.findViewById(R.id.rv_recommend);

    }

    private void bindView() {
        llWeibo.setOnClickListener(this);
        llWX.setOnClickListener(this);
        llQQ.setOnClickListener(this);
        llAddress.setOnClickListener(this);
    }

    private void initData() {
        getPhoneContacts();
        contactlist = new ArrayList<>();
        adapter = new ContactListAdapter(this, contactlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFriends.setLayoutManager(linearLayoutManager);
        rvFriends.setAdapter(adapter);
        String phones = "";
        for (String phone:mContactsNumber){
            phones+=phone+",";
        }
        Log.e("ERROR", phones.substring(0, phones.length() - 1));
        matchPhoneList(phones.substring(0, phones.length() - 1));
    }

    @Override
    public void onClick(View view) {

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
                    contactlist.clear();
                    contactlist.addAll(addressListResult.getAddressList());
                    adapter.notifyItemChanged(contactlist.size() - 1);
                    setAdapterHeght(contactlist.size(), 60);
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
    private void setAdapterHeght(int count, int base){
        int height = count*Util.dip2px(this, base);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.setMargins(60,0,0,0);
        rvFriends.setLayoutParams(params);
    }
}
