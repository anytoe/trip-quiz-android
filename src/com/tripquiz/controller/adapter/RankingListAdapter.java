package com.tripquiz.controller.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.webapi.model.CompetitorProgress.RankingListItem;

public class RankingListAdapter extends ArrayAdapter<RankingListItem> {

	private final Context context;
	private int userRanking;

	public RankingListAdapter(Context context, int userRanking) {
		super(context, R.layout.detail_task);
		this.context = context;
		this.userRanking = userRanking;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// inflate
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ranking_listitem, null);
		}

		final RankingListItem rankingItem = getItem(position);
		final TextView rankingText = (TextView) convertView.findViewById(R.id.ranking_text);
		rankingText.setText((position + 1) + ". " + rankingItem.getUserName() + " (" + Math.round(rankingItem.getPercentageFinished() * 100) + "%)");
		if (position == userRanking - 1)
			rankingText.setTypeface(null, Typeface.BOLD);
		else
			rankingText.setTypeface(null, Typeface.NORMAL);

		return convertView;
	}
}
