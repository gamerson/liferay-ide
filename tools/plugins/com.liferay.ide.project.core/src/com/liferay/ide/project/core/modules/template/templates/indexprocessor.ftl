<#include "./basetemplate.ftl">

<#include "./component.ftl">

public class ${classname}  extends ${supperclass} {

	@Override
	public void onFailureByEmailAddress(long companyId, String emailAddress,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

	}

	@Override
	public void onFailureByScreenName(long companyId, String screenName,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

	}

	@Override
	public void onFailureByUserId(long companyId, long userId,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

	}

}