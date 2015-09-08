package com.fingertip.tuding.my.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.util.Tools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdapterGridPic extends BaseAdapter implements OnItemClickListener {

	private Context context;
	private ArrayList<String> small_pics = new ArrayList<String>();
	private ArrayList<String> big_pics = new ArrayList<String>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public AdapterGridPic(Context context, ArrayList<String> small_pics, ArrayList<String> big_pics) {
		this.context = context;
		this.small_pics = small_pics;
		this.big_pics = big_pics;
		this.options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_pic_empty)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	}

	@Override
	public int getCount() {
		return small_pics.size();
	}

	@Override
	public String getItem(int position) {
		return small_pics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_pic, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.img = (ImageView)convertView.findViewById(R.id.pic_img);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder)convertView.getTag();
		imageLoader.displayImage(getItem(position), viewHolder.img, options);
		return convertView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Tools.previewPics(context, big_pics, position);
	}
	
	class ViewHolder {
		ImageView img;
	}
}
