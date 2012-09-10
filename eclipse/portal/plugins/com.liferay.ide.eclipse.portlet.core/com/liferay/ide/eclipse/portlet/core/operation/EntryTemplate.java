package com.liferay.ide.eclipse.portlet.core.operation;

import java.util.*;
import com.liferay.ide.eclipse.portlet.core.operation.*;
import org.eclipse.jst.j2ee.internal.common.operations.*;

/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 
/**
 * @author Cindy Li
 */
@SuppressWarnings({"restriction","unused"})
public class EntryTemplate implements INewPortletClassDataModelProperties
 {
  protected static String nl;
  public static synchronized EntryTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    EntryTemplate result = new EntryTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "package ";
  protected final String TEXT_2 = ";";
  protected final String TEXT_3 = NL;
  protected final String TEXT_4 = NL + "import ";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL;
  protected final String TEXT_7 = NL;
  protected final String TEXT_8 = "/**" + NL + " * Control panel entry class ";
  protected final String TEXT_9 = NL + " */" + NL;
  protected final String TEXT_10 = NL;

   public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     CreatePortletTemplateModel model = (CreatePortletTemplateModel) argument; 
    
	if (model.getJavaPackageName() != null && model.getJavaPackageName().length() > 0) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append( model.getJavaPackageName() );
    stringBuffer.append(TEXT_2);
    
	}

    stringBuffer.append(TEXT_3);
     
	Collection<String> imports = model.getImports();
	for (String anImport : imports) { 

    stringBuffer.append(TEXT_4);
    stringBuffer.append( anImport );
    stringBuffer.append(TEXT_5);
     
	}

    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append( model.getEntryClassName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(TEXT_10);
    return stringBuffer.toString();
  }
}