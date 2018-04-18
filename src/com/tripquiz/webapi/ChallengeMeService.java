package com.tripquiz.webapi;

import java.util.ArrayList;
import java.util.List;

import android.os.CountDownTimer;

import com.tripquiz.generic.Coordinates;
import com.tripquiz.webapi.action.ActionAskQuestion;
import com.tripquiz.webapi.action.ActionCheckin;
import com.tripquiz.webapi.action.ActionGetChallenge;
import com.tripquiz.webapi.action.ActionGetNewChallenges;
import com.tripquiz.webapi.action.ActionGetUserChallenges;
import com.tripquiz.webapi.action.ActionLoginUser;
import com.tripquiz.webapi.action.ActionRegisterUser;
import com.tripquiz.webapi.action.ActionVisibilityChallenge;
import com.tripquiz.webapi.converter.ChallengeConverter;
import com.tripquiz.webapi.converter.ChallengesConverter;
import com.tripquiz.webapi.converter.PrimitiveConverter;
import com.tripquiz.webapi.converter.UserConverter;
import com.tripquiz.webapi.model.ChallengeSet;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.AppStage;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webapi.types.ICredentialsValidator;
import com.tripquiz.webgeneric.ErrorCode;
import com.tripquiz.webgeneric.Event;
import com.tripquiz.webgeneric.ReturnText;

public class ChallengeMeService extends AChallengeMeService implements IActionListener {

	// add challenge
	private List<EntityKey> addChallengeBuffer;
	private boolean isAddExecuting = false;

	protected User user;
	private ICredentialsValidator credentialsListener;
	private WebserviceModel webserviceModel;
	private AppStage appStage = AppStage.Start;
	private CountDownTimer countDownTimer;

	private Coordinates lastCoordinates;

	public ChallengeMeService(WebserviceModel webserviceModel) {
		super();
		this.webserviceModel = webserviceModel;
		addChallengeBuffer = new ArrayList<EntityKey>();

		if (webserviceModel.isCheckedIn()) {
			setupTimer(webserviceModel.getCheckedInMillisLeft());
		}
	}

	public void setLastCoordinates(Coordinates lastCoordinates) {
		this.lastCoordinates = lastCoordinates;
	}

	public void setWebserviceInitialisedListener(ICredentialsValidator initListener) {
		this.credentialsListener = initListener;
		appStage = AppStage.Start;
	}

	private void execute(AsyncAction action) {
		action.action();
	}

	// @Override
	public void login(User user) {
		if (credentialsListener == null)
			throw new RuntimeException("Credentials listener may never be null");
		this.user = user;
		if (user.hasValidToken()) {
			appStage = AppStage.Authenticated;
			credentialsListener.initialised(user, this);
			if (webserviceModel.isInitialised()) {
				appStage = AppStage.Initialised;
				credentialsListener.initialised(webserviceModel);
			}
		} else {
			ActionLoginUser checkin = new ActionLoginUser(this, new UserConverter(user), user.getIdentifier(), user.getPassword());
			execute(checkin);
		}
	}

	@Override
	public void checkin(EntityKey locationKey) {
		ActionCheckin checkin = new ActionCheckin(user.getToken(), this, new ChallengeConverter(), locationKey);
		execute(checkin);
	}

	@Override
	public void answerQuestion(EntityKey task, String answer) {
		ActionAskQuestion question = new ActionAskQuestion(user.getToken(), this, new ChallengeConverter(), task, answer);
		execute(question);
	}

	@Override
	public void changeVisibilityOfChallenge(EntityKey challengeKey, boolean isVisible) {
		ActionVisibilityChallenge visibAction = new ActionVisibilityChallenge(user.getToken(), this, new PrimitiveConverter<Boolean>(), challengeKey, isVisible);
		execute(visibAction);
	}

	@Override
	public void loadChallenge(EntityKey challengeKey) {
		ActionGetChallenge challAction = new ActionGetChallenge(user.getToken(), this, new ChallengeConverter(), challengeKey);
		execute(challAction);
	}

	@Override
	public void loadNewChallenges(String searchtext) {
		ActionGetNewChallenges challAction = new ActionGetNewChallenges(user.getToken(), AsyncActionType.ChallengesNew, this, new ChallengesConverter(),
				searchtext, lastCoordinates);
		execute(challAction);
	}

	@Override
	public void loadUserChallenges() {
		ActionGetUserChallenges challAction = new ActionGetUserChallenges(user.getToken(), this, new ChallengesConverter());
		execute(challAction);
	}

	@Override
	public void notifyListenerActionStarted(AsyncAction action) {
		webserviceModel.notifyListenerActionStarted(action);
		super.notifyListenersStarted(action);
	}

	@Override
	public void notifyListenerActionStopped(AsyncAction action) {
		webserviceModel.notifyListenerActionStopped(action);

		isAddExecuting = false;
		if (addChallengeBuffer.size() > 0) {
			// execute next in buffer
			EntityKey nextChallengeKey = addChallengeBuffer.remove(0);
			addChallenge(nextChallengeKey, true);
		} else if (action.getAppActionType() == AsyncActionType.CHALLENGE_ADD) {
			// remove it immediately and refresh the data
			ChallengeSet set = webserviceModel.getChallengeSet();
			set.removeChallenge(AsyncActionType.ChallengesNew.toString(), action.getEntityKey());
			set.removeChallenge(AsyncActionType.ChallengesUser.toString(), action.getEntityKey());
			loadUserChallenges();
		}

		if (action.getAsyncActionCategory() == AsyncActionCategory.READ_USER) {
			if (action.isSuccess()) {
				user = (User) action.getConversionResult();
				login(user);
			} else {
				credentialsListener.initialisedFailure(action.getErrorCodes());
			}
		} else if (action.getAppActionType() == AsyncActionType.CHECKIN) {
			setupTimer(WebserviceModel.MAX_LOGGEDIN_TIMESPAN);
		}

		if (appStage == AppStage.Authenticated && action.getAsyncActionCategory() == AsyncActionCategory.READ_CHALLENGES && webserviceModel.isInitialised()) {
			appStage = AppStage.Initialised;
			credentialsListener.initialised(webserviceModel);
		}

		super.notifyListenersStopped(action);

		// call finish if a challenge has been finished
		if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE
				&& (action.getEvent() == Event.PartialComplete || action.getEvent() == Event.Complete)) {
			webserviceModel.checkout();
			super.notifyListenerActionFinished(action.getEvent(), action.getAppActionType(), action.getAsyncActionCategory());
		}
	}

	private void setupTimer(long milliseconds) {
		if (countDownTimer != null)
			countDownTimer.cancel();

		countDownTimer = new CountDownTimer(milliseconds, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				notifyListenerActionUpdate(AsyncActionType.CHECKIN, AsyncActionCategory.MODIFY_CHALLENGE);
			}

			@Override
			public void onFinish() {
				notifyListenerActionFinished(Event.None, AsyncActionType.CHECKIN, AsyncActionCategory.MODIFY_CHALLENGE);
			}
		};
		countDownTimer.start();
	}

	@Override
	public void notifyOnError(AsyncAction action, Exception exception) {
		webserviceModel.notifyListenerActionStopped(action);
		if (action.getReturnText() == ReturnText.TokenInvalid)
			credentialsListener.initialisedFailure(new ErrorCode[] { ErrorCode.TokenNotRenewable });
		else
			super.notifyOnError(action, exception);
	}

	@Override
	public void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory) {
		super.notifyListenerActionUpdate(actionType, actionCategory);
	}

	@Override
	public void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory) {
		super.notifyListenerActionFinished(event, actionType, actionCategory);
	}

	@Override
	public void addChallenge(EntityKey challengeKey, boolean addToUser) {
		if (isAddExecuting) {
			// add to queue
			addChallengeBuffer.add(challengeKey);
		} else {
			isAddExecuting = true;
			ActionVisibilityChallenge challAction = new ActionVisibilityChallenge(user.getToken(), this, new PrimitiveConverter<Boolean>(), challengeKey, true);
			execute(challAction);
		}
	}

	public void register(User user) {
		ActionRegisterUser registerAction = new ActionRegisterUser(this, new UserConverter(user), user);
		execute(registerAction);
	}

}
