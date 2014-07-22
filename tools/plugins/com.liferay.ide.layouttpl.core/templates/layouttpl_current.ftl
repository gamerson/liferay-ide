<#assign appendIndent="">
<#macro printLayout this>
<#assign rowElement="div" trBegin="" trEnd="" columnElement="div" columnNewline="\n">
<#assign rowCounter=0>
<#if !this.getRows().isEmpty()>
<#list this.getRows() as row>
<#assign rowCounter = rowCounter + 1>
	${appendIndent}<${rowElement} class="${row.className}">${trBegin}
<#list row.getColumns() as col>
<#if row.getColumns().size() == 1>
<#assign columnContentDescriptor = " portlet-column-content-only" columnDescriptor = " portlet-column-only">
<#elseif (row.getColumns().size() > 1)>
<#if col.isFirst()>
<#assign columnContentDescriptor = " portlet-column-content-first" columnDescriptor = " portlet-column-first">
<#elseif col.isLast()><#rt>
<#assign columnContentDescriptor = " portlet-column-content-last" columnDescriptor = " portlet-column-last">
<#else><#rt>
<#assign columnContentDescriptor = "" columnDescriptor = "">
</#if>
</#if>
		${appendIndent}<${columnElement} class="portlet-column${columnDescriptor} span${col.getWeight()}"<#if !(col.numId==0)> id="column-${col.numId}"</#if>>
<#if !col.getRows().isEmpty()>
<#assign appendIndent = stack.push(appendIndent) + "\t\t">
<@printLayout this=col/>
<#assign appendIndent = stack.pop()>
<#else>
			${appendIndent}$processor.processColumn("column-${col.numId}", "portlet-column-content${columnContentDescriptor}")
</#if>
		${appendIndent}</${columnElement}>
</#list>
	${trEnd}${appendIndent}</${rowElement}>
</#list>
</#if>
</#macro>
<#if (root.getRows().size() >= 0)>
<div class="${templateName}" id="${root.id}" role="${root.role}">
<@printLayout this=root/>
</div><#rt>
</#if>