package com.fingertip.tuding.common.gif;

public interface GifAction {

	/**
	 * gif����۲���
	 * @param parseStatus �����Ƿ�ɹ����ɹ���Ϊtrue
	 * @param frameIndex ��ǰ����ĵڼ�֡����ȫ������ɹ�������Ϊ-1
	 */
	public void parseOk(boolean parseStatus,int frameIndex);
}
