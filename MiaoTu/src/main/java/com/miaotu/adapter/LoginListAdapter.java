package com.miaotu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miaotu.R;
import com.miaotu.model.Account;
import com.miaotu.model.CustomTour;

import java.util.List;


/**
 * @author zhanglei
 * 
 */
public class LoginListAdapter extends ArrayAdapter<String> {
	private int layoutid;
	private LayoutInflater mLayoutInflater;
	public LoginListAdapter(Context context, int layoutid ,List<String> list) {
		super(context, layoutid, list);
		this.layoutid = layoutid;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder holder = null;
		String account = getItem(position);
//		if (convertView == null) {
//			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(layoutid, null);
			TextView tvName = (TextView) convertView
					.findViewById(R.id.tv_name);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}

		// 对ListView的Item中的控件的操作
		tvName.setText(account);
		return convertView;
	}

	public final class ViewHolder {
		public TextView name = null;
	}
}
