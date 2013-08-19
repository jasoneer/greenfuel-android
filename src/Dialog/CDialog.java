package com.isotix.nufuel;

import android.app.Dialog;
import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.LinearLayout;

public class CDialog extends Dialog
{
	public final static int ACCEPT = 1;
	public final static int CANCEL = 2;

	private ImageView iconView;
	private TextView titleView;
	private ScrollView scrollView;
	private LinearLayout bottomView;

	private ViewGroup.LayoutParams dividerLayout;
	private LinearLayout.LayoutParams buttonLayout;

	public CDialog(Context context)
	{
		super(context, R.style.Dialog);
		setCancelable(false);
		setContentView(R.layout.dialog);
		
		iconView = (ImageView)findViewById(R.id.dialog_icon);
		titleView = (TextView)findViewById(R.id.dialog_title);
		scrollView = (ScrollView)findViewById(R.id.dialog_content);

		bottomView = (LinearLayout)findViewById(R.id.dialog_bottom);

		dividerLayout = new LinearLayout.LayoutParams(2,
													  ViewGroup.LayoutParams.MATCH_PARENT, 0);
		buttonLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
													 ViewGroup.LayoutParams.WRAP_CONTENT, 1);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
									 WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	public void setIcon(int resId) { iconView.setImageResource(resId); }
	@Override
	public void setTitle(CharSequence title) { titleView.setText(title); }
	public void setView(View view) { scrollView.addView(view); }

	public void addButton(int id, String title, View.OnClickListener listener)
	{
		Button button = new Button(getContext());
		button.setBackgroundResource(R.drawable.button_bar);
		button.setPadding(0,0,0,0);
		button.setId(id);
		button.setText(title);
		button.setOnClickListener(listener);

		bottomView.addView(button, buttonLayout);
	}
	public void addDivider()
	{
		View view = new View(getContext());
		view.setBackgroundResource(R.drawable.bar_vert);
		bottomView.addView(view, dividerLayout);
	}

	@Override
	public void onBackPressed() { DialogStack.Back(); }
}
