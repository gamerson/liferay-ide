package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTpl;
import com.liferay.ide.layouttpl.core.model.PortletColumn;
import com.liferay.ide.layouttpl.core.model.PortletLayout;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Kuo Zhang
 *
 */
public class PortletColumnsListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        LayoutTpl layouttpl = event.property().element().nearest( LayoutTpl.class );
        updateColumns( layouttpl, 1 );
    }

    private int updateColumns( CanAddPortletLayouts columnsParent, int numId )
    {
        for( PortletLayout portletLayout : ( columnsParent.getPortletLayouts() ) )
        {
            ElementList<PortletColumn> columns = portletLayout.getPortletColumns();

            int size = columns.size();

            for( int i = 0; i < size; i++ )
            {
                PortletColumn column = columns.get( i );

                if( column.getPortletLayouts().size() == 0 )
                {
                    column.setNumId( numId++ );
                }
                else if( column.getPortletLayouts().size() > 0 )
                {
                    // when new child is added, the parent column will have no numId
                    column.setNumId( 0 );
                    numId = updateColumns( column, numId++ );
                }

                column.setOnly( false );
                column.setFirst( false );
                column.setLast( false );

                if( size == 1 )
                {
                    column.setOnly( true );
                }
                else if( size > 1 )
                {
                    if( i == 0 )
                    {
                        column.setFirst( true );
                    }
                    else if( i == size - 1 )
                    {
                        column.setLast( true );
                    }
                }
            }
        }

        return numId;
    }

}
