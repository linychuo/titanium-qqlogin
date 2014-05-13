package com.liyongchao;

import java.util.HashMap;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@Kroll.module(name = "QQlogin", id = "com.liyongchao.qqlogin")
public class QQLoginModule extends KrollModule {

	// Standard Debugging variables
	private static final String TAG = "QQLoginModule";
	private String appId;
	private Tencent instance;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public QQLoginModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		Log.d(TAG, "---- QQLogin onAppCreate ----");
	}

	@Kroll.method
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Kroll.method
	public String getAppId() {
		return appId;
	}

	@Kroll.method
	public void logout() {
		injectInstance();
		instance.logout(TiApplication.getAppCurrentActivity());
		Log.d(TAG, "---------------qq logout------------");
	}

	@Kroll.method
	public void login() {
		injectInstance();
		if (instance.getOpenId() != null) {
			Log.d(TAG, "---------------qq reauth------------");
			instance.reAuth(TiApplication.getAppCurrentActivity(), "all",
					new QQLoginIUiListener(this));
		} else {
			Log.d(TAG, "------------- qq login ------------");
			instance.login(TiApplication.getAppCurrentActivity(), "all",
					new QQLoginIUiListener(this));
		}
	}

	private void injectInstance() {
		if (instance == null) {
			if (appId == null) {
				appId = "22222222222";
			}
			instance = Tencent.createInstance(appId,
					TiApplication.getAppCurrentActivity());
			Log.d(TAG, " instance  = " + instance);
		}
	}

	private static class QQLoginIUiListener implements IUiListener {
		private QQLoginModule self;

		public QQLoginIUiListener(QQLoginModule self) {
			this.self = self;
		}

		@Override
		public void onError(UiError arg) {
			HashMap<String, Object> dict = new HashMap<String, Object>();
			dict.put("errorCode", arg.errorCode);
			dict.put("errorDetail", arg.errorDetail);
			dict.put("errorMessage", arg.errorMessage);
			self.fireEvent("qqlogin-onError", dict);
		}

		@Override
		public void onComplete(Object arg) {
			JSONObject response = (JSONObject) arg;
			HashMap<String, Object> dict = new HashMap<String, Object>();
			try {
				dict.put("openid", response.get("openid"));
				dict.put("accesstoken", response.get("access_token"));
			} catch (JSONException e) {
				// ignore
			}

			self.fireEvent("qqlogin-onComplete", dict);
		}

		@Override
		public void onCancel() {
			self.fireEvent("qqlogin-onCancel", null);
		}
	}
}
