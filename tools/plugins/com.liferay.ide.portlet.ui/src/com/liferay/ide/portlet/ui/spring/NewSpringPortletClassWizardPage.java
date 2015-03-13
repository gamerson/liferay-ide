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

package com.liferay.ide.portlet.ui.spring;

import com.liferay.ide.portlet.core.spring.INewSpringPortletClassDataModelProperties;
import com.liferay.ide.portlet.ui.wizard.NewPortletClassWizardPage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class NewSpringPortletClassWizardPage extends NewPortletClassWizardPage
    implements INewSpringPortletClassDataModelProperties
{
    protected Label springPortletClassLabel;

    public NewSpringPortletClassWizardPage(
        IDataModel model, String pageName, String pageDesc, String pageTitle, boolean fragment )
    {
        super( model, pageName, pageDesc, pageTitle, fragment );
    }

    protected void createClassnameGroup( Composite parent )
    {
        // class name
        classLabel = new Label( parent, SWT.LEFT );
        classLabel.setText( Msgs.springPortletClassLabel );
        classLabel.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_FILL ) );

        classText = new Text( parent, SWT.SINGLE | SWT.BORDER );
        classText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        synchHelper.synchText( classText, INewJavaClassDataModelProperties.CLASS_NAME, null );

        new Label( parent, SWT.LEFT );
    }

    @Override
    protected void createSuperclassGroup( Composite parent )
    {
    }

    protected String[] getValidationPropertyNames()
    {
        List<String> validationPropertyNames = new ArrayList<String>();

        if( this.fragment )
        {
            return new String[] { IArtifactEditOperationDataModelProperties.COMPONENT_NAME,
                INewJavaClassDataModelProperties.JAVA_PACKAGE, DEFAULT_VIEW_CONTROLLER_CLASS };
        }
        else
        {
            validationPropertyNames.add( IArtifactEditOperationDataModelProperties.PROJECT_NAME );
            validationPropertyNames.add( INewJavaClassDataModelProperties.SOURCE_FOLDER );
            validationPropertyNames.add( DEFAULT_VIEW_CONTROLLER_CLASS );
        }

        return validationPropertyNames.toArray( new String[0] );
    }

    @Override
    protected void setFocusOnClassText()
    {
        // dont set focus nothing really needs to be done on this page
    }

    private static class Msgs extends NLS
    {
        public static String springPortletClassLabel;

        static
        {
            initializeMessages( NewSpringPortletClassWizardPage.class.getName(), Msgs.class );
        }
    }
}