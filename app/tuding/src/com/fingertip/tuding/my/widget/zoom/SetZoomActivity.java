package com.fingertip.tuding.my.widget.zoom;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fingertip.tuding.Globals;
import com.fingertip.tuding.R;
import com.fingertip.tuding.base.BaseActivity;
import com.fingertip.tuding.my.widget.zoom.model.CityModel;
import com.fingertip.tuding.my.widget.zoom.model.DistrictModel;
import com.fingertip.tuding.my.widget.zoom.model.ProvinceModel;
import com.fingertip.tuding.my.widget.zoom.service.XmlParserHandler;

public class SetZoomActivity extends BaseActivity implements OnClickListener, OnWheelChangedListener {
	
	/**
	 * ����ʡ
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - ʡ value - ��
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - �� values - ��
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	
	/**
	 * key - �� values - �ʱ�
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>(); 

	/**
	 * ��ǰʡ������
	 */
	protected String mCurrentProviceName;
	/**
	 * ��ǰ�е�����
	 */
	protected String mCurrentCityName;
	/**
	 * ��ǰ��������
	 */
	protected String mCurrentDistrictName ="";
	
	/**
	 * ��ǰ������������
	 */
	protected String mCurrentZipCode ="";
	
	private boolean load_district;
	public static String KEY_LOAD_DISTRICT = "load_district";
	private static String[] DIRECT_CITYS = {"������", "�����", "�Ϻ���", "������"};
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private Button mBtnConfirm;
	private LinearLayout lineParent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		load_district = getIntent().getBooleanExtra(KEY_LOAD_DISTRICT, false);
		setContentView(R.layout.activity_set_zoom);
		setUpViews();
		setUpListener();
		setUpData();
	}
	
	private void setUpViews() {
		lineParent = (LinearLayout) findViewById(R.id.zoom_line);
		mViewProvince = (WheelView) findViewById(R.id.zoom_province);
		mViewCity = (WheelView) findViewById(R.id.zoom_city);
		mViewDistrict = (WheelView) findViewById(R.id.zoom_district);
		if (!load_district) {
			mViewDistrict.setVisibility(View.INVISIBLE);
			lineParent.removeView(mViewDistrict);
		}
		mBtnConfirm = (Button) findViewById(R.id.zoom_btn);
	}
	
	private void setUpListener() {
    	// ���change�¼�
    	mViewProvince.addChangingListener(this);
    	// ���change�¼�
    	mViewCity.addChangingListener(this);
    	// ���change�¼�
    	if (load_district)
    		mViewDistrict.addChangingListener(this);
    	// ���onclick�¼�
    	mBtnConfirm.setOnClickListener(this);
    }
	
	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(SetZoomActivity.this, mProvinceDatas));
		// ���ÿɼ���Ŀ����
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		if (load_district)
			mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (load_district && wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * ���ݵ�ǰ���У�������WheelView����Ϣ
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		if (load_district) {
			String[] areas = mDistrictDatasMap.get(mCurrentCityName);
			if (areas == null)
				areas = new String[] {""};
			mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
			mViewDistrict.setCurrentItem(0);
		}
	}

	/**
	 * ���ݵ�ǰ��ʡ��������WheelView����Ϣ
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.zoom_btn:
			showSelectedResult();
			break;
		}
	}

	private void showSelectedResult() {
		String result = mCurrentProviceName;
		if (!isDirectCiry(mCurrentProviceName)) {
			result += " " + mCurrentCityName;
			if (load_district)
				result += " " + mCurrentDistrictName;
		}
		Intent intent = new Intent();
		intent.putExtra(Globals.COMMON_RESULT, result);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private boolean isDirectCiry(String proviceName) {
		for (int i = 0; i < DIRECT_CITYS.length; i++) {
			if (DIRECT_CITYS[i].equals(proviceName))
				return true;
		}
		return false;
	}
	
	/**
	 * ����ʡ������XML����
	 */
	
    protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
    	AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // ����һ������xml�Ĺ�������
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// ����xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler(load_district);
			parser.parse(input, handler);
			input.close();
			// ��ȡ��������������
			provinceList = handler.getDataList();
			//*/ ��ʼ��Ĭ��ѡ�е�ʡ���С���
			if (provinceList!= null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList!= null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					if (load_district) {
						List<DistrictModel> districtList = cityList.get(0).getDistrictList();
						mCurrentDistrictName = districtList.get(0).getName();
						mCurrentZipCode = districtList.get(0).getZipcode();
					}
				}
			}
			//*/
			mProvinceDatas = new String[provinceList.size()];
        	for (int i=0; i< provinceList.size(); i++) {
        		// ��������ʡ������
        		mProvinceDatas[i] = provinceList.get(i).getName();
        		List<CityModel> cityList = provinceList.get(i).getCityList();
        		String[] cityNames = new String[cityList.size()];
        		for (int j = 0; j < cityList.size(); j++) {
        			// ����ʡ����������е�����
        			cityNames[j] = cityList.get(j).getName();
        			if (load_district) {
        				List<DistrictModel> districtList = cityList.get(j).getDistrictList();
        				String[] distrinctNameArray = new String[districtList.size()];
        				DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
        				for (int k=0; k<districtList.size(); k++) {
        					// ����������������/�ص�����
        					DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
        					// ��/�ض��ڵ��ʱ࣬���浽mZipcodeDatasMap
        					mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
        					distrinctArray[k] = districtModel;
        					distrinctNameArray[k] = districtModel.getName();
        				}
        				// ��-��/�ص����ݣ����浽mDistrictDatasMap
        				mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
        			}
        		}
        		// ʡ-�е����ݣ����浽mCitisDatasMap
        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
        	}
        } catch (Throwable e) {  
            e.printStackTrace();  
        } finally {
        } 
	}
}
