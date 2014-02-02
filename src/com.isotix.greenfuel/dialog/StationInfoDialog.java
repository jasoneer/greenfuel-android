package com.isotix.greenfuel.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isotix.greenfuel.R;
import com.isotix.greenfuel.model.Station;
import com.isotix.greenfuel.utility.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Map;

public class StationInfoDialog extends CDialog implements View.OnClickListener
{
	private TextView typeText,
					 statusText;
	
	public StationInfoDialog(Context context, Station station)
	{
		super(context);
		setIcon(R.drawable.icon);
		setTitle("Station Info");
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.dialog_stationinfo, null);

		typeText   = (TextView)layout.findViewById(R.id.dialog_stationinfo_type);
		statusText = (TextView)layout.findViewById(R.id.dialog_stationinfo_status);

		switch(station.type())
		{
			case Station.BIO:
				typeText.setText("Biodiesel (B20 and above)");
			break;
			case Station.ELE:
				typeText.setText("Electric");
			break;
			case Station.ETH:
				typeText.setText("Ethanol (E85)");
			break;
			case Station.HYD:
				typeText.setText("Hydrogen");
			break;
			case Station.CNG:
				typeText.setText("Natural Gas (Compressed)");
			break;
			case Station.LNG:
				typeText.setText("Natural Gas (Liquefied)");
			break;
			case Station.PRO:
				typeText.setText("Propane (LPG)");
			break;
		}
		switch(station.status())
		{
			case Station.PUBLIC:
				statusText.setText("Open/Public");
			break;
			case Station.PRIVATE:
				statusText.setText("Open/Private");
			break;
			case Station.CLOSED:
				statusText.setText("Closed");
			break;
		}

		JSON.Object stationInfo = null;
		try {
			InputStream stationDataStream = new StringBufferInputStream(station.data());
			stationInfo = (JSON.Object)new JSON.Parser(stationDataStream).parse();
			stationDataStream.close();
		} catch(IOException ex) {}

		if(stationInfo != null)
		for(Map.Entry<String, JSON> entry: stationInfo.entries()) {
			ViewGroup infoLayout = (ViewGroup)inflater.inflate(R.layout.dialog_stationinfo_info,
															   null);
			String text = Character.toUpperCase(entry.getKey().charAt(0)) +
												entry.getKey().substring(1) + ':';												   
			((TextView)infoLayout.getChildAt(0)).setText(text);
			((TextView)infoLayout.getChildAt(1)).setText(((JSON.String)entry.getValue()).value());
			layout.addView(infoLayout);
		}

		setView(layout);
		addButton(ACCEPT, "Close", this);
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			default: DialogStack.Back(); break;
		}
	}
}
