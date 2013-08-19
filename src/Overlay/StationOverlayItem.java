package com.isotix.nufuel;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class StationOverlayItem extends OverlayItem
{
	private Station _station;

	private Drawable defaultMarker,
					 focusedMarker;
	
	public StationOverlayItem(Station station)
	{
		super(new GeoPoint((int)(station.latitude() * 1E6),
						  (int)(station.longitude() * 1E6)), null, null);
		_station = station;

		switch(station.type())
		{
			case Station.BIO:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_bio);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_bio_d);
			break;
			case Station.ELE:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_ele);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_ele_d);
			break;
			case Station.ETH:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_eth);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_eth_d);
			break;
			case Station.HYD:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_hyd);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_hyd_d);
			break;
			case Station.CNG:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_cng);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_cng_d);
			break;
			case Station.LNG:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_lng);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_lng_d);
			break;
			case Station.PRO:
				defaultMarker = App.Current.getResources().getDrawable(R.drawable.s_pro);
				focusedMarker = App.Current.getResources().getDrawable(R.drawable.s_pro_d);
			break;
		}
	}

	public Station station() { return _station; }

	@Override
	public Drawable getMarker(int stateBitset)
	{
		switch(stateBitset)
		{
			case ITEM_STATE_FOCUSED_MASK:
			case ITEM_STATE_PRESSED_MASK:
			case ITEM_STATE_SELECTED_MASK:
				return focusedMarker;
		}
		if(this == App.OverlayStation.lastFocus())
			return focusedMarker;
		return defaultMarker;
	}
}
