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

package com.liferay.ide.kaleo.ui.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author Gregory Amerson
 */
public class KaleoNodeAddActionHandlerFactory extends SapphireActionHandlerFactory {

	@Override
	public List<SapphireActionHandler> create() {
		List<SapphireActionHandler> handlers = new ArrayList<>();

		SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)getPart();

		for (DiagramNodeTemplate nodeTemplate : diagramPart.getVisibleNodeTemplates()) {
			NewNodeAddActionHandler addNodeHandler = _createKaleoNodeActionHandlerForTemplate(nodeTemplate);

			handlers.add(addNodeHandler);
		}

		return handlers;
	}

	private NewNodeAddActionHandler _createKaleoNodeActionHandlerForTemplate(DiagramNodeTemplate nodeTemplate) {
		NewNodeAddActionHandler retval = null;

		IDiagramNodeDef diagramNode = nodeTemplate.definition();

		Value<String> nodeId = diagramNode.getId();

		if (Objects.equals("state", nodeId.content())) {
			retval = new StateNodeAddActionHandler(nodeTemplate);
		}
		else if (Objects.equals("task", nodeId.content())) {
			retval = new TaskNodeAddActionHandler(nodeTemplate);
		}
		else if (Objects.equals("condition", nodeId.content())) {
			retval = new ConditionNodeAddActionHandler(nodeTemplate);
		}
		else if (Objects.equals("fork", nodeId.content())) {
			retval = new ForkNodeAddActionHandler(nodeTemplate);
		}
		else if (Objects.equals("join", nodeId.content())) {
			retval = new JoinNodeAddActionHandler(nodeTemplate);
		}
		else if (Objects.equals("join-xor", nodeId.content())) {
			retval = new JoinXorNodeAddActionHandler(nodeTemplate);
		}

		return retval;
	}

}