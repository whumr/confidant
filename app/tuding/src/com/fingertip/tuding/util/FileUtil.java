package com.fingertip.tuding.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.fingertip.tuding.Globals;

/**
 * �ļ�������
 * 1.ĳ��·���µ��ļ���Ŀ¼�Ƿ����
 * 2.��bitmap���浽ĳĿ¼·����
 * 3.��ȡ�ļ���չ��
 * 4.ɾ��ĳĿ¼�µ������ļ��м��ļ�
 * @author Administrator
 *
 */
public class FileUtil {
	
	/**
	 * ĳ��·���µ��ļ���Ŀ¼�Ƿ����
	 * @param path
	 * @return
	 */
	public static boolean isExistHead(String path){
		return new File(path).exists();
	}
	
	/**
	 * ��bitmap���浽ĳĿ¼·����
	 * @param photo
	 * @param spath
	 * @return
	 */
	public static boolean saveImage(Bitmap photo, String spath) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(spath, false));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean saveCert(Bitmap photo, String title, Context context) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
				+ Globals.PATH_BASE + File.separator;
		File base = new File(path);
		if (!base.exists())
			base.mkdirs();
		String file_name = (title.length() > 10 ? title.substring(0, 10) : title) + ".jpg";
		boolean saved = saveImage(photo, path + file_name);
		if (saved)
			refreshDicm(context, path + file_name);
		return saved;
	}
	
	public static void refreshDicm(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(new File(path));
		intent.setData(uri);
		context.sendBroadcast(intent);
	}
	
	/**
	 * Java�ļ����� ��ȡ�ļ���չ��
	 **/
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}
	
	/** 
	 * �����ļ�/Ŀ¼������Ŀ¼δ�����򴴽��������Ѵ��ڣ������κβ���������false
	 * 
	 *  **/
	public static boolean createNewFile(String filename){
		boolean flag = false;
		
		File file = new File(filename);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return flag;
	}//end createNewFile
	
	/** ɾ��ĳĿ¼�µ������ļ��м��ļ� **/
	public static void deleteDir(File rootFile){		
		File[] files = rootFile.listFiles();
		if(files == null){
			return;
		}
		for(int i = 0; i < files.length; i++){
			File file = files[i];
			if(file.isDirectory()){
				deleteDir(file);
				file.delete();
			}else{
				file.delete();
			}
		}
	}//end deleteDir
	
	public static long getFolderSize(File file) {
		long size = 0;
		File[] fileList = file.listFiles();
		if(fileList == null){
			return size;
		}
		for (int i = 0; i < fileList.length; i++) {
			// ������滹���ļ�
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}
		return size;
	}
	
	public static String getCacheSize(String dir) {
		long size = getFolderSize(new File(dir));
		double m_size = ((double)size) / 1024 / 1024;
		String[] sizes = Double.toString(m_size).split("\\.");
		if (sizes.length > 1) {
			String s = sizes[1];
			if (s.length() > 1)
				s = s.substring(0, 1);
			return sizes[0] + "." + s + "M";
		} else
			return sizes[0] + ".0M";
	}
	
	public static void writeLog(String log) {
		try {
			String log_path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
				+ Globals.PATH_CACHE + File.separator + Globals.LOG_DIR;
			File dir = new File(log_path);
			if (!dir.exists())
				dir.mkdirs();
			FileOutputStream fos = new FileOutputStream(log_path + File.separator + System.currentTimeMillis() + ".log");
			fos.write(log.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean copyFile(String from_path, String to_path) {
		File from = new File(from_path);
		if (!from.exists())
			return false;
		try {  
            FileInputStream in = new FileInputStream(from);  
            FileOutputStream out = new FileOutputStream(to_path);  
            byte[] bt = new byte[1024];  
            int count;  
            while ((count = in.read(bt)) > 0) {  
                out.write(bt, 0, count);  
            }  
            in.close();  
            out.close();  
            return true;  
        } catch (IOException ex) {  
            return false;  
        }  
	}
}
