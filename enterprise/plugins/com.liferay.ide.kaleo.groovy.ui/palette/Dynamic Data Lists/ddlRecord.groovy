import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalServiceUtil;

long ddlRecordId = GetterUtil.getLong(serviceContext.getAttribute("ddlRecordId"));
DDLRecord ddlRecord = DDLRecordLocalServiceUtil.getRecord(ddlRecordId);