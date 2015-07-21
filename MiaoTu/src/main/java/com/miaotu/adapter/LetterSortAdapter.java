package com.miaotu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.model.NativePhoneAddress;

import java.util.List;

/**
 * Created by Jayden on 2015/7/21.
 */
public class LetterSortAdapter extends BaseAdapter implements SectionIndexer {

    private List<NativePhoneAddress> addressList;
    private Context mContext;
    public LetterSortAdapter(Context mContext, List<NativePhoneAddress> addressList){
        this.addressList = addressList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return addressList == null?0:addressList.size();
    }

    @Override
    public Object getItem(int i) {
        return addressList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_phoneaddress, null);
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvNumber = (TextView) view.findViewById(R.id.tv_number);
            holder.cbBox = (CheckBox) view.findViewById(R.id.cb_add);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvNumber.setText(addressList.get(i).getNumber());
        holder.tvName.setText(addressList.get(i).getName());
        return view;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return addressList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = addressList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    class ViewHolder{
        TextView tvName;
        TextView tvNumber;
        CheckBox cbBox;
    }
}
