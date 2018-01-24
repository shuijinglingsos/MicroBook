package com.lidong.daymoney.ui;

import com.lidong.daymoney.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageViewText extends RelativeLayout {
	
	public ImageViewText(Context context) {
		this(context,null);		
	}

	public ImageViewText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.imageview_text, this, true);
		
		iv=(ImageView)findViewById(R.id.imageView1);
		tv=(TextView)findViewById(R.id.textView1);
	}

	private ImageView iv;
	private TextView tv;
	
	public ImageView getImageView()
	{
		return iv;
	}
	
	public TextView getTextView()
	{
		return tv;
	}

}
