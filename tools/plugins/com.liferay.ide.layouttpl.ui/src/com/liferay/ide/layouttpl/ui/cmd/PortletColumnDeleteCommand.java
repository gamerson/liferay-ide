/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.cmd;

import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class PortletColumnDeleteCommand extends Command
{
    protected final PortletColumn child;
    protected final PortletLayout parent;
    protected LayoutTplDiagram diagram = null;
    protected boolean wasRemoved;

    public PortletColumnDeleteCommand( PortletLayout parent, PortletColumn child )
    {
        if( parent == null || child == null )
        {
            throw new IllegalArgumentException();
        }

        setLabel( Msgs.portletColumnDeleted );

        this.parent = parent;
        this.child = child;
    }

    public boolean canUndo()
    {
        return wasRemoved;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        wasRemoved = parent.removeColumn( child );
        final int columnsNum = parent.getColumns().size();

        if( columnsNum == 0 )
        {
            diagram = (LayoutTplDiagram) parent.getParent();
            diagram.removeChild( parent );
        }
        else if( columnsNum == 1 )
        {
            ((PortletColumn) parent.getColumns().get( 0 )).setWeight( 100 );
        }
        else
        {
            //if there are 2 or more columns left, pick the right adjacent one only when there are more right remaining ones than the left,
            //otherwise pick the left one.
            final PortletColumn firstColumn = (PortletColumn) parent.getColumns().get( 0 );
            final int childIndex = child.getNumId() - firstColumn.getNumId();
            int adjustedColumnIndex = 0;

            if( childIndex < ( ( columnsNum + 1 ) / 2 ) )
            {
                adjustedColumnIndex = childIndex;
            }
            else
            {
                adjustedColumnIndex = childIndex - 1;
            }

            PortletColumn adjustedColumn = (PortletColumn) parent.getColumns().get( adjustedColumnIndex );
            adjustedColumn.setWeight( adjustedColumn.getWeight() + child.getWeight() );
        }
    }

    public void undo()
    {
        parent.addColumn( child );

        if( diagram != null )
        {
            diagram.addRow( parent );
        }
    }

    private static class Msgs extends NLS
    {
        public static String portletColumnDeleted;

        static
        {
            initializeMessages( PortletColumnDeleteCommand.class.getName(), Msgs.class );
        }
    }
}
