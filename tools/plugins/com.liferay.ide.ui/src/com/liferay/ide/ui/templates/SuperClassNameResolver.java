
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
        super( "super_class_name", "get superclass name" );
    }

    @Override
    protected String resolve( TemplateContext context )
    {
        String superClassName = "";

        if( context instanceof CompilationUnitContext )
        {
            CompilationUnitContext compilationUnitContext = (CompilationUnitContext) context;

            ICompilationUnit unit = compilationUnitContext.getCompilationUnit();

            String typeName = JavaCore.removeJavaLikeExtension( unit.getElementName() );

            IType type = unit.getType( typeName );

            try
            {
                superClassName = type.getSuperclassName();
            }
            catch( JavaModelException e )
            {
            }
        }

        return superClassName;
    }

    @Override
    protected boolean isUnambiguous( TemplateContext context )
    {
        return true;
    }

}
