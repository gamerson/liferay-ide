<#include "./basetemplate.ftl">

<#if ( componenttype != "activator" ) >
<#include "./component.ftl">
</#if>

public class ${classname}  extends ${supperclass} {

<#if ( componenttype == "portlet" ) >
    @Override
    protected void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {

        PrintWriter printWriter = response.getWriter();

        printWriter.print("${projectname} Portlet - Hello World!");
    }
<#elseif ( componenttype == "mvcportlet" ) >
<#elseif ( componenttype == "service" )>
<#elseif ( componenttype == "servicewrapper" )>
        public ${classname} {
            super(null);
        }
<#elseif ( componenttype == "activator" )>
    @Override
    public void start(BundleContext context) throws Exception {
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}
</#if>
}