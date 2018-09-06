package com.liferay.ide.upgrade;

public interface UpgradeStep {

	public boolean isComplete();

	public void setComplete(boolean complete);

	public void perform();

}
