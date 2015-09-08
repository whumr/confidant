package com.fingertip.tuding.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

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
			Log.e("saveImage", spath + " " + e.getMessage());
			return false;
		}
		return true;
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
				+ Globals.PATH_CACH + File.separator + "log";
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
}
