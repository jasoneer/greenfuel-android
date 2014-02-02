package com.isotix.greenfuel.overlay;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;
import com.isotix.greenfuel.model.Search;
import com.isotix.greenfuel.model.Station;

import java.util.ArrayList;

public class StationOverlay extends ItemizedOverlay
{
	private ArrayList<StationOverlayItem> stationOverlayItemList;
	
	public StationOverlay()
	{
		super(boundCenterBottom(App.Current.getResources()
								.getDrawable(R.drawable.station)));
		stationOverlayItemList = new ArrayList<StationOverlayItem>();
		populate();
	}

	public void buildOverlayItems()
	{
		stationOverlayItemList.clear();
		stationOverlayItemList.ensureCapacity(Search.Stations.size());
		setLastFocusedIndex(-1);
		populate();
		for(Station station : Search.Stations) {
			StationOverlayItem item = new StationOverlayItem(station);
			boundCenterBottom(item.getMarker(0));
			boundCenterBottom(item.getMarker(OverlayItem.ITEM_STATE_FOCUSED_MASK));
			stationOverlayItemList.add(item);
		}
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	public int size()
	{
		return stationOverlayItemList.size();
	}

	@Override
	protected OverlayItem createItem(int i)
	{
		return stationOverlayItemList.get(i);
	}

	private StationOverlayItem lastFocusedItem;
	public StationOverlayItem lastFocus() { return lastFocusedItem; }

	@Override
	protected boolean onTap(int index)
	{
		//Toast.makeText(App.Current, "Station Tapped", Toast.LENGTH_SHORT).show();

		lastFocusedItem = stationOverlayItemList.get(index);
		Station station = lastFocusedItem.station();

		App.CurrentActivity.openStationInfoDialog(station);
		App.CurrentActivity.onPositionFix(station.latitude(), station.longitude());
		
		return true;
	}
}
