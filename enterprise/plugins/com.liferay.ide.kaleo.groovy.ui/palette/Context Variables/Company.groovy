import com.liferay.portal.kernel.util.GetterUtil
import com.liferay.portal.kernel.workflow.WorkflowConstants
import com.liferay.portal.kernel.Company
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil

long companyId = GetterUtil.getLong((String)workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID));
Company company = CompanyLocalServiceUtil.getCompanyById(companyId)