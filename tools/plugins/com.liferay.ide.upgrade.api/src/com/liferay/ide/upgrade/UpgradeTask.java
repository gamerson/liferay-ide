package com.liferay.ide.upgrade;

import java.util.List;

public interface UpgradeTask {

	public List<UpgradeStep> getUpgradeSteps();

	public int getPriority();
}
