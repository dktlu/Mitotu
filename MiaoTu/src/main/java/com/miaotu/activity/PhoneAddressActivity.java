package com.miaotu.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.adapter.LetterSortAdapter;
import com.miaotu.model.NativePhoneAddress;
import com.miaotu.util.CharacterParser;
import com.miaotu.util.PinyinComparator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhoneAddressActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvLeft,tvTitle,tvRight;
    private ListView lvAddress;
    private List<NativePhoneAddress> phoneAddressList;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_address);
        initView();
        findView();
        initData();
    }

    private void initView(){
        tvLeft = (TextView) this.findViewById(R.id.tv_left);
        tvRight = (TextView) this.findViewById(R.id.tv_right);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
        lvAddress = (ListView) this.findViewById(R.id.lv_address);
    }

    private void findView(){
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
    }

    private void initData(){
        tvTitle.setText("手机通讯录朋友");
        tvRight.setText("邀请");
        phoneAddressList = new ArrayList<>();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        getPhoneContacts();
        filledData(phoneAddressList);
        lvAddress.setAdapter(new LetterSortAdapter(this, phoneAddressList));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_right:
                showToastMsg("该功能还没开发");
                break;
        }
    }

    /**
     * 为ListView填充数据
     * @param
     * @return
     */
    private List<NativePhoneAddress> filledData(List<NativePhoneAddress> addressList){
        for(int i=0; i<addressList.size(); i++){
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(addressList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                addressList.get(i).setSortLetters(sortString.toUpperCase());
            }else{
                addressList.get(i).setSortLetters("#");
            }
        }
        Collections.sort(phoneAddressList, pinyinComparator);
        return addressList;

    }

    /**
     * 得到手机通讯录联系人信息*
     */
    private void getPhoneContacts() {
        ContentResolver resolver = this.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                NativePhoneAddress address = new NativePhoneAddress();
                address.setName(contactName);
                address.setNumber(phoneNumber);
                phoneAddressList.add(address);
            }

            phoneCursor.close();
        }
    }
}
