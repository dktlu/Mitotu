package com.miaotu.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.DateUtils;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.miaotu.R;
import com.miaotu.activity.BaseActivity;
import com.miaotu.activity.BaseFragmentActivity;
import com.miaotu.activity.CustomTourDetailActivity;
import com.miaotu.activity.HomeBannerWebActivity;
import com.miaotu.activity.JoinedListActivity;
import com.miaotu.activity.PersonCenterActivity;
import com.miaotu.activity.TogetherDetailActivity;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.PhotoInfo;
import com.miaotu.model.Together;
import com.miaotu.result.BaseResult;
import com.miaotu.util.LogUtil;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;
import com.miaotu.view.CircleImageView;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.util.CommonUtils;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Jayden
 */
public class TogetherlistAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater = null;
    private List<Together> mList = null;
    private Context mContext;
    private boolean isMine;         //是否是我的
    private boolean isOwner;       //是否是我发起的

    public TogetherlistAdapter(Context context, List<Together> list, boolean isMine, boolean isOwner) {
        mList = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.isMine = isMine;
        this.isOwner = isOwner;
    }

    @Override
    public int getCount() {
        return this.mList != null ? this.mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        BannerViewHolder bannerholder = null;
        View bannerView = null;
        View togetherView = null;
        if (StringUtil.isBlank(mList.get(position).getExtend())) {
            if (convertView != null && convertView.getTag() instanceof BannerViewHolder){
                convertView = null;
            }
            togetherView = convertView;
            if (togetherView == null) {
                holder = new ViewHolder();
                togetherView = mLayoutInflater.inflate(
                        R.layout.item_together, null);
                holder.tvNickname = (TextView) togetherView.findViewById(R.id.tv_name);
                holder.ivHeadPhoto = (CircleImageView) togetherView.findViewById(R.id.iv_head_photo);
                holder.ivGender = (ImageView) togetherView
                        .findViewById(R.id.iv_gender);
                holder.tvAge = (TextView) togetherView
                        .findViewById(R.id.tv_age);
//			holder.tvJob = (TextView) convertView.findViewById(R.id.tv_identity);
                holder.tvTime = (TextView) togetherView
                        .findViewById(R.id.tv_date);
                holder.tvDate = (TextView) togetherView
                        .findViewById(R.id.tv_tag_date);
                holder.tvDesCity = (TextView) togetherView
                        .findViewById(R.id.tv_tag_des);
                holder.tvNum = (TextView) togetherView
                        .findViewById(R.id.tv_tag_require);
                holder.tvFee = (TextView) togetherView
                        .findViewById(R.id.tv_tag_price);
                holder.tvComment = (TextView) togetherView
                        .findViewById(R.id.tv_introduce);
                holder.tvDistance = (TextView) togetherView
                        .findViewById(R.id.tv_distance);

                holder.tvJoinCount = (TextView) togetherView
                        .findViewById(R.id.tv_join_count);
                holder.tvCommentCount = (TextView) togetherView
                        .findViewById(R.id.tv_comment_count);
                holder.layoutImg = (LinearLayout) togetherView
                        .findViewById(R.id.layout_img);
                holder.ivLike = (ImageView) togetherView
                        .findViewById(R.id.iv_like);
                holder.tvJoinList = (TextView) togetherView
                        .findViewById(R.id.tv_join_list);
                holder.tvWantGo = (TextView) togetherView
                        .findViewById(R.id.tv_wantgo);
                holder.ivTop = (ImageView) togetherView.
                        findViewById(R.id.iv_top);
                togetherView.setTag(holder);
            } else {
                holder = (ViewHolder) togetherView.getTag();
            }
            convertView = togetherView;
        } else {
            if (convertView != null && convertView.getTag()instanceof ViewHolder){
                convertView = null;
            }
            bannerView = convertView;
            if (bannerView == null){
                bannerholder = new BannerViewHolder();
                bannerView = mLayoutInflater.inflate(R.layout.item_together_banner, null);
                bannerholder.ivBanner = (ImageView) bannerView.findViewById(R.id.iv_banner);
                bannerView.setTag(bannerholder);
            }else {
                bannerholder = (BannerViewHolder) bannerView.getTag();
            }
            convertView = bannerView;
        }
        if (StringUtil.isBlank(mList.get(position).getExtend())) {
            if (isMine) { //我的
                holder.tvDistance.setVisibility(View.GONE);
                holder.ivLike.setVisibility(View.GONE);
            } else {
                holder.tvDistance.setVisibility(View.VISIBLE);
                holder.ivLike.setVisibility(View.VISIBLE);
            }
            if (isOwner) {        //我发起的
                holder.tvJoinList.setVisibility(View.VISIBLE);
                holder.tvJoinCount.setVisibility(View.GONE);
                holder.tvCommentCount.setVisibility(View.GONE);
            }
            holder.tvJoinList.setTag(position);
            holder.tvJoinList.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Util.isNetworkConnected(mContext)) {
                        ((BaseFragmentActivity) mContext).showToastMsg("当前未联网，请检查网络设置");
                        return;
                    }
                    int pos = (int) view.getTag();
                    Intent intent = new Intent(mContext, JoinedListActivity.class);
                    intent.putExtra("flag", "1");
                    intent.putExtra("yid", mList.get(pos).getId());
                    intent.putExtra("title", mList.get(pos).getStartDate() +
                            "一起去" + mList.get(pos).getDesCity());
                    mContext.startActivity(intent);
                }
            });

            // 对ListView的Item中的控件的操作
            UrlImageViewHelper.setUrlDrawable(holder.ivHeadPhoto,
                    mList.get(position).getHeadPhoto() + "100x100",
                    R.drawable.default_avatar);
            holder.ivHeadPhoto.setTag(position);
            holder.ivHeadPhoto.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Util.isNetworkConnected(mContext)) {
                        return;
                    }
                    int pos = (int) view.getTag();
                    Intent intent = new Intent(mContext, PersonCenterActivity.class);
                    intent.putExtra("uid", mList.get(pos).getUid());
                    mContext.startActivity(intent);
                }
            });
            holder.tvNickname.setText(mList.get(position).getNickname());
            holder.tvDesCity.setText(mList.get(position).getDesCity());
            try {
                holder.tvTime.setText(DateUtils.getTimestampString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mList.get(position).getPublishDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvNum.setText("需要" + mList.get(position).getNum() + "人");
            if ("0".equals(mList.get(position).getNum())) {
                holder.tvNum.setText("人数不限");
            }
            holder.tvFee.setText(mList.get(position).getFee());
            holder.tvComment.setText(mList.get(position).getComment());
            if ("true".equals(mList.get(position).getIsjoin())||
                    mList.get(position).getIsjoin() == "true"){
                holder.tvJoinCount.setText("已报名");
            }else {
                holder.tvJoinCount.setText("报名");
            }
            if ("true".equals(mList.get(position).getIstop())||
                    mList.get(position).getIstop() == "true"){
                holder.ivTop.setVisibility(View.VISIBLE);
            }else {
                holder.ivTop.setVisibility(View.GONE);
            }
            holder.tvCommentCount.setText("评论" + mList.get(position).getReplyCount() + "人");
            if (mList.get(position).isLike()) {
                holder.ivLike.setBackgroundResource(R.drawable.icon_like);
            } else {
                holder.ivLike.setBackgroundResource(R.drawable.icon_unlike);
            }
            holder.ivLike.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    like(position);
                }
            });
            holder.tvAge.setText(mList.get(position).getAge() + "岁");
        /*if (StringUtil.isBlank(mList.get(position).getJob())){
            holder.tvJob.setVisibility(View.GONE);
        }
        holder.tvJob.setText(mList.get(position).getJob());*/
            if (mList.get(position).getGender().equals("男")) {
                holder.ivGender.setBackgroundResource(R.drawable.icon_man);
            } else {
                holder.ivGender.setBackgroundResource(R.drawable.icon_woman);
            }
            holder.tvDate.setText(mList.get(position).getStartDate());
            if (mList.get(position).getPicList().size() == 0) {
                holder.layoutImg.setVisibility(View.GONE);
            } else {
                holder.layoutImg.setVisibility(View.VISIBLE);
            }
            //添加图片
            int limit = 0;
            if (mList.get(position).getPicList().size() > 3) {
                limit = 3;
            } else {
                limit = mList.get(position).getPicList().size();
            }
            if (limit == 0) {
                holder.layoutImg.setVisibility(View.GONE);
            } else {
                holder.layoutImg.setVisibility(View.VISIBLE);
            }
            holder.layoutImg.removeAllViews();
            for (int i = 0; i < limit; i++) {
                PhotoInfo photoInfo = mList.get(position).getPicList().get(i);
                if (i == 2 && mList.get(position).getPicList().size() > 3) {
                    //添加图片个数textview
                    ImageView imageView = new ImageView(mContext);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.dip2px(mContext, 80), Util.dip2px(mContext, 80));
                    imageView.setLayoutParams(params);
                    TextView tvCount = new TextView(mContext);
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(Util.dip2px(mContext, 80), Util.dip2px(mContext, 20));
                    params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    tvCount.setGravity(Gravity.CENTER);
                    tvCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    tvCount.setLayoutParams(params1);
                    tvCount.setText("共" + mList.get(position).getPicList().size() + "张图片");
                    tvCount.setTextColor(mContext.getResources().getColor(R.color.white));
                    tvCount.setBackgroundColor(mContext.getResources().getColor(R.color.transparen_black));
                    RelativeLayout relativeLayout = new RelativeLayout(mContext);
                    relativeLayout.addView(imageView);
                    relativeLayout.addView(tvCount);
                    holder.layoutImg.addView(relativeLayout);
                    UrlImageViewHelper.setUrlDrawable(imageView,
                            photoInfo.getUrl() + "240x240",
                            R.drawable.icon_default_image);
                } else {
                    ImageView imageView = new ImageView(mContext);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            Util.dip2px(mContext, 80), Util.dip2px(mContext, 80));
                    params.rightMargin = Util.dip2px(mContext, 10);
                    imageView.setLayoutParams(params);
                    holder.layoutImg.addView(imageView);
                    UrlImageViewHelper.setUrlDrawable(imageView,
                            photoInfo.getUrl() + "240x240",
                            R.drawable.icon_default_image);
                }
            }
            holder.tvDistance.setText(mList.get(position).getDistance() + "km");
            holder.tvWantGo.setText("想去 " + mList.get(position).getWantgo());
        } else {
            UrlImageViewHelper.setUrlDrawable(bannerholder.ivBanner,
                    mList.get(position).getPicurl());
            bannerholder.ivBanner.setTag(position);
            bannerholder.ivBanner.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    if ("1".equals(mList.get(pos).getType())) {    //进入秒旅团详情页
                        Intent customtourIntent = new Intent(mContext, CustomTourDetailActivity.class);
                        customtourIntent.putExtra("id", mList.get(pos).getExtend());
                        customtourIntent.putExtra("picurl", mList.get(pos).getPicurl());
                        mContext.startActivity(customtourIntent);
                    } else if ("2".equals(mList.get(pos).getType())) {    //进入一起去详情页
                        Intent togetherIntent = new Intent(mContext, TogetherDetailActivity.class);
                        togetherIntent.putExtra("id", mList.get(pos).getExtend());
                        togetherIntent.putExtra("picurl", mList.get(pos).getPicurl());
                        mContext.startActivity(togetherIntent);
                    } else if ("3".equals(mList.get(pos).getType())) {    //网页
                        Intent webIntent = new Intent(mContext, HomeBannerWebActivity.class);
                        webIntent.putExtra("url", mList.get(pos).getExtend());
                        mContext.startActivity(webIntent);
                    } else {    //其他
                        Toast.makeText(mContext, "活动已过期", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return convertView;
    }

    private void like(final int position) {
        new BaseHttpAsyncTask<Void, Void, BaseResult>((BaseFragmentActivity) mContext, true) {
            @Override
            protected void onCompleteTask(BaseResult result) {
                if (result.getCode() == BaseResult.SUCCESS) {
                    if (mList.get(position).isLike()) {
//                        ((BaseFragmentActivity)mContext).showToastMsg("取消喜欢成功！");
                        showMyToast(((BaseFragmentActivity) mContext), "取消喜欢成功！");
                        mList.get(position).setIsLike(false);
                    } else {
//                        ((BaseFragmentActivity)mContext).showToastMsg("喜欢成功！");
                        showMyToast(((BaseFragmentActivity) mContext), "喜欢成功！");
                        mList.get(position).setIsLike(true);
                    }
                    notifyDataSetChanged();
                } else {
                    if (StringUtil.isEmpty(result.getMsg())) {
                        ((BaseFragmentActivity) mContext).showToastMsg("失败！");
                    } else {
                        ((BaseFragmentActivity) mContext).showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected BaseResult run(Void... params) {
                return HttpRequestUtil.getInstance().likeTogether(
                        ((BaseFragmentActivity) mContext).readPreference("token"),
                        mList.get(position).getId());
            }

        }.execute();
    }

    public class ViewHolder {
        private TextView tvNickname = null;
        private CircleImageView ivHeadPhoto = null;
        private ImageView ivGender = null;
        private TextView tvAge = null;
        //        private TextView tvJob= null;
        private TextView tvTime = null;
        private TextView tvDate = null;
        private TextView tvDesCity = null;
        private TextView tvNum = null;
        private TextView tvFee = null;
        private TextView tvComment = null;
        private TextView tvDistance = null;
        private TextView tvJoinCount = null;
        private TextView tvCommentCount = null;
        private ImageView ivLike = null;
        private LinearLayout layoutImg = null;
        private TextView tvJoinList = null;
        private TextView tvWantGo = null;
        private ImageView ivTop;
    }

    class BannerViewHolder {
        private ImageView ivBanner;
    }

    /**
     * 显示自定义的Toast
     *
     * @param context
     * @param content
     */
    private void showMyToast(Activity context, String content) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_like, null);
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView tv = (TextView) toastView.findViewById(R.id.tv_content);
        tv.setLayoutParams(params);
        tv.setText(content);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, Util.dip2px(context, 44));
        changeBackground(tv, 0.8f);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }

    //改变背景透明度
    private void changeBackground(View view, float value){
        view.setAlpha(value);
    }
}
