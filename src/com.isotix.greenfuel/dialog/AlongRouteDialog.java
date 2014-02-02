package com.isotix.greenfuel.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;
import com.isotix.greenfuel.model.Search;
import com.isotix.greenfuel.utility.PositionListener;

public class AlongRouteDialog extends CDialog implements PositionListener,
														 View.OnClickListener
{
	private TextView begText, endText;
	private CheckBox startBox, endBox;

	public AlongRouteDialog(Context context)
	{
		super(context);
		setIcon(R.drawable.icon);
		setTitle("Along Route");
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_alongroute, null);

		begText = (TextView)layout.findViewById(R.id.dialog_alongroute_start_address);
		endText   = (TextView)layout.findViewById(R.id.dialog_alongroute_end_address);
		startBox = (CheckBox)layout.findViewById(R.id.dialog_alongroute_start_current);
		endBox   = (CheckBox)layout.findViewById(R.id.dialog_alongroute_end_current);

		setView(layout);

		addButton(ACCEPT, "Accept", this);
		addDivider();
		addButton(CANCEL, "Cancel", this);
	}

	private boolean first;
	private double sLatitude, sLongitude;

	public void onStatusEvent(int event) {}
	public void onPositionFix(double latitude, double longitude)
	{
		if(!first) {
			sLatitude = latitude;
			sLongitude = longitude;
			first = true;
		} else {
			Search.SearchStations(App.SearchRadius, sLatitude, sLongitude, latitude, longitude);
			App.CurrentActivity.onPositionFix(latitude, longitude);
		}
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			case ACCEPT:
				DialogStack.Clear();
				first = false;
				App.Position.startGeocoding(begText.getText().toString(),
											endText.getText().toString(), this);
			break;
			default: DialogStack.Back(); break;
		}
	}
}
