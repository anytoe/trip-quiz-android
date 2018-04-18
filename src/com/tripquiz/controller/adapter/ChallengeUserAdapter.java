package com.tripquiz.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.persistance.ImageRepositoryInMemory;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webgeneric.ImageDownloader;

public class ChallengeUserAdapter extends ArrayAdapter<Challenge> {

	private Context context;
	private int resource;
	private ImageRepositoryInMemory imageRepository;

	public ChallengeUserAdapter(Context context, ImageRepositoryInMemory imageRepository, int resource, Challenge[] challenges) {
		super(context, resource, challenges);
		this.context = context;
		this.imageRepository = imageRepository;
		this.resource = resource;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// inflate
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(resource, null);

		// populate
		TextView title = (TextView) convertView.findViewById(R.id.challenge_user_listitem_title);
		title.setText(getItem(position).getName());

		TextView percentage = (TextView) convertView.findViewById(R.id.challenge_user_listitem_percentage);
		if (getItem(position).getProgress() > 0) {
			percentage.setText(Math.round(getItem(position).getProgress() * 100) + "%");
			percentage.setVisibility(View.VISIBLE);
		} else {
			percentage.setVisibility(View.GONE);
		}

		if (getItem(position).isRestricted())
			convertView.findViewById(R.id.challenge_user_listitem_lock).setVisibility(View.VISIBLE);
		else
			convertView.findViewById(R.id.challenge_user_listitem_lock).setVisibility(View.GONE);

		if (getItem(position).hasCompetitorProgress()) {
			convertView.findViewById(R.id.challenge_user_listitem_single).setVisibility(View.GONE);
			convertView.findViewById(R.id.challenge_user_listitem_group).setVisibility(View.VISIBLE);
		} else {
			convertView.findViewById(R.id.challenge_user_listitem_single).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.challenge_user_listitem_group).setVisibility(View.GONE);
		}

		// load image
		final View contentView = convertView.findViewById(R.id.challenge_user_listitem_content);
		final View container = convertView.findViewById(R.id.challenge_user_listitem_container);
		final ImageView imageView = (ImageView) convertView.findViewById(R.id.challenge_user_listitem_image);
		
		OnLayoutChangeListener onLayoutListener = new LayoutChangedListener(context, new ImageDownloader(imageRepository), getItem(position),
				imageView, contentView, container);
		contentView.addOnLayoutChangeListener(onLayoutListener);

		return convertView;
	}
}
