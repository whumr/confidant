package com.fingertip.tuding.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.entity.ShareEntity;
import com.fingertip.tuding.main.widget.AttentionSelectedActivity;
import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.Tools;
import com.fingertip.tuding.util.http.common.ServerConstants.URL;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;
import com.umeng.analytics.social.UMPlatformData.UMedia;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareDialog extends BaseActivity {

	private ShareEntity shareEntity;
	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	private static int WX_IMG_SIZE = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share);

		setupViews();
		initData();
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); 
		layoutParams.gravity = Gravity.BOTTOM;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels;
	}

	private void setupViews() {
		findViewById(R.id.tuding).setOnClickListener(onClickListener);
		findViewById(R.id.wechat_friend).setOnClickListener(onClickListener);
		findViewById(R.id.wechat).setOnClickListener(onClickListener);
		findViewById(R.id.sms).setOnClickListener(onClickListener);
		findViewById(R.id.qq).setOnClickListener(onClickListener);
		findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
	}

	private void initData() {
		shareEntity = (ShareEntity)getIntent().getSerializableExtra(EXTRA_PARAM);
		if(shareEntity == null){
			toastShort("�������ݲ���Ϊ��");
			finish();
			return;
		}
		initShare();
	}
	
	private void initShare() {
		addSMS();
		// ���QQ��QZoneƽ̨
        addQQQZonePlatform();
		addWXPlatform();
	}
	
	/**
     * ��Ӷ���ƽ̨</br>
     */
    private void addSMS() {
        // ��Ӷ���
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }
	
	/**
     * @�������� : ���΢��ƽ̨����
     * @return
     */
    private void addWXPlatform() {
        // ע�⣺��΢����Ȩ��ʱ�򣬱��봫��appSecret
        // ���΢��ƽ̨
        UMWXHandler wxHandler = new UMWXHandler(ShareDialog.this, Globals.APPID_WX, Globals.SECRET_WX);
        wxHandler.addToSocialSDK();
        // ֧��΢������Ȧ
        UMWXHandler wxCircleHandler = new UMWXHandler(ShareDialog.this, Globals.APPID_WX, Globals.SECRET_WX);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }
    
    /**
     * @�������� : ���QQƽ̨֧�� QQ��������ݣ� �����������ͣ� �����������֡�ͼƬ�����֡���Ƶ. ����˵�� : title, summary,
     *       image url�б�����������һ��, targetUrl��������,��ҳ��ַ������"http://"��ͷ . title :
     *       Ҫ������� summary : Ҫ��������ָ��� image url : ͼƬ��ַ [������������������дһ��] targetUrl
     *       : �û�����÷���ʱ��ת����Ŀ���ַ [����] ( ������д��Ĭ������Ϊ������ҳ )
     * @return
     */
    private void addQQQZonePlatform() {
        String appId = "1104698067";
        String appKey = "gKXBi42MOQeQeSG7";
        // ���QQ֧��, ��������QQ�������ݵ�target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ShareDialog.this, appId, appKey);
        qqSsoHandler.setTargetUrl(URL.BASE_URL);
        qqSsoHandler.addToSocialSDK();
        // ���QZoneƽ̨
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ShareDialog.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }
	
	private void jumpToShare(SHARE_MEDIA shareMedia) {
		Bitmap shar_img = BitmapFactory.decodeResource(getResources(), R.drawable.icon_share_img);
		UserSession session = UserSession.getInstance();
		if (session.isLogin()) {
			Bitmap head = ImageCache.getUserImg(session.getId(), true, WX_IMG_SIZE);
			if (head != null)
				shar_img = head;
		}
		mController.getConfig().closeToast();
		if (SHARE_MEDIA.WEIXIN == shareMedia) {
			WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
			weiXinShareContent.setShareContent(shareEntity.shareContent);
			weiXinShareContent.setTargetUrl(shareEntity.targetUrl);
			weiXinShareContent.setTitle(shareEntity.shareTitle);
			weiXinShareContent.setShareImage(new UMImage(this, shar_img));
			mController.setShareMedia(weiXinShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		} else if (SHARE_MEDIA.WEIXIN_CIRCLE == shareMedia) {
			CircleShareContent circleShareContent = new CircleShareContent();
			circleShareContent.setShareContent(shareEntity.shareContent);
			circleShareContent.setTargetUrl(shareEntity.targetUrl);
			circleShareContent.setTitle(shareEntity.shareTitle);
			circleShareContent.setShareImage(new UMImage(this, shar_img));
			mController.setShareMedia(circleShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		} else if (SHARE_MEDIA.QQ == shareMedia) {
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(shareEntity.shareContent);
			qqShareContent.setTargetUrl(shareEntity.targetUrl);
			qqShareContent.setTitle(shareEntity.shareTitle);
			qqShareContent.setShareImage(new UMImage(this, shar_img));
			mController.setShareMedia(qqShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		} else if (SHARE_MEDIA.SMS == shareMedia) {
			SmsShareContent smsShareContent = new SmsShareContent();
			smsShareContent.setShareContent(getResources().getString(R.string.share_sms) + "\n" + shareEntity.targetUrl);
			mController.setShareMedia(smsShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		}
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** ʹ��SSO��Ȩ����������´��� */
		// UMSsoHandler ssoHandler =
		// mController.getConfig().getSsoHandler(requestCode) ;
		// if(ssoHandler != null){
		// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		// }
		UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
		if (ssoHandler != null)
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.tuding) {
				if (Tools.checkLogin(ShareDialog.this)) {
					Intent intent = new Intent();
					intent.setClass(ShareDialog.this, AttentionSelectedActivity.class);
					intent.putExtra(BaseActivity.EXTRA_PARAM, shareEntity);
					startActivity(intent);
				}
				finish();
			} else if (v.getId() == R.id.wechat)
				jumpToShare(SHARE_MEDIA.WEIXIN);
			else if (v.getId() == R.id.wechat_friend)
				jumpToShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			else if (v.getId() == R.id.qq)
				jumpToShare(SHARE_MEDIA.QQ);
			else if (v.getId() == R.id.sms)
				jumpToShare(SHARE_MEDIA.SMS);
			else if (v.getId() == R.id.btn_cancel)
				finish();
		}
	};
	
	private SnsPostListener snsPostListener = new SnsPostListener() {					
		@Override
		public void onStart() {
			finish();
		}
		
		@Override
		public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
			try {
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					UserSession session = UserSession.getInstance();
					String user_id = session.getId() == null ? "null" : session.getId();
					UMPlatformData pf = new UMPlatformData(UMedia.valueOf(platform.toString()), user_id);
					MobclickAgent.onSocialEvent(ShareDialog.this, pf);
				}
			} catch (Exception e) {
			}
			finish();
		}
	};
	
}
