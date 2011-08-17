/**
 * 
 */

package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp;

import java.util.List;

import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.junit.Test;

import com.liferay.ide.eclipse.portlet.core.model.IAliasQName;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;
import com.liferay.ide.eclipse.portlet.core.model.IPublicRenderParameter;

/**
 * @author kamesh
 */
public class PortletPublicRenderParameterTest extends PortletAppBaseTestBase {

	@Test
	public void test() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IPublicRenderParameter publicRenderParam = portletApp.getPublicRenderParameters().addNewElement();
		publicRenderParam.setIdentifier( "param-1001" );
		publicRenderParam.setNamespaceURI( "http://workspace7.org.in" );
		publicRenderParam.setLocalPart( "param1" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-pub-render-param-result.xml", byteArrayResourceStore );

	}


	@Test
	public void testAlias() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IPublicRenderParameter publicRenderParam = portletApp.getPublicRenderParameters().addNewElement();
		publicRenderParam.setIdentifier( "param-1001" );
		publicRenderParam.setNamespaceURI( "http://workspace7.org.in" );
		publicRenderParam.setLocalPart( "param1" );
		IAliasQName alias = publicRenderParam.getAliases().addNewElement();
		alias.setNamespaceURI( "http://workspace7.org.in" );
		alias.setLocalPart( "alias-1" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-pub-render-param-alias-result.xml", byteArrayResourceStore );
	}

	@Test
	public void testAliasRead() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IPublicRenderParameter publicRenderParam = portletApp.getPublicRenderParameters().addNewElement();
		publicRenderParam.setIdentifier( "param-1001" );
		publicRenderParam.setNamespaceURI( "http://workspace7.org.in" );
		publicRenderParam.setLocalPart( "param1" );
		IAliasQName alias = publicRenderParam.getAliases().addNewElement();
		alias.setNamespaceURI( "http://workspace7.org.in" );
		alias.setLocalPart( "alias-1" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-pub-render-param-alias-result.xml", byteArrayResourceStore );
		List<IAliasQName> aliases = publicRenderParam.getAliases();
		for ( IAliasQName iAliasQName : aliases ) {
			assertEquals( "http://workspace7.org.in", iAliasQName.getNamespaceURI().getContent() );
			assertEquals( "alias-1", iAliasQName.getLocalPart().getContent() );
		}
	}
}
