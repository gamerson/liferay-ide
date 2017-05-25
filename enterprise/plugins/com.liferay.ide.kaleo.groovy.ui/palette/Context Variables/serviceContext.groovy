import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.service.ServiceContext;

long companyId = GetterUtil.getLong((String) workflowContext.get(WorkflowConstants.CONTEXT_COMPANY_ID));
ServiceContext serviceContext = (ServiceContext) workflowContext.get(WorkflowConstants.CONTEXT_SERVICE_CONTEXT);