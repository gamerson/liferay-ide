package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kuo Zhang
 * @author Simon Jiang
 */
@Image( path = "images/eview16/portlet_app_hi.gif" )
@XmlBinding( path = "liferay-portlet-app" )
public interface LiferayPortletApp extends Element
{
    ElementType TYPE = new ElementType( LiferayPortletApp.class );

    // *** Liferay Portlet *** /

    @Type( base = LiferayPortlet.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet", type = LiferayPortlet.class ) )
    ListProperty PROP_PORTLETS = new ListProperty( TYPE, "Portlets" );

    ElementList<LiferayPortlet> getPortlets();

    // *** Role Mapper ***

    @Type( base = SecurityRoleRef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "role-mapper", type = SecurityRoleRef.class ) )
    ListProperty PROP_ROLE_MAPPERS= new ListProperty( TYPE, "RoleMappers" );

    ElementList<SecurityRoleRef> getRoleMappers();

    // *** Customer User Attribute ***

    @Type( base = CustomUserAttribute.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "custom-user-attribute", type = CustomUserAttribute.class ) )
    ListProperty PROP_CUSTOM_USER_ATTRIBUTES = new ListProperty( TYPE, "CustomUserAttributes" );

    ElementList<CustomUserAttribute> getCustomUserAttributes();
}
