/**
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
 */

package com.liferay.ide.project.ui;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.sapphire.Context;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.osgi.BundleBasedContext;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Terry Jia
 */
public class SapphireEditorForXml extends SapphireEditor implements IExecutableExtension {

	public SapphireEditorForXml() {
	}

	public SapphireEditorForXml(ElementType type, DefinitionLoader.Reference<EditorPageDef> definition) {
		if (type == null) {
			throw new IllegalArgumentException();
		}

		_type = type;
		_definition = definition;
	}

	public SapphireEditorForXml(Map<String, ElementType> typeMap) {
		_typeMap = typeMap;
	}

	@Override
	public void dispose() {
		super.dispose();

		_type = null;
		_definition = null;
		_sourcePage = null;
	}

	@Override
	public IContentOutlinePage getContentOutline(final Object page) {
		if (page == _sourcePage) {
			return (IContentOutlinePage)_sourcePage.getAdapter(IContentOutlinePage.class);
		}

		return super.getContentOutline(page);
	}

	public final StructuredTextEditor getXmlEditor() {
		return _sourcePage;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
		super.setInitializationData(config, propertyName, data);

		if (_definition == null) {
			Map<?, ?> properties = (Map<?, ?>)data;

			IContributor contributor = config.getContributor();

			_context = BundleBasedContext.adapt(contributor.getName());

			_sdef = (String)properties.get("sdef");
			_pageName = (String)properties.get("pageName");
			_pageDefinitionId = (String)properties.get("pageDefinitionId");
		}
	}

	@Override
	protected void createFormPages() throws PartInitException {
		if (_pageName == null) {
			IEditorPart page = createPage(getDefinition(null));

			if (page instanceof IFormPage) {
				addPage(0, (IFormPage)page);
			}
			else {
				addPage(0, page, getEditorInput());
			}
		}
		else {
			addDeferredPage(0, _pageName, _pageDefinitionId);
		}
	}

	protected Element createModel() {
		ElementType type = _type;

		XmlEditorResourceStore store = createResourceStore(_sourcePage);

		Document document = store.getDomDocument();

		if ((_typeMap != null) && (type == null)) {
			NodeList nodes = document.getChildNodes();

			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				NamedNodeMap attributes = node.getAttributes();

				if (attributes != null) {
					Node xmlns = attributes.getNamedItem("xmlns");

					if (xmlns instanceof Attr) {
						Attr attr = (Attr)xmlns;

						String value = attr.getValue();

						type = _typeMap.get(value);
					}
				}
			}
		}

		if (type == null) {
			Reference<EditorPageDef> definition = getDefinition(_pageDefinitionId);

			EditorPageDef def = definition.resolve();

			if (def == null) {
				throw new IllegalStateException();
			}

			ReferenceValue<JavaTypeName, JavaType> referenceValue = def.getElementType();

			JavaType elementJavaType = referenceValue.target();

			type = ElementType.read((Class<?>)elementJavaType.artifact(), true);
		}

		return type.instantiate(new RootXmlResource(store));
	}

	protected XmlEditorResourceStore createResourceStore(StructuredTextEditor sourceEditor) {
		return new XmlEditorResourceStore(this, _sourcePage);
	}

	@Override
	protected final void createSourcePages() throws PartInitException {
		_sourcePage = new StructuredTextEditor();

		_sourcePage.setEditorPart(this);

		int index = addPage(_sourcePage, getEditorInput());

		setPageText(index, _sourcePageTitle.text());
	}

	@Override
	protected Reference<EditorPageDef> getDefinition(String id) {
		if (_definition != null) {
			return _definition;
		}
		else {
			return super.getDefinition(id);
		}
	}

	@Override
	protected DefinitionLoader getDefinitionLoader() {
		DefinitionLoader definitionLoader = DefinitionLoader.context(_context);

		return definitionLoader.sdef(_sdef);
	}

	@Text("Source")
	private static LocalizableText _sourcePageTitle;

	static {
		LocalizableText.init(SapphireEditorForXml.class);
	}

	private Context _context;
	private DefinitionLoader.Reference<EditorPageDef> _definition;
	private String _pageDefinitionId;
	private String _pageName;
	private String _sdef;
	private StructuredTextEditor _sourcePage;
	private ElementType _type;
	private Map<String, ElementType> _typeMap;

}