package com.tripquiz.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.tripquiz.controller.defaulthandler.ActionResultNotifier;
import com.tripquiz.persistance.ImageRepositoryInMemory;
import com.tripquiz.persistance.Repository;
import com.tripquiz.positionservice.IPositionService;
import com.tripquiz.positionservice.NotificationConfiguration;
import com.tripquiz.positionservice.PositionListenerAdapter;
import com.tripquiz.positionservice.PositionServiceAccess;
import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.ChallengeMeService;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.ICredentialsValidator;
import com.tripquiz.webgeneric.ImageDownloader;

public class TripQuizContext extends Application {

	private static WebserviceModel webserviceModel;
	private static UIState uiState;
	private User user;

	private static Repository<User> userPersistance;
	private static Repository<WebserviceModel> webserviceModelPersistance;

	private static PositionServiceAccess positionServiceAccess;
	private static ChallengeMeService bufferedChallengeMeService;

	private ICredentialsValidator modelInitListener;
	private SharedPreferences preferences;

	private Activity activity;

	private static ImageRepositoryInMemory bitmapStore;
	private static TripQuizContext tripQuizContext;

	public static TripQuizContext getTripQuizContext() {
		return tripQuizContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tripQuizContext = (TripQuizContext) getApplicationContext();
	}

	public ImageRepositoryInMemory getImageRepository() {
		if (bitmapStore == null)
			bitmapStore = new ImageRepositoryInMemory(this);
		return bitmapStore;
	}

	public ImageDownloader getImageDownloader() {
		return new ImageDownloader(bitmapStore);
	}

	public void init(final Activity activity, ICredentialsValidator modelInitListener) {
		this.activity = activity;

		userPersistance = new Repository<User>(tripQuizContext, "User");
		webserviceModelPersistance = new Repository<WebserviceModel>(activity.getApplicationContext(), "WebserviceModel");

		if (uiState == null)
			uiState = new UIState();
		uiState.setAppMinimized(false);

		if (webserviceModel == null)
			if (webserviceModelPersistance.hasObject()) {
				webserviceModel = webserviceModelPersistance.loadObject();
				webserviceModel.afterLoad();
			} else
				webserviceModel = new WebserviceModel();

		if (bufferedChallengeMeService == null) {
			bufferedChallengeMeService = new ChallengeMeService(webserviceModel);
		}

		bufferedChallengeMeService.removeListenerAll();
		bufferedChallengeMeService.addListenerAllActions(new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				// update service after challenges have been read
				if (action.getAsyncActionCategory() == AsyncActionCategory.READ_CHALLENGES) {
					positionServiceAccess.setLocationforService(webserviceModel.getChallengeSet().getAllLocationsActive());
				}

				if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE) {
					positionServiceAccess.setLocationforService(webserviceModel.getChallengeSet().getAllLocationsActive());
				}

				// save after finished action
				if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE
						|| action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGES)
					webserviceModelPersistance.saveObject(webserviceModel);
			}

		});

		// TODO move out of tripquiz context and remove the activity
		bufferedChallengeMeService.addListenerAllActions(new ActionResultNotifier(activity, uiState, webserviceModel));

		// positioning service
		if (positionServiceAccess == null) {
			positionServiceAccess = new PositionServiceAccess(tripQuizContext, new NotificationConfiguration());
		}
		positionServiceAccess.startBinding();

		// load preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(tripQuizContext);
		boolean sendActionBarMessage = preferences.getBoolean("notifications_new_message", true);
		boolean vibrate = preferences.getBoolean("notifications_new_message_vibrate", true);
		positionServiceAccess.setNotificationSettings(sendActionBarMessage, sendActionBarMessage, vibrate);

		// observe preferences
		preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				boolean sendActionBarMessage = sharedPreferences.getBoolean("notifications_new_message", true);
				boolean vibrate = sharedPreferences.getBoolean("notifications_new_message_vibrate", true);
				positionServiceAccess.setNotificationSettings(sendActionBarMessage, sendActionBarMessage, vibrate);
			}
		});

		this.modelInitListener = modelInitListener;
		bufferedChallengeMeService.setWebserviceInitialisedListener(modelInitListener);
		positionServiceAccess.addListener(new PositionListenerAdapter() {

			@Override
			public void positionUpdate(boolean enabled, IPositionService positionService) {
				if (positionService != null && positionService.hasLastCoordinates())
					bufferedChallengeMeService.setLastCoordinates(positionService.getLastCoordinates());
			}
		});
	}

	public void setWebserviceInitialisedListener(ICredentialsValidator initListener) {
		this.modelInitListener = initListener;
		bufferedChallengeMeService.setWebserviceInitialisedListener(modelInitListener);
	}

	public void register(User user) {
		this.user = user;
		userPersistance.saveObject(user);
		bufferedChallengeMeService.register(user);
	}

	public void checkCredentials(User user) {
		this.user = user;
		userPersistance.saveObject(user);
		bufferedChallengeMeService.login(user);
	}

	public void checkCredentials() {
		if (user == null && userPersistance.hasObject())
			user = userPersistance.loadObject();
		if (user != null)
			bufferedChallengeMeService.login(user);
		else
			modelInitListener.initialisedFailure(null);
	}

	public void logoutUser() {
		userPersistance.deleteObject();
		webserviceModelPersistance.deleteObject();
		webserviceModel = new WebserviceModel();
		bufferedChallengeMeService = new ChallengeMeService(webserviceModel);
		bufferedChallengeMeService.setLastCoordinates(null);
		bitmapStore.clearImages();
	}

	public boolean isCurrentUser(String userName) {
		return userName.equals(user.getUserName());
	}

	public void stop() {
		uiState.setAppMinimized(true);
		positionServiceAccess.stopBinding();		
	}

	public PositionServiceAccess getPositionServiceAccess() {
		return positionServiceAccess;
	}

	public AChallengeMeService getActionFactory() {
		return bufferedChallengeMeService;
	}

	public WebserviceModel getWebserviceModel() {
		return webserviceModel;
	}

	public UIState getUIState() {
		return uiState;
	}

	public boolean hasValidCredentials() {
		return user != null && user.hasValidToken();
	}

	public User getUser() {
		return user;
	}

	public boolean hasUser() {
		return user != null;
	}

	public void setProcessRunBackground() {
		positionServiceAccess.setProcessRunBackground(!positionServiceAccess.isProcessRunInBackground());
	}

	public boolean isProcessRunBackground() {
		return positionServiceAccess.isProcessRunInBackground();
	}

	public Context getContext() {
		return activity;
	}

}
