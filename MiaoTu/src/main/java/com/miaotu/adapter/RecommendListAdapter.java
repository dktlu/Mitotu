package com.miaotu.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miaotu.R;
import com.miaotu.activity.BaseActivity;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.Address;
import com.miaotu.model.Recommend;
import com.miaotu.result.BaseResult;
import com.miaotu.result.LikeResult;
import com.miaotu.util.StringUtil;
import com.miaotu.util.Util;

import java.util.List;

/**
 * Created by Jayden on 2015/7/15.
 */
public class RecommendListAdapter extends
        RecyclerView.Adapter<RecommendListAdapter.ViewHolder>
        implements View.OnClickListener {

    private Context mContext;
    private List<Recommend> recommendList;
    private LayoutInflater inflater;

    public RecommendListAdapter(Context context, List<Recommend> recommendList){
        this.mContext = context;
        this.recommendList = recommendList;
        inflater = LayoutInflater.from(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_addresslist, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.ivControl.setTag(i);
        viewHolder.ivControl.setOnClickListener(this);
        viewHolder.ivControl.setBackgroundResource(R.drawable.icon_add);
        viewHolder.tvName.setText(recommendList.get(i).getNickname());
        viewHolder.tvPs.setText("手机联系人：" + recommendList.get(i).getNickname() + "正在使用妙途");
    }

    @Override
    public int getItemCount() {
        return recommendList == null?0:recommendList.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_control:
                like(((BaseActivity)mContext).readPreference("token"),
                        recommendList.get((int) view.getTag()).getUid(), (ImageView) view);
                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivControl;
        public TextView tvPs,tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivControl = (ImageView) itemView.findViewById(R.id.iv_control);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPs = (TextView) itemView.findViewById(R.id.tv_ps);
        }
    }

    private void like(final String token, final String touid, final ImageView view){
        new BaseHttpAsyncTask<Void, Void, LikeResult>(((BaseActivity)mContext)){

            @Override
            protected void onCompleteTask(LikeResult result) {
                if (result.getCode() == BaseResult.SUCCESS){
                    int count = 0;

                    if (!StringUtil.isBlank(((BaseActivity)mContext).readPreference("followcount"))){
                        count = Integer.parseInt(((BaseActivity)mContext).readPreference("followcount"));
                    }
                    if ("0".equals(result.getLikeInfo().getLid())){
                        view.setBackgroundResource(R.drawable.icon_add);
                        count-=1;
                        if (count < 0){
                            count = 0;
                        }
//                        ((BaseActivity)mContext).showToastMsg("取消喜欢成功");
                        showMyToast((BaseActivity)mContext, "取消喜欢成功");
                    }else {
                        view.setBackgroundResource(R.drawable.icon_minus);
                        count+=1;
                        showMyToast((BaseActivity)mContext, "喜欢成功");
//                        ((BaseActivity)mContext).showToastMsg("喜欢成功");
                    }
                    ((BaseActivity)mContext).writePreference("followcount", count + "");
                }else {
                    if (StringUtil.isBlank(result.getMsg())){
                        showMyToast((BaseActivity)mContext, "添加好友失败");
//                        ((BaseActivity)mContext).showToastMsg("添加好友失败");
                    }else {
                        showMyToast((BaseActivity)mContext, result.getMsg());
//                        ((BaseActivity)mContext).showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected LikeResult run(Void... params) {
                return HttpRequestUtil.getInstance().likeforparam(token, touid);
            }
        }.execute();
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
        tv.setAlpha(0.8f);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }
}
