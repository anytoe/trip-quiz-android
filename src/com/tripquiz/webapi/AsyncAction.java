package com.tripquiz.webapi;

import org.json.JSONException;

import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;
import com.tripquiz.webgeneric.ErrorCode;
import com.tripquiz.webgeneric.Event;
import com.tripquiz.webgeneric.ReturnText;
import com.tripquiz.webgeneric.UrlBuilder;

public abstract class AsyncAction {

	protected IActionListener actionListener;
	protected UrlBuilder urlBuilder;
	protected ConvertJSON converter;
	private EntityKey entityKey;
	private AsyncActionType actionType;
	private AsyncActionCategory actionCategory;
	protected String token;

	public Event getEvent() {
		return converter.getEventText();
	}

	public boolean isSuccess() {
		return ReturnText.isSuccess(converter.getReturnText());
	}

	public ReturnText getReturnText() {
		return converter.getReturnText();
	}

	public ErrorCode[] getErrorCodes() {
		return converter.getErrorCodes();
	}

	public AsyncAction(AsyncActionType actionType, AsyncActionCategory actionCategory, IActionListener actionListener, ConvertJSON converter,
			EntityKey entityKey) {
		super();
		this.actionType = actionType;
		this.actionCategory = actionCategory;
		this.actionListener = actionListener;
		this.converter = converter;
		this.entityKey = entityKey;
//		this.urlBuilder = new UrlBuilder("http", "192.168.1.2/"); 
		this.urlBuilder = new UrlBuilder("https", "tripquiz.azure-mobile.net/","","TLEvjlxFrVCVJjHUtfcVfFuKLkABTQ51");
	}

	public AsyncAction(AsyncActionType actionType, AsyncActionCategory actionCategory, IActionListener actionListener, ConvertJSON converter) {
		this(actionType, actionCategory, actionListener, converter, null);
	}

	public AsyncActionType getAppActionType() {
		return actionType;
	}

	public AsyncActionCategory getAsyncActionCategory() {
		return actionCategory;
	}

	public EntityKey getEntityKey() {
		return entityKey;
	}

	public boolean hasEntityKey() {
		return entityKey != null;
	}

	public abstract void action();

	public Object getConversionResult() {
		return converter.getConvertedResult();
	}

	public UrlBuilder getUrl() {
		return urlBuilder;
	}

	void setResult(String serverResult) throws JSONException {
		converter.convert(serverResult);
	}

	void setToken(String token) {
		this.token = token;
	}

}
