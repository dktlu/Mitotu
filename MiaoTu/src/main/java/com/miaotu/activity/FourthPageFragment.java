package com.miaotu.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;
import com.miaotu.view.CircleImageView;

public class FourthPageFragment extends BaseFragment implements View.OnClickListener {
    private View root;
    private FragmentManager fragmentManager;
    private FirstPageTab1Fragment mTab01;
    private FirstPageTab1Fragment mTab02;
    private int curPage;

    private CircleImageView iv_userhead;
    private TextView tv_username;
    private RelativeLayout rl_userinfo, rl_homepage, rl_setting, rl_hongbao,
            rl_order, rl_customer, rl_recommend;
    private TextView tv_left, tv_title, tv_right, tv_wantgo;
    private LinearLayout llFans,llFollow,llSearch;
    private TextView tvFansCount,tvFollowCount;
    private TextView tvAge;
    private ImageView ivGender;
    private LinearLayout llSexandAge;
    private RelativeLayout rlTip;
    private View line;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_fourth_page, container, false);
        findView();
        bindView();
        initUserInfo();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void bindView() {
        rl_userinfo.setOnClickListener(this);
        rl_homepage.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        rl_hongbao.setOnClickListener(this);
        rl_order.setOnClickListener(this);
        rl_customer.setOnClickListener(this);
        rl_recommend.setOnClickListener(this);
        tv_right.setVisibility(View.GONE);
        tv_left.setVisibility(View.GONE);
        tv_title.setText("我的");
        llSearch.setOnClickListener(this);
        llFollow.setOnClickListener(this);
        llFans.setOnClickListener(this);
        rlTip.setOnClickListener(this);
    }

    private void findView() {
        rlTip = (RelativeLayout) root.findViewById(R.id.rl_info_tip);
        line = root.findViewById(R.id.view1);
        tv_wantgo = (TextView) root.findViewById(R.id.tv_wantgo);
        tvAge = (TextView) root.findViewById(R.id.tv_age);
        ivGender = (ImageView) root.findViewById(R.id.iv_gender);
        llSexandAge = (LinearLayout) root.findViewById(R.id.ll_sexandage);
        llFans = (LinearLayout) root.findViewById(R.id.ll_fans);
        llFollow = (LinearLayout) root.findViewById(R.id.ll_follow);
        llSearch= (LinearLayout) root.findViewById(R.id.ll_search);
        tvFansCount = (TextView) root.findViewById(R.id.tv_fans_count);
        tvFollowCount = (TextView) root.findViewById(R.id.tv_follow_count);
        iv_userhead = (CircleImageView) root.findViewById(R.id.iv_userhead);
        tv_title = (TextView) root.findViewById(R.id.tv_title);
        tv_left = (TextView) root.findViewById(R.id.tv_left);
        tv_right = (TextView) root.findViewById(R.id.tv_right);
        rl_hongbao = (RelativeLayout) root.findViewById(R.id.rl_hongbao);
        rl_userinfo = (RelativeLayout) root.findViewById(R.id.rl_userinfo);
        rl_homepage = (RelativeLayout) root.findViewById(R.id.rl_homepage);
        rl_setting = (RelativeLayout) root.findViewById(R.id.rl_setting);
        rl_order = (RelativeLayout) root.findViewById(R.id.rl_order);
        rl_customer = (RelativeLayout) root.findViewById(R.id.rl_customtour);
        rl_recommend = (RelativeLayout) root.findViewById(R.id.rl_recommend);
        tv_username = (TextView) root.findViewById(R.id.tv_username);
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        String headimg = readPreference("headphoto");
        String wantgo = readPreference("wantgo");
        String age = readPreference("age");
        String name = readPreference("name");
        String gender = readPreference("gender");
        String followcount = readPreference("followcount");
        String fanscount = readPreference("fanscount");
        UrlImageViewHelper.setUrlDrawable(iv_userhead, headimg, R.drawable.default_avatar);
        if ("男".equals(gender)) {
            ivGender.setBackgroundResource(R.drawable.icon_boy);
        }else {
            ivGender.setBackgroundResource(R.drawable.icon_girl);
            llSexandAge.setBackgroundResource(R.drawable.bg_woman);
        }
        tvAge.setText(age);
        tv_username.setText(name);
        tvFansCount.setText(fanscount);
        tvFollowCount.setText(followcount);
        tv_wantgo.setText("想去"+wantgo);
        if (isEmpty()){
            line.setVisibility(View.GONE);
            rlTip.setVisibility(View.VISIBLE);
        }else {
            line.setVisibility(View.VISIBLE);
            rlTip.setVisibility(View.GONE);
        }
    }

    private void init() {
        fragmentManager = getChildFragmentManager();
    }

    @Override
    public void onClick(View view) {
        if (!Util.isNetworkConnected(getActivity())) {
            showToastMsg("当前未联网，请检查网络设置");
            return;
        }
        switch (view.getId()) {
            case R.id.rl_userinfo:
                Intent intent = new Intent();
                intent.setClass(FourthPageFragment.this.getActivity(),
                        EditUserInfoActivity.class);
                startActivityForResult(intent, 1001);
                break;
            case R.id.rl_homepage:
                if(!Util.isNetworkConnected(FourthPageFragment.this.getActivity())) {
                    showToastMsg("当前未联网，请检查网络设置");
                    return;
                }
                Intent homepageIntent = new Intent();
                String uid = readPreference("uid");
                homepageIntent.putExtra("uid", uid);
                homepageIntent.setClass(FourthPageFragment.this.getActivity(),
                        PersonCenterActivity.class);
                startActivityForResult(homepageIntent, 1001);
                break;
            case R.id.rl_setting:
                Intent settingIntent = new Intent();
                settingIntent.setClass(FourthPageFragment.this.getActivity(),
                        SettingActivity.class);
                startActivityForResult(settingIntent, 1001);
                break;
            case R.id.rl_hongbao:
                Intent hongbaoIntent = new Intent();
                hongbaoIntent.setClass(FourthPageFragment.this.getActivity(),
                        RedPackageActivity.class);
                startActivityForResult(hongbaoIntent, 1001);
                break;
            case R.id.rl_order:
                Intent orderIntent = new Intent(FourthPageFragment.this.getActivity(),
                        OrderListActivity.class);
                startActivity(orderIntent);
                return;
            case R.id.rl_customtour:  //约游秒旅团
                Intent customIntent = new Intent(FourthPageFragment.this.getActivity(),
                        TogetherCustomerTourActivity.class);
                startActivity(customIntent);
                return;
            case R.id.rl_recommend: //应用推荐
                Intent recommendIntent = new Intent();
                recommendIntent.setClass(FourthPageFragment.this.getActivity(),
                        AppRecommendActivity.class);
                startActivityForResult(recommendIntent, 1001);
                break;
            case R.id.ll_fans:
                Intent fansIntent = new Intent();
                fansIntent.setClass(FourthPageFragment.this.getActivity(),
                        MyLikeAndFansActivity.class);
                fansIntent.putExtra("flag", 1);
                startActivityForResult(fansIntent, 1001);
                break;
            case R.id.ll_follow:
                Intent followIntent = new Intent();
                followIntent.setClass(FourthPageFragment.this.getActivity(),
                        MyLikeAndFansActivity.class);
                followIntent.putExtra("flag", 0);
                startActivityForResult(followIntent, 1001);
                break;
            case R.id.ll_search:
                Intent searchIntent = new Intent();
                searchIntent.setClass(FourthPageFragment.this.getActivity(),
                        FindMFriendsActivity.class);
                startActivityForResult(searchIntent, 1);
                break;
            case R.id.rl_info_tip:
                Intent findIntent = new Intent();
                findIntent.setClass(FourthPageFragment.this.getActivity(),
                        Register1Activity.class);
                findIntent.putExtra("third","third");
                startActivityForResult(findIntent, 1001);
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    @SuppressLint("NewApi")
    public void setTabSelection(int index) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                curPage = 0;
                if (mTab01 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mTab01 = new FirstPageTab1Fragment();
                    transaction.add(R.id.id_content, mTab01);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mTab01);
                }
                break;
            case 1:
                curPage = 1;
                if (mTab02 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mTab02 = new FirstPageTab1Fragment();
                    transaction.add(R.id.id_content, mTab02);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mTab02);
                }
                break;
        }
//        transaction.commit();
        transaction.commitAllowingStateLoss(); //为了解决换量页面返回时报的错误，详情见http://blog.csdn.net/ranxiedao/article/details/8214936
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (mTab01 != null) {
            transaction.hide(mTab01);
        }
        if (mTab02 != null) {
            transaction.hide(mTab02);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == 1) {
            ((MainActivity) getActivity()).writePreference("movement_city", data.getStringExtra("city"));
        }
        if (requestCode == 1001 && resultCode == 1){
            initUserInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
    }

    /**
     * 判断必填字段是否全为空
     *
     * @return
     */
    private boolean isEmpty() {
        boolean empty = false;
        if (StringUtil.isBlank(readPreference("name")) ){
            empty = true;
            return empty;
        }
        if (StringUtil.isBlank(readPreference("gender")) ){
            empty = true;
            return empty;
        }
        if (StringUtil.isBlank(readPreference("age")) ){
            empty = true;
            return empty;
        }
        if (StringUtil.isBlank(readPreference("emotion")) ){
            empty = true;
            return empty;
        }
        if (StringUtil.isBlank(readPreference("wantgo")) ){
            empty = true;
            return empty;
        }
        return empty;
    }
}