package com.hdbsnc.smartiot.common.ism.sm;

public interface ISessionStateListener {

	public void changedState(int caller, int sessionState, ISession session);
}
