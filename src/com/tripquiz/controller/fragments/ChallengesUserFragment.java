package com.tripquiz.controller.fragments;

import java.util.Comparator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.ChallengeUserAdapter;
import com.tripquiz.positionservice.IPositionService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.ChallengeSet;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;

public class ChallengesUserFragment extends AListFragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	private Challenge[] userChallengesByOrder;
	private ActionListenerAdapter actionListener;

	public ChallengesUserFragment() {
		actionListener = new ActionListenerAdapter() {
			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.getAppActionType() == AsyncActionType.ChallengesUser) {
					update();
				} else if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE) {
					update();
				}
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_challenges_user, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onResumeOnFinish() {
		super.onResumeOnFinish();
		challengeManager.getActionFactory().addListenerAction(AsyncActionType.ChallengesUser, actionListener);
		challengeManager.getActionFactory().addListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
		if (challengeManager.getWebserviceModel().isInitialised()) {
			update();
		}
	}

	private void update() {
		if (isFragmentInitialised() && challengeManager.getWebserviceModel().isInitialised()) {
			setEmptyText(R.string.main_user_none);
			ChallengeSet set = challengeManager.getWebserviceModel().getChallengeSet();
			userChallengesByOrder = set.getChallengeArrayByOrder(AsyncActionType.ChallengesUser.toString(), new CompareUserChallenges(positionService));
			ChallengeUserAdapter adapter = new ChallengeUserAdapter(activity, challengeManager.getImageRepository(), R.layout.challenge_user_listitem,
					userChallengesByOrder);
			setListAdapter(adapter);
		}
	}

	public class CompareUserChallenges implements Comparator<Challenge> {

		private IPositionService positionService;

		public CompareUserChallenges(IPositionService positionService) {
			this.positionService = positionService;
		}

		@Override
		public int compare(Challenge lhs, Challenge rhs) {
			if (positionService != null) {

				boolean lhsHasLocationInRange = false;
				boolean rhsHasLocationInRange = false;
				for (LocationDt loc : lhs.getLocations())
					if (positionService.hasLocationInRange(loc.getKey().getKey())) {
						lhsHasLocationInRange = true;
						break;
					}

				for (LocationDt loc : rhs.getLocations())
					if (positionService.hasLocationInRange(loc.getKey().getKey())) {
						rhsHasLocationInRange = true;
						break;
					}

				if (lhsHasLocationInRange && !rhsHasLocationInRange)
					return -1;
				else if (!lhsHasLocationInRange && rhsHasLocationInRange)
					return 1;
			}

			return (int) Math.round(rhs.getProgress() - lhs.getProgress());
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Challenge clickedChallenge = userChallengesByOrder[position];
		challengeClick.notifyOnChallengeClick(clickedChallenge.getKey());
	}

	@Override
	public String getHeader() {
		return "Your Challenges";
	}

	@Override
	public void onPause() {
		if (challengeManager != null) {
			challengeManager.getActionFactory().removeListenerAction(AsyncActionType.ChallengesUser, actionListener);
			challengeManager.getActionFactory().removeListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
		}
		super.onPause();
	}

}
