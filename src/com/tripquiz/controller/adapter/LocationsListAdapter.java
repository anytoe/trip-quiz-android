package com.tripquiz.controller.adapter;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.LocationsListAdapter.LocationItem;
import com.tripquiz.controller.string.StringCreator;
import com.tripquiz.persistance.ImageRepositoryInMemory;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.ImageUrl;
import com.tripquiz.webgeneric.ImageDownloader;

public class LocationsListAdapter extends ArrayAdapter<LocationItem> {

	private HashMap<EntityKey, TextView> itemDistanceViewMap;
	private Context context;
	private ImageRepositoryInMemory imageRepository;

	public LocationsListAdapter(Context context, ImageRepositoryInMemory imageRepository) {
		super(context, R.layout.location_listitem);
		this.context = context;
		this.imageRepository = imageRepository;
		itemDistanceViewMap = new HashMap<EntityKey, TextView>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// inflate
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.location_listitem, null);

		final LocationItem locationItem = getItem(position);

		final TextView rankingText = (TextView) convertView.findViewById(R.id.location_name);
		rankingText.setText(locationItem.name);

		final TextView distanceText = (TextView) convertView.findViewById(R.id.location_distance);
		final ImageView finishedLocation = (ImageView) convertView.findViewById(R.id.location_finished);

		if (locationItem.isSolved) {
			finishedLocation.setVisibility(View.VISIBLE);
			distanceText.setVisibility(View.INVISIBLE);
		} else {
			distanceText.setText(StringCreator.getDistanceString(locationItem.distance));
			itemDistanceViewMap.put(locationItem.entityKey, distanceText);
			distanceText.setVisibility(View.VISIBLE);
			finishedLocation.setVisibility(View.INVISIBLE);
		}

		// load image
		final ImageView imageView = (ImageView) convertView.findViewById(R.id.location_image);
		final View contentView = convertView.findViewById(R.id.location_listitem_content);
		OnLayoutChangeListener layoutChangeListener = new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (getItem(position).url != null) {
					if (v.getWidth() > 0 && v.getHeight() > 0) {
						String imageUrl = getItem(position).url.getUrl(v.getWidth(), v.getHeight());
						ImageDownloader imageDownload = new ImageDownloader(imageRepository);
						imageDownload.init(imageUrl, imageView, null, null);
						imageDownload.execute();
					}
				}
				contentView.removeOnLayoutChangeListener(this);
			}
		};
		contentView.addOnLayoutChangeListener(layoutChangeListener);

		return convertView;
	}

	public void update(EntityKey entityKey, double newDistance) {
		if (itemDistanceViewMap != null && itemDistanceViewMap.containsKey(entityKey))
			itemDistanceViewMap.get(entityKey).setText(StringCreator.getDistanceString(newDistance));
	}

	public static class LocationItem {

		private EntityKey entityKey;
		private String name;
		private double distance;
		private boolean isSolved;
		private ImageUrl url;

		public LocationItem(EntityKey entityKey, String name, double distance, boolean isSolved, ImageUrl url) {
			super();
			this.entityKey = entityKey;
			this.name = name;
			this.distance = distance;
			this.isSolved = isSolved;
			this.url = url;
		}

		public EntityKey getEntityKey() {
			return entityKey;
		}

		public String getName() {
			return name;
		}

		public double getDistance() {
			return distance;
		}

		public boolean isSolved() {
			return isSolved;
		}

		public ImageUrl getUrl() {
			return url;
		}
	}

}
