/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class UrlPathValidationService extends ValidationService
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.services.ValidationService#validate()
     */
    @Override
    public Status validate()
    {
        final ValueProperty valueProperty = context( ValueProperty.class );
        final Value<?> value = context( IModelElement.class ).read( valueProperty );
        final String urlPath = value.getText();
        if( urlPath != null && !urlPath.startsWith( "/" ) )
        {
            return Status.createErrorStatus( Resources.bind(
                Resources.invalidUrlPath, valueProperty.getLabel( false, CapitalizationType.FIRST_WORD_ONLY, false ) ) );
        }
        return Status.createOkStatus();
    }

    private static final class Resources extends NLS
    {

        public static String invalidUrlPath;

        static
        {
            initializeMessages( UrlPathValidationService.class.getName(), Resources.class );
        }
    }

}
