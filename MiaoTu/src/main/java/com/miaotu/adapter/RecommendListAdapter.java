package com.miaotu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.model.Address;
import com.miaotu.model.Recommend;

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
        viewHolder.tvPs.setText("手机联系人："+recommendList.get(i).getNickname()+"正在使用妙途");
    }

    @Override
    public int getItemCount() {
        return recommendList == null?0:recommendList.size();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_control:

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
}
