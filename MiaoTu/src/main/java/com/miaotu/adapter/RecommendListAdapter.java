package com.miaotu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.activity.BaseActivity;
import com.miaotu.async.BaseHttpAsyncTask;
import com.miaotu.http.HttpRequestUtil;
import com.miaotu.model.Address;
import com.miaotu.model.Recommend;
import com.miaotu.result.BaseResult;
import com.miaotu.result.LikeResult;
import com.miaotu.util.StringUtil;

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
                        ((BaseActivity)mContext).showToastMsg("取消喜欢成功");
                    }else {
                        view.setBackgroundResource(R.drawable.icon_minus);
                        count+=1;
                        ((BaseActivity)mContext).showToastMsg("喜欢成功");
                    }
                    ((BaseActivity)mContext).writePreference("followcount", count + "");
                }else {
                    if (StringUtil.isBlank(result.getMsg())){
                        ((BaseActivity)mContext).showToastMsg("添加好友失败");
                    }else {
                        ((BaseActivity)mContext).showToastMsg(result.getMsg());
                    }
                }
            }

            @Override
            protected LikeResult run(Void... params) {
                return HttpRequestUtil.getInstance().likeforparam(token, touid);
            }
        }.execute();
    }
}
