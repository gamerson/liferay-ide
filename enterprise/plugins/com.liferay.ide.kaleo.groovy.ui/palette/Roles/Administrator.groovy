import com.liferay.portal.kernel.util.GetterUtil
import com.liferay.portal.kernel.workflow.WorkflowConstants
import com.liferay.portal.model.Role
import com.liferay.portal.service.CompanyLocalServiceUtil
import com.liferay.portal.service.RoleLocalServiceUtil

long companyId = GetterUtil.getLong((String)workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID))
Role adminRole = RoleLocalServiceUtil.getRole(companyId, "Administrator")