package com.isotix.greenfuel.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;

public class SearchDialog extends CDialog implements View.OnClickListener,
													 DialogInterface.OnCancelListener
{
	private DestinationDialog destDialog;
	private AlongRouteDialog routeDialog;

	private TextView locationText,
					 destinationText,
					 alongrouteText;
	
	public SearchDialog(Context context)
	{
		super(context);
		setIcon(R.drawable.icon);
		setTitle("Search Stations Within");
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_search, null);
		
		layout.findViewById(R.id.dialog_show_location_button).setOnClickListener(this);
		layout.findViewById(R.id.dialog_show_destination_button).setOnClickListener(this);
		layout.findViewById(R.id.dialog_show_alongroute_button).setOnClickListener(this);
		
		locationText = (TextView)layout.findViewById(R.id.dialog_show_location_edit);
		destinationText = (TextView)layout.findViewById(R.id.dialog_show_destination_edit);
		alongrouteText = (TextView)layout.findViewById(R.id.dialog_show_alongroute_edit);
		
		setView(layout);

		addButton(CANCEL, "Cancel", this);
		
		destDialog = new DestinationDialog(context);
		routeDialog = new AlongRouteDialog(context);
	}

	public void onCancel(DialogInterface dialog)
	{
		App.Position.disable();
		Toast.makeText(App.Current, "Location Fix Canceled", Toast.LENGTH_SHORT).show();
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.dialog_show_location_button:
				if(locationText.getText().toString().equals("")) break;
				App.SearchRadius = Double.parseDouble(locationText.getText().toString());
				App.Position.enable(App.CurrentActivity);
				DialogStack.ShowProgressDialog(getContext(), "Loading",
															 "Obtaining location..", this);
				DialogStack.Back();
			break;
			case R.id.dialog_show_destination_button:
				if(destinationText.getText().toString().equals("")) break;
				App.SearchRadius = Double.parseDouble(destinationText.getText().toString());
				DialogStack.Show(destDialog);
			break;
			case R.id.dialog_show_alongroute_button:
				if(alongrouteText.getText().toString().equals("")) break;
				App.SearchRadius = Double.parseDouble(alongrouteText.getText().toString());
				DialogStack.Show(routeDialog);
			break;
			default: DialogStack.Back(); break;
		}
	}
}
