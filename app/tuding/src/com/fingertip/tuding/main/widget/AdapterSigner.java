package com.fingertip.tuding.main.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseListAdapter;
import com.fingertip.tuding.db.SharedPreferenceUtil;
import com.fingertip.tuding.entity.SignerEntity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.lidroid.xutils.BitmapUtils;

public class AdapterSigner extends BaseListAdapter<SignerEntity> {

	private Context context;
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	private boolean is_owner;

	public AdapterSigner(Context context, boolean is_owner) {
		this.context = context;
		this.is_owner = is_owner;
		bitmapUtils = new BitmapUtils(context);
		sp = new SharedPreferenceUtil(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHoler viewHoler;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_signer, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			viewHoler.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHoler.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHoler.tv_sign_time = (TextView) convertView.findViewById(R.id.tv_sign_time);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHoler) convertView.getTag();
		}
		final SignerEntity signer = getItem(position);
		ImageCache.loadUserHeadImg(signer.user.head_img_url, signer.user.id, sp, bitmapUtils, viewHoler.iv_head);
		viewHoler.tv_name.setText(signer.user.nick_name);
		if (is_owner) {
			viewHoler.tv_status.setVisibility(View.VISIBLE);
			if (signer.checked) {
				viewHoler.tv_status.setText("“——È∆±");
				viewHoler.tv_status.setTextColor(context.getResources().getColor(R.color.green_event));
			} else {
				viewHoler.tv_status.setText("Œ¥—È∆±");
				viewHoler.tv_status.setTextColor(context.getResources().getColor(R.color.red_event));
			}
		}
		viewHoler.tv_sign_time.setText(Tools.getTimeStr(signer.time.getTime()));
		viewHoler.iv_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.openUser(context, signer.user.id);
			}
		});
		return convertView;
	}
	
	class ViewHoler {
		ImageView iv_head;
		TextView tv_name, tv_status, tv_sign_time;
	}
}
