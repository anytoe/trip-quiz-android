package com.tripquiz.controller.adapter;

import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.controller.fragments.INamedFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private HashMap<String, Fragment> fragmentByKey;
	private HashMap<String, Integer> orderByKey;
	private HashMap<Integer, Fragment> fragmentByOrder;

	private int fragmentPosition;

	public SectionsPagerAdapter(TripQuizContext context, FragmentManager fm) {
		super(fm);
		fragmentByKey = new HashMap<String, Fragment>();
		orderByKey = new HashMap<String, Integer>();
		fragmentByOrder = new HashMap<Integer, Fragment>();
		fragmentPosition = 0;
	}

	public void addFragment(String key, Fragment fragment) {
		if (fragmentByKey.containsKey(key))
			throw new RuntimeException("Key for fragment does already exist");
		fragmentByKey.put(key, fragment);
		orderByKey.put(key, fragmentPosition);
		fragmentByOrder.put(fragmentPosition++, fragment);
	}

	public int getPositionForKey(String key) {
		return orderByKey.get(key);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = fragmentByOrder.get(position);
		return fragment;
	}

	@Override
	public int getCount() {
		return fragmentPosition;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ((INamedFragment) fragmentByOrder.get(position)).getHeader();
	}

//	public void destroyAllItems(ViewGroup container) {
//		for (Entry<Integer, Fragment> entry : fragmentByOrder.entrySet()) {
//			destroyItem(container, entry.getKey(), entry.getValue());
//		}
//	}

//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		super.destroyItem(container, position, object);
//		Fragment fragment = (Fragment) object;
//		if (position <= getCount()) {
//			FragmentManager manager = fragment.getFragmentManager();
//			FragmentTransaction trans = manager.beginTransaction();
//			trans.remove(fragment);
//			trans.commit();
//		}
//	}
}