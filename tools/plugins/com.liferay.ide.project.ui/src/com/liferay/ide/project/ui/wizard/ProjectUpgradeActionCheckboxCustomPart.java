package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.model.UpgradeAction;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.graphics.Color;


/**
 * @author Simon Jiang
 */


public class ProjectUpgradeActionCheckboxCustomPart extends AbstractCheckboxCustomPart
{

    private final static Map<String,String> actionMaps = new  HashMap<String,String>();

    static
    {
        actionMaps.put( "RuntimeUpgrade", "Update targeted runtime to setting" );
        actionMaps.put( "MetadataUpgrade", "Update all deployment descriptor metadata" );
        actionMaps.put( "ServicebuilderUpgrade", "Rebuild Services for service-builder projects" );
        actionMaps.put( "AlloyUIExecute", "Run Liferay Alloy UI Upgrade tool" );
    }

    protected class ProjectActionUpgradeLabelProvider extends AbstractLabelProvider
    implements IColorProvider, IStyledLabelProvider
{
    public Color getBackground( Object element )
    {
        return null;
    }


    public Color getForeground( Object element )
    {
        return null;
    }

    public StyledString getStyledText( Object element )
    {
        if( element instanceof CheckboxElement )
        {
            return new StyledString( ( ( CheckboxElement ) element ).context );
        }
        return null;

    }

    @Override
    public String getText( Object element )
    {
        if( element instanceof CheckboxElement )
        {
            return ( (CheckboxElement) element ).context;
        }

        return super.getText( element );
    }


    @Override
    protected void initalizeImageRegistry( ImageRegistry registry )
    {
    }

}
    @Override
    protected void checkAndUpdateCheckboxElement()
    {
        List<CheckboxElement> checkboxElementList = new ArrayList<CheckboxElement>();
        actionMaps.keySet().iterator();
        String  context = null;
        for (String actionName : actionMaps.keySet())
        {
            context = getProjectAction( actionName );
            CheckboxElement checkboxElement = new CheckboxElement( actionName, context );
            checkboxElementList.add( checkboxElement );
        }

        checkboxElements = checkboxElementList.toArray( new CheckboxElement[checkboxElementList.size()]);

        UIUtil.async
        (
            new Runnable()
            {
                public void run()
                {
                    checkBoxViewer.setInput( checkboxElements );
                    for( Iterator<UpgradeAction> iterator = op().getSelectedActions().iterator(); iterator.hasNext(); )
                    {
                        UpgradeAction upgradeAction = iterator.next();
                        for( CheckboxElement checkboxElement : checkboxElements )
                        {
                            if ( checkboxElement.name.equals( upgradeAction.getAction().content() ))
                            {
                                checkBoxViewer.setChecked( checkboxElement, true );
                                break;
                            }
                        }
                    }

                    updateValidation();
                }
            }
        );
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

    @Override
    protected void handleCheckStateChangedEvent( CheckStateChangedEvent event )
    {
        if( event.getSource().equals( checkBoxViewer ) )
        {
            final Object element = event.getElement();

            if( element instanceof CheckboxElement )
            {
                checkBoxViewer.setGrayed( element, false );
            }

            op().getSelectedActions().clear();

            for( CheckboxElement checkboxElement : checkboxElements )
            {
                if( checkBoxViewer.getChecked( checkboxElement ) )
                {
                    final UpgradeAction newUpgradeAction = op().getSelectedActions().insert();
                    newUpgradeAction.setAction( checkboxElement.name );
                }

            }
            updateValidation();
        }
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new ProjectActionUpgradeLabelProvider();
    }

    @Override
    protected void setSelectAll( CheckboxElement[] checkboxElements )
    {
        for( CheckboxElement checkboxElement : checkboxElements )
        {
            checkBoxViewer.setChecked( checkboxElement, true );
            ElementList<UpgradeAction> upgradeActions = op().getSelectedActions();
            if ( !upgradeActions.contains( checkboxElement ) )
            {
                UpgradeAction upgradeAction = op().getSelectedActions().insert();
                upgradeAction.setAction( checkboxElement.name  );
            }
        }
        updateValidation();

    }

    @Override
    protected void setDeSelectAll( CheckboxElement[] checkboxElements )
    {
        for( CheckboxElement checkboxElement : checkboxElements )
        {
            checkBoxViewer.setChecked( checkboxElement, false );
        }
        op().getSelectedActions().clear();
        updateValidation();

    }

    private static String getProjectAction(String actionName)
    {
        return actionMaps.get( actionName );
    }

}
