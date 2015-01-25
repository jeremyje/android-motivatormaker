package com.futonredemption.makemotivator.fragments;

import java.util.ArrayList;

import org.beryl.app.ContractRegistry;

import com.futonredemption.makemotivator.R;
import com.futonredemption.makemotivator.analytics.AnalyticsClient.ICustomVariableCallback;
import com.futonredemption.makemotivator.contracts.IAnalyticsCommand;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class FragmentBase extends Fragment {

	private ContractRegistry contractSource = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		contractSource = ContractRegistry.getContractRegistry(activity);
		contractSource.add(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		contractSource.remove(this);
		contractSource = null;
	}
	
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		final int layoutId = getFragmentLayoutId();
		if(layoutId != -1) {
			final View view = inflater.inflate(layoutId, root, false);
			onCreateView(view);
			return view;
		}
		return null;
	}
	
	protected void goBack() {
		final FragmentManager fragman = this.getFragmentManager();
		fragman.popBackStack();
	}
	
	protected abstract int getFragmentLayoutId();
	protected abstract void onCreateView(View view);
	
	/*
	protected ContractRegistry getContractRegistry() {
		return contractSource;
	}
	*/
	protected <T extends Object> ArrayList<T> getContracts(Class<T> clazz) {
		ContractRegistry contracts = contractSource;
		if(contracts == null) {
			return new ArrayList<T>();
		} else {
			return contracts.getAll(clazz);
		}
	}
	
	protected boolean isPhoneFormFactor() {
		return ! getResources().getBoolean(R.bool.IsTablet);
	}
	
	protected boolean isTabletFormFactor() {
		return getResources().getBoolean(R.bool.IsTablet);
	}
	
	public void trackerRegisterCustomVariable(int variableId, ICustomVariableCallback callback) {
		final ArrayList<IAnalyticsCommand> cmds = getContracts(IAnalyticsCommand.class);
		for(IAnalyticsCommand cmd : cmds) {
			cmd.registerCustomVariable(variableId, callback);
		}
	}
	
	public void trackerEventFail(String category, String action, String label) {
		final ArrayList<IAnalyticsCommand> cmds = getContracts(IAnalyticsCommand.class);
		for(IAnalyticsCommand cmd : cmds) {
			cmd.eventFail(category, action, label);
		}
	}

	public void trackerEventSuccess(String category, String action, String label) {
		final ArrayList<IAnalyticsCommand> cmds = getContracts(IAnalyticsCommand.class);
		for(IAnalyticsCommand cmd : cmds) {
			cmd.eventSuccess(category, action, label);
		}
	}
}
