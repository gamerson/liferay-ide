<#include "./basetemplate.ftl">

<#include "./component.ftl">

public class ${classname} {

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	private static final String _MAPPING = "NetworkUtilities";

}