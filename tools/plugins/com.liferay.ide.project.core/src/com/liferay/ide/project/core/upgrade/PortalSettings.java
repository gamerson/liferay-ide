package com.liferay.ide.project.core.upgrade;

import com.liferay.blade.api.Problem;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public class PortalSettings implements LiferayProblems
{

    private String _previousLiferayPortalLocation;
    private String _newName;
    private String _newliferayPortalLocation;
    private String _type;
    private Problem[] _problems;

    public PortalSettings()
    {
        super();
    }

    public PortalSettings(
        String previousLiferayPortalLocation, String newName, String newliferayPortalLocation, String type )
    {
        super();
        _previousLiferayPortalLocation = previousLiferayPortalLocation;
        _newName = newName;
        _newliferayPortalLocation = newliferayPortalLocation;
        _type = type;
    }

    public String getPreviousLiferayPortalLocation()
    {
        return _previousLiferayPortalLocation;
    }

    public void setPreviousLiferayPortalLocation( String liferayPortalLocation )
    {
        _previousLiferayPortalLocation = liferayPortalLocation;
    }

    public String getNewName()
    {
        return _newName;
    }

    public void setNewName( String newName )
    {
        _newName = newName;
    }

    public String getNewLiferayPortalLocation()
    {
        return _newliferayPortalLocation;
    }

    public void setNewLiferayPortalLocation( String newliferayPortalLocation )
    {
        _newliferayPortalLocation = newliferayPortalLocation;
    }

    public Problem[] getProblems()
    {
        return _problems;
    }

    public void setProblems( Problem[] problems )
    {
        _problems = problems;
    }

    public String getType()
    {
        return _type;
    }

    public void setType( String type )
    {
        _type = type;
    }

}
