/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.AbstractUpgradeProjectHandler;
import com.liferay.ide.project.core.UpgradeProjectHandlerReader;
import com.liferay.ide.project.core.upgrade.NamedItem;
import com.liferay.ide.project.core.upgrade.UpgradeLiferayProjectsOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.graphics.Image;

/**
 * @author Simon Jiang
 */
public class ProjectUpgradeActionCheckboxCustomPart extends AbstractCheckboxCustomPart
{
    private static Map<String,String> handlerMaps = new  HashMap<String,String>();

    static
    {
        UpgradeProjectHandlerReader upgradeLiferayProjectActionReader = new UpgradeProjectHandlerReader();
        handlerMaps = getUpgradeHandlers( upgradeLiferayProjectActionReader.getUpgradeActions() );
    }

    class ProjectActionUpgradeLabelProvider extends ElementLabelProvider implements IColorProvider, IStyledLabelProvider
    {

        @Override
        public StyledString getStyledText( Object element )
        {
            if( element instanceof CheckboxElement )
            {
                return new StyledString( ( ( CheckboxElement ) element ).context );
            }
            return null;

        }

        @Override
        protected void initalizeImageRegistry( ImageRegistry registry )
        {
        }

        @Override
        public Image getImage( Object element )
        {
            return null;
        }

    }

    private static HashMap<String, String> getUpgradeHandlers( List<AbstractUpgradeProjectHandler> upgradeActions )
    {
        HashMap<String, String> actionMaps = new HashMap<String,String>();

        for( AbstractUpgradeProjectHandler upgradeHandler : upgradeActions)
        {
            actionMaps.put( upgradeHandler.getName(), upgradeHandler.getDescription() );
        }

        return actionMaps;
    }

    @Override
    protected ElementList<NamedItem> getCheckboxList()
    {
        return op().getSelectedActions();
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new ProjectActionUpgradeLabelProvider();
    }

    @Override
    protected ElementList<NamedItem> getSelectedElements()
    {
        return op().getSelectedActions();
    }

    @Override
    protected List<CheckboxElement> getInitItemsList()
    {
        final List<CheckboxElement> checkboxElementList = new ArrayList<CheckboxElement>();
        handlerMaps.keySet().iterator();
        String  context = null;

        for (String handlerName : handlerMaps.keySet())
        {
            context = handlerMaps.get( handlerName );
            CheckboxElement checkboxElement = new CheckboxElement( handlerName, context );
            checkboxElementList.add( checkboxElement );
        }

        return checkboxElementList;
    }

    private UpgradeLiferayProjectsOp op()
    {
        return getLocalModelElement().nearest( UpgradeLiferayProjectsOp.class );
    }

    @Override
    protected void updateValidation()
    {
        retval = Status.createOkStatus();

        if( op().getSelectedActions().size() < 1 )
        {

            retval = Status.createErrorStatus( "At least one upgrade action must be specified " );
        }

        refreshValidation();
    }
}
