<#include "./basetemplate.ftl">

<#include "./component.ftl">

@Path("/${projectname}")
public class ${classname} extends ${supperclass} {

	@Override
	public Set<Object> getSingletons() {
		return Collections.<Object> singleton(this);
	}
}