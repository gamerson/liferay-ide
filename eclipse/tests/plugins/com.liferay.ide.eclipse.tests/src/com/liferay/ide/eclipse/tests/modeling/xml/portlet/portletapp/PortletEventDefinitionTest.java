/**
 * 
 */

package com.liferay.ide.eclipse.tests.modeling.xml.portlet.portletapp;

import java.util.List;

import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.junit.Test;

import com.liferay.ide.eclipse.portlet.core.model.IAliasQName;
import com.liferay.ide.eclipse.portlet.core.model.IEventDefinition;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author kamesh
 */
public class PortletEventDefinitionTest extends PortletAppBaseTestBase {

	@Test
	public void test() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setNamespaceURI( "http://workspace7.org.in" );
		eventDef.setLocalPart( "demo-event" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-event-result.xml", byteArrayResourceStore );

	}

	@Test
	public void testAliasWrite() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setNamespaceURI( "http://workspace7.org.in" );
		eventDef.setLocalPart( "demo-event" );
		IAliasQName alias = eventDef.getAliases().addNewElement();
		alias.setNamespaceURI( "http://workspace7.org.in" );
		alias.setLocalPart( "alias-1" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-event-alias-result.xml", byteArrayResourceStore );

	}

	@Test
	public void testAliasRead() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setNamespaceURI( "http://workspace7.org.in" );
		eventDef.setLocalPart( "demo-event" );
		IAliasQName alias = eventDef.getAliases().addNewElement();
		alias.setNamespaceURI( "http://workspace7.org.in" );
		alias.setLocalPart( "alias-1" );
		portletApp.resource().save();
		doXmlAssert( TEST_DATA_FILES_PATH + "portlet-event-alias-result.xml", byteArrayResourceStore );
		List<IAliasQName> aliases = eventDef.getAliases();
		for ( IAliasQName iAliasQName : aliases ) {
			assertEquals( "http://workspace7.org.in", iAliasQName.getNamespaceURI().getContent() );
			assertEquals( "alias-1", iAliasQName.getLocalPart().getContent() );
		}

	}

	@Test
	public void testNameEnablement() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setNamespaceURI( "http://workspace7.org.in" );
		eventDef.setLocalPart( "demo-event" );
		portletApp.resource().save();
		assertFalse(
			IEventDefinition.PROP_NAME.getName() + " Enablement",
			eventDef.isPropertyEnabled( IEventDefinition.PROP_NAME ) );
	}

	@Test
	public void testQNameEnablement() throws Exception {
		final IPortletApp portletApp = IPortletApp.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
		portletApp.setId( "ID_001" );
		IEventDefinition eventDef = portletApp.getEventDefinitions().addNewElement();
		eventDef.setName( "myName" );
		portletApp.resource().save();
		assertFalse(
			IEventDefinition.PROP_NAMESPACE_URI.getName() + " Enablement",
			eventDef.isPropertyEnabled( IEventDefinition.PROP_NAMESPACE_URI ) );
		assertFalse(
			IEventDefinition.PROP_LOCAL_PART.getName() + " Enablement",
			eventDef.isPropertyEnabled( IEventDefinition.PROP_LOCAL_PART ) );

	}

}
