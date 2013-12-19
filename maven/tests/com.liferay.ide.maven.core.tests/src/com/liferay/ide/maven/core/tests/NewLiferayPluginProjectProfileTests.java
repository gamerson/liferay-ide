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
package com.liferay.ide.maven.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.Profile;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.sapphire.services.PossibleValuesService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectProfileTests
{

    @Test
    public void testProfiles() throws Exception
    {
        final NewLiferayPluginProjectOp op = newMavenProjectOp();

        final String emptyActiveProfilesValue = op.getActiveProfilesValue().content();

        assertEquals( emptyActiveProfilesValue, null );

        op.setActiveProfilesValue( "foo,bar" );

        assertEquals( "foo,bar", op.getActiveProfilesValue().content() );

        assertEquals( 0, op.getSelectedProfiles().size() );

        assertEquals( 0, op.getNewLiferayProfiles().size() );
    }


    @Test
    @Ignore
    public void testProfilesPossibleValues() throws Exception
    {
        final NewLiferayPluginProjectOp op = newMavenProjectOp();

        final PossibleValuesService possibleValues = op.getSelectedProfiles().service( PossibleValuesService.class );

        final Set<String> profileValues = possibleValues.values();

        assertEquals( true, profileValues.size() > 0 );

        final Iterator<String> itr = profileValues.iterator();

        final Profile firstProfile = op.getSelectedProfiles().insert();

        final String firstProfileId = itr.next();

        firstProfile.setId( firstProfileId );

        assertEquals( firstProfileId, op.getActiveProfilesValue().content() );

        final Profile secondProfile = op.getSelectedProfiles().insert();

        final String secondProfileId = itr.next();

        secondProfile.setId( secondProfileId );

        assertEquals( firstProfileId + ',' + secondProfileId, op.getActiveProfilesValue().content() );
    }

    @Test
    public void testSelectProfiles() throws Exception
    {
        final NewLiferayPluginProjectOp op = newMavenProjectOp();

        assertEquals( 0, op.getSelectedProfiles().size() );
    }

    @Test
    public void testNewLiferayProfileIdDefaultValue() throws Exception
    {
//        final NewLiferayPluginProjectOp op = newMavenProjectOp();
//
//        op.getSelectedProfiles().insert().setId( "foo" );
//
//        final NewLiferayProfile newLiferayProfile = op.getNewLiferayProfiles().insert();
//
//        newLiferayProfile.getId().content();
    }

    private NewLiferayPluginProjectOp newMavenProjectOp()
    {
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectProvider( "maven" );

        return op;
    }

    protected NewLiferayPluginProjectOp newProjectOp()
    {
        return NewLiferayPluginProjectOp.TYPE.instantiate();
    }

}
