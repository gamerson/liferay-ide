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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.cmd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUIUtil;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class PortletColumnChangeConstraintCommand extends Command
{

    protected PortletColumn column;
    protected PortletLayout currentParent;
    protected int diffWeight = 0;
    protected PortletLayout newParent;
    protected LayoutConstraint layoutConstraint;

    public PortletColumnChangeConstraintCommand( PortletColumn column,
                                                 PortletLayout currentParent,
                                                 PortletLayout newParent,
                                                 LayoutConstraint constraint )
    {
        this.column = column;
        this.currentParent = currentParent;
        this.newParent = newParent;
        this.layoutConstraint = constraint;
        setLabel( Msgs.portletColumnChanged );
    }

    public boolean canExecute()
    {
        return column != null && currentParent != null && newParent != null && layoutConstraint != null &&
            layoutConstraint.refColumn != null;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        if( currentParent.equals( newParent ) )
        {
            int existingWeight = column.getWeight();
            column.setWeight( layoutConstraint.weight );
            diffWeight = existingWeight - layoutConstraint.weight;

            PortletColumnElement refColumn = layoutConstraint.refColumn;
            int newWeight = refColumn.getWeight() + diffWeight;

            if( CoreUtil.compareVersions( column.getVersion(), ILiferayConstants.V620 ) < 0 )
            {
                //IDE-800 to avoid changing one column from 66% to 65% but the refColumn stay 33%
                //since diffWeight is only 1% not enough to get 35% according to adjustWeight
                //the conflict is caused by 33% + 66% < 100%
                if( refColumn.getWeight() == 33 )
                {
                    newWeight = newWeight + 1;
                }

                newWeight = LayoutTplUIUtil.adjustWeight( newWeight );
            }

            refColumn.setWeight( newWeight );
        }
    }

    public void undo()
    {
        //do the opposite of redo: give diffWeight back to the column and take it away from refColumn
        //since diffWeight means the reduction of the modified column according to redo
        column.setWeight( column.getWeight() + diffWeight );

        PortletColumnElement refColumn = layoutConstraint.refColumn;
        int newWeight = refColumn.getWeight() - diffWeight;

        if( CoreUtil.compareVersions( column.getVersion(), ILiferayConstants.V620 ) < 0 )
        {
            if( refColumn.getWeight() == 33 )
            {
                newWeight = newWeight + 1;
            }

            newWeight = LayoutTplUIUtil.adjustWeight( newWeight );
        }

        refColumn.setWeight( newWeight );
    }

    private static class Msgs extends NLS
    {
        public static String portletColumnChanged;

        static
        {
            initializeMessages( PortletColumnChangeConstraintCommand.class.getName(), Msgs.class );
        }
    }
}
