<#include "./basetemplate.ftl">
<#include "./component.ftl">

public class ${classname}  extends ${supperclass} {

	@Activate
	public void activate() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
						"classpath:${classname}/userauth.ini");
		SecurityUtils.setSecurityManager(factory.getInstance());

		_log.info("activate");
	}

	@Override
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		_log.info("authenticateByEmailAddress");

		return FAILURE;
	}

	@Override
	public int authenticateByScreenName(
			long companyId, String screenName, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		_log.info("authenticateByScreenName  - not implemented ");

		return SUCCESS;
	}

	@Override
	public int authenticateByUserId(
			long companyId, long userId, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		_log.info("authenticateByScreenName  - not implemented ");

		return SUCCESS;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ShiroAuthenticatorPre.class);
}