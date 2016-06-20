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

package com.liferay.ide.ui.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitContext;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class SuperClassNameResolver extends TemplateVariableResolver
{

    public SuperClassNameResolver()
    {
        super(
            "super_class_name", "get superclass name" );
    }

    @Override
    protected String resolve( TemplateContext context )
    {
        String superClassName = "";

        ICompilationUnit unit = ( (CompilationUnitContext) context ).getCompilationUnit();

        String typeName = JavaCore.removeJavaLikeExtension( unit.getElementName() );

        IType type = unit.getType( typeName );

        try
        {
            superClassName = type.getSuperclassName();
        }
        catch( JavaModelException e )
        {
        }

        return superClassName;
    }

    @Override
    protected boolean isUnambiguous( TemplateContext context )
    {
        return false;
    }

}
