<#assign appendIndent="">
<#macro printLayout this>
<#assign rowElement="div" trBegin="" trEnd="" columnElement="div" columnNewline="\n">
<#assign rowCounter=0>
<#if (this.getPortletLayouts().size() > 0)>
<#list this.getPortletLayouts() as row>
<#assign rowCounter = rowCounter + 1>
    ${appendIndent}<${rowElement} class="${row.getClassName().content()}">${trBegin}
<#list row.getPortletColumns() as col>
<#if col.getOnly().content() >
<#assign columnContentDescriptor = " portlet-column-content-only"  columnDescriptor = " portlet-column-only">
<#elseif col.getFirst().content() >
<#assign columnContentDescriptor = " portlet-column-content-first" columnDescriptor = " portlet-column-first">
<#elseif col.getLast().content()>
<#assign columnContentDescriptor = " portlet-column-content-last"  columnDescriptor = " portlet-column-last">
<#else>
<#assign columnContentDescriptor = "" columnDescriptor = "">
</#if>
        ${appendIndent}<${columnElement} class="portlet-column${columnDescriptor} span${col.getWeight().content()}"<#if !(col.getNumId().content()==0)> id="column-${col.getNumId().content()}"</#if>>
<#if (col.getPortletLayouts().size() > 0) >
<#assign appendIndent = stack.push(appendIndent) + "\t\t">
<@printLayout this=col/>
<#assign appendIndent = stack.pop()>
<#else>
            ${appendIndent}$processor.processColumn("column-${col.getNumId().content()}", "portlet-column-content${columnContentDescriptor}")
</#if>
        ${appendIndent}</${columnElement}>
</#list>
    ${trEnd}${appendIndent}</${rowElement}>
</#list>
</#if>
</#macro>
<#if (root.getPortletLayouts().size() >= 0)>
<div class="${templateName}" id="${root.getId().content()}" role="${root.getRole().content()}">
<@printLayout this=root/>
</div><#rt>
</#if>