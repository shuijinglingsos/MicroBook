package com.lidong.daymoney.ui;

import com.lidong.daymoney.R;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class MultiChoiceDialog{

	private Context _c;
	private String[] _text;
	private boolean[] _checked;
	private ListView listview;
	private DialogListAdapter adapter;
	private Builder builder;
	
	public MultiChoiceDialog(Context context, int titleResId, String[] text,boolean[] _check) {
		
		_c=context;
		_text=text;
		if(_check==null)
		{
			_checked=new boolean[_text.length];
			setAllBoolean(_checked,true);
		}
		else
		{
			_checked=new boolean[_check.length];
			for(int i=0;i<_check.length;i++)
				_checked[i]=_check[i];
		}
		
		
		View v= LayoutInflater.from(_c).inflate(R.layout.listview, null);
		listview=(ListView)v.findViewById(R.id.listView1);
		listview.setBackgroundColor(Color.WHITE);
	    
	    adapter=new DialogListAdapter(_c);
	    listview.setAdapter(adapter);
	    
	    listview.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				adapter.setChecked(arg2);
				adapter.notifyDataSetChanged();
			}});
		
		builder=new Builder(_c);
		builder.setView(v);
		builder.setTitle(titleResId);
		builder.setNegativeButton(R.string.cancel, null);

	}
	
	public void setOKButton(DialogInterface.OnClickListener listener)
	{
		builder.setPositiveButton(R.string.ok, listener);
	}
	
	public void show()
	{
		builder.show();
	}
	

	public boolean[] getCheckedArray()
	{
		return _checked;
	}
	
	
	private void setAllBoolean(boolean[] array,boolean value)
	{
		for(int i=0;i<array.length;i++)
		{
			array[i]=value;
		}
	}
	
	private class DialogListAdapter extends BaseAdapter
	{
		private Context _context;
		private LayoutInflater _inflater;

		public DialogListAdapter(Context c) {
			_context = c;
			_inflater = LayoutInflater.from(_context);
			
		}

		public int getCount() {
			return _text.length;
		}

		public Object getItem(int position) {
			return _text[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null)
				convertView = _inflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
			
			CheckedTextView ctv=((CheckedTextView)convertView.findViewById(android.R.id.text1));
			
			ctv.setTextColor(Color.BLACK);
			ctv.setText(_text[position]);
			ctv.setChecked(_checked[position]);
			
//			convertView.setPadding((int)_c.getResources().getDimension(R.dimen.list_item_left), 
//					(int)_c.getResources().getDimension(R.dimen.list_item_top), 
//					(int)_c.getResources().getDimension(R.dimen.list_item_right),
//					(int)_c.getResources().getDimension(R.dimen.list_item_bottom));
			
			return convertView;
		}
		
		public void setChecked(int index)
		{
			_checked[index]=!_checked[index];
			
			if(index==0)
			{
				setAllBoolean(_checked,_checked[0]);
			}
			else
			{
				for(int i=1;i<_checked.length;i++)
				{
					if(!_checked[i])
					{
						_checked[0]=false;
						return;
					}	
				}
				_checked[0]=true;
			}
		}
	}
}
