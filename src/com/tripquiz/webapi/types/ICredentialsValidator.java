package com.tripquiz.webapi.types;

import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webgeneric.ErrorCode;

public interface ICredentialsValidator {

	void initialised(User user, AChallengeMeService challengeMeService);

	void initialisedFailure(ErrorCode[] errorCodes);

	void initialised(WebserviceModel webserviceModel);
}
