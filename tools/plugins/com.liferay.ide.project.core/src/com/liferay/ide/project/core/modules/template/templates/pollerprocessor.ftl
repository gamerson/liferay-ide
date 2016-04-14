<#include "./basetemplate.ftl">

<#include "./component.ftl">

public class ${classname} extends ${supperclass} {

<#if ( supperclass == "BasePollerProcessor" ) >
	@Override
	protected PollerResponse doReceive(PollerRequest pollerRequest)
		throws Exception {
	}

	@Override
	protected void doSend(PollerRequest pollerRequest) throws Exception {
	}
</#if>
}