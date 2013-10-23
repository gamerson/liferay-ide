
package com.liferay.ide.portal.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.portal.core.structures.model.DynamicContent;
import com.liferay.ide.portal.core.structures.model.DynamicElement;
import com.liferay.ide.portal.core.structures.model.DynamicElementMetadata;
import com.liferay.ide.portal.core.structures.model.Entry;
import com.liferay.ide.portal.core.structures.model.Option;
import com.liferay.ide.portal.core.structures.model.Root;
import com.liferay.ide.portal.core.structures.model.Structure;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.junit.After;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Tao Tao
 */
public class StructuresTests extends PortalCoreTests
{

    static final IPath DDM_STRUCTURE_BASIC_DOCUMENT = new Path( "structures/ddm_structure_basic_document.xml" );
    static final IPath DDM_STRUCTURE_DDL = new Path( "structures/ddm_structure_ddl.xml" );
    static final IPath DDM_STRUCTURE = new Path( "structures/ddmstructure.xml" );
    static final IPath DOCUMENT_LIBRARY_STRUCTURES = new Path( "structures/document-library-structures.xml" );
    static final IPath DYNAMIC_DATA_MAPPING_STRUCTURES_WRONG_FORMAT = new Path(
                    "structures/dynamic-data-mapping-structures-wrong-format.xml" );
    static final IPath DYNAMIC_DATA_MAPPING_STRUCTURES = new Path(
        "structures/dynamic-data-mapping-structures.xml" );
    static final IPath TEST_DDM_STRUCTURE_ALL_FIELDS =
        new Path( "structures/test-ddm-structure-all-fields.xml" );
    static final IPath TEST_JOURNAL_CONTENT_BOOLEAN_REPEATABLE_FIELD = new Path(
        "structures/test-journal-content-boolean-repeatable-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_DOC_LIBRARY_FIELD = new Path(
        "structures/test-journal-content-doc-library-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_LINK_TO_PAGE_FIELD = new Path(
        "structures/test-journal-content-link-to-page-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_LIST_FIELD = new Path( "structures/test-journal-content-list-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_MULTI_LIST_FIELD = new Path(
        "structures/test-journal-content-multi-list-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_NESTED_FIELDS = new Path(
        "structures/test-journal-content-nested-fields.xml" );
    static final IPath TEST_JOURNAL_CONTENT_TEXT_AREA_FIELD = new Path(
        "structures/test-journal-content-text-area-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_TEXT_BOX_REPEATABLE_FIELD = new Path(
        "structures/test-journal-content-text-box-repeatable-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_TEXT_FIELD = new Path( "structures/test-journal-content-text-field.xml" );
    static final IPath HOME_CAROUSEL = new Path( "structures/zoe-brochure-theme/journal/articles/Carousel/Home Carousel.xml" );
    static final IPath How_DO_WE_DO_IT = new Path( "structures/zoe-brochure-theme/journal/articles/Featured Content/How Do We Do It.xml" );
    static final IPath OUR_ACHIEVEMENTS = new Path( "structures/zoe-brochure-theme/journal/articles/Featured Content/Our Achievements.xml" );
    static final IPath OUR_STRATEGY = new Path( "structures/zoe-brochure-theme/journal/articles/Featured Content/Our Strategy.xml" );
    static final IPath WHO_WE_ARE = new Path( "structures/zoe-brochure-theme/journal/articles/Heading Featured Content/Who We Are.xml" );
    static final IPath INNOVATION_FOR_OVER_30_YEARS = new Path( "structures/zoe-brochure-theme/journal/articles/Main Content/Innovating For Over 30 Years.xml" );
    static final IPath DOWNLOAD_POD = new Path( "structures/zoe-brochure-theme/journal/articles/Pod/Download Pod.xml" );
    static final IPath CAROUSEL = new Path( "structures/zoe-brochure-theme/journal/structures/Carousel.xml" );
    static final IPath FEATURED_CONTENT = new Path( "structures/zoe-brochure-theme/journal/structures/Featured Content.xml" );
    static final IPath MAIN_CONTENT = new Path( "structures/zoe-brochure-theme/journal/structures/Main Content.xml" );
    static final IPath POD = new Path( "structures/zoe-brochure-theme/journal/structures/Pod.xml" );
    static final IPath HEADER_SOCIAL_ARTICLES = new Path( "structures/zoe-political-theme/journal/articles/Header Social/Header Social.xml" );
    static final IPath TWITTER_BLOCK = new Path( "structures/zoe-political-theme/journal/articles/Twitter/Twitter Block.xml" );
    static final IPath HEADER_SOCIAL_STRUCTURES = new Path( "structures/zoe-political-theme/journal/structures/Header Social.xml" );
    static final IPath TWITTER = new Path( "structures/zoe-political-theme/journal/structures/Twitter.xml" );

    private Element currentElement;

    @After
    public void cleanup() throws Exception
    {
        if( this.currentElement != null )
        {
            if( ! this.currentElement.disposed() )
            {
                this.currentElement.dispose();
            }

            this.currentElement = null;
        }

        super.cleanup();
    }

    @Test
    public void testDDMStructureBasicDocumentRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DDM_STRUCTURE_BASIC_DOCUMENT, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content() );
        assertEquals( "en_US", root.getDefaultLocale().content() );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertEquals( 154, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );

        assertEquals( "ClimateForcast_COMMAND_LINE", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metaData = dynamicElement.getMetadata().content( false );

        assertNotNull( metaData );
        assertEquals( "en_US", metaData.getLocale().content( false ) );

        final ElementList<Entry> entries = metaData.getEntries();

        assertNotNull( entries );
        assertEquals( 4, entries.size() );

        final Entry entry1 = entries.get( 0 );

        assertNotNull( entry1 );
        assertEquals( "label", entry1.getName().content( false ) );
        assertEquals( "metadata.ClimateForcast.COMMAND_LINE", entry1.getValue().content( false ) );

        final Entry entry2 = entries.get( 2 );

        assertNotNull( entry2 );
        assertEquals( "required", entry2.getName().content( false ) );
        assertEquals( "false", entry2.getValue().content( false ) );
    }

    private void setElement( Element element )
    {
        assertNotNull( element );

        this.currentElement = element;
    }

    @Test
    public void testDocumentLibraryStructuresRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DOCUMENT_LIBRARY_STRUCTURES, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<Structure> structures = root.getStructures();

        assertNotNull( structures );
        assertEquals( 8, structures.size() );

        final Structure structure = structures.get( 2 );

        assertNotNull( structure );
        assertEquals( "Learning Module Metadata", structure.getName().content( false ) );
        assertEquals( "Learning Module Metadata", structure.getDescription().content( false ) );

        final Root structureRoot = structure.getRoot().content( false );

        assertNotNull( structureRoot );
        assertEquals( "[$LOCALE_DEFAULT$]", structureRoot.getAvailableLocales().content( false ) );
        assertEquals( "[$LOCALE_DEFAULT$]", structureRoot.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = structureRoot.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );
        assertEquals( "keyword", dynamicElement.getIndexType().content( false ) );
        assertEquals( true, dynamicElement.getMultiple().content( false ) );
        assertEquals( "select3212", dynamicElement.getName().content( false ) );
        assertEquals( false, dynamicElement.isReadOnly().content( false ) );
        assertEquals( false, dynamicElement.isRequired().content( false ) );
        assertEquals( true, dynamicElement.isShowLabel().content( false ) );
        assertEquals( "select", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );
        assertEquals( "[$LOCALE_DEFAULT$]", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 3, entries.size() );

        final Entry entry = entries.get( 1 );

        assertNotNull( entry );
        assertEquals( "predefinedValue", entry.getName().content( false ) );
        assertEquals( null, entry.getValue().content( false ) );

        final ElementList<DynamicElement> childDynamicElements = dynamicElement.getDynamicElements();

        assertNotNull( childDynamicElements );
        assertEquals( 3, childDynamicElements.size() );

        final DynamicElement childDynamicElement = childDynamicElements.get( 1 );

        assertNotNull( childDynamicElement );
        assertEquals( "2_0", childDynamicElement.getName().content( false ) );
        assertEquals( "option", childDynamicElement.getType().content( false ) );
        assertEquals( "2", childDynamicElement.getValue().content( false ) );

        final DynamicElementMetadata childMetadata = childDynamicElement.getMetadata().content( false );

        assertNotNull( childMetadata );
        assertEquals( "[$LOCALE_DEFAULT$]", childMetadata.getLocale().content( false ) );

        final Entry childEntry = metadata.getEntry().content( false );

        assertNotNull( childEntry );
        assertEquals( "label", childEntry.getName().content( false ) );
        assertEquals( "2.0", childEntry.getValue().content( false ) );
    }

    @Test
    public void testDDMStructureDDLRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DDM_STRUCTURE_DDL, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );
        assertEquals( "text2102", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );
        assertEquals( "en_US", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 7, entries.size() );

        final Entry entry1 = entries.get( 1 );

        assertNotNull( entry1 );
        assertEquals( "showLabel", entry1.getName().content( false ) );
        assertEquals( true, entry1.getValue().content( false ) );

        final Entry entry2 = entries.get( 3 );

        assertNotNull( entry2 );
        assertEquals( "predefinedValue", entry2.getName().content( false ) );
        assertEquals( null, entry2.getValue().content( false ) );
    }

    @Test
    public void testDDMStructureRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DDM_STRUCTURE, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US,hu_HU", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "true", dynamicElement.getAutoGeneratedName().content( false ) );
        assertEquals( "date", dynamicElement.getDataType().content( false ) );
        assertEquals( "ddm", dynamicElement.getFieldNamespace().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( "date_an", dynamicElement.getName().content( false ) );
        assertEquals( "ddm_date", dynamicElement.getType().content( false ) );

        final ElementList<DynamicElementMetadata> metadatas = dynamicElement.getMetadatas();

        assertNotNull( metadatas );
        assertEquals( 2, metadatas.size() );

        final DynamicElementMetadata metadata = metadatas.get( 1 );

        assertNotNull( metadata );
        assertEquals( "hu_HU", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 7, entries.size() );

        final Entry entry1 = entries.get( 0 );

        assertNotNull( entry1 );
        assertEquals( "label", entry1.getName().content( false ) );
        assertEquals( "Date_HU", entry1.getValue().content( false ) );

        final Entry entry2 = entries.get( 6 );

        assertNotNull( entry2 );
        assertEquals( "fieldCssClass", entry2.getName().content( false ) );
        assertEquals( "w25", entry2.getValue().content( false ) );
    }

    @Test
    public void testDynamicDataMappingStructuresWrongFormatRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DYNAMIC_DATA_MAPPING_STRUCTURES_WRONG_FORMAT, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "Contacts", root.getName().content( false ) );
        assertEquals( "Contacts", root.getDescription().content( false ) );

        final Root childRoot = root.getRoot().content( false );

        assertNotNull( childRoot );
        assertEquals( "en_US", childRoot.getAvailableLocales().content( false ) );
        assertEquals( "en_US", childRoot.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = childRoot.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );
        assertEquals( true, dynamicElement.getMultiple().content( false ) );
        assertEquals( "select3212", dynamicElement.getName().content( false ) );
        assertEquals( false, dynamicElement.getReadOnly().content( false ) );
        assertEquals( true, dynamicElement.getShowLabel().content( false ) );
        assertEquals( "select", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );
        assertEquals( "en_US", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 3, entries.size() );

        final Entry entry1 = entries.get( 0 );

        assertNotNull( entry1 );
        assertEquals( "label", entry1.getName().content( false ) );
        assertEquals( "Version", entry1.getValue().content( false ) );

        final Entry entry2 = entries.get( 1 );

        assertNotNull( entry2 );
        assertEquals( "predefinedValue", entry2.getName().content( false ) );
        assertEquals( null, entry2.getValue().content( false ) );

        final ElementList<DynamicElement> childDynamicElements = dynamicElement.getDynamicElements();

        assertNotNull( childDynamicElements );
        assertEquals( 3, childDynamicElements.size() );

        final DynamicElement childDynamicElement = childDynamicElements.get( 1 );

        assertNotNull( childDynamicElement );
        assertEquals( "2_0", childDynamicElement.getName().content( false ) );
        assertEquals( "option", childDynamicElement.getType().content( false ) );
        assertEquals( "2", childDynamicElement.getValue().content( false ) );

        final DynamicElementMetadata childMetadata = childDynamicElement.getMetadata().content( false );

        assertNotNull( childMetadata );
        assertEquals( "en_US", childMetadata.getLocale().content( false ) );

        final Entry childEntry = childMetadata.getEntry().content( false );

        assertNotNull( childEntry );
        assertEquals( "label", childEntry.getName().content( false ) );
        assertEquals( "2.0", childEntry.getValue().content( false ) );
    }

    @Test
    public void testDynamicDataMappingStructuresRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DYNAMIC_DATA_MAPPING_STRUCTURES, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<Structure> structures = root.getStructures();

        assertNotNull( structures );
        assertEquals( 6, structures.size() );

        final Structure structure = structures.get( 4 );

        assertNotNull( structure );
        assertEquals( "Meeting Minutes", structure.getName().content( false ) );
        assertEquals( "Meeting Minutes", structure.getDescription().content( false ) );

        final Root structureRoot = structure.getRoot().content( false );

        assertNotNull( structureRoot );
        assertEquals( "en_US", structureRoot.getAvailableLocales().content( false ) );
        assertEquals( "en_US", structureRoot.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = structureRoot.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 7, dynamicElements.size() );

        final DynamicElement dynamicElement1 = dynamicElements.get( 0 );

        assertNotNull( dynamicElement1 );
        assertEquals( "file-upload", dynamicElement1.getDataType().content( false ) );
        assertEquals( "ddm", dynamicElement1.getFieldNamespace().content( false ) );
        assertEquals( "attachment", dynamicElement1.getName().content( false ) );
        assertEquals( false, dynamicElement1.getRequired().content( false ) );
        assertEquals( true, dynamicElement1.getShowLabel().content( false ) );
        assertEquals( "ddm-fileupload", dynamicElement1.getType().content( false ) );

        final DynamicElementMetadata metadata1 = dynamicElement1.getMetadata().content( false );

        assertNotNull( metadata1 );
        assertEquals( "en_US", metadata1.getLocale().content( false ) );

        final ElementList<Entry> firstEntries = metadata1.getEntries();

        assertNotNull( firstEntries );
        assertEquals( 5, firstEntries.size() );

        final Entry firstEntry1 = firstEntries.get( 0 );

        assertNotNull( firstEntry1 );
        assertEquals( "acceptFiles", firstEntry1.getName().content( false ) );
        assertEquals( "*", firstEntry1.getValue().content( false ) );

        final Entry entry2 = firstEntries.get( 1 );

        assertNotNull( entry2 );
        assertEquals( "folder", entry2.getName().content( false ) );
        assertEquals( "{\"folderId\":0,\"folderName\":\"Documents Home\"}", entry2.getValue().content( false ) );

        final DynamicElement dynamicElement2 = dynamicElements.get( 2 );

        assertNotNull( dynamicElement2 );
        assertEquals( "string", dynamicElement2.getDataType().content( false ) );
        assertEquals( "description", dynamicElement2.getName().content( false ) );
        assertEquals( false, dynamicElement2.getRequired().content( false ) );
        assertEquals( true, dynamicElement2.getShowLabel().content( false ) );
        assertEquals( "textarea", dynamicElement2.getType().content( false ) );
        assertEquals( "100", dynamicElement2.getWidth().content( false ) );

        final DynamicElementMetadata metadata2 = dynamicElement2.getMetadata().content( false );

        assertNotNull( metadata2 );
        assertEquals( "en_US", metadata2.getLocale().content( false ) );

        final ElementList<Entry> secondEntries = metadata2.getEntries();

        assertNotNull( secondEntries );
        assertEquals( 2, secondEntries.size() );

        final Entry secondEntry1 = secondEntries.get( 0 );

        assertNotNull( secondEntry1 );
        assertEquals( "label", secondEntry1.getName().content( false ) );
        assertEquals( "Description", secondEntry1.getValue().content( false ) );

        final Entry secondEntry2 = secondEntries.get( 1 );

        assertNotNull( secondEntry2 );
        assertEquals( "predefinedValue", secondEntry2.getName().content( false ) );
        assertEquals( null, secondEntry2.getValue().content( false ) );
    }

    @Test
    public void testDDMStructureAllFieldsRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_DDM_STRUCTURE_ALL_FIELDS, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 12, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 6 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );
        assertEquals( "contact", dynamicElement.getName().content( false ) );
        assertEquals( true, dynamicElement.getRepeatable().content( false ) );
        assertEquals( false, dynamicElement.getRequired().content( false ) );
        assertEquals( true, dynamicElement.getShowLabel().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( "25", dynamicElement.getWidth().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );
        assertEquals( "en_US", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 3, entries.size() );

        final Entry entry = entries.get( 0 );

        assertNotNull( entry );
        assertEquals( "label", entry.getName().content( false ) );
        assertEquals( "Contact", entry.getValue().content( false ) );

        final DynamicElement childDynamicElement = dynamicElement.getDynamicElement().content( false );

        assertNotNull( childDynamicElement );
        assertEquals( "string", childDynamicElement.getDataType().content( false ) );
        assertEquals( "text", childDynamicElement.getIndexType().content( false ) );
        assertEquals( "contact", childDynamicElement.getName().content( false ) );
        assertEquals( true, childDynamicElement.getRepeatable().content( false ) );
        assertEquals( false, childDynamicElement.getRequired().content( false ) );
        assertEquals( true, childDynamicElement.getShowLabel().content( false ) );
        assertEquals( "text", childDynamicElement.getType().content( false ) );
        assertEquals( "25", childDynamicElement.getWidth().content( false ) );

        final DynamicElementMetadata childMetaData = childDynamicElement.getMetadata().content( false );

        assertNotNull( childMetaData );
        assertEquals( "en_US", childMetaData.getLocale().content( false ) );

        final ElementList<Entry> childEntries = childMetaData.getEntries();

        assertNotNull( childEntries );
        assertEquals( 3, childEntries.size() );

        final Entry childEntry = childEntries.get( 1 );

        assertNotNull( childEntry );
        assertEquals( "predefinedValue", childEntry.getName().content( false ) );
        assertEquals( null, childEntry.getValue().content( false ) );

        final ElementList<DynamicElement> childChildDynamicElements = childDynamicElement.getDynamicElements();

        assertNotNull( childChildDynamicElements );
        assertEquals( 1, childChildDynamicElements.size() );

        final DynamicElement childChildDynamicElement = childChildDynamicElements.get( 0 );

        assertNotNull( childChildDynamicElement );
        assertEquals( "string", childChildDynamicElement.getDataType().content( false ) );
        assertEquals( "text", childChildDynamicElement.getIndexType().content( false ) );
        assertEquals( "contact", childChildDynamicElement.getName().content( false ) );
        assertEquals( true, childChildDynamicElement.getRepeatable().content( false ) );
        assertEquals( false, childChildDynamicElement.getRequired().content( false ) );
        assertEquals( true, childChildDynamicElement.getShowLabel().content( false ) );
        assertEquals( "text", childChildDynamicElement.getType().content( false ) );
        assertEquals( "25", childChildDynamicElement.getWidth().content( false ) );

        final DynamicElementMetadata childChildMetadata = childChildDynamicElement.getMetadata().content( false );

        assertNotNull( childChildMetadata );
        assertEquals( "en_US", childChildMetadata.getLocale().content( false ) );

        final ElementList<Entry> childChildEntries = childChildMetadata.getEntries();

        assertNotNull( childChildEntries );
        assertEquals( 3, childChildEntries.size() );

        final Entry childChildEntry = childChildEntries.get( 2 );

        assertNotNull( childChildEntry );
        assertEquals( "tip", childChildEntry.getName().content( false ) );
        assertEquals( null, childChildEntry.getValue().content( false ) );
    }

    @Test
    public void testJournalContentBooleanRepeatableFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_BOOLEAN_REPEATABLE_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 2, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "1SYNQuhg", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "boolean", dynamicElement.getName().content( false ) );
        assertEquals( "boolean", dynamicElement.getType().content( false ) );
        assertEquals( "keyword", dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );
        assertEquals( null, dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentDocLibraryFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_DOC_LIBRARY_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "4aGOvP3N", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "document_library", dynamicElement.getType().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );
        assertEquals( null, dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentLinkToPageFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_LINK_TO_PAGE_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "MiO7vIJu", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "link_to_layout", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );
        assertEquals( "1@public", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentListFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_LIST_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "pcm9WPVX", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "list", dynamicElement.getName().content( false ) );
        assertEquals( "list", dynamicElement.getType().content( false ) );
        assertEquals( "keyword", dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );
        assertEquals( "a", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentMultiListFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_MULTI_LIST_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "9X5wVsSv", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "multi-list", dynamicElement.getName().content( false ) );
        assertEquals( "keyword", dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );

        final ElementList<Option> options = dynamicContent.getOptions();

        assertNotNull( options );
        assertEquals( 2, options.size() );

        final Option option = options.get( 1 );

        assertNotNull( option );
        assertEquals( "b", option.getValue().content( false ) );
    }

    @Test
    public void testJournalContentNestedFieldsRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_NESTED_FIELDS, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US,pt_BR", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 2, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "RF3do1m5", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "contact", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );

        final ElementList<DynamicElement> childDynamicElements = dynamicElement.getDynamicElements();

        assertNotNull( childDynamicElements );
        assertEquals( 3, childDynamicElements.size() );

        final DynamicElement childDynamicElement = childDynamicElements.get( 0 );

        assertNotNull( childDynamicElement );
        assertEquals( "QK6B0wK9", childDynamicElement.getInstanceID().content( false ) );
        assertEquals( "phone", childDynamicElement.getName().content( false ) );
        assertEquals( "text", childDynamicElement.getType().content( false ) );
        assertEquals( "text", childDynamicElement.getIndexType().content( false ) );

        final DynamicElement childChildDynamicElement = dynamicElements.get( 1 );

        assertNotNull( childChildDynamicElement );
        assertEquals( "8uxzZl41", childChildDynamicElement.getInstanceID().content( false ) );
        assertEquals( "ext", childChildDynamicElement.getName().content( false ) );
        assertEquals( "text", childChildDynamicElement.getType().content( false ) );
        assertEquals( "text", childChildDynamicElement.getIndexType().content( false ) );

        final ElementList<DynamicContent> dynamicContents = dynamicElement.getDynamicContents();

        assertNotNull( dynamicContents );
        assertEquals( 2, dynamicContents.size() );

        final DynamicContent dynamicContent = dynamicContents.get( 0 );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );
        assertEquals( "2", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentTextAreaFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_TEXT_AREA_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "RFnJ1nCn", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "text_area", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "en_US", dynamicContent.getLanguageID().content( false ) );
        assertEquals( "<p>Hello World!</p>", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentTextBoxRepeatableFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_TEXT_BOX_REPEATABLE_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US,pt_BR", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertEquals( 3, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "HvemvQgl", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "text_box", dynamicElement.getName().content( false ) );
        assertEquals( "text_box", dynamicElement.getType().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );

        final ElementList<DynamicContent> dynamicContents = dynamicElement.getDynamicContents();

        assertNotNull( dynamicContents );
        assertEquals( 2, dynamicContents.size() );

        final DynamicContent dynamicContent = dynamicContents.get( 1 );

        assertNotNull( dynamicContent );
        assertEquals( "pt_BR", dynamicContent.getLanguageID().content( false ) );
        assertEquals( "dois", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testJournalContentTextFieldRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TEST_JOURNAL_CONTENT_TEXT_FIELD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US,pt_BR", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 1, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "bf4sdx6Q", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "text", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( "text", dynamicElement.getIndexType().content( false ) );

        final ElementList<DynamicContent> dynamicContents = dynamicElement.getDynamicContents();

        assertNotNull( dynamicContents );
        assertEquals( 2, dynamicContents.size() );

        final DynamicContent dynamicContent = dynamicContents.get( 1 );

        assertNotNull( dynamicContent );
        assertEquals( "pt_BR", dynamicContent.getLanguageID().content( false ) );
        assertEquals( "um", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testHomeCarouselRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), HOME_CAROUSEL, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 7, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 5 );

        assertNotNull( dynamicElement );
        assertEquals( "KvW92QWJ", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "document_library", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "[$FILE=image2.png$]", dynamicContent.getValue().content( false ) );

        final DynamicElement childDynamicElement = dynamicElement.getDynamicElement().content( false );

        assertNotNull( childDynamicElement );
        assertEquals( "mUJqXMFs", childDynamicElement.getInstanceID().content( false ) );
        assertEquals( "link", childDynamicElement.getName().content( false ) );
        assertEquals( "text", childDynamicElement.getType().content( false ) );
        assertEquals( null, childDynamicElement.getIndexType().content( false ) );

        final DynamicContent childDynamicContent = childDynamicElement.getDynamicContent().content( false );

        assertNotNull( childDynamicContent );
        assertEquals( null, childDynamicContent.getValue().content( false ) );

        final DynamicElement childChildDynamicElement = childDynamicElement.getDynamicElement().content( false );

        assertNotNull( childChildDynamicElement );
        assertEquals( "sWvozbm7", childChildDynamicElement.getInstanceID().content( false ) );
        assertEquals( "url-location", childChildDynamicElement.getName().content( false ) );
        assertEquals( "text", childChildDynamicElement.getType().content( false ) );
        assertEquals( null, childChildDynamicElement.getIndexType().content( false ) );

        final DynamicContent childChildDynamicContent = childChildDynamicElement.getDynamicContent().content( false );

        assertNotNull( childChildDynamicContent );
        assertEquals( null, childChildDynamicContent.getValue().content( false ) );
    }

    @Test
    public void testHowDoWeDoItRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), How_DO_WE_DO_IT, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 5, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 4 );

        assertNotNull( dynamicElement );
        assertEquals( "Q9mUXJt6", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "content", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals(
            "<p>"
                + "\n"
                + "Nulla turpis erat, sagittis eget mattis sit amet, sagittis at leo. Maecenas condimentum, mi nec iaculis faucibus, ligula libero malesuada massa, quis consequat leo sem et lorem. Sed dignissim, augue bibendum convallis faucibus, sapien ante sollicitudin felis, ac tristique est sapien a enim.<br />"
                + "\n"
                + "<br />"
                + "\n"
                + "Nunc ut felis nibh. Pellentesque cursus vulputate leo in tincidunt. Nullam lacinia nibh et elit tristique volutpat. Fusce vel arcu vitae velit fermentum blandit.<br />"
                + "\n"
                + "<br />"
                + "\n"
                + "Sed lobortis venenatis mauris, eu scelerisque lectus rutrum ac. Proin dui magna, dapibus ac ultricies vel, condimentum sed magna. Aenean eleifend molestie nunc a sagittis. Duis leo tellus, consequat nec suscipit tempus, sagittis molestie erat. &nbsp;&nbsp;</p>"
                + "\n", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testOurAchievementsRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), OUR_ACHIEVEMENTS, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 5, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 4 );

        assertNotNull( dynamicElement );
        assertEquals( "12HH4wl6", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "content", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals(
            "<p>"
                + "\n"
                + "Aliquam consequat, purus vehicula tristique scelerisque, lorem neque rutrum erat, quis aliquet diam magna at ligula. Etiam eget mi tortor, vel pretium lectus. Morbi sit amet dui velit. Cras odio nisl, blandit quis rhoncus eget, convallis nec tortor. In quis tellus lorem. Proin posuere ligula et nisi rhoncus eu mollis ligula varius. Donec pharetra molestie felis non tincidunt. Donec faucibus consectetur cursus. Donec viverra dignissim tempus.<br />"
                + "\n"
                + "<br />"
                + "\n"
                + "Suspendisse dictum tellus sit amet sapien ullamcorper id facilisis nisi tempor. Integer pulvinar lacus vitae sem scelerisque mollis. Praesent at est eu magna mollis scelerisque sit amet sed lacus.</p>"
                + "\n", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testOurStrategyRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), OUR_STRATEGY, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 5, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 4 );

        assertNotNull( dynamicElement );
        assertEquals( "99r6u5Hk", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "content", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals(
            "<p>"
                + "\n"
                + "Etiam porta condimentum vulputate. Etiam in eros viverra mauris vehicula commodo vitae vehicula justo. Vestibulum massa diam, iaculis ut posuere dapibus, blandit volutpat nibh.<br />"
                + "\n"
                + "<br />"
                + "\n"
                + "Neth vehicula, sapien tincidunt aliquet condimentum, nisi ante sodales ligula, ac bibendum risus tortor at lacus. Aliquam sit amet purus felis. Suspendisse velit justo, suscipit eget convallis eget, viverra at ligula. Aliquam ut mollis orci. Mauris condimentum viverra dui a dictum. Donec adipiscing arcu non sapien hendrerit pellentesque.<br />"
                + "\n"
                + "<br />"
                + "\n"
                + "Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Suspendisse laoreet pharetra lorem sit amet volutpat. Quisque felis nulla, rhoncus at blandit vitae, tempus a ante.</p>"
                + "\n", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testWhoWeAreRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), WHO_WE_ARE, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 6, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 5 );

        assertNotNull( dynamicElement );
        assertEquals( "EVZd09wH", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "content", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals(
            "<p>"
                + "\n"
                + "Aliquam consequat, purus vehicula tristique scelerisque, lorem neque rutrum erat, quis aliquet diam magna at ligula. Etiam eget mi tortor, vel pretium lectus. Morbi sit amet dui velit. Cras odio nisl, blandit quis rhoncus eget, convallis nec tortor. In quis tellus lorem. Proin posuere ligula et nisi rhoncus eu mollis ligula varius. Donec pharetra molestie felis non tincidunt. Donec faucibus consectetur cursus. Donec viverra dignissim tempus.<br />"
                + "\n"
                + "<br />"
                + "\n"
                + "Suspendisse dictum tellus sit amet sapien ullamcorper id facilisis nisi tempor. Integer pulvinar lacus vitae sem scelerisque mollis. Praesent at est eu magna mollis. Nulla turpis erat, sagittis eget mattis sit amet, sagittis at leo. Maecenas condimentum, mi nec iaculis faucibus, ligula libero malesuada massa. Proin posuere ligula et nisi rhoncus eu mollis ligula varius. Donec pharetra molestie felis non tincidunt. Donec faucibus consectetur cursus. Donec viverra dignissim tempus. Maecenas condimentum, mi nec iaculis faucibus, ligula libero malesuada massa. Proin posuere ligula et nisi rhoncus eu mollis ligula varius.</p>"
                + "\n", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testInnovatingForOver30YearsRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), INNOVATION_FOR_OVER_30_YEARS, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "c7kCbmEK", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "col-one", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals(
            "<p>Proin quis dui sem, quis suscipit diam. Vestibulum laoreet mollis justo. Etiam quis dapibus justo. Praesent posuere tellus ac dui consequat accumsan. Pellentesque rutrum ultrices nisi non luctus. In leo velit, ornare a consectetur ut, semper gravida elit. Nulla facilisis ante libero.<br /><br />Suspendisse tellus risus, auctor vitae suscipit et, fermentum a est. Donec magna nulla, malesuada ut semper viverra, bibendum in mi. Nullam semper, nisi in elementum tempus, massa urna suscipit turpis, ac bibendum lectus nisl ac enim. Duis rhoncus dictum ipsum, eu dignissim neque vestibulum a. Nam vel tortor urna. Aliquam erat volutpat. Donec diam massa, aliquet quis lacinia nec, aliquam ut mi. Etiam non condimentum mi. Donec faucibus purus vel leo fermentum fringilla. Donec interdum, libero sed consectetur luctus, enim nunc dictum lorem, sed ullamcorper nibh nisi eu magna.<br /><br />Praesent posuere tellus ac dui consequat accumsan. Pellentesque rutrum ultrices nisi non luctus. In leo velit, ornare a consectetur ut, semper gravida elit. Nulla facilisis ante libero. Suspendisse tellus risus, auctor vitae suscipit et, fermentum a est. Donec magna nulla, malesuada ut semper viverra, bibendum in mi. Nullam semper, nisi in elementum tempus, massa urna suscipit turpis, ac bibendum lectus nisl ac enim. Duis rhoncus dictum ipsum, eu dignissim neque vestibulum a. Nam vel tortor urna. Aliquam erat volutpat. Donec diam massa, aliquet quis lacinia nec, aliquam ut mi. Etiam non condimentum mi. Donec faucibus purus vel leo fermentum fringilla. Donec interdum, libero sed consectetur luctus, enim nunc dictum lorem, sed ullamcorper nibh nisi eu magna.</p>"
                + "\n", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testDownloadPodRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DOWNLOAD_POD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 5, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 3 );

        assertNotNull( dynamicElement );
        assertEquals( "htn4tqLU", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "description", dynamicElement.getName().content( false ) );
        assertEquals( "text_box", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals(
            "Released Date: December 2010<br/> Issue: #46<br/> Type: PDF<br/>",
            dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testCarouselRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), CAROUSEL, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 5, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 2 );

        assertNotNull( dynamicElement );
        assertEquals( "duration", dynamicElement.getName().content( false ) );
        assertEquals( "list", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( false, dynamicElement.getRepeatable().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 5, entries.size() );

        final Entry entry = entries.get( 0 );

        assertNotNull( entry );
        assertEquals( "displayAsTooltip", entry.getName().content( false ) );
        assertEquals( "true", entry.getValue().content( false ) );

        final ElementList<DynamicElement> childDynamicElements = dynamicElement.getDynamicElements();

        assertNotNull( childDynamicElements );
        assertEquals( 10, childDynamicElements.size() );

        final DynamicElement childDynamicElement = childDynamicElements.get( 0 );

        assertNotNull( childDynamicElement );
        assertEquals( "1", childDynamicElement.getName().content( false ) );
        assertEquals( "1", childDynamicElement.getType().content( false ) );
        assertEquals( null, childDynamicElement.getIndexType().content( false ) );
        assertEquals( false, childDynamicElement.getRepeatable().content( false ) );
    }

    @Test
    public void testFeaturedContentRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), FEATURED_CONTENT, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 5, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "link", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( false, dynamicElement.getRepeatable().content( false ) );

        final DynamicElementMetadata dynamicElementMetadata = dynamicElement.getMetadata().content( false );

        assertNotNull( dynamicElementMetadata );

        final ElementList<Entry> entries = dynamicElementMetadata.getEntries();

        assertNotNull( entries );
        assertEquals( 5, entries.size() );

        final Entry entry = entries.get( 0 );

        assertNotNull( entry );
        assertEquals( "displayAsTooltip", entry.getName().content( false ) );
        assertEquals( "true", entry.getValue().content( false ) );
    }

    @Test
    public void testMainContentRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), MAIN_CONTENT, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 2 );

        assertNotNull( dynamicElement );
        assertEquals( "col-two", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( false, dynamicElement.getRepeatable().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 5, entries.size() );

        final Entry entry = entries.get( 3 );

        assertNotNull( entry );
        assertEquals( "label", entry.getName().content( false ) );
        assertEquals( "Column 2", entry.getValue().content( false ) );
    }

    @Test
    public void testPodRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), POD, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 3 );

        assertNotNull( dynamicElement );
        assertEquals( "col-three", dynamicElement.getName().content( false ) );
        assertEquals( "text_area", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( false, dynamicElement.getRepeatable().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 5, entries.size() );

        final Entry entry = entries.get( 3 );

        assertNotNull( entry );
        assertEquals( "label", entry.getName().content( false ) );
        assertEquals( "Column 3", entry.getValue().content( false ) );
    }

    @Test
    public void testHeaderSocialArticlesRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), HEADER_SOCIAL_ARTICLES, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 3, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 0 );

        assertNotNull( dynamicElement );
        assertEquals( "24gLsQq7", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "social-links", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "Facebook", dynamicContent.getValue().content( false ) );

        final DynamicElement childDynamicElement = dynamicElement.getDynamicElement().content( false );

        assertNotNull( childDynamicElement );
        assertEquals( "PiUn7wUE", childDynamicElement.getInstanceID().content( false ) );
        assertEquals( "url-location", dynamicElement.getName().content( false ) );
        assertEquals( "text", childDynamicElement.getType().content( false ) );
        assertEquals( null, childDynamicElement.getIndexType().content( false ) );

        final DynamicContent childDynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( childDynamicContent );
        assertEquals( "//www.facebook.com/liferay", childDynamicContent.getValue().content( false ) );
    }

    @Test
    public void testTwitterBlockRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TWITTER_BLOCK, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );
        assertEquals( "en_US", root.getAvailableLocales().content( false ) );

        final DynamicElement dynamicElement = root.getDynamicElement().content( false );

        assertNotNull( dynamicElement );
        assertEquals( "a0KUbOFU", dynamicElement.getInstanceID().content( false ) );
        assertEquals( "username", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );

        final DynamicContent dynamicContent = dynamicElement.getDynamicContent().content( false );

        assertNotNull( dynamicContent );
        assertEquals( "liferay", dynamicContent.getValue().content( false ) );
    }

    @Test
    public void testHeaderSocialStructuresRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), HEADER_SOCIAL_STRUCTURES, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final DynamicElement dynamicElement = root.getDynamicElement().content( false );

        assertNotNull( dynamicElement );
        assertEquals( "url-location", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( false, dynamicElement.getRepeatable().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 5, entries.size() );

        final Entry entry = entries.get( 4 );

        assertEquals( "instructions", entry.getName().content( false ) );
        assertEquals( "social name, no spaces (facebook, twitter, linkedin)", entry.getValue().content( false ) );
    }

    @Test
    public void testTwitterRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), TWITTER, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final DynamicElement dynamicElement = root.getDynamicElement().content( false );

        assertNotNull( dynamicElement );
        assertEquals( "username", dynamicElement.getName().content( false ) );
        assertEquals( "text", dynamicElement.getType().content( false ) );
        assertEquals( null, dynamicElement.getIndexType().content( false ) );
        assertEquals( false, dynamicElement.getRepeatable().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 5, entries.size() );

        final Entry entry = entries.get( 5 );

        assertNotNull( entry );
        assertEquals( "label", entry.getName().content( false ) );
        assertEquals( "Twitter Username", entry.getValue().content( false ) );
    }
}
