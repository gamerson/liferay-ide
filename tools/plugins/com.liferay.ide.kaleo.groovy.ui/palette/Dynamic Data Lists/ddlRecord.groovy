import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.dynamicdatalists.model.DDLRecord;
import com.liferay.portlet.dynamicdatalists.service.DDLRecordLocalServiceUtil;

long ddlRecordId = GetterUtil.getLong(serviceContext.getAttribute("ddlRecordId"));
DDLRecord ddlRecord = DDLRecordLocalServiceUtil.getRecord(ddlRecordId);