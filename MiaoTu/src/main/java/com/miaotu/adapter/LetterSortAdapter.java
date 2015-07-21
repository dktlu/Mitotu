package com.miaotu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.miaotu.R;
import com.miaotu.model.NativePhoneAddress;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Jayden on 2015/7/21.
 */
public class LetterSortAdapter extends BaseAdapter implements SectionIndexer {

    private List<NativePhoneAddress> addressList;
    private Context mContext;
    private List<String> numbers;
    public LetterSortAdapter(Context mContext, List<NativePhoneAddress> addressList,
                             List<String> numbers){
        this.mContext = mContext;
        this.addressList = addressList;
        this.numbers = numbers;
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
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_phoneaddress, null);
            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvNumber = (TextView) view.findViewById(R.id.tv_number);
            holder.cbBox = (CheckBox) view.findViewById(R.id.cb_add);
            holder.tvLetter = (TextView) view.findViewById(R.id.tv_letter);
            holder.llLetter = (LinearLayout) view.findViewById(R.id.ll_letter);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvNumber.setText(addressList.get(i).getNumber());
        holder.tvName.setText(addressList.get(i).getName());
        holder.cbBox.setTag(i);
        if (addressList.get(i).isSelected() == true){
            holder.cbBox.setChecked(true);
        }else {
            holder.cbBox.setChecked(false);
        }
        holder.cbBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    addressList.get((Integer) compoundButton.getTag()).setIsSelected(true);
                    numbers.add(addressList.get((Integer) compoundButton.getTag()).getNumber());
                }else {
                    addressList.get((Integer) compoundButton.getTag()).setIsSelected(false);
                    numbers.remove(addressList.get((Integer) compoundButton.getTag()).getNumber());
                }
            }
        });
        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(i);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(i == getPositionForSection(section)){
            holder.llLetter.setVisibility(View.VISIBLE);
            holder.tvLetter.setText(addressList.get(i).getSortLetters());
        }else{
            holder.llLetter.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public Object[] getSections() {
        return null;
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
        TextView tvLetter;
        LinearLayout llLetter;
    }
}
