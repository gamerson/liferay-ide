
package com.liferay.ide.swtbot.project.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SelectTypeWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.page.CTabItem;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sunny Shi
 */
public class ImportLiferayWorkspaceProjectTests extends SwtbotBase
{

    private String liferayWorkspaceRootPath = System.getProperty( "user.dir" );

    Tree projectTree = ide.showPackageExporerView().getProjectTree();

    ImportLiferayWorkspaceProjectWizard importLiferayWorkspaceProject = new ImportLiferayWorkspaceProjectWizard( bot );

    ImportLiferayWorkspaceProjectWizard importLiferayWorkspaceProjectWithServerLabel =
        new ImportLiferayWorkspaceProjectWizard( bot );

    SelectTypeWizard selectImportPage = new SelectTypeWizard( bot );

    DeleteResourcesContinueDialog continueDeleteResources =
        new DeleteResourcesContinueDialog( bot, "Delete Resources" );

    @Before
    public void importWorkspaceProject()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        ide.getLiferayWorkspacePerspective().activate();
        ide.showPackageExporerView();

        importLiferayWorkspaceProject();
    }

    @After
    public void clean()
    {
        ide.closeShell( IMPORT_LIFERAY_WORKSPACE );

        if( addedProjects() )
        {
            ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, false );
        }
    }

    public void importLiferayWorkspaceProject()
    {
        ide.getFileMenu().clickMenu( IMPORT );

        selectImportPage.selectItem( "liferay", "Liferay", LIFERAY_WORKSPACE_PROJECT );
        assertEquals( IMPORT_AN_EXISTING_LIFERAY_WORKSPACE_PROJECT, selectImportPage.getValidationMsg() );
        selectImportPage.next();
    }

    @Test
    public void importGradleLiferayWorkspaceProject()
    {
        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );

        String liferayWorkspaceName = "testGradleWorkspace";

        sleep();
        assertEquals( GRADLE_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        sleep();
        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getServerName().getText() );
        importLiferayWorkspaceProject.finish();
        sleep( 8000 );

        projectTree.setFocus();
        sleep();

        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName ).select();
        projectTree.expandNode( liferayWorkspaceName, "bundles" ).select();
        projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "bundles" ).doAction( DELETE );

        continueDeleteResources.confirm();
        sleep( 6000 );

        if( addedProjects() )
        {
            ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, false );
        }

        importLiferayWorkspaceProject();

        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );
        sleep();
        assertEquals(
            SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, importLiferayWorkspaceProject.getValidationMsg() );

        assertEquals( GRADLE_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );

        importLiferayWorkspaceProject.getDownloadLiferaybundle().select();
        sleep();

        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getServerName().getText() );
        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getBundleUrl().getText() );
        sleep();

        importLiferayWorkspaceProject.getServerName().setText( "test-lrws" );
        importLiferayWorkspaceProject.finish();
        sleep( 20000 );

        projectTree.setFocus();
        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName, "bundles" );
        projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "bundles" ).doAction( DELETE );
        continueDeleteResources.confirm();

        sleep( 6000 );

        projectTree.getTreeItem( liferayWorkspaceName ).doAction( "Liferay", "Initialize Server Bundle" );

        sleep( 10000 );
        projectTree.getTreeItem( liferayWorkspaceName ).expand();

        sleep( 2000 );
        assertTrue( projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "bundles" ).isVisible() );
        assertTrue( projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "configs" ).isVisible() );
        assertTrue( projectTree.getTreeItem( liferayWorkspaceName ).getTreeItem( "gradle" ).isVisible() );

        String gradlePropertyFileName = "gradle.properties";
        String settingGradleFileName = "settings.gradle";

        projectTree.expandNode( liferayWorkspaceName, gradlePropertyFileName ).doubleClick();
        Editor gradlePropertiesFile = ide.getEditor( gradlePropertyFileName );

        assertContains( "liferay.workspace.modules.dir", gradlePropertiesFile.getText() );
        assertContains( "liferay.workspace.home.dir", gradlePropertiesFile.getText() );
        gradlePropertiesFile.close();
        sleep();

        projectTree.expandNode( liferayWorkspaceName, settingGradleFileName ).doubleClick();
        Editor settingGradleFile = ide.getEditor( settingGradleFileName );

        assertContains( "buildscript", settingGradleFile.getText() );
        assertContains( "repositories", settingGradleFile.getText() );

        settingGradleFile.close();
        sleep( 2000 );
    }

    @Test
    public void importMavenLiferayWorkspaceProject()
    {
        String liferayWorkspaceName = "testMavenWorkspace";

        importLiferayWorkspaceProject.setWorkspaceLocation( liferayWorkspaceRootPath + "/projects/testMavenWorkspace" );
        sleep();
        assertEquals(
            SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, importLiferayWorkspaceProject.getValidationMsg() );

        assertEquals( MAVEN_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        assertFalse( importLiferayWorkspaceProject.getDownloadLiferaybundle().isChecked() );

        importLiferayWorkspaceProject.finish();
        sleep( 6000 );

        projectTree.setFocus();
        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName ).doubleClick();
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-modules" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-themes" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-wars" ).isVisible() );
        projectTree.getTreeItem( "testMavenWorkspace-modules" ).getTreeItem( "pom.xml" ).doubleClick();

        CTabItem switchCTabItem = new CTabItem( bot, "pom.xml" );
        sleep();
        switchCTabItem.click();

        Editor pomXmlFileModules = ide.getEditor( "testMavenWorkspace-modules/pom.xml" );
        assertContains( "testMavenWorkspace-modules", pomXmlFileModules.getText() );
        assertContains( "artifactId", pomXmlFileModules.getText() );

        pomXmlFileModules.close();
        sleep();

        projectTree.expandNode( liferayWorkspaceName, "pom.xml" ).doubleClick();
        sleep();
        switchCTabItem.click();

        Editor pomXmlFile = ide.getEditor( "testMavenWorkspace/pom.xml" );
        assertContains( "testMavenWorkspace", pomXmlFile.getText() );

        pomXmlFile.close();
        sleep();

        importLiferayWorkspaceProject();

        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );

        assertEquals(
            A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS,
            importLiferayWorkspaceProjectWithServerLabel.getValidationMsg() );

        importLiferayWorkspaceProject.cancel();

    }

    @Test
    public void initialStateAndValidationProjectName()
    {
        assertEquals( PLEASE_SELECT_THE_WORKSPACE_LOCATION, importLiferayWorkspaceProject.getValidationMsg() );

        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getWorkspaceLocation().getText() );
        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        assertFalse( importLiferayWorkspaceProject.getAddProjectToWorkingSet().isChecked() );

        assertTrue( importLiferayWorkspaceProject.backBtn().isEnabled() );
        assertFalse( importLiferayWorkspaceProject.nextBtn().isEnabled() );
        assertFalse( importLiferayWorkspaceProject.finishBtn().isEnabled() );
        assertTrue( importLiferayWorkspaceProject.cancelBtn().isEnabled() );

        importLiferayWorkspaceProject.setWorkspaceLocation( ".." );
        sleep();
        assertEquals(
            " " + "\"" + ".." + "\"" + IS_NOT_AN_ABSOLUTE_PATH, importLiferayWorkspaceProject.getValidationMsg() );

        importLiferayWorkspaceProject.setWorkspaceLocation( "1.*" );
        sleep();
        assertEquals(
            " " + "\"" + "1.*" + "\"" + IS_NOT_A_VALID_PATH, importLiferayWorkspaceProject.getValidationMsg() );

        importLiferayWorkspaceProject.setWorkspaceLocation( " " );
        sleep();
        assertEquals( WORKSPACE_LOCATION_MUST_BE_SPECIFIED, importLiferayWorkspaceProject.getValidationMsg() );

        importLiferayWorkspaceProject.setWorkspaceLocation( "C://non-exist-dir" );
        sleep();
        assertEquals( DIRECTORY_DOESNT_EXIST, importLiferayWorkspaceProject.getValidationMsg() );

        importLiferayWorkspaceProject.setWorkspaceLocation( "C:/Users" );
        sleep();
        assertEquals( INVALID_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getValidationMsg() );

        importLiferayWorkspaceProject.setWorkspaceLocation(
            liferayWorkspaceRootPath + "/projects/testGradleWorkspace" );
        sleep();

        assertEquals(
            THE_SERVER_OR_RUNTIME_NAME_IS_ALREADY_IN_USE,
            importLiferayWorkspaceProjectWithServerLabel.getValidationMsg() );

        importLiferayWorkspaceProject.cancel();;

    }

}
