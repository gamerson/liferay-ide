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
public interface SocialActivityInterpreterClass extends Element
{
    ElementType TYPE = new ElementType( SocialActivityInterpreterClass.class );

    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @MustExist
    @JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.social.model.SocialActivityInterpreter" )
    @XmlBinding( path = "social-activity-interpreter-class" )
    ValueProperty PROP_SOCIAL_ACTIVITY_INTERPRETER_CLASS = new ValueProperty( TYPE, "SocialActivityInterpreterClass" );
    
    ReferenceValue<JavaTypeName, JavaType> getSocialActivityInterpreterClass();

    void setSocialActivityInterpreterClass( JavaTypeName value );

    void setSocialActivityInterpreterClass( String value );
}
