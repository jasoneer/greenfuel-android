package com.isotix.greenfuel.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;
import com.isotix.greenfuel.model.Search;
import com.isotix.greenfuel.utility.PositionListener;

public class DestinationDialog extends CDialog implements PositionListener,
														  View.OnClickListener,
														  DialogInterface.OnCancelListener
{
	private TextView addressText;
					 //latitudeText,
					 //longitudeText;

	public DestinationDialog(Context context)
	{
		super(context);
		setIcon(R.drawable.icon);
		setTitle("Destination");
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_destination, null);
		
		//((RadioButton)layout.findViewById(R.id.dialog_destination_add)).toggle();
		
		addressText   = (TextView)layout.findViewById(R.id.dialog_destination_address);
		//latitudeText  = (TextView)layout.findViewById(R.id.dialog_destination_latitude);
		//longitudeText = (TextView)layout.findViewById(R.id.dialog_destination_longitude);
		
		setView(layout);

		addButton(ACCEPT, "Accept", this);
		addDivider();
		addButton(CANCEL, "Cancel", this);
	}

	public void onCancel(DialogInterface dialog)
	{
		
	}

	public void onStatusEvent(int event) {}
	public void onPositionFix(double latitude, double longitude)
	{
		//App.Database.getStations(latitude, longitude, App.SearchRadius);
		Search.SearchStations(App.SearchRadius, latitude, longitude);
		App.CurrentActivity.onPositionFix(latitude, longitude);
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			case ACCEPT:
				DialogStack.Clear();
				App.Position.startGeocoding(addressText.getText().toString(), this);
			break;
			default: DialogStack.Back(); break;
		}
	}
}
