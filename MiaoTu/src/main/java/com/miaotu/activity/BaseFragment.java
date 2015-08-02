package com.miaotu.activity;

import com.miaotu.R;
import com.miaotu.util.Util;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragment extends Fragment {
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass() + ""); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass() + ""); // 保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
	}

	/**
	 * 显示toast提示信息
	 * 
	 * @param msg
	 *            提示信息
	 */
	public void showToastMsg(String msg) {
		showToastMsg(msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示toast提示信息
	 * 
	 * @param msg
	 *            提示信息
	 * @param duration
	 *            显示时间长短
	 */
	protected void showToastMsg(String msg, int duration) {
		Toast.makeText(this.getActivity(), msg, duration).show();
	}
	public static final String NAME_COMMON = "COMMON";
	// *********************** 数据存储方法 ****************************
	/**
	 * SharedPreferences工具方法,用来读取一个值 如果没有读取到，会返回""
	 *
	 * @param key
	 * @return
	 */
	public String readPreference(String key) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(NAME_COMMON,
				getActivity().MODE_PRIVATE);
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	/**
	 * SharedPreferences工具方法,用来写入一个值
	 *
	 * @param key
	 * @param value
	 */
	public void writePreference(String key, String value) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(NAME_COMMON,
				getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 删除 SharedPreferences
	 *
	 * @param key
	 */
	public void deletePreference(String key) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(NAME_COMMON,
				getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * SharedPreferences工具方法,用来读取一个值 如果没有读取到，会返回""
	 *
	 * @param key
	 * @return
	 */
	public String readPreference(String xmlname, String key) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(xmlname,
				getActivity().MODE_PRIVATE);
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	/**
	 * SharedPreferences工具方法,用来写入一个值进自定义文件名
	 *
	 * @param key
	 * @param value
	 * @param xmlname
	 *            自定义xml的名字
	 */
	public void writePreference(String xmlname, String key, String value) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(xmlname,
				getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 删除信息
	 *
	 * @param xmlname
	 *            指定的信息文件
	 * @param key
	 *            特定的key值
	 */
	public void deletePreference(String xmlname, String key) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(xmlname,
				getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 删除特定信息
	 *
	 * @param xmlname
	 *            指定的信息文件
	 */
	public void deleteAllPreference(String xmlname) {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(xmlname,
				getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 显示自定义的Toast
	 *
	 * @param content
	 */
	private void showMyToast(String content) {
		View toastView = LayoutInflater.from(getActivity()).inflate(R.layout.toast_like, null);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, LinearLayout.LayoutParams.MATCH_PARENT);
		TextView tv = (TextView) toastView.findViewById(R.id.tv_content);
		tv.setLayoutParams(params);
		tv.setText(content);
		Toast toast = new Toast(getActivity());
		toast.setGravity(Gravity.TOP, 0, Util.dip2px(getActivity(), 44));
		tv.setAlpha(0.8f);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();
	}

}
