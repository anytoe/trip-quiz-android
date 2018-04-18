package com.tripquiz.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.ChallengeNewAdapter;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.ChallengeSet;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionType;

public class ChallengesNewFragment extends AListFragment {

	public static final String ARG_SECTION_NUMBER = "section_number";

	private Challenge[] newChallengesByOrder;
	private final ActionListenerAdapter actionListener;

	private ChallengeNewAdapter adapter;

	public ChallengesNewFragment() {
		// default action listener
		actionListener = new ActionListenerAdapter() {
			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				setEmptyText(R.string.main_new_none);
				update();
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_challenges_new, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onResumeOnFinish() {
		super.onResumeOnFinish();
		challengeManager.getActionFactory().addListenerAction(AsyncActionType.ChallengesNew, actionListener);

		// search box
		// final LinearLayout container = (LinearLayout) activity.findViewById(R.id.challenge_new_container);
		final EditText text = (EditText) activity.findViewById(R.id.challenge_new_searchtext);
		final ImageButton button = (ImageButton) activity.findViewById(R.id.challenge_new_searchbutton);
		final ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.challenge_new_loadimage);

		if (challengeManager.getWebserviceModel().isExecuting(AsyncActionType.ChallengesNew)) {
			button.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			button.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}

		OnClickListener l = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!text.getText().toString().equals(activity.getString(R.string.main_new_searchtext)))
					challengeManager.getActionFactory().loadNewChallenges(text.getText().toString().trim());
				else
					challengeManager.getActionFactory().loadNewChallenges("");
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getActivity().getCurrentFocus() != null)
					inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		};
		button.setOnClickListener(l);

		challengeManager.getActionFactory().addListenerAction(AsyncActionType.ChallengesNew, new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStarted(AsyncAction action) {
				button.setVisibility(View.INVISIBLE);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				button.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}

			@Override
			public void notifyOnError(AsyncAction action, Exception exception) {
				button.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}

		});

		challengeManager.getActionFactory().addListenerAction(AsyncActionType.CHALLENGE_ADD, new ActionListenerAdapter() {
			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				update();
			}
		});

		text.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String searchText = text.getText().toString();
				if (hasFocus) {
					if (searchText.equals(activity.getString(R.string.main_new_searchtext)))
						text.setText("");
					text.setTextColor(getResources().getColor(R.color.black));
				} else if (searchText.equals("")) {
					text.setTextColor(getResources().getColor(R.color.grey_light));
					text.setText(activity.getString(R.string.main_new_searchtext));
				}
			}
		});

		// adapter
		update();

		if (newChallengesByOrder == null || newChallengesByOrder.length == 0
				&& !challengeManager.getWebserviceModel().isExecuting(AsyncActionType.ChallengesNew))
			button.performClick();
	}

	private void update() {
		if (isFragmentInitialised() && challengeManager.getWebserviceModel().isInitialised()) {
			ChallengeSet set = challengeManager.getWebserviceModel().getChallengeSet();
			newChallengesByOrder = set.getChallengeArrayByOrder(AsyncActionType.ChallengesNew.toString(), null);
			adapter = new ChallengeNewAdapter(activity, challengeManager.getImageRepository(), R.layout.challenge_new_listitem, newChallengesByOrder,
					challengeManager.getActionFactory());
			setListAdapter(adapter);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Challenge clickedChallenge = newChallengesByOrder[position];
		challengeClick.notifyOnChallengeClick(clickedChallenge.getKey());
	}

	@Override
	public String getHeader() {
		return "Search for your TripQuiz";
	}

	@Override
	public void onPause() {
		if (challengeManager != null) {
			challengeManager.getActionFactory().removeListenerAction(AsyncActionType.ChallengesNew, actionListener);
		}
		super.onPause();
	}

}
