/**
 * 
 */

package com.liferay.ide.eclipse.tests;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author kamesh
 */
public abstract class AbstractTestCase extends XMLTestCase {

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @param resourceName
	 * @return
	 */
	protected abstract InputStream loadResourceAsStream( String resourceName ) throws IOException;

	protected void doXmlAssert( String myControlXMLFile, ByteArrayResourceStore byteArrayResourceStore )
		throws SAXException, IOException {

		InputSource myControlXML = new InputSource( new FileReader( myControlXMLFile ) );
		InputSource myTestXML = new InputSource( new ByteArrayInputStream( byteArrayResourceStore.getContents() ) );

		DetailedDiff myDiff = new DetailedDiff( compareXML( myControlXML, myTestXML ) );
		@SuppressWarnings( "rawtypes" )
		List allDifferences = myDiff.getAllDifferences();
		if ( !allDifferences.isEmpty() ) {
			try {
				printDocument( new ByteArrayInputStream( byteArrayResourceStore.getContents() ), System.out );
			}
			catch ( TransformerException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertEquals( myDiff.toString(), 0, allDifferences.size() );
	}

	private void printDocument( InputStream bin, OutputStream out ) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
		transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
		transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

		transformer.transform( new StreamSource( bin ), new StreamResult( new OutputStreamWriter( out, "UTF-8" ) ) );
	}
}
