package com.tripquiz.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tripquiz.R;
import com.tripquiz.controller.dialog.AlertMessageDialog;
import com.tripquiz.controller.fragments.ChallengeDetailFragment;
import com.tripquiz.controller.fragments.IChallengeClickListener;
import com.tripquiz.controller.fragments.IHeaderClickListener;
import com.tripquiz.controller.fragments.ILocationClickListener;
import com.tripquiz.controller.fragments.OverviewFragment;
import com.tripquiz.controller.fragments.StatusBarFragment;
import com.tripquiz.controller.fragments.TextFragment;
import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;

public class MainActivity extends AFragmentActivity implements IHeaderClickListener, IChallengeClickListener, ILocationClickListener {

	private AChallengeMeService webservice;
	private Class<?> activeFragment;
	private boolean isMinimized = false;
	private EntityKey locationKey;

	private boolean initialised = false;
	private ChallengeDetailFragment challengeDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init and start
		Bundle params = getIntent().getExtras();
		String locKey = null;
		if (params != null) {
			locKey = params.getString(WebserviceModel.LOCATION_ID);
			if (locKey != null) {
				locationKey = new EntityKey(locKey, LocationDt.class);
			}
		}
		challengeManager.getImageRepository().loadImages();
	}

	@Override
	public void onBackPressed() {

		// always close dialog first
		if (challengeManager.getUIState().isActionDialogOpened()) {
			this.getFragmentManager().popBackStack();
			challengeManager.getUIState().setActionDialogOpened(false);
			return;
		}

		// jump back to Summary
		if (activeFragment != null && activeFragment.equals(ChallengeDetailFragment.class) && challengeDetailFragment != null
				&& !challengeDetailFragment.isResetToSummary()) {
			challengeDetailFragment.resetToSummary();
			return;
		}

		// swap fragments
		if (activeFragment != null && activeFragment.equals(ChallengeDetailFragment.class) && !isMinimized)
			activeFragment = OverviewFragment.class;
		isMinimized = false;

		try {
			super.onBackPressed();
		} catch (Exception ex) {
			// TODO remove if found and solved the problem
		}
	}

	@Override
	public void initialised(final WebserviceModel webserviceModel) {
		statusBarFragment = (StatusBarFragment) getSupportFragmentManager().findFragmentById(R.id.statusbar_fragment);
		setVisibilityStatusBar();

		if (!initialised) { // change only if not running yet
			Challenge challenge = (locationKey != null) ? challenge = challengeManager.getWebserviceModel().getChallengeSet()
					.getChallengeByLocation(locationKey) : null;
			if (challenge == null) {
				showOverviewFragment(false);
				activeFragment = OverviewFragment.class;
			} else {
				showChallengeDetailFragment(challenge.getKey(), locationKey, false);
				activeFragment = ChallengeDetailFragment.class;
			}
		}
		initialised = true;

		challengeManager.getActionFactory().addListenerAction(AsyncActionType.CHECKIN, new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.isSuccess()) {
					showChallengeDetailFragment(webserviceModel.getChallengeSet().getChallengeByLocation(action.getEntityKey()).getKey(),
							action.getEntityKey(), true);
					activeFragment = ChallengeDetailFragment.class;
				}
			}
		});

		challengeManager.getActionFactory().addListenerAction(AsyncActionCategory.READ_CHALLENGES, new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStarted(AsyncAction action) {
				if (action.getAppActionType() == AsyncActionType.ChallengesUser) {
					MenuItem item = menu.findItem(R.id.menu_refresh);
					item.setIcon(R.drawable.navigationbar_invisible);
				}
			}

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.getAppActionType() == AsyncActionType.ChallengesUser) {
					MenuItem item = menu.findItem(R.id.menu_refresh);
					item.setIcon(R.drawable.navigationbar_refresh);
				}
			}
		});

		// if (!challengeManager.getWebserviceModel().getChallengeSet().hasChallenges(AsyncActionType.ChallengesNew.toString())) {
		// challengeManager.getActionFactory().loadNewChallenges("");
		// }
	}

	@Override
	public void initialised(User user, AChallengeMeService challengeMeService) {
		super.initialised(user, challengeMeService);
		webservice = challengeMeService;
	}

	@Override
	public void notifyOnChallengeClick(EntityKey challengeKey) {
		showChallengeDetailFragment(challengeKey, null, true);
		activeFragment = ChallengeDetailFragment.class; // TODO has it been forgotten? I've added it
	}

	@Override
	public void notifyOnHeaderClick(String headerText) {
		if (!isMinimized) {
			TextFragment fragment = new TextFragment();
			fragment.initFragment(headerText, this);
			startFragment(fragment, TextFragment.class, true);
			isMinimized = true;
		} else {
			onBackPressed();
		}
	}

	private void showOverviewFragment(boolean addToBackStack) {
		OverviewFragment overviewFragment = (OverviewFragment) getSupportFragmentManager().findFragmentByTag(OverviewFragment.class.toString());
		if (overviewFragment == null) {
			// create fragment
			overviewFragment = new OverviewFragment();
		}
		startFragment(overviewFragment, OverviewFragment.class, addToBackStack);
	}

	private void showChallengeDetailFragment(EntityKey challengeKey, EntityKey locationKey, boolean addToBackStack) {
		challengeDetailFragment = new ChallengeDetailFragment();
		Bundle params = new Bundle();
		params.putString(WebserviceModel.FRAGMENT_NAME, "Challenge Details");
		params.putString(WebserviceModel.CHALLENGE_ID, challengeKey.getKey());
		if (locationKey != null)
			params.putString(WebserviceModel.LOCATION_ID, locationKey.getKey());
		challengeDetailFragment.setArguments(params);
		startFragment(challengeDetailFragment, ChallengeDetailFragment.class, addToBackStack);
	}

	@Override
	public void LocationClicked(EntityKey key) {
		if (challengeDetailFragment != null)
			challengeDetailFragment.selectLocation(key);
	}

	private void startFragment(Fragment fragment, Class<?> clazz, boolean addToBackStack) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.content_fragment, fragment, clazz.toString());
		if (addToBackStack) {
			ft.addToBackStack(clazz.toString());
		}
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_permanent, menu);
		inflater.inflate(R.menu.main_expandable, menu);
		updateMenu();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			challengeManager.getImageRepository().clearImages();
			this.webservice.loadUserChallenges();
			return true;
		case android.R.id.home:
			showOverviewFragment(true);
			return true;
		case R.id.menu_enable_notification:
			challengeManager.setProcessRunBackground();
			switchNotifictionItem(item, challengeManager.isProcessRunBackground());
			return true;
		case R.id.menu_warninggpswifi:
			// TODO check if add is better to be replaced!
			FragmentManager fragmentManager = getSupportFragmentManager();
			if (fragmentManager.findFragmentByTag("menu_warninggpswifi") == null || !fragmentManager.findFragmentByTag("menu_warninggpswifi").isVisible()) {

				// message
				String headerMessage = getString(R.string.popupmessage_gpsdisabled);
				String message = getString(R.string.popupmessage_gpsdisabled_explanation);
				if (positionService != null && positionService.isGpsEnabled()) {
					headerMessage = getString(R.string.popupmessage_colourcode);
					message = getString(R.string.popupmessage_colourcode_explanation);
				}

				// init and show dialog
				AlertMessageDialog alertMessage = AlertMessageDialog.newInstance(headerMessage, message);
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction.add(android.R.id.content, alertMessage, "menu_warninggpswifi").addToBackStack(null).commit();
				return true;
			}
			return false;
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.action_logout:
			challengeManager.logoutUser();
			Intent loginIntent = new Intent(this, LoginActivity.class);
			startActivity(loginIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void updateMenu() {
		if (menu != null) {
			MenuItem item = menu.findItem(R.id.menu_warninggpswifi);
			if (item != null) {
				if (positionService == null || !positionService.isGpsEnabled())
					item.setIcon(R.drawable.navigationbar_warning);
				else if (positionAccuracy >= 1)
					item.setIcon(R.drawable.navigationbar_position_green);
				else if (positionAccuracy >= 0.3)
					item.setIcon(R.drawable.navigationbar_position_yellow);
				else
					item.setIcon(R.drawable.navigationbar_position_red);
			}
			item = menu.findItem(R.id.menu_enable_notification);
			if (item != null) {
				boolean processRunBackground = challengeManager.isProcessRunBackground();
				switchNotifictionItem(item, processRunBackground);
			}
		}
	}

	private void switchNotifictionItem(MenuItem item, boolean enable) {
		if (enable) {
			item.setIcon(R.drawable.navigationbar_bell_enabled);
		} else {
			item.setIcon(R.drawable.navigationbar_bell_disabled);
		}
		item.setVisible(true);
	}

	@Override
	protected void onStop() {
		if (challengeManager != null)
			challengeManager.getImageRepository().saveImages();
		super.onStop();
	}

	// TODO code scan
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// if (requestCode == 0) {
	// if (resultCode == RESULT_OK) {
	// String stringExtra = data.getStringExtra("SCAN_RESULT");
	// challengeManager = ChallengeManager.getInstance(this);
	// challengeMeWebservice.answerQuestion(challengeManager.getAppState().getExecutingTask(), stringExtra);
	// } else if (resultCode == RESULT_CANCELED) {
	// challengeManager.getUIState().setActionDialogOpened(false);
	// }
	// } else {
	// throw new RuntimeException("no other request code than 0 defined yet ;-)");
	// }
	// }

}
