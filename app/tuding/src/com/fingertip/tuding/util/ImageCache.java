package com.fingertip.tuding.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.util.http.common.ServerConstants;
import com.fingertip.tuding.util.http.common.UploadImgEntity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

public class ImageCache {

	private static String IMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
			+ Globals.PATH_CACHE + File.separator + Globals.IMG_DIR + File.separator;
	private static String IMG_FORMAT = ".h";
	private static String IMG_HD_FORMAT = ".hd.h";
	private static String IMG_TMP_FORMAT = ".tmp";
	
	private static String IMG_UPLOAD_SMALL = "small.u";
	private static String IMG_UPLOAD_BIG = "big.u";
	
	private static String IMG_CUT = "cut.u";
	
	public static String getUserImgPath(String user_id) {
		return getUserImgPath(user_id, true, false);
	}

	public static String getUserImgPath(String user_id, boolean small) {
		return getUserImgPath(user_id, small, false);
	}

	public static String getUserImgPath(String user_id, boolean small, boolean tmp) {
		String img_path = IMG_PATH + user_id;
		if (tmp)
			img_path += IMG_TMP_FORMAT;
		return img_path += (small ? IMG_FORMAT : IMG_HD_FORMAT);
	}
	
	public static Bitmap getUserImg(String user_id, boolean small) {
		return getCompressImg(getUserImgPath(user_id, small, false), 0);
	}
	
	public static Bitmap getUserImg(String user_id, boolean small, int kb) {
		return getCompressImg(getUserImgPath(user_id, small, false), kb);
	}
	
	public static boolean saveUserImg(Bitmap img, String user_id, boolean small, boolean tmp) {
		if (checkImgDir())
			return FileUtil.saveImage(img, getUserImgPath(user_id, small, tmp));
		return false;
	}
	
	public static boolean saveTmpImg(String user_id, boolean small) {
		File tmp_img = new File(getUserImgPath(user_id, small, true));
		if (!tmp_img.exists())
			return false;
		else {
			File img = new File(getUserImgPath(user_id, small));
			return tmp_img.renameTo(img);
		}
	}
	
	private static boolean checkImgDir() {
		File dir = new File(IMG_PATH);
		if (!dir.exists())
			return dir.mkdirs();
		return true;
	}
	
	/**
	 * 加载本地用户头像
	 * 
	 * @param user_id
	 * @param head_img
	 * @return
	 */
	public static boolean setUserHeadImg(String user_id, ImageView head_img) {
		String img_path = getUserImgPath(user_id);
		if (new File(img_path).exists()) {
			head_img.setImageBitmap(Tools.toRoundCorner(BitmapFactory.decodeFile(img_path)));
			return true;
		}
		return false;
	}
	public static void loadUserHeadImg(final String down_url, final String user_id, final SharedPreferenceUtil sp,
			final BitmapUtils bitmapUtils, final ImageView image) {
		loadUserHeadImg(down_url, user_id, sp, bitmapUtils, image, null);
	}
	
	/**
	 * 加载用户头像，优先从缓存加载
	 * 
	 * @param down_url
	 * @param user_id
	 * @param sp
	 * @param bitmapUtils
	 * @param image
	 */
	public static void loadUserHeadImg(final String down_url, final String user_id, final SharedPreferenceUtil sp,
			final BitmapUtils bitmapUtils, final ImageView image, final UserHeadCallback callback) {
		String last_url = sp.getStringValue(user_id, SharedPreferenceUtil.HEADIMAGE);
		boolean download_img = false;
		//本地缓存
		boolean has_cache = false;
		String local_img_path = getUserImgPath(user_id);
		File dir = new File(local_img_path);
    	if (dir.exists())
    		has_cache = true;
		//无缓存
		if (Validator.isEmptyString(last_url)) {
			//有头像，下载
			if (!Validator.isEmptyString(down_url))
				download_img = true;
			else {
				if (callback != null)
					callback.loadSucceed();
				return;
			}
		//有缓存
		} else {
			//有头像
			if (!Validator.isEmptyString(down_url))
				download_img = !last_url.equals(down_url) || !has_cache;
		}
		if (download_img) {
			bitmapUtils.display(image, down_url, new BitmapLoadCallBack<ImageView>() {
				@Override
				public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
					if (saveUserImg(bitmap, user_id, true, false))
						sp.setStringValue(user_id, SharedPreferenceUtil.HEADIMAGE, down_url);
					container.setImageBitmap(Tools.toRoundCorner(bitmap));
					if (callback != null)
						callback.loadSucceed();
				}
				
				@Override
				public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
					Log.e("ImageCache", "下载头像失败");
					if (callback != null)
						callback.loadFail();
				}
			});
		} else if (has_cache) {
			image.setImageBitmap(Tools.toRoundCorner(BitmapFactory.decodeFile(local_img_path)));
			if (callback != null)
				callback.loadSucceed();
		}
	}

	/**
	 * 加载网络图片，不缓存
	 * 
	 * @param url
	 * @param image
	 * @param bitmapUtils
	 */
	public static void loadUrlImg(final String url, final ImageView image, final BitmapUtils bitmapUtils) {
		loadUrlImg(url, image, bitmapUtils, false);
	}

	/**
	 * 加载网络图片
	 * 
	 * @param url
	 * @param image
	 * @param bitmapUtils
	 * @param cache	是否缓存到本地
	 */
	public static void loadUrlImg(final String url, final ImageView image, final BitmapUtils bitmapUtils, final boolean cache) {
		if (Validator.isEmptyString(url))
			return;
		boolean has_cache = false;
		final String file_name = IMG_PATH + Tools.encodeString(url);
		if (cache) {
			File cache_fie = new File(file_name);
			if (cache_fie.exists()) 
				has_cache = true;
		}
		if (has_cache)
			image.setImageBitmap(BitmapFactory.decodeFile(file_name));
		else
			bitmapUtils.display(image, url, new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
					image.setImageBitmap(bitmap);
					if (cache)
						FileUtil.saveImage(bitmap, file_name);
				}
				
				@Override
				public void onLoadFailed(View container, String uri, Drawable drawable) {
					Log.e("ImageCache", "下载图片失败");
				}
			});
	}
	
	public static boolean checkImgSize(String path, int kb) {
		Bitmap image = BitmapFactory.decodeFile(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray().length / 1024 <= kb;
	}
	
	private static Bitmap getCompressImg(String image_path, int result_kb) {
		File file = new File(image_path);
		if (!file.exists())
			return null;
		long kb = file.length() / 1024;
		if (result_kb <= 0 || kb <= result_kb)
			return BitmapFactory.decodeFile(image_path);
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        int size = (int) kb / result_kb;
        int i = 1;
        while (i < size)
        	i = i<<1;
        options.inSampleSize = i;
        return BitmapFactory.decodeFile(image_path, options);
	}
	
	public static UploadImgEntity compressImageForUpload(String path) {
		return compressImageForUpload(path, Globals.UPLOAD_DIR, -1, false);
	}
	
	public static UploadImgEntity compressImageForPreview(String path, int index) {
		if (index == 0)
			clearPreviewCache();
		return compressImageForUpload(path, Globals.PREVIEW_DIR, index, true);
	}
	
	private static void clearPreviewCache() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
				+ Globals.PATH_CACHE + File.separator + Globals.PREVIEW_DIR;
		File dir_file = new File(path);
		if (dir_file.exists()) {
			FileUtil.deleteDir(dir_file);
		}
	}
	
	private static UploadImgEntity compressImageForUpload(String path, String dir, int index, boolean force_copy) {
		File file = new File(path);
		long kb = file.length() / 1024;
		boolean save_big = false;
		UploadImgEntity entity = new UploadImgEntity();
		if (kb <= ServerConstants.SMALL_PIC_KB) {
			if (force_copy) {
				entity.small_file = new File(copyImageForUpload(path, dir, index, false));
				entity.big_file = new File(copyImageForUpload(path, dir, index, true));
			} else {
				entity.small_file = file;
				entity.big_file = file;
			}
			return entity;
		}
		if (kb <= ServerConstants.BIG_PIC_KB) {
			entity.big_file = file;
			save_big = true;
		}
		if (!save_big)
			entity.big_file = new File(compressImageForUpload(path, dir, index, true, kb));
		else if (force_copy)
			entity.big_file = new File(copyImageForUpload(path, dir, index, true));
		entity.small_file = new File(compressImageForUpload(path, dir, index, false, kb));
		return entity;
	}
	
	private static String copyImageForUpload(String path, String dir, int index, boolean big) {
		String to_path = getUploadImgPath(dir, index, big);
		FileUtil.copyFile(path, to_path);
		return to_path;
	}
	
	private static String compressImageForUpload(String image_path, String dir, int index, boolean big, long kb) {
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        int size = (int)kb / (big ? ServerConstants.BIG_PIC_KB : ServerConstants.SMALL_PIC_KB);
        int i = 1;
        while (i <= size)
        	i = i<<1;
        options.inSampleSize = i;
        Bitmap image = BitmapFactory.decodeFile(image_path, options);
		String path = getUploadImgPath(dir, index, big);
		FileUtil.saveImage(image, path);
		if (big)
			Log.e("saveImage  big", (new File(path).length() / 1024) + " " + path);
		else
			Log.e("saveImage  small", (new File(path).length() / 1024) + " " + path);
		return path;
	}

	private static String getUploadImgPath(String dir, int index, boolean big) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
				+ Globals.PATH_CACHE + File.separator + dir;
		File dir_file = new File(path);
		if (!dir_file.exists())
			dir_file.mkdirs();
		String prefix = index >= 0 ? index + "" : "";
		return path + File.separator + prefix + (big ? IMG_UPLOAD_BIG : IMG_UPLOAD_SMALL);
	}
	
	public static String getPreviewDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
				+ Globals.PATH_CACHE + File.separator + Globals.PREVIEW_DIR;
	}

	public static String getCutImgPath() {
		File dir = new File(IMG_PATH);
		if (!dir.exists())
			dir.mkdirs();
		return IMG_PATH + IMG_CUT;
	}
	
	public static String getAnOtherUploadImgPath(String name, boolean big) {
		if (big) {
			int index = name.indexOf(IMG_UPLOAD_BIG);
			if (index > 0)
				return getPreviewDir() + File.separator + name.substring(0, index) + IMG_UPLOAD_SMALL;
		} else {
			int index = name.indexOf(IMG_UPLOAD_SMALL);
			if (index > 0)
				return getPreviewDir() + File.separator + name.substring(0, index) + IMG_UPLOAD_BIG;
		}
		return null;
	}
	
	public static String getPreviewRelativDir() {
		return File.separator + Globals.PATH_CACHE + File.separator + Globals.PREVIEW_DIR + File.separator;
	}
	
	public interface UserHeadCallback {
		
		public void loadSucceed();
		
		public void loadFail();
	}
}
