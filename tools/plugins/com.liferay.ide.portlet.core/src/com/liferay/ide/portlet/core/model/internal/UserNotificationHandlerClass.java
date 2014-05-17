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
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Kuo Zhang
 */
public interface UserNotificationHandlerClass extends Element
{
    ElementType TYPE = new ElementType( UserNotificationHandlerClass.class );

    // Value

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @JavaTypeConstraint ( kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.notigications.UserNotificationHandler")
    @XmlBinding( path = "" )
    @MustExist
    ValueProperty PROP_USER_NOTIFICATION_HANDLER_CLASS = new ValueProperty( TYPE, "UserNotificationHandlerClass" );

    ReferenceValue<JavaTypeName, JavaType> getUserNotificationHandlerClass();

    void setUserNotificationHandlerClass( String value );

    void setUserNotificationHandlerClass( JavaTypeName value );

}
