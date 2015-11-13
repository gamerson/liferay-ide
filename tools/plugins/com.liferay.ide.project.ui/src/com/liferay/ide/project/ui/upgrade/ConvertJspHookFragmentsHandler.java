package com.liferay.ide.project.ui.upgrade;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.dialog.LiferayProjectSelectionDialog;
import com.liferay.ide.ui.WorkspaceHelper;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.modeling.Status;


/**
 * @author Gregory Amerson
 */
public class ConvertJspHookFragmentsHandler extends AbstractOSGiCommandHandler
{

    public ConvertJspHookFragmentsHandler()
    {
        super( "convertJspHook" );
    }

    @Override
    protected Object execute( ExecutionEvent event, Command command ) throws ExecutionException
    {
        ViewerFilter filter = new ViewerFilter()
        {
            @Override
            public boolean select( Viewer viewer, Object parentElement, Object element )
            {
                if( element instanceof IJavaProject )
                {
                    IJavaProject project = (IJavaProject) element;

                    if( CoreUtil.isLiferayProject( project.getProject() ) )
                    {
                        return true;
                    }
                }

                return false;
            }
        };

        LiferayProjectSelectionDialog dialog = new LiferayProjectSelectionDialog( UIUtil.getActiveShell(), filter );

        dialog.open();

        final Object[] projects = dialog.getResult();

        if( projects != null && projects.length > 0 )
        {
            IJavaProject javaProject = (IJavaProject) projects[0];

            File srcDir = javaProject.getProject().getLocation().toFile();

            File destDir = javaProject.getProject().getLocation().removeLastSegments( 2 ).append( "modules" ).append( srcDir.getName() + "-module" ).toFile();

            destDir.mkdirs();

            Map<String, String> parameters = new HashMap<>();
            parameters.put( "sourcePath", srcDir.getAbsolutePath() );
            parameters.put( "targetPath", destDir.getAbsolutePath() );

 /*          try
            {
                command.execute( parameters );

                new WorkspaceHelper().openDir( destDir.getAbsolutePath() );
            }
            catch( CommandException e )
            {
                e.printStackTrace();
            }*/
           
           try
           {
               List<String> targetPaths = (List<String>) command.execute( parameters );
               
               for(String path : targetPaths)
               {
                   IPath project = new Path( path + "/pom.xml" );
                   // import maven project
                   doCreateNewProject( project, new NullProgressMonitor() );
                   
               }

               fragmentPath = targetPaths;
           }
           catch( Exception e )
           {

           }
        }

        return null;
    }
    
    public static void doCreateNewProject( IPath pomFilePath, IProgressMonitor monitor ) throws CoreException
    {
        IPath pomPath = pomFilePath;

        if( pomPath != null && pomPath.toFile().exists() )
        {
            File pomFile = new File( pomPath.toPortableString() );
            MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
            final ResolverConfiguration resolverConfig = new ResolverConfiguration();
            final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();

            Model model = mavenModelManager.readMavenModel( pomFile );
            MavenProjectInfo projectInfo = new MavenProjectInfo( pomFile.getName(), pomFile, model, null );
            // setBasedirRename( projectInfo );

            projectInfos.add( projectInfo );

            ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration( resolverConfig );

            final IProjectConfigurationManager projectConfigurationManager =
                MavenPlugin.getProjectConfigurationManager();

            projectConfigurationManager.importProjects( projectInfos, importConfiguration, monitor );
        }
    }

    public static List<String> fragmentPath = null;

}
