/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Kay-Uwe Graw - initial API and implementation

 *******************************************************************************/

package com.liferay.ide.ui.tests.swtbot.page.impl;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;

import com.liferay.ide.ui.tests.swtbot.page.IWidgetPageObject;

public abstract class AbstractWidgetPageObject<T extends SWTBot> extends AbstractPageObject<SWTBot>
    implements IWidgetPageObject
{

    protected String label;

    @Override
    public String getLabel()
    {
        return label;
    }

    public AbstractWidgetPageObject( T bot, String label )
    {
        super( bot );
        this.label = label;
    }

    public String getText()
    {
        return getWidget().getText();
    }

    protected abstract AbstractSWTBot<?> getWidget();

}
