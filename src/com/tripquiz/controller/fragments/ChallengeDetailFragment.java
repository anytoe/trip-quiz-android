package com.tripquiz.controller.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.SectionsPagerAdapter;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.model.WebserviceModel;

public class ChallengeDetailFragment extends AFragment {

	private View rootView;
	private Challenge challenge;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	// private IHeaderClickListener headerClick;
	private EntityKey locationKey;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_challengedetail, container, false);
		return rootView;
	}

	@Override
	protected void onResumeOnFinish() {
		// read arguments
		EntityKey challengeKey = new EntityKey(getArguments().getString(WebserviceModel.CHALLENGE_ID), Challenge.class);
		challenge = challengeManager.getWebserviceModel().getChallengeSet().getChallenge(challengeKey);
		if (getArguments().containsKey(WebserviceModel.LOCATION_ID))
			locationKey = new EntityKey(getArguments().getString(WebserviceModel.LOCATION_ID), LocationDt.class);

		super.onResumeOnFinish();

		mSectionsPagerAdapter = new SectionsPagerAdapter(challengeManager, getChildFragmentManager());
		SummaryFragment fragment = new SummaryFragment();
		Bundle paramsSummary = new Bundle();
		paramsSummary.putString(WebserviceModel.FRAGMENT_NAME, "Summary");
		paramsSummary.putString(WebserviceModel.CHALLENGE_ID, challenge.getKey().getKey());
		fragment.setArguments(paramsSummary);
		mSectionsPagerAdapter.addFragment(SummaryFragment.class.toString(), fragment);

		for (LocationDt loc : challenge.getLocations()) {
			TaskFragment taskFragment = new TaskFragment();
			Bundle taskParams = new Bundle();
			taskParams.putString(WebserviceModel.FRAGMENT_NAME, loc.getName());
			taskParams.putString(WebserviceModel.LOCATION_ID, loc.getKey().getKey());
			taskFragment.setArguments(taskParams);
			mSectionsPagerAdapter.addFragment(loc.getKey().toString(), taskFragment);
		}

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// overwrite the layout manually
		// final PagerTitleStrip pagerTitleStrip = (PagerTitleStrip) rootView.findViewById(R.id.pager_title_strip);

		// pagerTitleStrip.setOnClickListener(new OnClickListener() { .. }
		// for (int counter = 0; counter < pagerTitleStrip.getChildCount(); counter++) {
		// if (pagerTitleStrip.getChildAt(counter) instanceof TextView) {
		// ((TextView) pagerTitleStrip.getChildAt(counter)).setTextAppearance(activity, R.style.FragmentTitleText);
		// if (counter == 0)
		// ((TextView) pagerTitleStrip.getChildAt(counter)).setTextSize(5);
		// }
		// }

		// mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int arg0) {
		// if (pagerTitleStrip.getChildCount() >= arg0) {
		// ((TextView) pagerTitleStrip.getChildAt(arg0)).setTextSize(20);
		// if (arg0 - 1 >= 0) {
		// ((TextView) pagerTitleStrip.getChildAt(arg0 - 1)).setTextSize(20);
		// } else if (arg0 + 1 <= pagerTitleStrip.getChildCount()) {
		// ((TextView) pagerTitleStrip.getChildAt(arg0 + 1)).setTextSize(20);
		// }
		// }
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		// }
		// });

		if (locationKey != null) {
			mViewPager.setCurrentItem(mSectionsPagerAdapter.getPositionForKey(locationKey.getKey()));
			locationKey = null;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public String getHeader() {
		return getArguments().getString(WebserviceModel.FRAGMENT_NAME);
	}

	public boolean isResetToSummary() {
		return mViewPager != null && mViewPager.getCurrentItem() == 0;
	}

	public void selectLocation(EntityKey locationKey) {
		if (mViewPager != null)
			mViewPager.setCurrentItem(mSectionsPagerAdapter.getPositionForKey(locationKey.getKey()));
	}

	public void resetToSummary() {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(0);
		}
	}

}
