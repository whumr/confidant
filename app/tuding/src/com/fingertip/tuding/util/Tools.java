package com.fingertip.tuding.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.account.LoginActivity;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.common.UserSession;
import com.fingertip.tuding.entity.MessageEntity;
import com.fingertip.tuding.entity.MessageEntity.MessageDbEntity;
import com.fingertip.tuding.entity.UserEntity;
import com.fingertip.tuding.info.PublishEventActivity;
import com.fingertip.tuding.main.MainActivity;
import com.fingertip.tuding.main.widget.MapPositionSelectionActivity;
import com.fingertip.tuding.my.UserInfoActivity;
import com.fingertip.tuding.util.UmengConfig.EVENT;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.widget.PicPreviewActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;

public class Tools {
	
	public static Bitmap toRoundCorner(Bitmap bitmap) {
		return toRoundCorner(bitmap, bitmap.getWidth() / 2);
	}
	
	/**
	 * 
	 * ͼƬԲ��
	 * @param bitmap:��Ҫת����Բ�ǵ�ͼƬ
	 * @param pixels:Բ�ǵĶ�������ֵԽ��Բ��Խ��
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}//end toRoundCorner
	
	
	/** �ַ���64λ���� **/
	public static String decodeString(String encode){
		return new String(Base64.decode(encode.getBytes(), Base64.DEFAULT));
	}
	
	/** �ַ���64λ���� **/
	public static String encodeString(String encode_string){
		return new String(Base64.encodeToString(encode_string.getBytes(), Base64.DEFAULT));
	}
	
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * �ַ�����ʽ
	 * 
	 * @param str
	 * @return 
	 */
	public static String strToFormat(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
		String returnString = "";
		Date date = null;
		try {
			date = format.parse(str);
			returnString = format.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;
	}//end StrToDate
	
	/**
	 * �ַ�����ʽ
	 * 
	 * @param str
	 * @return 
	 */
	public static String strToFormatDay(String str) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
		SimpleDateFormat format2 = new SimpleDateFormat("MM��dd��", Locale.getDefault());
		String returnString = "";
		Date date = null;
		try {
			date = format.parse(str);
			returnString = format2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;
	}//end StrToDate
	
	/**
	 * �ַ���ת��������
	 * 
	 * @param str
	 * @param formatString
	 * @return date
	 */
	public static Date strToDate(String str) {
		return strToDate(str, null);
	}

	/**
	 * �ַ���ת��������
	 * 
	 * @param str
	 * @param formatString
	 * @return date
	 */
	public static Date strToDate(String str, String formatString) {
		if(formatString == null){
			formatString = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			date = new Date();
//			e.printStackTrace();
		}
		return date;
	}
	
	
	public static void toastShort(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void toastLeng(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public static boolean checkLogin(Context context) {
		UserSession session = UserSession.getInstance();
		if (!session.isLogin()) {
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			context.startActivity(intent);
			return false;
		}
		return true;
	}
	
	/**
	 * �����
	 * @param context
	 */
	public static void pubEvent(Context context) {
		Intent intent = new Intent();
//		intent.setClass(context, PublishInfoActivity.class);
		intent.setClass(context, PublishEventActivity.class);
		context.startActivity(intent);
	}

	/**
	 * ѡ������
	 * @param context
	 * @param request_code
	 */
	public static void choosePosition(Activity context, int request_code) {
		Intent intent = new Intent();
		intent.setClass(context, MapPositionSelectionActivity.class);
		context.startActivityForResult(intent, request_code);
	}
	
	/**
	 * �һ����ת����ҳ
	 * @param context
	 */
	public static void searchEvent(Context context) {
		Globals.clearActivityList(false);
	}

	/**
	 * ��ת����������
	 * @param context
	 * @param event_id
	 */
	public static void openEvent(Context context, String event_id) {
//		OverlayEntity overlayEntity = new OverlayEntity();
//		overlayEntity.actionid = event_id;
//		Intent intent = new Intent();
//		intent.setClass(context, MapShowPositionActivity.class);
//		intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
//		context.startActivity(intent);
		Intent intent = new Intent();
		intent.setClass(context, MainActivity.class);
		intent.putExtra(BaseActivity.EXTRA_PARAM, event_id);
		context.startActivity(intent);
	}
	
	/**
	 * ��ת���û��������
	 * @param context
	 * @param id
	 */
	public static void openUser(Context context, String id) {
		Intent intent = new Intent();
		intent.setClass(context, UserInfoActivity.class);
		intent.putExtra(UserInfoActivity.KEY_USER_ID, id);
		context.startActivity(intent);
		MobclickAgent.onEvent(context, EVENT.CLICK_USER, id);
	}

	/**
	 * ��ת���û��������
	 * @param context
	 * @param user
	 */
	public static void openUser(Context context, UserEntity user) {
		Intent intent = new Intent();
		intent.setClass(context, UserInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(UserInfoActivity.KEY_USER, user);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	public static List<MessageEntity> getMessages(Context context, String receiver_id, int count, int page) {
		List<MessageDbEntity> list = null;
		DbUtils db = DbUtils.create(context);
		try {
			list = db.findAll(Selector.from(MessageDbEntity.class).where("receiver_id" ,"=", receiver_id).orderBy("send_time", true)
					.limit(count).offset(count * (page - 1)));
		} catch (DbException e) {
			Log.e("getMessages", e.getMessage(), e);
		}
		return MessageEntity.parseDbEntity(list);
	}
	
	public static void saveMessages(Context context, List<MessageEntity> list) {
		if (list != null && !list.isEmpty()) {
			List<MessageDbEntity> db_list = new ArrayList<MessageDbEntity>();
			for (MessageEntity msg : list)
				db_list.add(msg.msg_db);
			DbUtils db = DbUtils.create(context);
			try {
				db.saveAll(db_list);
			} catch (DbException e) {
				Log.e("saveMessages", e.getMessage(), e);
			}
		}
	}
	
	/**
	 * ɾ�����ݿ��е���Ϣ
	 * @param context
	 * @param receiver_id
	 * @param msg_ids
	 */
	public static void deleteMessages(Context context, String receiver_id, List<String> msg_ids) {
		if (!Validator.isEmptyString(receiver_id) && !Validator.isEmptyList(msg_ids)) {
			DbUtils db = DbUtils.create(context);
			StringBuilder buffer = new StringBuilder("delete from message where sender_id = '")
			.append(receiver_id).append("' and msg_id in(");
			StringBuilder ids = new StringBuilder();
			for (String msg_id : msg_ids) {
				ids.append("'").append(msg_id).append("',");
			}
			buffer.append(ids.subSequence(0, ids.length() - 1)).append(")");
			try {
				db.execNonQuery(buffer.toString());
			} catch (DbException e) {
				Log.e("deleteMessages", e.getMessage(), e);
			}
		}
	}
	
	public static String getTimeStr(long send_time) {
		long now = System.currentTimeMillis();
		long time = (now - send_time) / 1000 / 60;
		//һ��������
		if (time <= 0)
			return "�ո�";
		//һСʱ��
		long tmp = time / 60;
		if (tmp < 1)
			return time + "����ǰ";
		//24Сʱ��
		time = tmp;
		tmp /= 24;
		Calendar now_c = Calendar.getInstance();
		now_c.setTimeInMillis(now);
		Calendar send_c = Calendar.getInstance();
		send_c.setTimeInMillis(send_time);
		int M = send_c.get(Calendar.MONTH) + 1;
		int D = send_c.get(Calendar.DAY_OF_MONTH);
		if (tmp < 1) {
			if (D == now_c.get(Calendar.DAY_OF_MONTH))
				return time + "Сʱǰ";
			else
				return "����";
		}
		if (tmp < 3) {
			int days = getDays(now_c, send_c);
			if (days == 1)
				return "����";
			if (days == 2)
				return "ǰ��";
		}
//		//һ������
//		time = tmp;
//		tmp /= 30;
//		if (tmp < 0)
//			return time + "��ǰ";
//		//һ����
//		time = tmp;
//		tmp /= 12;
//		if (tmp < 0)
//			return time + "��ǰ";
		return M + "��" + D + "��"; 
	}
	
	private static int getDays(Calendar now, Calendar before) {
		int y1 = now.get(Calendar.YEAR);
		int y2 = before.get(Calendar.YEAR);
		if (y1 == y2)
			return now.get(Calendar.DAY_OF_YEAR) - before.get(Calendar.DAY_OF_YEAR);
		else {
			int d1 = now.get(Calendar.DAY_OF_YEAR);
			while (y1 > y2) {
				y1--;
				now.set(Calendar.YEAR, y1 - 1);
				d1 += now.getMaximum(Calendar.DAY_OF_YEAR);
			}
			return d1 - before.get(Calendar.DAY_OF_YEAR);
		}
	}
	
	public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
	
	public static HttpUtils getHttpUtils() {
		return new HttpUtils(ServerConstants.HTTP_TIME_OUT);
	}
	
	public static void previewPics(Context context, ArrayList<String> pics, int index) {
		Intent intent = new Intent();
		intent.setClass(context, PicPreviewActivity.class);
		intent.putStringArrayListExtra(PicPreviewActivity.KEY_PICS, pics);
		intent.putExtra(PicPreviewActivity.KEY_INDEX, index);
		context.startActivity(intent);
	}
	
	public static Bitmap compressImage(Bitmap image, int kb) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while (baos.toByteArray().length / 1024 > kb) {	//ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��		
			baos.reset();//����baos�����baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options /= 2;//ÿ�ζ�����
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}

	public static Bitmap compressImage(String path, int kb) {
		Bitmap image = BitmapFactory.decodeFile(path);
		return compressImage(image, kb);
	}
	
	public static void viewWeb(Context context, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		context.startActivity(intent);
	}
	
	public static String getShortString(String str, int length, int dot_count) {
		if (Validator.isEmptyString(str) || str.length() <= length)
			return str;
		StringBuilder buffer = new StringBuilder(str.substring(0, length));
		for (int i = 0; i < dot_count; i++)
			buffer.append(".");
		return buffer.toString();
	}
	
	public static String convertLine(String str) {
		return str.replaceAll("\\\\n", "\\\n");
	}
	
	public static String getDistince(int meters) {
		if (meters <= 0)
			return "δ֪";
		return meters < 1000 ? meters + "m" : (meters / 1000) + "km";
	}
}
