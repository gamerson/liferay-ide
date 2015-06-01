package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.model.SDKProjectsImportOp30;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.functors.ForClosure;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.junit.Before;
import org.junit.Test;


public class ImportPluginsSDKProjectTests
{

    @Before
    public void cleanProjects()
    {
        cleanProject("D:/liferay_sdk/liferay-plugins-sdk-6.2.10.1/portlets/test11-portlet");
    }

    private void cleanProject(String projectLocation )
    {
        File projectDir = new File(projectLocation);
        new File(projectDir,".classpath").delete();
        new File(projectDir,".project").delete();
        FileUtil.deleteDir( new File(projectDir,".settings"), true );
    }
    
    @Test
    public void importBasicPortlet() throws Exception
    {
        SDKProjectsImportOp30 op = SDKProjectsImportOp30.TYPE.instantiate();
        op.setLocation( "D:/liferay_sdk/liferay-plugins-sdk-6.2.10.1/portlets/test11-portlet" );
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ));
        assertTrue(status.ok());
        
        IProject project = CoreUtil.getProject( "test11-portlet" );
        assertTrue(project.exists());
        
//        String[] natureIds = project.getDescription().getNatureIds();
//        
//        assertTrue(Arrays.asList( natureIds ).contains( "com.liferay.ide.core.liferaynature" ));

        IJavaProject javaProject = JavaCore.create( project );
        IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
        
        IClasspathEntry classpathContainer = getClasspathContainer(rawClasspath,"com.liferay.ide.sdk.container");
        assertNotNull( classpathContainer);
        IClasspathContainer container = JavaCore.getClasspathContainer( new Path("com.liferay.ide.sdk.container"), javaProject );
        assertNotNull(container);
        assertTrue(container.getClasspathEntries().length>0);

        
        IFacetedProject facetedProject = ProjectFacetsManager.create( project );
        
        assertNotNull(getFaceted(facetedProject,"jst.java"));
        assertNotNull(getFaceted(facetedProject,"jst.web"));
        assertNotNull(getFaceted(facetedProject,"liferay.portlet"));
        
        assertNull(facetedProject.getPrimaryRuntime());
        
        
        
        
        
        
        
    }
    
    
    @Test
    public void testAntProperty() throws Exception
    {
    }

    private IClasspathEntry getClasspathContainer( IClasspathEntry[] rawClasspath, String path )
    {
        for( IClasspathEntry cpe : rawClasspath )
        {
            if( cpe.getPath().toString().equals( path ) )
            {
                return cpe;
            }
        }
        return null;
    }

    public IProjectFacetVersion getFaceted(IFacetedProject facedProject, String factedId )
    {
        Set<org.eclipse.wst.common.project.facet.core.IProjectFacetVersion> facets = facedProject.getProjectFacets();
        for( Iterator<IProjectFacetVersion> iterator = facets.iterator(); iterator.hasNext(); )
        {
            IProjectFacetVersion iProjectFacetVersion = (IProjectFacetVersion) iterator.next();
            if (iProjectFacetVersion.getProjectFacet().getId().equals( factedId ))
            {
                return iProjectFacetVersion;
            }
        }
        return null;
    }
}
