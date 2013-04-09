/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.maven.core.internal.converters;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.maven.core.LiferayMavenUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author kamesh.sampath
 */
public class LiferayProjectConverter extends AbstractProjectConversionParticipant
{

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant#accept(org.eclipse.core.resources
     * .IProject)
     */
    @Override
    public boolean accept( IProject iProject ) throws CoreException
    {

        if( iProject == null )
        {
            return false;
        }
        else
        {
            ILiferayProject liferayProject = LiferayCore.create( iProject );
            return liferayProject instanceof ILiferayProject;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.m2e.core.project.conversion.AbstractProjectConversionParticipant#convert(org.eclipse.core.resources
     * .IProject, org.apache.maven.model.Model, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void convert( IProject iProject, Model model, IProgressMonitor monitor ) throws CoreException
    {
        IFacetedProject facetedProject = null;
        if( iProject != null )
        {

            try
            {
                facetedProject = ProjectFacetsManager.create( iProject );
            }
            catch( Exception e )
            {
                // TODO log exception
                e.printStackTrace();
            }

            if( facetedProject != null && ProjectUtil.hasAnyLiferayPluginFacet( facetedProject ) )
            {

                addDependencies( model );

                Build build = LiferayMavenUtil.addLiferayMavenPlugin( model, iProject );

                model.setBuild( build );
            }

        }

    }

    /**
     * @param model
     */
    private void addDependencies( Model model )
    {
        List<Dependency> existingDependencies = model.getDependencies();
        List<Dependency> liferayProjectDependencies = LiferayMavenUtil.liferayDependencies( model );
        liferayProjectDependencies.removeAll( existingDependencies );
        model.getDependencies().addAll( liferayProjectDependencies );
    }

}
