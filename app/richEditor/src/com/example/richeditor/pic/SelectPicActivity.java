package com.example.richeditor.pic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.richeditor.R;
import com.example.richeditor.pic.DirPopupWindow.OnDirSelectListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SelectPicActivity extends Activity implements OnDirSelectListener {
	
	public static String KEY_MAX_COUNT = "max_count", KEY_PICS = "pics", KEY_SINGLE = "single", KEY_CUT = "cut";
	public static int DEFAULT_MAX_COUNT = 9, CUT_CODE = 100;
	
	private ProgressDialog progress_dialog;
	private GridView gird_view;
	private PicAdapter adapter;
	private RelativeLayout bottom_layout;

	private TextView selected_dir_txt;
	private TextView selected_count_txt;
	private Button ok_btn;
	
	private ArrayList<String> pics = new ArrayList<String>();
	private ArrayList<PicFolder> pic_folders = new ArrayList<PicFolder>();
	private HashMap<String, PicFolder> dir_map = new HashMap<String, PicFolder>();


	private int mScreenHeight;
	private int max_count = DEFAULT_MAX_COUNT;
	private int selected_count = 0;
	private int selected_folder_index = 0;
	
	private boolean single = false;
	private boolean cut_pic = false;

	private DirPopupWindow dir_popup;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progress_dialog.dismiss();
			setGridView();
			initDirPopupWindw();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pics);
		initConfig();
		initView();
		getImages();
		initEvent();
	}
	
	private void initConfig() {
		Intent intent = getIntent();
		if (intent.hasExtra(KEY_SINGLE))
			single = true;
		else if (intent.hasExtra(KEY_MAX_COUNT))
			max_count = intent.getIntExtra(KEY_MAX_COUNT, DEFAULT_MAX_COUNT);
		if (intent.hasExtra(KEY_CUT) && intent.getBooleanExtra(KEY_CUT, false))
			cut_pic = true;
	}

	private void initView() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		
		gird_view = (GridView) findViewById(R.id.gridView);
		selected_dir_txt = (TextView) findViewById(R.id.selected_dir_txt);
		selected_count_txt = (TextView) findViewById(R.id.selected_count_txt);
		bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
		ok_btn = (Button) findViewById(R.id.submit_btn);
		if (!single)
			selected_count_txt.setText("��ѡ(0/" + max_count + ")��");
		else
			selected_count_txt.setVisibility(View.GONE);
	}
	
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "���ⲿ�洢", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		// ��ʾ�����
		progress_dialog = ProgressDialog.show(this, null, "���ڼ���...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				ContentResolver mContentResolver = getContentResolver();
				// ֻ��ѯjpeg��png��ͼƬ
				Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {MediaStore.Images.Media.DATA},
						MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] {"image/jpeg", "image/png"},
						MediaStore.Images.Media.DATE_MODIFIED + " desc");
				while (cursor.moveToNext()) {
					// ��ȡͼƬ��·��
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					// ��ȡ��ͼƬ�ĸ�·����
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dir_path = parentFile.getAbsolutePath();
					pics.add(path);
					PicFolder pic_folder = null;
					// ����һ��HashSet��ֹ���ɨ��ͬһ���ļ��У���������жϣ�ͼƬ�����������൱�ֲ���~~��
					if (dir_map.containsKey(dir_path)) {
						dir_map.get(dir_path).getPics().add(path);
						continue;
					} else {
						// ��ʼ��imageFloder
						pic_folder = new PicFolder();
						pic_folder.setDir(dir_path);
						pic_folder.getPics().add(path);
						dir_map.put(dir_path, pic_folder);
						pic_folders.add(pic_folder);
					}
				}
				cursor.close();
				PicFolder all_folder = new PicFolder();
				all_folder.setDir("/all");
				all_folder.getPics().addAll(pics);
				dir_map.put("", all_folder);
				pic_folders.add(0, all_folder);
				// ֪ͨHandlerɨ��ͼƬ���
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	private void setGridView() {
		if (pic_folders.isEmpty()) {
			Toast.makeText(getApplicationContext(), "û��ͼƬ", Toast.LENGTH_SHORT).show();
			return;
		}
		adapter = new PicAdapter(this, pics, single, cut_pic);
		gird_view.setAdapter(adapter);
		gird_view.setOnItemClickListener(adapter);
	};

	@SuppressLint("InflateParams")
	private void initDirPopupWindw() {
		dir_popup = new DirPopupWindow(LayoutInflater.from(this).inflate(R.layout.listview_popup_img_dir, null), 
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), pic_folders, selected_folder_index, this);
		dir_popup.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
	}

	private void initEvent() {
		bottom_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dir_popup != null) {
//					dir_popup.setAnimationStyle(R.style.anim_popup_dir);
					dir_popup.showAsDropDown(bottom_layout, 0, 0);
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 0.3f;
					getWindow().setAttributes(lp);
				}
			}
		});

		if (!single) {
			ok_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (adapter != null) {
						Intent data = new Intent();
						Bundle bundle = new Bundle();
						bundle.putStringArrayList(KEY_PICS, adapter.getSelect_pics());
						data.putExtras(bundle);
						setResult(RESULT_OK, data);
					}
					finish();
				}
			});
		} else
			ok_btn.setVisibility(View.GONE);
	}
	
	public void selected(boolean select) {
		if (!single) {
			selected_count = select ? selected_count + 1 : selected_count - 1;
			selected_count_txt.setText("��ѡ(" + selected_count + "/" + max_count + ")��");
		}
	}

	public boolean canSelect() {
		if (selected_count < max_count)
			return true;
		Toast.makeText(this, "�����ֻ��ѡ��" + max_count + "��ͼƬ", Toast.LENGTH_SHORT).show();
		return false;
	}
	
	@Override
	public void OnDirSelect(int position) {
		if (selected_folder_index != position) {
			selected_dir_txt.setText(pic_folders.get(position).getName());
			adapter.setPics(pic_folders.get(position).getPics());
			selected_folder_index = position;
		}
		dir_popup.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
		pics.clear();
		pics = null;
		pic_folders.clear();
		pic_folders = null;
		dir_map.clear();
		dir_map = null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	public void returnSinglePath(String path) {
		Intent data = new Intent();
		data.setData(Uri.fromFile(new File(path)));
		setResult(RESULT_OK, data);
		finish();
	}
}
