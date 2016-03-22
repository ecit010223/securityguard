package com.mylover57.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonAdapter<E> extends BaseAdapter {
	private List<E> list;
	private LayoutInflater inflater;
	private int resId;
	private int[] bindIds;
	
	public CommonAdapter(Context context,int resId, int...bindIds) {
		inflater = LayoutInflater.from(context);
		this.resId = resId;
		this.bindIds = bindIds;
		list = new ArrayList<E>();
	}
	
	public List<E> getList(){
		return list;
	}
	
	public CommonAdapter<E> add(E item){
		list.add(item);
		return this;
	}
	
	public CommonAdapter<E> remove(int position){
		list.remove(position);
		return this;
	}
	
	public CommonAdapter<E> update(int position,E e){
		list.set(position, e);
		return this;
	}
	
	public CommonAdapter<E> fill(E...args){
		for(E e : args)
			list.add(e);
		return this;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public E getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//优化处理
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		View[] views = null;
		if(convertView == null){
			v = inflater.inflate(resId, null);
			int len = bindIds.length;
			views = new View[len];
			for(int i=0; i<len; i++)
				views[i] = v.findViewById(bindIds[i]);
			v.setTag(views);
		}else{
			v = convertView;
			views = (View[]) v.getTag();
		}
		bindView(getItem(position),views);
		return v;
	}

	protected abstract void bindView(E e,View...views);
}
