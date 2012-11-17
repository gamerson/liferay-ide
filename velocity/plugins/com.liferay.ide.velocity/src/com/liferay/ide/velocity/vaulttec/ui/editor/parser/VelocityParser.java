package com.liferay.ide.velocity.vaulttec.ui.editor.parser;

import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.editor.LangMessages;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.DirectiveConstants;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.apache.velocity.runtime.parser.Parser;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 26 $
 * @author <a href="mailto:akmal.sarhan@gmail.com">Akmal Sarhan </a>
 */
public class VelocityParser extends RuntimeInstance
{

    /**
     * Indicate whether the Parser has been fully initialized.
     */
    private boolean   fIsInitialized = false;
    /**
     * This is a hashtable of initialized Velocity directives. This hashtable is
     * passed to each parser that is created.
     */
    private Hashtable fDirectives;
    private List      fUserDirectives;

/* (non-Javadoc)
 * @see org.apache.velocity.runtime.RuntimeInstance#addProperty(java.lang.String, java.lang.Object)
 */

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Collection getUserDirectives()
    {
        return fUserDirectives;
    }

    /**
     * Used by goto definition
     */
    public VelocimacroProxy getLibraryMacro(String aName)
    { 
        // blank string is the global namespace
        return (VelocimacroProxy)getVelocimacro(aName, ""); //$NON-NLS-1$
    }

    /**
     * This is called by completion code to list all possible macro completion
     */
    public Collection<VelocimacroProxy> getLibraryMacros()
    {
      ArrayList<VelocimacroProxy> macros = new ArrayList<VelocimacroProxy>(128);
      // Blank is the global namespace

		for( VelocimacroProxy vp : getVelocimacros( "" ) ) //$NON-NLS-1$
      {
        macros.add(vp);
      }
      return macros;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param aName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isUserDirective(String aName)
    {
        return fUserDirectives.contains(aName);
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws Exception
     *             DOCUMENT ME!
     */
    public synchronized void init()
    {
        if (!fIsInitialized)
        {
            // Set Velocity library
            IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
            setProperty("file.resource.loader.path", store.getString(IPreferencesConstants.LIBRARY_PATH)); //$NON-NLS-1$
            setProperty("velocimacro.library", store.getString(IPreferencesConstants.LIBRARY_LIST)); //$NON-NLS-1$
            setProperty("parser.pool.size", 1); //$NON-NLS-1$
            // Initialize system and user directives
            initializeDirectives();
            // Call super implementation last because it calls createNewParser()
			try
			{
				super.init();
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            fIsInitialized = true;
        }
    }

    /**
     * Returns a JavaCC generated Parser.
     */
    public Parser createNewParser()
    {
        Parser parser = super.createNewParser();
        parser.setDirectives(fDirectives);

        return parser;
    }
//    public Map get Resources
    /**
     * This methods initializes all the directives that are used by the Velocity
     * Runtime. The directives to be initialized are listed in the
     * RUNTIME_DEFAULT_DIRECTIVES properties file.
     */
    private void initializeDirectives()
    {
        /*
         * Initialize the runtime directive table. This will be used for
         * creating parsers.
         */
        fDirectives = new Hashtable();
        Properties directiveProperties = new Properties();
        /*
         * Grab the properties file with the list of directives that we should
         * initialize.
         */
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_RUNTIME_DIRECTIVES);
        if (inputStream == null) { throw new RuntimeException(LangMessages.VelocityParser_error_loading_directive_properties + LangMessages.VelocityParser_something_is_very_wrong_if_these_properties + LangMessages.VelocityParser_arent_being_located_either_your_velocity
                + LangMessages.VelocityParser_distribution_is_incomplete_or_your_velocity + LangMessages.VelocityParser_jar_file_is_corrupted); }
        try
        {
          directiveProperties.load(inputStream);
        }
        catch(Exception e)
        {
          throw new RuntimeException(e);
        }
        /*
         * Grab all the values of the properties. These are all class names for
         * example:
         * 
         * org.apache.velocity.runtime.directive.Foreach
         */
        Enumeration directiveClasses = directiveProperties.elements();
        while (directiveClasses.hasMoreElements())
        {
            String directiveClass = (String) directiveClasses.nextElement();
            loadDirective(directiveClass, "System"); //$NON-NLS-1$
        }
        /*
         * now the user's directives
         */
        fUserDirectives = new ArrayList();
        Iterator userDirectives = VelocityPlugin.getVelocityUserDirectives().iterator();
        while (userDirectives.hasNext())
        {
            String directive = (String) userDirectives.next();
            String name = directive.substring(0, directive.indexOf(' '));
            int type = (directive.endsWith("[Block]") ? DirectiveConstants.BLOCK : DirectiveConstants.LINE); //$NON-NLS-1$
            fUserDirectives.add('#' + name);
            fDirectives.put(name, new VelocityDirective(name, type));
        }
    }

    /**
     * instantiates and loads the directive with some basic checks
     * 
     * @param directiveClass
     *            classname of directive to load
     */
    private void loadDirective(String directiveClass, String caption)
    {
        try
        {
            Object o = Class.forName(directiveClass).newInstance();
            if (o instanceof Directive)
            {
                Directive directive = (Directive) o;
                fDirectives.put(directive.getName(), directive);
            } else
            {
        	
//                error(caption + " Directive " + directiveClass + " is not org.apache.velocity.runtime.directive.Directive." + " Ignoring. ");
            }
        }
        catch (Exception e)
        {
//            error("Exception Loading " + caption + " Directive: " + directiveClass + " : " + e);
        }
    }
}
