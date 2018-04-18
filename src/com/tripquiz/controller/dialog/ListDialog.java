package com.tripquiz.controller.dialog;

import java.util.Collection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.controller.adapter.RankingListAdapter;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.CompetitorProgress.RankingListItem;

public class ListDialog extends DialogFragment {

	private Challenge challenge;
	private TripQuizContext tripQuizContext;

	public static ListDialog newInstance(TripQuizContext tripQuizContext, Challenge challenge) {
		ListDialog listDialog = new ListDialog();
		listDialog.init(tripQuizContext, challenge);
		return listDialog;
	}

	public void init(TripQuizContext tripQuizContext, Challenge challenge) {
		this.tripQuizContext = tripQuizContext;
		this.challenge = challenge;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(tripQuizContext.getContext());
		// builderSingle.setTitle(tripQuizContext.getString(R.string.challenges_summary_ranking));

		final RankingListAdapter rankingAdapter = new RankingListAdapter(tripQuizContext.getContext(), challenge.getCompetitorProgress().getUserRanking());
		Collection<RankingListItem> rankingList = challenge.getCompetitorProgress().GetRanking();
		rankingAdapter.addAll(rankingList);
		
		builderSingle.setAdapter(rankingAdapter, null);
		AlertDialog dialog = builderSingle.show();
		return dialog;
	}

}
