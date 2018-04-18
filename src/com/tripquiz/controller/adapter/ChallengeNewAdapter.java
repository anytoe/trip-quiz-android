package com.tripquiz.controller.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.persistance.ImageRepositoryInMemory;
import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webgeneric.ImageDownloader;

public class ChallengeNewAdapter extends ArrayAdapter<Challenge> implements OnClickListener {

	private Context context;
	private int resource;
	private Challenge[] challenges;
	private AChallengeMeService webservice;

	private HashMap<String, Boolean> markedByUser;
	private Set<EntityKey> removedChallenges;
	private ImageRepositoryInMemory imageRepository;

	public ChallengeNewAdapter(Context context, ImageRepositoryInMemory imageRepository, int resource, Challenge[] challenges, AChallengeMeService webservice) {
		super(context, resource, challenges);
		this.context = context;
		this.imageRepository = imageRepository;
		this.resource = resource;
		this.challenges = challenges;
		this.webservice = webservice;

		markedByUser = new HashMap<String, Boolean>();
		removedChallenges = new HashSet<EntityKey>();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// inflate
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(resource, null);

		if (removedChallenges.contains(challenges[position].getKey())) {
			convertView.setVisibility(View.GONE);
			return convertView;
		}

		// populate
		TextView title = (TextView) convertView.findViewById(R.id.challenge_new_listitem_title);
		title.setText(challenges[position].getName());

		String description = challenges[position].getDescription();
		TextView desc = (TextView) convertView.findViewById(R.id.challenge_new_listitem_description);
		if (description != null && !description.equals("")) {
			desc.setText(description);
			desc.setVisibility(View.VISIBLE);
		} else {
			desc.setVisibility(View.GONE);
		}

		ImageView addRemoveButton = (ImageView) convertView.findViewById(R.id.challenge_new_listitem_addremove);
		addRemoveButton.setTag(challenges[position].getKey().getKey());
		markedByUser.put(challenges[position].getKey().getKey(), false);
		addRemoveButton.setOnClickListener(this);

		// if (!challenges[position].hasCompetitorProgress())
		// convertView.findViewById(R.id.challenge_new_redmarker).setBackgroundColor(context.getResources().getColor(R.color.green));
		// else
		// convertView.findViewById(R.id.challenge_new_redmarker).setBackgroundColor(context.getResources().getColor(R.color.red));

		if (challenges[position].isRestricted())
			convertView.findViewById(R.id.challenge_new_listitem_lock).setVisibility(View.VISIBLE);
		else
			convertView.findViewById(R.id.challenge_new_listitem_lock).setVisibility(View.GONE);

		if (challenges[position].hasCompetitorProgress()) {
			convertView.findViewById(R.id.challenge_new_listitem_single).setVisibility(View.GONE);
			convertView.findViewById(R.id.challenge_new_listitem_group).setVisibility(View.VISIBLE);
		} else {
			convertView.findViewById(R.id.challenge_new_listitem_single).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.challenge_new_listitem_group).setVisibility(View.GONE);
		}

		// load image
		final View contentView = convertView.findViewById(R.id.challenge_new_listitem_content);
		contentView.setVisibility(View.INVISIBLE);
		final ImageView imageView = (ImageView) convertView.findViewById(R.id.challenge_new_listitem_image);

		OnLayoutChangeListener onLayoutListener = new LayoutChangedListener(context, new ImageDownloader(imageRepository), getItem(position),
				imageView, contentView, contentView);
		contentView.addOnLayoutChangeListener(onLayoutListener);

		return convertView;
	}

	@Override
	public void onClick(View v) {
		String key = v.getTag().toString();
		if (!markedByUser.get(key)) {
			markedByUser.put(key, !markedByUser.get(key));
			webservice.addChallenge(new EntityKey(key, Challenge.class), true);
			ImageView addRemoveButton = (ImageView) v;
			if (markedByUser.get(key))
				addRemoveButton.setImageResource(R.drawable.overview_plus_blue);
			else
				addRemoveButton.setImageResource(R.drawable.overview_plus_grey);
		}
	}

}
