package com.liferay.ide.portlet.core.spring.operation;

import java.util.*;
import com.liferay.ide.portlet.core.spring.*;
import com.liferay.ide.portlet.core.spring.operation.*;
import com.liferay.ide.portlet.core.operation.*;
import org.eclipse.jst.j2ee.internal.common.operations.*;

/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
 * @author Terry Jia
 */
@SuppressWarnings({"restriction","unused"})
public class SpringPortletTemplate implements INewSpringPortletClassDataModelProperties
 {
  protected static String nl;
  public static synchronized SpringPortletTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    SpringPortletTemplate result = new SpringPortletTemplate();
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
  protected final String TEXT_8 = "@Controller" + NL + "@RequestMapping(\"VIEW\")";
  protected final String TEXT_9 = NL + "public class ";
  protected final String TEXT_10 = " {";
  protected final String TEXT_11 = NL + NL + "\t@RenderMapping" + NL + "\tpublic String question(Model model) {" + NL + "\t\treturn \"";
  protected final String TEXT_12 = "/view\";" + NL + "\t}" + NL + "" + NL + "}";
  protected final String TEXT_13 = NL;

   public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     CreateSpringPortletTemplateModel model = (CreateSpringPortletTemplateModel) argument; 
    
	model.removeFlags(CreateJavaEEArtifactTemplateModel.FLAG_QUALIFIED_SUPERCLASS_NAME); 

    
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
    
	if (model.isPublic()) { 

    stringBuffer.append(TEXT_9);
    stringBuffer.append( model.getClassName() );
    stringBuffer.append(TEXT_10);
    
	}

    stringBuffer.append(TEXT_11);
    stringBuffer.append( model.getPortletName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}