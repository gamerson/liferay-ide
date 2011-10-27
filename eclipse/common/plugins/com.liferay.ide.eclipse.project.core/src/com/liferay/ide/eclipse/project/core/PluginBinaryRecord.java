/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.project.core;

import java.io.File;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class PluginBinaryRecord {

	private String binaryName;
	private File binaryFile;
	private String displayName;
	private String label;
	private String liferayVersion;
	boolean conflicts;
	boolean isHook;
	boolean isTheme;
	boolean isPortlet;
	boolean isLayoutTpl;

	public PluginBinaryRecord( File binaryFile ) {
		this.binaryFile = binaryFile;
		setNames();
	}

	private void setNames() {
		if ( binaryFile != null ) {
			label = binaryFile.getAbsolutePath();
			binaryName = binaryFile.getName();
			setPluginProperties();
		}

	}

	private void setPluginProperties() {
		if ( binaryName != null ) {
			int index = -1;
			if ( binaryName.contains( "-hook" ) ) {
				index = binaryName.indexOf( "-hook" );
				isHook = index != -1 ? true : false;
			}
			else if ( binaryName.contains( "-theme" ) ) {
				index = binaryName.indexOf( "-theme" );
				isTheme = index != -1 ? true : false;
			}
			else if ( binaryName.contains( "-portlet" ) ) {
				index = binaryName.indexOf( "-portlet" );
				isPortlet = index != -1 ? true : false;
			}
			else if ( binaryName.contains( "-layouttpl" ) ) {
				index = binaryName.indexOf( "-layouttpl" );
				isLayoutTpl = index != -1 ? true : false;
			}
			if ( index != -1 ) {
				displayName = binaryName.substring( 0, index );
			}
			index = binaryName.lastIndexOf( "-" );
			if ( index != -1 ) {
				liferayVersion = binaryName.substring( index + 1, binaryName.lastIndexOf( "." ) );
			}
		}

	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel( String label ) {
		this.label = label;
	}

	/**
	 * @return the binaryName
	 */
	public String getBinaryName() {
		return binaryName;
	}

	/**
	 * @param binaryName
	 *            the binaryName to set
	 */
	public void setBinaryName( String binaryName ) {
		this.binaryName = binaryName;
	}

	/**
	 * @return the binaryFile
	 */
	public File getBinaryFile() {
		return binaryFile;
	}

	/**
	 * @param binaryFile
	 *            the binaryFile to set
	 */
	public void setBinaryFile( File binaryFile ) {
		this.binaryFile = binaryFile;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName( String liferayPluginName ) {
		this.displayName = liferayPluginName;
	}

	/**
	 * @return the conflicts
	 */
	public boolean isConflicts() {
		return conflicts;
	}

	/**
	 * @param conflicts
	 *            the conflicts to set
	 */
	public void setConflicts( boolean hasConflicts ) {
		this.conflicts = hasConflicts;
	}

	/**
	 * @return the liferayVersion
	 */
	public String getLiferayVersion() {
		return liferayVersion;
	}

	/**
	 * @return the isHook
	 */
	public boolean isHook() {
		return isHook;
	}

	/**
	 * @return the isTheme
	 */
	public boolean isTheme() {
		return isTheme;
	}

	/**
	 * @return the isPortlet
	 */
	public boolean isPortlet() {
		return isPortlet;
	}

	/**
	 * @return the isLayoutTpl
	 */
	public boolean isLayoutTpl() {
		return isLayoutTpl;
	}

	public String getLiferayPluginName() {
		if ( isHook ) {
			return getDisplayName() + "-hook";
		}
		else if ( isLayoutTpl ) {
			return getDisplayName() + "-layputtpl";
		}
		else if ( isPortlet ) {
			return getDisplayName() + "-portlet";
		}
		else if ( isTheme ) {
			return getDisplayName() + "-theme";
		}
		return null;
	}

}
