package com.tripquiz.controller.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.LocationsListAdapter;
import com.tripquiz.controller.adapter.LocationsListAdapter.LocationItem;
import com.tripquiz.controller.dialog.ConfirmDialog;
import com.tripquiz.controller.dialog.ListDialog;
import com.tripquiz.positionservice.IPositionService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;

public class SummaryFragment extends AFragment {

	private Challenge challenge;

	// UI Container
	private ViewGroup fragmentContainer;

	private LocationsListAdapter laAdapter;
	private ActionListenerAdapter actionListener;

	private ListView locationView;
	// private ProgressBar challengeProgress;
	private TextView userProgress;

	private ILocationClickListener locationItemClickListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentContainer = (ViewGroup) inflater.inflate(R.layout.fragment_summary, null);

		return fragmentContainer;
	}

	@Override
	protected void onResumeOnFinish() {
		// read arguments
		EntityKey challengeKey = new EntityKey(getArguments().getString(WebserviceModel.CHALLENGE_ID), Challenge.class);
		challenge = challengeManager.getWebserviceModel().getChallengeSet().getChallenge(challengeKey);
		locationItemClickListener = (ILocationClickListener) activity;

		super.onResumeOnFinish();

		TextView challName = (TextView) fragmentContainer.findViewById(R.id.summary_challengename);
		TextView challDesc = (TextView) fragmentContainer.findViewById(R.id.summary_challengedescription);
		userProgress = (TextView) fragmentContainer.findViewById(R.id.summary_userprogress);

		locationView = (ListView) fragmentContainer.findViewById(R.id.summary_locationlist);

		// title
		challName.setText(challenge.getName());
		challDesc.setText(challenge.getDescription());

		// progress
		// challengeProgress = (ProgressBar) fragmentContainer.findViewById(R.id.summary_userprogressbar);

		refresh();

		laAdapter = new LocationsListAdapter(activity, challengeManager.getImageRepository());
		setLocations();

		Button rankingButton = (Button) fragmentContainer.findViewById(R.id.ranking_button);
		if (challenge.hasCompetitorProgress() && challenge.getCompetitorProgress().hasUserRanking()) {
			rankingButton.setVisibility(View.VISIBLE);
			rankingButton.setText(challenge.getCompetitorProgress().getUserRanking() + ".");
			rankingButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (!challengeManager.getUIState().isActionDialogOpened()) {
					// challengeManager.getUIState().setActionDialogOpened(true);
					FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
					transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					transaction.add(ListDialog.newInstance(challengeManager, challenge), ListDialog.class.toString()).addToBackStack(null);
					transaction.commit();
					// }
				}
			});
		} else
			rankingButton.setVisibility(View.GONE);

		View deleteButton = fragmentContainer.findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (!challengeManager.getUIState().isActionDialogOpened()) {
				// challengeManager.getUIState().setActionDialogOpened(true);
				FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction.add(ConfirmDialog.newInstance(challengeManager, challenge, challengeManager.getString(R.string.challenge_delete)),
						ConfirmDialog.class.toString()).addToBackStack(null);
				transaction.commit();
				// }
			}
		});

		if (challenge.hasCompetitorProgress()) {
			fragmentContainer.findViewById(R.id.listitem_single).setVisibility(View.GONE);
			fragmentContainer.findViewById(R.id.listitem_group).setVisibility(View.VISIBLE);
		} else {
			fragmentContainer.findViewById(R.id.listitem_single).setVisibility(View.VISIBLE);
			fragmentContainer.findViewById(R.id.listitem_group).setVisibility(View.GONE);
		}

		View lockImage = fragmentContainer.findViewById(R.id.listitem_lock);
		if (challenge.isRestricted())
			lockImage.setVisibility(View.VISIBLE);
		else
			lockImage.setVisibility(View.GONE);

		actionListener = new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE) {
					Challenge loadedChallenge = (Challenge) action.getConversionResult();
					if (loadedChallenge.getKey().equals(challenge.getKey())) {
						challenge = loadedChallenge;
						refresh();
						setLocations();
					}
				}
			}
		};

		challengeManager.getActionFactory().addListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
	}

	private void refresh() {
		if (challenge.getProgress() > 0) {
			userProgress.setText(Math.round(challenge.getProgress() * 100) + "%");
			userProgress.setVisibility(View.VISIBLE);
		} else {
			userProgress.setText(Math.round(challenge.getProgress() * 100) + "%");
			userProgress.setVisibility(View.GONE);
		}
		// challengeProgress.setMax(100);
		// if (challenge.getProgress() > 0)
		// challengeProgress.setProgress((int) Math.round(challenge.getProgress() * 100));
		// else
		// challengeProgress.setProgress(2);
	}

	private void setLocations() {
		if (locationView != null) {
			laAdapter.clear();
			for (LocationDt loc : challenge.getLocations()) {
				double distance = -1;
				if (positionService != null && positionService.hasDistanceTo(loc.getKey().getKey()))
					distance = positionService.getDistanceTo(loc.getKey().getKey());
				laAdapter.add(new LocationItem(loc.getKey(), loc.getName(), distance, loc.isFinished(), loc.getImageUrl()));
			}
			locationView.setAdapter(laAdapter);
			locationView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (locationItemClickListener != null) {
						LocationItem item = (LocationItem) parent.getItemAtPosition(position);
						locationItemClickListener.LocationClicked(item.getEntityKey());
					}
				}
			});
		}
	}

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		super.positionUpdate(enabled, positionService);
		if (laAdapter != null)
			for (LocationDt loc : challenge.getLocations()) {
				double distance = -1;
				if (positionService != null && positionService.hasDistanceTo(loc.getKey().getKey()))
					distance = positionService.getDistanceTo(loc.getKey().getKey());
				laAdapter.update(loc.getKey(), distance);
			}
	}

	public void onPause() {
		super.onPause();
		if (challengeManager != null)
			challengeManager.getActionFactory().removeListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
	}

	@Override
	public String getHeader() {
		return getArguments().getString(WebserviceModel.FRAGMENT_NAME);
	}

}
