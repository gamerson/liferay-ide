/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;


/**
 * @author Simon Jiang
 */
public class NewLiferayComponentModelClassPossibleValuesService extends PossibleValuesService
{
	private final static String[] MODEL_CLASS_VALUES = 
	{
			"com.liferay.portal.model.Account",
			"com.liferay.portal.model.Address",
			"com.liferay.portal.model.AnnouncementsDelivery",
			"com.liferay.portal.model.AnnouncementsEntry",
			"com.liferay.portal.model.AnnouncementsFlag",
			"com.liferay.portal.model.AssetCategory",
			"com.liferay.portal.model.AssetCategoryProperty",
			"com.liferay.portal.model.AssetEntry",
			"com.liferay.portal.model.AssetLink",
			"com.liferay.portal.model.AssetTag",
			"com.liferay.portal.model.AssetTagStats",
			"com.liferay.portal.model.AssetVocabulary",
			"com.liferay.portal.model.BlogsEntry",
			"com.liferay.portal.model.BlogsStatsUser",
			"com.liferay.portal.model.BrowserTracker",
			"com.liferay.portal.model.CalEvent",
			"com.liferay.portal.model.ClassName",
			"com.liferay.portal.model.ClusterGroup",
			"com.liferay.portal.model.Company",
			"com.liferay.portal.model.Contact",
			"com.liferay.portal.model.Counter",
			"com.liferay.portal.model.Country",
			"com.liferay.portal.model.DLContent",
			"com.liferay.portal.model.DLFileEntryMetadata",
			"com.liferay.portal.model.DLFileEntry",
			"com.liferay.portal.model.DLFileEntryType",
			"com.liferay.portal.model.DLFileRank",
			"com.liferay.portal.model.DLFileShortcut",
			"com.liferay.portal.model.DLFileVersion",
			"com.liferay.portal.model.DLFolder",
			"com.liferay.portal.model.DLSyncEvent",
			"com.liferay.portal.model.Dummy",
			"com.liferay.portal.model.EmailAddress",
			"com.liferay.portal.model.ExpandoColumn",
			"com.liferay.portal.model.ExpandoRow",
			"com.liferay.portal.model.ExpandoTable",
			"com.liferay.portal.model.ExpandoValue",
			"com.liferay.portal.model.ExportImportConfiguration",
			"com.liferay.portal.model.Group",
			"com.liferay.portal.model.StagedGroup",
			"com.liferay.portal.model.Image",
			"com.liferay.portal.model.LayoutBranch",
			"com.liferay.portal.model.LayoutFriendlyURL",
			"com.liferay.portal.model.Layout",
			"com.liferay.portal.model.LayoutPrototype",
			"com.liferay.portal.model.LayoutRevision",
			"com.liferay.portal.model.LayoutSetBranch",
			"com.liferay.portal.model.LayoutSet",
			"com.liferay.portal.model.LayoutSetPrototype",
			"com.liferay.portal.model.ListType",
			"com.liferay.portal.model.MBBan",
			"com.liferay.portal.model.MBCategory",
			"com.liferay.portal.model.MBDiscussion",
			"com.liferay.portal.model.MBMailingList",
			"com.liferay.portal.model.MBMessage",
			"com.liferay.portal.model.MBStatsUser",
			"com.liferay.portal.model.MBThreadFlag",
			"com.liferay.portal.model.MBThread",
			"com.liferay.portal.model.MembershipRequest",
			"com.liferay.portal.model.Organization",
			"com.liferay.portal.model.OrgGroupRole",
			"com.liferay.portal.model.OrgLabor",
			"com.liferay.portal.model.PasswordPolicy",
			"com.liferay.portal.model.PasswordPolicyRel",
			"com.liferay.portal.model.PasswordTracker",
			"com.liferay.portal.model.Phone",
			"com.liferay.portal.model.PluginSetting",
			"com.liferay.portal.model.PortalPreferences",
			"com.liferay.portal.model.PortletItem",
			"com.liferay.portal.model.Portlet",
			"com.liferay.portal.model.PortletPreferences",
			"com.liferay.portal.model.RatingsEntry",
			"com.liferay.portal.model.RatingsStats",
			"com.liferay.portal.model.RecentLayoutBranch",
			"com.liferay.portal.model.RecentLayoutRevision",
			"com.liferay.portal.model.RecentLayoutSetBranch",
			"com.liferay.portal.model.Region",
			"com.liferay.portal.model.Release",
			"com.liferay.portal.model.RepositoryEntry",
			"com.liferay.portal.model.Repository",
			"com.liferay.portal.model.ResourceAction",
			"com.liferay.portal.model.ResourceBlock",
			"com.liferay.portal.model.ResourceBlockPermission",
			"com.liferay.portal.model.ResourcePermission",
			"com.liferay.portal.model.ResourceTypePermission",
			"com.liferay.portal.model.Role",
			"com.liferay.portal.model.ServiceComponent",
			"com.liferay.portal.model.SocialActivityAchievement",
			"com.liferay.portal.model.SocialActivityCounter",
			"com.liferay.portal.model.SocialActivityLimit",
			"com.liferay.portal.model.SocialActivity",
			"com.liferay.portal.model.SocialActivitySet",
			"com.liferay.portal.model.SocialActivitySetting",
			"com.liferay.portal.model.SocialRelation",
			"com.liferay.portal.model.SocialRequest",
			"com.liferay.portal.model.Subscription",
			"com.liferay.portal.model.SystemEvent",
			"com.liferay.portal.model.Team",
			"com.liferay.portal.model.Ticket",
			"com.liferay.portal.model.TrashEntry",
			"com.liferay.portal.model.TrashVersion",
			"com.liferay.portal.model.UserGroupGroupRole",
			"com.liferay.portal.model.UserGroup",
			"com.liferay.portal.model.UserGroupRole",
			"com.liferay.portal.model.UserIdMappe",
			"com.liferay.portal.model.User",
			"com.liferay.portal.model.UserNotificationDelivery",
			"com.liferay.portal.model.UserNotificationEvent",
			"com.liferay.portal.model.UserTracker",
			"com.liferay.portal.model.UserTrackerPath",
			"com.liferay.portal.model.VirtualHost",
			"com.liferay.portal.model.WebDAVProps",
			"com.liferay.portal.model.Website",
			"com.liferay.portal.model.WorkflowDefinitionLink",
			"com.liferay.portal.model.WorkflowInstanceLink"
	};

    private List<String> possibleValues;

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        possibleValues = new ArrayList<String>();

        for( final String modelClass : Arrays.asList( MODEL_CLASS_VALUES ) )
        {
            possibleValues.add( modelClass );
        }

        Collections.sort( possibleValues );
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( this.possibleValues );
    }
}
