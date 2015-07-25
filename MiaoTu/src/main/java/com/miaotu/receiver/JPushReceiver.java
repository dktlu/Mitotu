package com.miaotu.receiver;

import java.io.IOException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.easemob.util.DateUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miaotu.jpush.InviteMessage;
import com.miaotu.jpush.MessageCountDatabaseHelper;
import com.miaotu.jpush.MessageDatabaseHelper;
import com.miaotu.jpush.LikeMessage;
import com.miaotu.util.LogUtil;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
	private Context mContext;
	private static final String TAG = "JPush";
	public static final String ACTION_JPUSH_INVITE_MESSAGE_RECIEVE = "action_jpush_invite_message_recieve";
	public static final String ACTION_JPUSH_SYS_MESSAGE_RECIEVE = "action_jpush_sys_message_recieve";
	public static final String ACTION_JPUSH_TOPIC_MESSAGE_RECIEVE = "action_jpush_topic_message_recieve";
	public static final String ACTION_UPDATE_MESSAGE_UI = "action_update_message_ui";
	public static final String ACTION_JPUSH_LOGIN_MESSAGE_RECIEVE = "action_jpush_login_message_recieve";
	public static final String ACTION_JPUSH_EXIT_MESSAGE_RECIEVE = "action_jpush_exit_message_recieve";
	public static final String ACTION_JPUSH_MY_INFO_MESSAGE_RECIEVE = "action_jpush_my_info_message_recieve";
	public static final String ACTION_JPUSH_INTEREST_MESSAGE_RECIEVE = "action_jpush_interest_message_recieve";
//	public static final String ACTION_TEST = "action_test";
	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity
//        	Intent i = new Intent(context, TestActivity.class);
//        	i.putExtras(bundle);
//        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
		//PushMessage pushMessage = new PushMessage();
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		try {
			//pushMessage = mapper.readValue(message,PushMessage.class);
			InviteMessage inviteMessage = new InviteMessage();
			JSONObject rootJson = new JSONObject(message);
			if(rootJson.get("Type").equals("invite")){
				//邀请消息
				inviteMessage = mapper.readValue(rootJson.getJSONObject("item").toString(), InviteMessage.class);
				inviteMessage.setMessageStatus("0");
				MessageDatabaseHelper helper = new MessageDatabaseHelper(context);
				long l = helper.saveInviteMessage(inviteMessage);
				LogUtil.d("插入收到的邀请消息"+l);
				Intent msgIntent = new Intent(ACTION_JPUSH_INVITE_MESSAGE_RECIEVE);
				msgIntent.putExtras(new Bundle());
				context.sendOrderedBroadcast(msgIntent,null);
			}else if(rootJson.get("Type").equals("user")){
				//消息-喜欢
				LikeMessage likeMessage = new LikeMessage();
				likeMessage = mapper.readValue(rootJson.getJSONObject("Content").toString(), LikeMessage.class);
				SharedPreferences sharedPreferences = mContext.getSharedPreferences("COMMON",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("like_date", "" + DateUtils.getTimestampString(new Date(Long.parseLong(rootJson.get("Time") + "000"))));
				editor.commit();
				MessageDatabaseHelper helper = new MessageDatabaseHelper(context);
				long l = helper.saveSysMessage(likeMessage);
				LogUtil.d("插入收到的喜欢人消息：喜欢"+l);
				Intent msgIntent = new Intent(ACTION_JPUSH_SYS_MESSAGE_RECIEVE);
				msgIntent.putExtras(new Bundle());
				context.sendOrderedBroadcast(msgIntent,null);
			}else if(rootJson.get("Type").equals("like")){
                //消息-喜欢约游
				LikeMessage likeMessage = new LikeMessage();
				likeMessage = mapper.readValue(rootJson.getJSONObject("Content").toString(), LikeMessage.class);
				SharedPreferences sharedPreferences = mContext.getSharedPreferences("COMMON",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("tour_like_date", "" + DateUtils.getTimestampString(new Date(Long.parseLong(rootJson.get("Time") + "000"))));
				editor.putString("tour_like_name", "" + likeMessage.getNickname());
				try {
					editor.putString("tour_like_count", "" + (1+Integer.parseInt(sharedPreferences.getString("tour_like_count", "0"))));
				}catch (Exception e){
					e.printStackTrace();
					editor.putString("tour_like_count", "1");
				}
				editor.commit();
                LogUtil.d("插入收到的系统消息：喜欢约游");
                Intent msgIntent = new Intent(ACTION_JPUSH_SYS_MESSAGE_RECIEVE);
                msgIntent.putExtras(new Bundle());
                context.sendOrderedBroadcast(msgIntent,null);
            }else if(rootJson.get("Type").equals("join")){
				//消息-参加约游
				LikeMessage likeMessage = new LikeMessage();
				likeMessage = mapper.readValue(rootJson.getJSONObject("Content").toString(), LikeMessage.class);
				SharedPreferences sharedPreferences = mContext.getSharedPreferences("COMMON",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("tour_join_date", "" + DateUtils.getTimestampString(new Date(Long.parseLong(rootJson.get("Time") + "000"))));
				editor.putString("tour_join_name", "" + likeMessage.getContent());
				try {
					editor.putString("tour_join_count", "" + (1+Integer.parseInt(sharedPreferences.getString("tour_join_count", "0"))));
				}catch (Exception e){
					e.printStackTrace();
					editor.putString("tour_join_count", "1");
				}
				editor.commit();
				LogUtil.d("插入收到的系统消息：参加约游");
				Intent msgIntent = new Intent(ACTION_JPUSH_SYS_MESSAGE_RECIEVE);
				msgIntent.putExtras(new Bundle());
				context.sendOrderedBroadcast(msgIntent,null);
            }else if(rootJson.get("Type").equals("reply")){
				//消息-参加约游
				LikeMessage likeMessage = new LikeMessage();
				likeMessage = mapper.readValue(rootJson.getJSONObject("Content").toString(), LikeMessage.class);
				SharedPreferences sharedPreferences = mContext.getSharedPreferences("COMMON",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("tour_comment_date", "" + DateUtils.getTimestampString(new Date(Long.parseLong(rootJson.get("Time") + "000"))));
				editor.putString("tour_comment_name", "" + likeMessage.getNickname());
				try {
					editor.putString("tour_comment_count", "" + (1+Integer.parseInt(sharedPreferences.getString("tour_comment_count", "0"))));
				}catch (Exception e){
					e.printStackTrace();
					editor.putString("tour_comment_count", "1");
				}
				editor.commit();
				LogUtil.d("插入收到的系统消息：评论约游");
				Intent msgIntent = new Intent(ACTION_JPUSH_SYS_MESSAGE_RECIEVE);
				msgIntent.putExtras(new Bundle());
				context.sendOrderedBroadcast(msgIntent,null);
            }else if(rootJson.get("Type").equals("system")){
				//消息-系统消息
				LikeMessage likeMessage = new LikeMessage();
				likeMessage = mapper.readValue(rootJson.getJSONObject("Content").toString(), LikeMessage.class);
				SharedPreferences sharedPreferences = mContext.getSharedPreferences("COMMON",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();

				editor.putString("sys_date", "" + DateUtils.getTimestampString(new Date(Long.parseLong(rootJson.get("Time") + "000"))));
				editor.putString("sys_name", "" + likeMessage.getContent());
				try {
					editor.putString("sys_count", "" + (1+Integer.parseInt(sharedPreferences.getString("sys_count", "0"))));
				}catch (Exception e){
					e.printStackTrace();
					editor.putString("sys_count", "1");
				}
				editor.commit();
				LogUtil.d("插入收到的系统消息：系统消息");
				Intent msgIntent = new Intent(ACTION_JPUSH_SYS_MESSAGE_RECIEVE);
				msgIntent.putExtras(new Bundle());
				context.sendOrderedBroadcast(msgIntent,null);
            }else if(rootJson.get("Type").equals("state")){
				//消息-参加约游
				SharedPreferences sharedPreferences = mContext.getSharedPreferences("COMMON",
						mContext.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("topic_comment", "1");
				editor.commit();
				LogUtil.d("插入收到的系统消息：妙友回复");
				Intent msgIntent = new Intent(ACTION_JPUSH_TOPIC_MESSAGE_RECIEVE);
				msgIntent.putExtras(new Bundle());
				context.sendOrderedBroadcast(msgIntent,null);
            }
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
 catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
