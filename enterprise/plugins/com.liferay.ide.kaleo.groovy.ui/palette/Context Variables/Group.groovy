import com.liferay.portal.kernel.util.GetterUtil
import com.liferay.portal.kernel.workflow.WorkflowConstants
import com.liferay.portal.kernel.model.Group
import com.liferay.portal.kernel.service.GroupLocalServiceUtil

long groupId = GetterUtil.getLong((String)workflowContext.get(WorkflowConstants.CONTEXT_GROUP_ID))
Group group = GroupLocalServiceUtil.getGroup(groupId)