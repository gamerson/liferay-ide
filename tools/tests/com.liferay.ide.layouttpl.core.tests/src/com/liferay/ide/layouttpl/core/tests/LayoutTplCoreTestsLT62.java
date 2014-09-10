package com.liferay.ide.layouttpl.core.tests;

import com.liferay.ide.layouttpl.core.model.LayoutTpl;
import com.liferay.ide.layouttpl.core.model.PortletColumn;
import com.liferay.ide.layouttpl.core.model.PortletLayout;

import org.eclipse.core.resources.IFile;
import org.junit.Test;


/**
 * @author Kuo Zhang
 *
 */
public class LayoutTplCoreTestsLT62 extends LayoutTplCoreTests
{

    @Override
    protected String getVersion()
    {
        return "6.1.0";
    }

    @Override
    protected String getFilesPrefix()
    {
        return "files/lt62/";
    }

    @Test
    public void evalTemplateFromChangedModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_changed_columns.tpl" );

        final String className = convertToTplClassName( "1_3_2_nest_changed_columns.tpl" );
        LayoutTpl layoutTpl = createModel_132_nest( getVersion(), className );

        PortletLayout row1 = (PortletLayout) layoutTpl.getPortletLayouts().get( 0 );
        PortletLayout row2 = (PortletLayout) layoutTpl.getPortletLayouts().get( 1 );
        PortletLayout row3 = (PortletLayout) layoutTpl.getPortletLayouts().get( 2 );

        PortletLayout row311 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 0 );

        PortletLayout row312 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 1 );

        PortletLayout row31221 = row312.getPortletColumns().get( 1 ).getPortletLayouts().get( 0 );

        row1.getPortletColumns().remove( row1.getPortletColumns().get( 0 ) );
        layoutTpl.getPortletLayouts().remove( row1 );

        PortletColumn insertedColumn = row311.getPortletColumns().insert();
        insertedColumn.setWeight( 20 );
        row311.getPortletColumns().get( 0 ).setWeight( 80 );

        row2.getPortletColumns().remove( row2.getPortletColumns().get( 0 ) );
        row2.getPortletColumns().get( 0 ).setWeight( 66 );

        insertedColumn = row31221.getPortletColumns().insert();
        insertedColumn.setWeight( 10 );
        row31221.getPortletColumns().get( 0 ).setWeight( 90 );

        evalModelWithFile( refTplFile, layoutTpl );
    }

    protected LayoutTpl createModel_132_nest( String version, String className )
    {
        LayoutTpl layoutTpl = LayoutTpl.TYPE.instantiate();
        layoutTpl.setVersion( version );
        layoutTpl.setClassName( className );

        PortletLayout row1 = layoutTpl.getPortletLayouts().insert();

        PortletColumn column11 = row1.getPortletColumns().insert();
        column11.setWeight( 100 );

        PortletLayout row2 = layoutTpl.getPortletLayouts().insert();

        PortletColumn column21 = row2.getPortletColumns().insert();
        column21.setWeight( 33 );

        PortletColumn column22 = row2.getPortletColumns().insert();
        column22.setWeight( 33 );

        PortletColumn column23 = row2.getPortletColumns().insert();
        column23.setWeight( 33 );

        PortletLayout row3 = layoutTpl.getPortletLayouts().insert();

        PortletColumn column31 = row3.getPortletColumns().insert();
        column31.setWeight( 66 );

        PortletLayout row311 = column31.getPortletLayouts().insert();

        PortletColumn column3111 = row311.getPortletColumns().insert();
        column3111.setWeight( 100 );

        PortletLayout row312 = column31.getPortletLayouts().insert();

        PortletColumn column3121 = row312.getPortletColumns().insert();
        column3121.setWeight( 50 );

        PortletColumn column3122 = row312.getPortletColumns().insert();
        column3122.setWeight( 50 );

        PortletLayout row31221 = column3122.getPortletLayouts().insert();

        PortletColumn column312211 = row31221.getPortletColumns().insert();
        column312211.setWeight( 100 );

        PortletColumn column32 = row3.getPortletColumns().insert();
        column32.setWeight( 33 );

        return layoutTpl;
    }

}
