package com.fingertip.tuding.my.widget.zoom.service;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fingertip.tuding.my.widget.zoom.model.CityModel;
import com.fingertip.tuding.my.widget.zoom.model.DistrictModel;
import com.fingertip.tuding.my.widget.zoom.model.ProvinceModel;

public class XmlParserHandler extends DefaultHandler {

	private boolean load_district = false;
	
	/**
	 * �洢���еĽ�������
	 */
	private List<ProvinceModel> provinceList = new ArrayList<ProvinceModel>();
	 	  
	public XmlParserHandler(boolean load_district) {
		this.load_district = load_district;
	}

	public List<ProvinceModel> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// ��������һ����ʼ��ǩ��ʱ�򣬻ᴥ���������
	}

	ProvinceModel provinceModel = new ProvinceModel();
	CityModel cityModel = new CityModel();
	DistrictModel districtModel = new DistrictModel();
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// ��������ʼ��ǵ�ʱ�򣬵����������
		if (qName.equals("province")) {
			provinceModel = new ProvinceModel();
			provinceModel.setName(attributes.getValue(0));
			provinceModel.setCityList(new ArrayList<CityModel>());
		} else if (qName.equals("city")) {
			cityModel = new CityModel();
			cityModel.setName(attributes.getValue(0));
			cityModel.setDistrictList(new ArrayList<DistrictModel>());
		} else if (qName.equals("district") && load_district) {
			districtModel = new DistrictModel();
			districtModel.setName(attributes.getValue(0));
			districtModel.setZipcode(attributes.getValue(1));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// ����������ǵ�ʱ�򣬻�����������
		if (qName.equals("district") && load_district) {
			cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals("city")) {
        	provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
        	provinceList.add(provinceModel);
        }
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
	}

}
