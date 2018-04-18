package com.tripquiz.webapi.types;

import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webgeneric.ErrorCode;

public class CrendentialsValidator implements ICredentialsValidator {

	@Override
	public void initialised(User user, AChallengeMeService challengeMeService) {
		// nothing to do
	}

	@Override
	public void initialisedFailure(ErrorCode[] errorCodes) {
		// nothing to do
	}

	@Override
	public void initialised(WebserviceModel webserviceModel) {
		// nothing to do
	}

}
