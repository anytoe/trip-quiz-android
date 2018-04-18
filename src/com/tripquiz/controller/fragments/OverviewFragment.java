package com.tripquiz.controller.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.SectionsPagerAdapter;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionType;

public class OverviewFragment extends AFragment {

	private ChallengesUserFragment userChallengesFragment;
	private ChallengesNewFragment newChallengesFragment;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private View rootView;
//	private IHeaderClickListener headerClick;
//	private IChallengeClickListener challengeClick;

	private boolean hasCheckedForUserChallenges = false;
	private ActionListenerAdapter actionListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_overview, container, false);
		return rootView;
	}

	@Override
	protected void onResumeOnFinish() {
		super.onResumeOnFinish();

		actionListener = new ActionListenerAdapter() {
			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (!hasCheckedForUserChallenges && action.getAppActionType() == AsyncActionType.ChallengesUser) {
					WebserviceModel webserviceModel = challengeManager.getWebserviceModel();
					if (webserviceModel.isInitialised() && !webserviceModel.getChallengeSet().hasChallenges(AsyncActionType.ChallengesUser.toString())) {
						mViewPager.setCurrentItem(1);
						hasCheckedForUserChallenges = true;
					}
				}
			}
		};

		challengeManager.getActionFactory().addListenerAction(AsyncActionType.ChallengesUser, actionListener);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(10);

		// if (mSectionsPagerAdapter == null) {
		userChallengesFragment = new ChallengesUserFragment();
		newChallengesFragment = new ChallengesNewFragment();
		mSectionsPagerAdapter = new SectionsPagerAdapter(super.challengeManager, getChildFragmentManager());
		mSectionsPagerAdapter.addFragment(ChallengesUserFragment.class.toString(), userChallengesFragment);
		mSectionsPagerAdapter.addFragment(ChallengesNewFragment.class.toString(), newChallengesFragment);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// overwrite the layout manually
		// PagerTitleStrip pagerTitleStrip = (PagerTitleStrip) rootView.findViewById(R.id.pager_title_strip);
		// // pagerTitleStrip.setOnClickListener(new OnClickListener() { .. }
		// for (int counter = 0; counter < pagerTitleStrip.getChildCount(); counter++) {
		// if (pagerTitleStrip.getChildAt(counter) instanceof TextView) {
		// ((TextView) pagerTitleStrip.getChildAt(counter)).setTextAppearance(activity, R.style.FragmentTitleTextSelected);
		// if(counter == 0)
		// ((TextView) pagerTitleStrip.getChildAt(counter)).setTextSize(5);
		// }
		// }

		WebserviceModel webserviceModel = challengeManager.getWebserviceModel();
		if (!hasCheckedForUserChallenges && webserviceModel.isInitialised()
				&& !webserviceModel.getChallengeSet().hasChallenges(AsyncActionType.ChallengesUser.toString())) {
			mViewPager.setCurrentItem(mSectionsPagerAdapter.getPositionForKey(ChallengesNewFragment.class.toString()));
			hasCheckedForUserChallenges = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		if (challengeManager != null)
			challengeManager.getActionFactory().removeListenerAction(AsyncActionType.ChallengesUser, actionListener);
		super.onStop();
	}

	@Override
	public String getHeader() {
		return "Overview";
	}

}
