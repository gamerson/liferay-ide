package com.liferay.ide.project.core.model;

/**
 * @author Lovett Li
 */
public class LiferayPortalSetup
{

    private String previousName;
    private String liferayPortalLocation;
    private String newName;
    private String newliferayPortalLocation;

    public LiferayPortalSetup()
    {
    }

    public LiferayPortalSetup(
        String previousName, String liferayPortalLocation, String newName, String newliferayPortalLocation )
    {
        super();
        this.previousName = previousName;
        this.liferayPortalLocation = liferayPortalLocation;
        this.newName = newName;
        this.newliferayPortalLocation = newliferayPortalLocation;
    }

    public String getPreviousName()
    {
        return previousName;
    }

    public void setPreviousName( String previousName )
    {
        this.previousName = previousName;
    }

    public String getLiferayPortalLocation()
    {
        return liferayPortalLocation;
    }

    public void setLiferayPortalLocation( String liferayPortalLocation )
    {
        this.liferayPortalLocation = liferayPortalLocation;
    }

    public String getNewName()
    {
        return newName;
    }

    public void setNewName( String newName )
    {
        this.newName = newName;
    }

    public String getNewliferayPortalLocation()
    {
        return newliferayPortalLocation;
    }

    public void setNewliferayPortalLocation( String newliferayPortalLocation )
    {
        this.newliferayPortalLocation = newliferayPortalLocation;
    }
}
