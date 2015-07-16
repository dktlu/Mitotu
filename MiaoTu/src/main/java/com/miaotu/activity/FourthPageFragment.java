package com.miaotu.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.util.Util;
import com.miaotu.view.CircleImageView;

public class FourthPageFragment extends BaseFragment implements View.OnClickListener {
    private View root;
    private FragmentManager fragmentManager;
    private FirstPageTab1Fragment mTab01;
    private FirstPageTab1Fragment mTab02;
    private int curPage;

    private ImageView iv_usergender;
    private CircleImageView iv_userhead;
    private TextView tv_username, tv_userage, tv_iden, tv_userfans;
    private RelativeLayout rl_userinfo, rl_homepage, rl_setting, rl_hongbao,
            rl_order, rl_customer, rl_recommend;
    private TextView tv_left, tv_title, tv_right;
    private LinearLayout llFans,llFollow,llSearch;
    private TextView tvFansCount,tvFollowCount;

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
        tv_userfans.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        llFollow.setOnClickListener(this);
        llFans.setOnClickListener(this);
    }

    private void findView() {
        llFans = (LinearLayout) root.findViewById(R.id.ll_fans);
        llFollow = (LinearLayout) root.findViewById(R.id.ll_follow);
        llSearch= (LinearLayout) root.findViewById(R.id.ll_search);
        tvFansCount = (TextView) root.findViewById(R.id.tv_fans_count);
        tvFollowCount = (TextView) root.findViewById(R.id.tv_follow_count);
        tv_userfans = (TextView) root.findViewById(R.id.tv_userfans);
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
        tv_iden = (TextView) root.findViewById(R.id.tv_iden);
        tv_userage = (TextView) root.findViewById(R.id.tv_userage);
        tv_username = (TextView) root.findViewById(R.id.tv_username);
        iv_usergender = (ImageView) root.findViewById(R.id.iv_usergender);
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        String headimg = readPreference("headphoto");
        String identity = readPreference("job");
        String age = readPreference("age");
        String name = readPreference("name");
        String gender = readPreference("gender");
        String followcount = readPreference("followcount");
        String fanscount = readPreference("fanscount");
        UrlImageViewHelper.setUrlDrawable(iv_userhead, headimg, R.drawable.default_avatar);
        if ("男".equals(gender)) {
            iv_usergender.setBackgroundResource(R.drawable.mine_boy);
        }else {
            iv_usergender.setBackgroundResource(R.drawable.mine_girl);
        }
        tv_iden.setText(identity);
        tv_userage.setText(age);
        tv_username.setText(name);
        tv_userfans.setText(followcount + "个关注|" + fanscount + "个粉丝");
        tvFansCount.setText(fanscount);
        tvFollowCount.setText(followcount);
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
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_userinfo:
                intent.setClass(FourthPageFragment.this.getActivity(),
                        EditUserInfoActivity.class);
                break;
            case R.id.rl_homepage:
                if(!Util.isNetworkConnected(FourthPageFragment.this.getActivity())) {
                    showToastMsg("当前未联网，请检查网络设置");
                    return;
                }
                String uid = readPreference("uid");
                intent.putExtra("uid", uid);
                intent.setClass(FourthPageFragment.this.getActivity(),
                        PersonCenterActivity.class);
                break;
            case R.id.rl_setting:
                intent.setClass(FourthPageFragment.this.getActivity(),
                        SettingActivity.class);
                break;
            case R.id.rl_hongbao:
                intent.setClass(FourthPageFragment.this.getActivity(),
                        RedPackageActivity.class);
                break;
            case R.id.rl_order:
                showToastMsg("还没开发");
                return;
            case R.id.rl_customtour:  //一起走
                showToastMsg("还没开发");
                return;
            case R.id.rl_recommend: //应用推荐
                intent.setClass(FourthPageFragment.this.getActivity(),
                        AppRecommendActivity.class);
                break;
            case R.id.tv_userfans:  //粉丝和关注
                intent.setClass(FourthPageFragment.this.getActivity(),
                        MyLikeAndFansActivity.class);
                break;
            case R.id.ll_fans:
                intent.setClass(FourthPageFragment.this.getActivity(),
                        MyLikeAndFansActivity.class);
                intent.putExtra("flag", 1);
                break;
            case R.id.ll_follow:
                intent.setClass(FourthPageFragment.this.getActivity(),
                        MyLikeAndFansActivity.class);
                intent.putExtra("flag", 0);
                break;
            default:
                break;
        }
        startActivityForResult(intent, 1001);
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
}