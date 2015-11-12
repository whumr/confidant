package com.fingertip.tuding.base;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<E> extends BaseAdapter {

	protected List<E> arrayList = new ArrayList<E>();

	public void addAllList(List<E> list) {
		if (list != null) {
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void appendList(List<E> list) {
		if (list != null) {
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public void insert(E e) {
		arrayList.add(0, e);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public E getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		return null;
	}
}
