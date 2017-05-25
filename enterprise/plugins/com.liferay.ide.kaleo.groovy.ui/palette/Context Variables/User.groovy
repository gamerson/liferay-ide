import com.liferay.portal.kernel.util.GetterUtil
import com.liferay.portal.kernel.workflow.WorkflowConstants
import com.liferay.portal.kernel.model.User
import com.liferay.portal.kernel.service.UserLocalServiceUtil

long userId = GetterUtil.getLong((String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID))
User user = UserLocalServiceUtil.getUser(userId)