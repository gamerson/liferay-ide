<#include "./basetemplate.ftl">

<#include "./component.ftl">

public class ${classname} implements ${supperclass} {

<#if ( supperclass == "MVCActionCommand" ) >
	@Override
	public boolean processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException {
		_handleActionCommand(actionRequest, actionResponse);

		return true;
	}

	private void _handleActionCommand(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String name = ParamUtil.get(actionRequest, "name", StringPool.BLANK);

		_log.info("Hello " + name);

	}

	private Log _log = LogFactoryUtil.getLog(${classname}.class);
</#if>
}