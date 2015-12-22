/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.project.ui.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection.StringArrayDialogCallback;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSectionCallback;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public abstract class LiferayPropertyModuleCustomPart extends LiferaySimpleModuleCustomPart
{
	protected StringArrayDialogCallback callback = new StringArrayTableWizardSectionCallback();

    protected Text packageText;

    protected Button packageSelect;

    protected Text componentText;

    protected Button componentSelect;

    protected Text serviceText;

    protected Button serviceSelect;

    protected TableViewer viewer;

    protected String[] fieldLabels = new String[]{"Property", "Value"};

    protected String[] columnTitles = new String[]{"Name", "Value"};

    
    protected abstract EditPropertyOverrideDialog getEditPropertyOverrideDialog(final Shell shell, String[] valuesForText );

    protected abstract AddPropertyOverrideDialog getAddPropertyOverrideDialog(final Shell shell);

    protected abstract String[] loadProperties();
    
    //TODO
    //will be replaced by Lovett's OSGI service tracker function.
    protected static String[] services = new String[] {
		"com.liferay.amazon.rankings.web.upgrade.AmazonRankingsWebUpgrade",
		"com.liferay.application.list.PanelApp",
		"com.liferay.application.list.PanelAppRegistry",
		"com.liferay.application.list.PanelCategory",
		"com.liferay.application.list.PanelCategoryRegistry",
		"com.liferay.asset.browser.web.upgrade.AssetBrowserWebUpgrade",
		"com.liferay.asset.categories.admin.web.upgrade.AssetCategoriesAdminWebUpgrade",
		"com.liferay.asset.categories.navigation.web.upgrade.AssetCategoriesNavigationWebUpgrade",
		"com.liferay.asset.publisher.web.exportimport.portlet.preferences.processor.AssetPublisherPortletDisplayTemplateExportCapability",
		"com.liferay.asset.publisher.web.exportimport.portlet.preferences.processor.AssetPublisherPortletDisplayTemplateImportCapability",
		"com.liferay.asset.publisher.web.upgrade.AssetPublisherWebUpgrade",
		"com.liferay.asset.tags.admin.web.upgrade.AssetTagsAdminWebUpgrade",
		"com.liferay.asset.tags.compiler.web.upgrade.AssetTagsCompilerWebUpgrade",
		"com.liferay.asset.tags.navigation.web.upgrade.AssetTagsNavigationWebUpgrade",
		"com.liferay.blogs.recent.bloggers.web.upgrade.RecentBloggersWebUpgrade",
		"com.liferay.blogs.web.exportimport.portlet.preferences.processor.BlogsPortletDisplayTemplateExportCapability",
		"com.liferay.blogs.web.exportimport.portlet.preferences.processor.BlogsPortletDisplayTemplateImportCapability",
		"com.liferay.blogs.web.upgrade.BlogsWebUpgrade",
		"com.liferay.bookmarks.configuration.BookmarksGroupServiceConfiguration",
		"com.liferay.bookmarks.service.BookmarksEntryLocalService",
		"com.liferay.bookmarks.service.BookmarksFolderLocalService",
		"com.liferay.calendar.service.CalendarBookingLocalService",
		"com.liferay.calendar.service.CalendarImporterLocalService",
		"com.liferay.calendar.service.CalendarResourceLocalService",
		"com.liferay.comment.page.comments.web.upgrade.PageCommentsWebUpgrade",
		"com.liferay.currency.converter.web.upgrade.CurrencyConverterWebUpgrade",
		"com.liferay.dictionary.web.upgrade.DictionaryWebUpgrade",
		"com.liferay.dynamic.data.lists.exporter.DDLExporter",
		"com.liferay.dynamic.data.lists.exporter.DDLExporterFactory",
		"com.liferay.dynamic.data.lists.service.DDLRecordLocalService",
		"com.liferay.dynamic.data.lists.service.DDLRecordService",
		"com.liferay.dynamic.data.lists.service.DDLRecordSetService",
		"com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer",
		"com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory",
		"com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer",
		"com.liferay.dynamic.data.mapping.io.DDMFormLayoutJSONDeserializer",
		"com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer",
		"com.liferay.dynamic.data.mapping.io.DDMFormXSDDeserializer",
		"com.liferay.dynamic.data.mapping.registry.DDMFormFieldType",
		"com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderer",
		"com.liferay.dynamic.data.mapping.render.DDMFormFieldRendererRegistry",
		"com.liferay.dynamic.data.mapping.render.DDMFormFieldValueRenderer",
		"com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService",
		"com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService",
		"com.liferay.dynamic.data.mapping.service.DDMStructureLocalService",
		"com.liferay.dynamic.data.mapping.service.DDMStructureService",
		"com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService",
		"com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService",
		"com.liferay.dynamic.data.mapping.service.DDMTemplateService",
		"com.liferay.dynamic.data.mapping.storage.StorageAdapter",
		"com.liferay.dynamic.data.mapping.storage.StorageEngine",
		"com.liferay.dynamic.data.mapping.type.checkbox.CheckboxDDMFormFieldRenderer",
		"com.liferay.dynamic.data.mapping.type.options.OptionsDDMFormFieldRenderer",
		"com.liferay.dynamic.data.mapping.type.radio.RadioDDMFormFieldRenderer",
		"com.liferay.dynamic.data.mapping.type.select.SelectDDMFormFieldRenderer",
		"com.liferay.dynamic.data.mapping.type.text.TextDDMFormFieldRenderer",
		"com.liferay.dynamic.data.mapping.util.DDM",
		"com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter",
		"com.liferay.dynamic.data.mapping.util.DDMXML",
		"com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper",
		"com.liferay.expando.web.upgrade.ExpandoWebUpgrade",
		"com.liferay.exportimport.controller.PortletExportController",
		"com.liferay.exportimport.controller.PortletImportController",
		"com.liferay.flags.page.flags.web.upgrade.PageFlagsWebUpgrade",
		"com.liferay.hello.velocity.web.upgrade.HelloVelocityWebUpgrade",
		"com.liferay.iframe.web.upgrade.IFrameWebUpgrade",
		"com.liferay.invitation.web.upgrade.InvitationWebUpgrade",
		"com.liferay.item.selector.ItemSelector",
		"com.liferay.item.selector.ItemSelectorCriterionHandler",
		"com.liferay.item.selector.ItemSelectorView",
		"com.liferay.item.selector.web.util.ItemSelectorCriterionSerializer",
		"com.liferay.journal.configuration.JournalGroupServiceConfiguration",
		"com.liferay.journal.content.asset.addon.entry.comments.CommentRatingsContentMetadataAssetAddonEntry",
		"com.liferay.journal.content.asset.addon.entry.common.ContentMetadataAssetAddonEntry",
		"com.liferay.journal.content.asset.addon.entry.common.UserToolAssetAddonEntry",
		"com.liferay.journal.content.search.web.upgrade.JournalContentSearchWebUpgrade",
		"com.liferay.journal.content.web.upgrade.JournalContentWebUpgrade",
		"com.liferay.journal.service.JournalArticleImageLocalService",
		"com.liferay.journal.service.JournalArticleLocalService",
		"com.liferay.journal.service.JournalArticleResourceLocalService",
		"com.liferay.journal.service.JournalArticleService",
		"com.liferay.journal.service.JournalContentSearchLocalService",
		"com.liferay.journal.service.JournalFeedLocalService",
		"com.liferay.journal.service.JournalFeedService",
		"com.liferay.journal.service.JournalFolderLocalService",
		"com.liferay.journal.service.JournalFolderService",
		"com.liferay.journal.upgrade.JournalServiceUpgrade",
		"com.liferay.journal.util.JournalConverter",
		"com.liferay.journal.web.upgrade.JournalWebUpgrade",
		"com.liferay.layout.admin.web.upgrade.LayoutAdminWebUpgrade",
		"com.liferay.layout.prototype.web.upgrade.LayoutPrototypeWebUpgrade",
		"com.liferay.layout.set.prototype.web.upgrade.LayoutSetPrototypeWebUpgrade",
		"com.liferay.loan.calculator.web.upgrade.LoanCalculatorWebUpgrade",
		"com.liferay.marketplace.service.AppLocalService",
		"com.liferay.marketplace.service.ModuleLocalService",
		"com.liferay.marketplace.store.web.oauth.util.OAuthManager",
		"com.liferay.mentions.util.MentionsNotifier",
		"com.liferay.mentions.util.MentionsUserFinder",
		"com.liferay.my.account.web.upgrade.MyAccountWebUpgrade",
		"com.liferay.nested.portlets.web.upgrade.NestedPortletWebUpgrade",
		"com.liferay.network.utilities.web.upgrade.NetworkUtilitiesWebUpgrade",
		"com.liferay.password.generator.web.upgrade.PasswordGeneratorWebUpgrade",
		"com.liferay.password.policies.admin.web.upgrade.PasswordPoliciesAdminWebUpgrade",
		"com.liferay.portal.cluster.ClusterChannelFactory",
		"com.liferay.portal.executor.internal.PortalExecutorFactory",
		"com.liferay.portal.expression.ExpressionFactory",
		"com.liferay.portal.instances.web.upgrade.PortalInstancesWebUpgrade",
		"com.liferay.portal.jmx.MBeanRegistry",
		"com.liferay.portal.js.loader.modules.extender.JSLoaderModulesServlet",
		"com.liferay.portal.kernel.atom.AtomCollectionAdapter",
		"com.liferay.portal.kernel.audit.AuditRouter",
		"com.liferay.portal.kernel.cache.MultiVMPool",
		"com.liferay.portal.kernel.cache.PortalCacheManager",
		"com.liferay.portal.kernel.cache.SingleVMPool",
		"com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings",
		"com.liferay.portal.kernel.cluster.ClusterEventListener",
		"com.liferay.portal.kernel.cluster.ClusterExecutor",
		"com.liferay.portal.kernel.cluster.ClusterLink",
		"com.liferay.portal.kernel.cluster.ClusterMasterExecutor",
		"com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener",
		"com.liferay.portal.kernel.comment.CommentManager",
		"com.liferay.portal.kernel.editor.configuration.EditorConfigContributor",
		"com.liferay.portal.kernel.editor.configuration.EditorConfigTransformer",
		"com.liferay.portal.kernel.editor.configuration.EditorOptionsContributor",
		"com.liferay.portal.kernel.events.LifecycleAction",
		"com.liferay.portal.kernel.executor.PortalExecutorConfig",
		"com.liferay.portal.kernel.executor.PortalExecutorManager",
		"com.liferay.portal.kernel.facebook.FacebookConnect",
		"com.liferay.portal.kernel.json.JSONFactory",
		"com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionsManager",
		"com.liferay.portal.kernel.jsonwebservice.JSONWebServiceRegistratorFactory",
		"com.liferay.portal.kernel.lock.LockManager",
		"com.liferay.portal.kernel.messaging.Destination",
		"com.liferay.portal.kernel.messaging.DestinationConfiguration",
		"com.liferay.portal.kernel.messaging.DestinationEventListener",
		"com.liferay.portal.kernel.messaging.DestinationFactory",
		"com.liferay.portal.kernel.messaging.MessageBus",
		"com.liferay.portal.kernel.messaging.MessageBusEventListener",
		"com.liferay.portal.kernel.messaging.MessageListener",
		"com.liferay.portal.kernel.messaging.sender.SingleDestinationMessageSenderFactory",
		"com.liferay.portal.kernel.messaging.sender.SynchronousMessageSender",
		"com.liferay.portal.kernel.mobile.device.DeviceRecognitionProvider",
		"com.liferay.portal.kernel.mobile.device.rulegroup.action.ActionHandler",
		"com.liferay.portal.kernel.mobile.device.rulegroup.rule.RuleHandler",
		"com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle",
		"com.liferay.portal.kernel.monitoring.DataSampleFactory",
		"com.liferay.portal.kernel.monitoring.DataSampleProcessor",
		"com.liferay.portal.kernel.monitoring.MonitoringControl",
		"com.liferay.portal.kernel.monitoring.PortalMonitoringControl",
		"com.liferay.portal.kernel.monitoring.PortletMonitoringControl",
		"com.liferay.portal.kernel.monitoring.ServiceMonitoringControl",
		"com.liferay.portal.kernel.notifications.UserNotificationDefinition",
		"com.liferay.portal.kernel.notifications.UserNotificationHandler",
		"com.liferay.portal.kernel.openid.OpenId",
		"com.liferay.portal.kernel.poller.PollerProcessor",
		"com.liferay.portal.kernel.pop.MessageListener",
		"com.liferay.portal.kernel.portlet.AddPortletProvider",
		"com.liferay.portal.kernel.portlet.BrowsePortletProvider",
		"com.liferay.portal.kernel.portlet.ConfigurationAction",
		"com.liferay.portal.kernel.portlet.DisplayInformationProvider",
		"com.liferay.portal.kernel.portlet.EditPortletProvider",
		"com.liferay.portal.kernel.portlet.FriendlyURLMapper",
		"com.liferay.portal.kernel.portlet.FriendlyURLResolver",
		"com.liferay.portal.kernel.portlet.ManagePortletProvider",
		"com.liferay.portal.kernel.portlet.PortletLayoutListener",
		"com.liferay.portal.kernel.portlet.ViewPortletProvider",
		"com.liferay.portal.kernel.portlet.configuration.PortletConfigurationIconFactory",
		"com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor",
		"com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator",
		"com.liferay.portal.kernel.repository.RepositoryFactory",
		"com.liferay.portal.kernel.repository.registry.RepositoryDefiner",
		"com.liferay.portal.kernel.repository.search.RepositorySearchQueryTermBuilder",
		"com.liferay.portal.kernel.resiliency.spi.cache.SPIPortalCacheManagerConfigurator",
		"com.liferay.portal.kernel.sanitizer.Sanitizer",
		"com.liferay.portal.kernel.scheduler.SchedulerEngine",
		"com.liferay.portal.kernel.scheduler.SchedulerEngineHelper",
		"com.liferay.portal.kernel.scheduler.SchedulerEntry",
		"com.liferay.portal.kernel.scripting.ScriptingExecutor",
		"com.liferay.portal.kernel.search.IndexSearcher",
		"com.liferay.portal.kernel.search.IndexWriter",
		"com.liferay.portal.kernel.search.Indexer",
		"com.liferay.portal.kernel.search.IndexerRegistry",
		"com.liferay.portal.kernel.search.OpenSearch",
		"com.liferay.portal.kernel.search.SearchEngine",
		"com.liferay.portal.kernel.search.SearchEngineConfigurator",
		"com.liferay.portal.kernel.search.SearchPermissionChecker",
		"com.liferay.portal.kernel.search.SearchResultManager",
		"com.liferay.portal.kernel.search.SummaryFactory",
		"com.liferay.portal.kernel.search.filter.FilterTranslator",
		"com.liferay.portal.kernel.search.hits.HitsProcessor",
		"com.liferay.portal.kernel.search.query.QueryPreProcessConfiguration",
		"com.liferay.portal.kernel.search.query.QueryTranslator",
		"com.liferay.portal.kernel.search.result.SearchResultContributor",
		"com.liferay.portal.kernel.search.suggest.QuerySuggester",
		"com.liferay.portal.kernel.search.suggest.SpellCheckIndexWriter",
		"com.liferay.portal.kernel.search.suggest.SuggesterTranslator",
		"com.liferay.portal.kernel.security.access.control.AccessControl",
		"com.liferay.portal.kernel.security.access.control.AccessControlPolicy",
		"com.liferay.portal.kernel.security.auth.AlwaysAllowDoAsUser",
		"com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager",
		"com.liferay.portal.kernel.security.auto.login.AutoLogin",
		"com.liferay.portal.kernel.security.sso.OpenSSO",
		"com.liferay.portal.kernel.security.sso.SSO",
		"com.liferay.portal.kernel.servlet.PortalWebResources",
		"com.liferay.portal.kernel.servlet.URLEncoder",
		"com.liferay.portal.kernel.servlet.taglib.DynamicInclude",
		"com.liferay.portal.kernel.servlet.taglib.TagDynamicIdFactory",
		"com.liferay.portal.kernel.settings.SettingsFactory",
		"com.liferay.portal.kernel.settings.SettingsLocatorHelper",
		"com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration",
		"com.liferay.portal.kernel.settings.definition.SettingsIdMapping",
		"com.liferay.portal.kernel.social.SocialActivityManager",
		"com.liferay.portal.kernel.struts.StrutsAction",
		"com.liferay.portal.kernel.struts.StrutsPortletAction",
		"com.liferay.portal.kernel.template.TemplateHandler",
		"com.liferay.portal.kernel.template.TemplateResourceLoader",
		"com.liferay.portal.kernel.transaction.TransactionLifecycleListener",
		"com.liferay.portal.kernel.trash.TrashHandler",
		"com.liferay.portal.kernel.trash.TrashRendererFactory",
		"com.liferay.portal.kernel.util.InfrastructureUtil",
		"com.liferay.portal.kernel.util.Props",
		"com.liferay.portal.kernel.webdav.WebDAVStorage",
		"com.liferay.portal.kernel.workflow.WorkflowHandler",
		"com.liferay.portal.kernel.xml.SAXReaderUtil",
		"com.liferay.portal.kernel.xmlrpc.Method",
		"com.liferay.portal.lock.service.LockLocalService",
		"com.liferay.portal.messaging.DestinationPrototype",
		"com.liferay.portal.model.LayoutTypeAccessPolicy",
		"com.liferay.portal.model.ModelListener",
		"com.liferay.portal.model.Portlet",
		"com.liferay.portal.model.Release",
		"com.liferay.portal.model.adapter.builder.ModelAdapterBuilder",
		"com.liferay.portal.monitoring.internal.statistics.portal.ServerStatistics",
		"com.liferay.portal.monitoring.internal.statistics.portal.ServerSummaryStatistics",
		"com.liferay.portal.monitoring.internal.statistics.portlet.ActionRequestSummaryStatistics",
		"com.liferay.portal.monitoring.internal.statistics.portlet.EventRequestSummaryStatistics",
		"com.liferay.portal.monitoring.internal.statistics.portlet.RenderRequestSummaryStatistics",
		"com.liferay.portal.monitoring.internal.statistics.portlet.ResourceRequestSummaryStatistics",
		"com.liferay.portal.monitoring.internal.statistics.portlet.ServerStatistics",
		"com.liferay.portal.monitoring.internal.statistics.service.ServerStatistics",
		"com.liferay.portal.scheduler.quartz.internal.QuartzSchedulerEngine",
		"com.liferay.portal.search.IndexerRequestBufferOverflowHandler",
		"com.liferay.portal.search.analysis.KeywordTokenizer",
		"com.liferay.portal.search.elasticsearch.connection.ElasticsearchConnection",
		"com.liferay.portal.search.elasticsearch.connection.ElasticsearchConnectionManager",
		"com.liferay.portal.search.elasticsearch.document.ElasticsearchDocumentFactory",
		"com.liferay.portal.search.elasticsearch.document.ElasticsearchUpdateDocumentCommand",
		"com.liferay.portal.search.elasticsearch.facet.FacetProcessor",
		"com.liferay.portal.search.elasticsearch.filter.BooleanFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.DateRangeTermFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.ExistsFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.GeoBoundingBoxFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.GeoDistanceFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.GeoDistanceRangeFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.GeoPolygonFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.MissingFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.PrefixFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.QueryFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.RangeTermFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.TermFilterTranslator",
		"com.liferay.portal.search.elasticsearch.filter.TermsFilterTranslator",
		"com.liferay.portal.search.elasticsearch.index.IndexFactory",
		"com.liferay.portal.search.elasticsearch.internal.cluster.ClusterSettingsContext",
		"com.liferay.portal.search.elasticsearch.internal.facet.CompositeFacetProcessor",
		"com.liferay.portal.search.elasticsearch.query.BooleanQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.DisMaxQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.FuzzyQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.MatchAllQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.MatchQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.MoreLikeThisQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.MultiMatchQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.NestedQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.StringQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.TermQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.TermRangeQueryTranslator",
		"com.liferay.portal.search.elasticsearch.query.WildcardQueryTranslator",
		"com.liferay.portal.search.elasticsearch.settings.SettingsContributor",
		"com.liferay.portal.search.elasticsearch.suggest.AggregateSuggesterTranslator",
		"com.liferay.portal.search.elasticsearch.suggest.CompletionSuggesterTranslator",
		"com.liferay.portal.search.elasticsearch.suggest.PhraseSuggesterTranslator",
		"com.liferay.portal.search.elasticsearch.suggest.TermSuggesterTranslator",
		"com.liferay.portal.security.auth.AuthFailure",
		"com.liferay.portal.security.auth.AuthToken",
		"com.liferay.portal.security.auth.Authenticator",
		"com.liferay.portal.security.auth.EmailAddressGenerator",
		"com.liferay.portal.security.auth.EmailAddressValidator",
		"com.liferay.portal.security.auth.FullNameGenerator",
		"com.liferay.portal.security.auth.FullNameValidator",
		"com.liferay.portal.security.auth.ScreenNameGenerator",
		"com.liferay.portal.security.auth.ScreenNameValidator",
		"com.liferay.portal.security.exportimport.UserExporter",
		"com.liferay.portal.security.ldap.AttributesTransformer",
		"com.liferay.portal.security.ldap.LDAPSettings",
		"com.liferay.portal.security.ldap.LDAPToPortalConverter",
		"com.liferay.portal.security.ldap.PortalToLDAPConverter",
		"com.liferay.portal.security.membershippolicy.OrganizationMembershipPolicy",
		"com.liferay.portal.security.membershippolicy.RoleMembershipPolicy",
		"com.liferay.portal.security.membershippolicy.SiteMembershipPolicy",
		"com.liferay.portal.security.membershippolicy.UserGroupMembershipPolicy",
		"com.liferay.portal.security.permission.PermissionCheckerFactory",
		"com.liferay.portal.security.permission.PermissionPropagator",
		"com.liferay.portal.security.permission.ResourceActions",
		"com.liferay.portal.security.permission.ResourcePermissionChecker",
		"com.liferay.portal.security.sso.ntlm.NetlogonConnectionManager",
		"com.liferay.portal.security.sso.openid.OpenIdProvider",
		"com.liferay.portal.security.sso.openid.OpenIdProviderRegistry",
		"com.liferay.portal.security.sso.token.events.LogoutProcessor",
		"com.liferay.portal.security.sso.token.security.auth.TokenRetriever",
		"com.liferay.portal.service.ClassNameLocalService",
		"com.liferay.portal.service.CompanyLocalService",
		"com.liferay.portal.service.GroupLocalService",
		"com.liferay.portal.service.LayoutBranchService",
		"com.liferay.portal.service.LayoutLocalService",
		"com.liferay.portal.service.LayoutPrototypeLocalService",
		"com.liferay.portal.service.LayoutRevisionLocalService",
		"com.liferay.portal.service.LayoutSetBranchLocalService",
		"com.liferay.portal.service.LayoutSetBranchService",
		"com.liferay.portal.service.LayoutSetLocalService",
		"com.liferay.portal.service.LayoutSetPrototypeLocalService",
		"com.liferay.portal.service.MembershipRequestLocalService",
		"com.liferay.portal.service.PortletLocalService",
		"com.liferay.portal.service.ReleaseLocalService",
		"com.liferay.portal.service.RepositoryEntryLocalService",
		"com.liferay.portal.service.RepositoryLocalService",
		"com.liferay.portal.service.ResourceActionLocalService",
		"com.liferay.portal.service.ResourceBlockLocalService",
		"com.liferay.portal.service.ResourceLocalService",
		"com.liferay.portal.service.ResourcePermissionLocalService",
		"com.liferay.portal.service.RoleLocalService",
		"com.liferay.portal.service.ServiceWrapper",
		"com.liferay.portal.service.SubscriptionLocalService",
		"com.liferay.portal.service.UserGroupRoleLocalService",
		"com.liferay.portal.service.UserLocalService",
		"com.liferay.portal.service.WorkflowDefinitionLinkLocalService",
		"com.liferay.portal.service.configuration.configurator.ServiceConfigurator",
		"com.liferay.portal.service.impl.LayoutLocalServiceHelper",
		"com.liferay.portal.store.db.DBStore",
		"com.liferay.portal.template.freemarker.FreeMarkerTemplateContextHelper",
		"com.liferay.portal.template.freemarker.FreeMarkerTemplateResourceLoader",
		"com.liferay.portal.template.soy.SoyTemplateContextHelper",
		"com.liferay.portal.template.velocity.VelocityTemplateContextHelper",
		"com.liferay.portal.template.velocity.VelocityTemplateResourceLoader",
		"com.liferay.portal.util.Portal",
		"com.liferay.portal.util.PortalInetSocketAddressEventListener",
		"com.liferay.portal.verify.VerifyProcess",
		"com.liferay.portal.workflow.kaleo.manager.PortalKaleoManager",
		"com.liferay.portlet.ControlPanelEntry",
		"com.liferay.portlet.PortletInstanceFactory",
		"com.liferay.portlet.admin.util.Omniadmin",
		"com.liferay.portlet.asset.model.AssetRendererFactory",
		"com.liferay.portlet.asset.service.AssetEntryLocalService",
		"com.liferay.portlet.asset.util.AssetEntryQueryProcessor",
		"com.liferay.portlet.calendar.service.CalEventLocalService",
		"com.liferay.portlet.configuration.web.upgrade.PortletConfigurationWebUpgrade",
		"com.liferay.portlet.css.web.upgrade.PortletCSSWebUpgrade",
		"com.liferay.portlet.display.template.PortletDisplayTemplate",
		"com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateExportCapability",
		"com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateImportCapability",
		"com.liferay.portlet.documentlibrary.service.DLAppHelperLocalService",
		"com.liferay.portlet.documentlibrary.service.DLAppLocalService",
		"com.liferay.portlet.documentlibrary.service.DLAppService",
		"com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService",
		"com.liferay.portlet.documentlibrary.service.DLFileEntryMetadataLocalService",
		"com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalService",
		"com.liferay.portlet.documentlibrary.service.DLFolderLocalService",
		"com.liferay.portlet.documentlibrary.store.Store",
		"com.liferay.portlet.documentlibrary.store.StoreWrapper",
		"com.liferay.portlet.documentlibrary.util.DLProcessor",
		"com.liferay.portlet.dynamicdatamapping.DDMStructureLinkManager",
		"com.liferay.portlet.dynamicdatamapping.DDMStructureManager",
		"com.liferay.portlet.dynamicdatamapping.DDMTemplateManager",
		"com.liferay.portlet.expando.model.CustomAttributesDisplay",
		"com.liferay.portlet.expando.service.ExpandoColumnLocalService",
		"com.liferay.portlet.expando.service.ExpandoTableLocalService",
		"com.liferay.portlet.expando.service.ExpandoValueLocalService",
		"com.liferay.portlet.exportimport.lar.PortletDataHandler",
		"com.liferay.portlet.exportimport.lar.StagedModelDataHandler",
		"com.liferay.portlet.exportimport.staging.LayoutStaging",
		"com.liferay.portlet.exportimport.staging.Staging",
		"com.liferay.portlet.exportimport.staging.permission.StagingPermission",
		"com.liferay.portlet.exportimport.xstream.XStreamAliasRegistryUtil$XStreamAlias",
		"com.liferay.portlet.exportimport.xstream.XStreamConverter",
		"com.liferay.portlet.messageboards.service.MBBanLocalService",
		"com.liferay.portlet.messageboards.service.MBBanService",
		"com.liferay.portlet.messageboards.service.MBCategoryLocalService",
		"com.liferay.portlet.messageboards.service.MBCategoryService",
		"com.liferay.portlet.messageboards.service.MBDiscussionLocalService",
		"com.liferay.portlet.messageboards.service.MBMessageLocalService",
		"com.liferay.portlet.messageboards.service.MBMessageService",
		"com.liferay.portlet.messageboards.service.MBStatsUserLocalService",
		"com.liferay.portlet.messageboards.service.MBThreadFlagLocalService",
		"com.liferay.portlet.messageboards.service.MBThreadLocalService",
		"com.liferay.portlet.messageboards.service.MBThreadService",
		"com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupInstanceLocalService",
		"com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupLocalService",
		"com.liferay.portlet.ratings.service.RatingsEntryLocalService",
		"com.liferay.portlet.ratings.service.RatingsStatsLocalService",
		"com.liferay.portlet.social.model.SocialActivityInterpreter",
		"com.liferay.portlet.social.model.SocialRequestInterpreter",
		"com.liferay.portlet.social.service.SocialActivityLocalService",
		"com.liferay.portlet.social.service.SocialRequestLocalService",
		"com.liferay.portlet.social.service.SocialRequestService",
		"com.liferay.portlet.trash.service.TrashEntryService",
		"com.liferay.quick.note.web.uprade.QuickNoteWebUpgrade",
		"com.liferay.ratings.page.ratings.web.upgrade.PageRatingsWebUpgrade",
		"com.liferay.roles.admin.web.upgrade.RolesAdminWebUpgrade",
		"com.liferay.rss.web.upgrade.RSSWebUpgrade",
		"com.liferay.search.web.upgrade.SearchWebUpgrade",
		"com.liferay.search.web.util.SearchFacet",
		"com.liferay.service.access.policy.service.SAPEntryLocalService",
		"com.liferay.service.access.policy.service.SAPEntryService",
		"com.liferay.site.admin.web.upgrade.SiteAdminWebUpgrade",
		"com.liferay.site.browser.web.upgrade.SiteBrowserWebUpgrade",
		"com.liferay.site.memberships.web.upgrade.SiteMembershipsWebUpgrade",
		"com.liferay.site.my.sites.web.upgrade.MySitesWebUpgrade",
		"com.liferay.site.navigation.breadcrumb.web.upgrade.BreadcrumbWebUpgrade",
		"com.liferay.site.navigation.directory.web.upgrade.SitesDirectoryWebUpgrade",
		"com.liferay.site.navigation.language.web.upgrade.LanguageWebUpgrade",
		"com.liferay.site.navigation.menu.web.upgrade.NavigationMenuWebUpgrade",
		"com.liferay.site.navigation.site.map.web.upgrade.SiteMapWebUpgrade",
		"com.liferay.site.teams.web.upgrade.SiteTeamsWebUpgrade",
		"com.liferay.social.activities.web.upgrade.SocialActivitiesWebUpgrade",
		"com.liferay.social.activity.web.upgrade.SocialActivityWebUpgrade",
		"com.liferay.social.requests.web.upgrade.SocialRequestsWebUpgrade",
		"com.liferay.translator.web.upgrade.TranslatorWebUpgrade",
		"com.liferay.trash.web.upgrade.TrashWebUpgrade",
		"com.liferay.unit.converter.web.upgrade.UnitConverterWebUpgrade",
		"com.liferay.user.groups.admin.web.exportimport.portlet.preferences.processor.UserGroupsAdminPortletDisplayTemplateExportCapability",
		"com.liferay.user.groups.admin.web.exportimport.portlet.preferences.processor.UserGroupsAdminPortletDisplayTemplateImportCapability",
		"com.liferay.user.groups.admin.web.upgrade.UserGroupsAdminWebUpgrade",
		"com.liferay.users.admin.web.upgrade.UsersAdminWebUpgrade",
		"com.liferay.web.proxy.web.upgrade.WebProxyWebUpgrade",
		"com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
		"com.liferay.wiki.display.context.WikiDisplayContextFactory",
		"com.liferay.wiki.engine.WikiEngine",
		"com.liferay.wiki.importer.WikiImporter",
		"com.liferay.wiki.service.WikiPageLocalService",
		"com.liferay.wiki.service.WikiPageResourceLocalService",
		"com.liferay.wiki.web.display.context.WikiDisplayContextProvider",
		"com.liferay.xsl.content.web.upgrade.XSLContentWebUpgrade",
		"java.net.ContentHandler", "java.util.ResourceBundle",
		"javax.management.DynamicMBean", "javax.management.MBeanServer",
		"javax.portlet.Portlet", "javax.portlet.PreferencesValidator",
		"javax.servlet.Filter", "javax.servlet.Servlet",
		"javax.servlet.ServletContext", "javax.servlet.http.HttpServlet",
		"javax.sql.DataSource", "javax.xml.parsers.SAXParserFactory",
		"org.apache.felix.bundlerepository.RepositoryAdmin",
		"org.apache.felix.cm.PersistenceManager",
		"org.apache.felix.fileinstall.ArtifactInstaller",
		"org.apache.felix.fileinstall.ArtifactTransformer",
		"org.apache.felix.fileinstall.ArtifactUrlTransformer",
		"org.apache.felix.gogo.api.CommandSessionListener",
		"org.apache.felix.service.command.CommandProcessor",
		"org.apache.felix.service.command.Converter",
		"org.apache.felix.webconsole.BrandingPlugin",
		"org.apache.felix.webconsole.WebConsoleSecurityProvider",
		"org.apache.felix.webconsole.bundleinfo.BundleInfoProvider",
		"org.eclipse.equinox.console.commands.CommandsTracker",
		"org.eclipse.equinox.http.servlet.context.ContextPathCustomizer",
		"org.eclipse.osgi.framework.console.CommandProvider",
		"org.eclipse.osgi.framework.console.ConsoleSession",
		"org.eclipse.osgi.service.debug.DebugOptionsListener",
		"org.eclipse.osgi.signedcontent.SignedContentFactory",
		"org.osgi.service.cm.ConfigurationAdmin",
		"org.osgi.service.cm.ConfigurationListener",
		"org.osgi.service.cm.ManagedService",
		"org.osgi.service.cm.ManagedServiceFactory",
		"org.osgi.service.cm.SynchronousConfigurationListener",
		"org.osgi.service.condpermadmin.ConditionalPermissionAdmin",
		"org.osgi.service.event.EventAdmin",
		"org.osgi.service.event.EventHandler",
		"org.osgi.service.http.HttpService",
		"org.osgi.service.http.context.ServletContextHelper",
		"org.osgi.service.http.runtime.HttpServiceRuntime",
		"org.osgi.service.log.LogReaderService",
		"org.osgi.service.log.LogService",
		"org.osgi.service.metatype.MetaTypeProvider",
		"org.osgi.service.metatype.MetaTypeService",
		"org.osgi.service.packageadmin.PackageAdmin",
		"org.osgi.service.permissionadmin.PermissionAdmin",
		"org.osgi.service.provisioning.ProvisioningService",
		"org.osgi.service.startlevel.StartLevel",
		"org.osgi.service.url.URLStreamHandlerService",
		"org.osgi.service.useradmin.UserAdmin",
		"org.springframework.context.ApplicationContext" };
    
    protected void createControlArea( final Composite composite )
    {
        SWTUtil.createLabel( composite, SWT.LEAD,"Package Name:", 1 );

        packageText = SWTUtil.createText( composite, 2 );
        
        packageText.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                	checkAndUpdateElement();
                	op().setPackageName( packageText.getText() );
                }
            }
        );
        
        SWTUtil.createLabel( composite, SWT.LEAD,"Component Name:", 1 );

        componentText = SWTUtil.createText( composite, 2 );
        
        componentText.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                	checkAndUpdateElement();
                	op().setComponentName( componentText.getText() );
                }
            }
        );
    }
    
    
    
	@Override
	protected FormComponentPresentation createModulePresentation(
			FormComponentPart part, SwtPresentation parent, final Composite composite) {
        return new FormComponentPresentation( this, parent, composite )
        {
            @Override
            public void render()
            {
                createControlArea( composite );

                SWTUtil.createSeparator( composite, 3 );

                Label label = new Label( composite, SWT.CHECK );
                label.setText( "Properties:" );
                label.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 4 ) );

                Table table = new Table( composite, SWT.FULL_SELECTION | SWT.BORDER );
                viewer = new TableViewer( table );

                table.setLayoutData( new GridData( GridData.FILL_BOTH ) );
                table.setHeaderVisible( true );

                // createTableColumn( );

                viewer.setContentProvider( new StringArrayListContentProvider() );

                final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 4 );
                tableData.heightHint = 225;
                tableData.widthHint = 400;
                table.setLayoutData( tableData );

                final Button addButton = new Button( composite, SWT.NONE );
                addButton.setText( "Add" );
                addButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                addButton.addSelectionListener( new SelectionListener()
                {

                    @Override
                    public void widgetSelected( SelectionEvent event )
                    {
                        handleAddButtonSelected( composite );
                    }

                    @Override
                    public void widgetDefaultSelected( SelectionEvent event )
                    {
                        // Do nothing
                    }
                } );

                final Button editButton = new Button( composite, SWT.NONE );
                editButton.setText( "Edit" );
                editButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                editButton.addSelectionListener( new SelectionListener()
                {

                    @Override
                    public void widgetSelected( SelectionEvent event )
                    {
                        handleEditButtonSelected( composite );
                    }

                    @Override
                    public void widgetDefaultSelected( SelectionEvent event )
                    {
                        // Do nothing
                    }
                } );
                editButton.setEnabled( false );

                final Button removeButton = new Button( composite, SWT.NONE );
                removeButton.setText( "Remove" );
                removeButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                removeButton.addSelectionListener( new SelectionListener()
                {

                    @Override
                    public void widgetSelected( SelectionEvent event )
                    {
                        handleRemoveButtonSelected( composite );
                    }

                    @Override
                    public void widgetDefaultSelected( SelectionEvent event )
                    {
                        // Do nothing
                    }
                } );
                removeButton.setEnabled( false );

                viewer.addSelectionChangedListener( new ISelectionChangedListener()
                {

                    @Override
                    public void selectionChanged( SelectionChangedEvent event )
                    {
                        ISelection selection = event.getSelection();

                        if( editButton != null )
                        {
                            boolean enabled = ( (IStructuredSelection) selection ).size() == 1;
                            editButton.setEnabled( enabled );
                        }
                        removeButton.setEnabled( !selection.isEmpty() );
                    }

                } );

                if( editButton != null )
                {
                    viewer.addDoubleClickListener( new IDoubleClickListener()
                    {
                        @Override
                        public void doubleClick( DoubleClickEvent event )
                        {
                            handleEditButtonSelected( composite );
                        }
                    } );
                }

                if (columnTitles.length > 1) {
                    for( int i = 0; i < columnTitles.length; i++ )
                    {
                        final TableViewerColumn viewerColumn = new TableViewerColumn( viewer, SWT.NONE );
                        final TableColumn column = viewerColumn.getColumn();
                        column.setText( columnTitles[i] );
                        column.setResizable( true );
                        column.setMoveable( true );
                        viewerColumn.setLabelProvider( new StringArrayListLabelProvider( column, i ) );
                    }

                    table.setHeaderVisible(true);

                    composite.addControlListener( new ControlAdapter()
                    {

                        @Override
                        public void controlResized( ControlEvent e )
                        {
                            Table table = viewer.getTable();
                            TableColumn[] columns = table.getColumns();
                            Point buttonArea = removeButton.computeSize( SWT.DEFAULT, SWT.DEFAULT );
                            Rectangle area = table.getParent().getClientArea();
                            Point preferredSize = viewer.getTable().computeSize( SWT.DEFAULT, SWT.DEFAULT );
                            int width = area.width - 2 * table.getBorderWidth() - buttonArea.x - columns.length * 2;
                            if( preferredSize.y > area.height + table.getHeaderHeight() )
                            {
                                // Subtract the scrollbar width from the total column width
                                // if a vertical scrollbar will be required
                                Point vBarSize = table.getVerticalBar().getSize();
                                width -= vBarSize.x;
                            }
                            Point oldSize = table.getSize();
                            int consumeWidth = 0;
                            for( int i = 0; i < columns.length; i++ )
                            {
                                if( oldSize.x > area.width )
                                {
                                    // table is getting smaller so make the columns
                                    // smaller first and then resize the table to
                                    // match the client area width
                                    consumeWidth = setColumntWidth( width, columns, consumeWidth, i );
                                    table.setSize( area.width - buttonArea.x - columns.length * 2, area.height );
                                }
                                else
                                {
                                    // table is getting bigger so make the table
                                    // bigger first and then make the columns wider
                                    // to match the client area width
                                    table.setSize( area.width - buttonArea.x - columns.length * 2, area.height );
                                    consumeWidth = setColumntWidth( width, columns, consumeWidth, i );
                                }
                            }
                        }

                        private int setColumntWidth( int width, TableColumn[] columns, int consumeWidth, int i )
                        {
                            if( i < columns.length - 1 )
                            {
                                columns[i].setWidth( width / columns.length );
                                consumeWidth += columns[i].getWidth();
                            }
                            else
                            {
                                columns[i].setWidth( width - consumeWidth );
                            }
                            return consumeWidth;
                        }
                    } );
                }
            }
        };
	}
	
    @Override
    protected void checkAndUpdateElement()
    {
        UIUtil.async
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    updateValidation();
                	refreshValidation();
                }
            }
        );
    }
	
    protected void updateValidation()
    {
        updateQualifiedClassname();
    }
    
    protected String qualifiedClassname;
    
	private void updateQualifiedClassname()
    {
        retval = Status.createOkStatus();
        
        int packageNameStatus = IStatus.OK;

        if( !CoreUtil.isNullOrEmpty( packageText.getText() ) )
        {
            this.qualifiedClassname = packageText.getText() + "." + componentText.getText(); //$NON-NLS-1$

            packageNameStatus =
                JavaConventions.validatePackageName(
                    packageText.getText(), CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 ).getSeverity();

            if( packageNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid package name" );
            }
        }

        if( !CoreUtil.isNullOrEmpty( componentText.getText() ) )
        {
            int classNameStatus =
                JavaConventions.validateJavaTypeName(
                    componentText.getText(), CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 ).getSeverity();;

            if( componentText.getText().indexOf( '.' ) != -1 )
            {
                classNameStatus = IStatus.ERROR;
            }

            if( classNameStatus == IStatus.ERROR && packageNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid class name and package name" );
            }
            else if( classNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid class name" );
            }
        }
    }

    
    private class AddStringArrayDialog extends Dialog implements ModifyListener {
        protected String windowTitle;
        protected String[] labelsForTextField;
        protected Text[] texts;
        protected String[] stringArray;
        protected int widthHint = 300;
        protected int numberColumns;
        protected CLabel errorMessageLabel;

        protected void createErrorMessageGroup( Composite parent )
        {
            errorMessageLabel = new CLabel( parent, SWT.LEFT_TO_RIGHT );
            errorMessageLabel.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false, 2, 1 ) );
            errorMessageLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage(
                ISharedImages.IMG_OBJS_ERROR_TSK ) );
            errorMessageLabel.setVisible( false );
        }
        
        /**
         * CMPFieldDialog constructor comment.
         */
        public AddStringArrayDialog(Shell shell, String windowTitle, String[] labelsForTextField, int numberColumns) {
            super(shell);
            this.windowTitle = windowTitle;
            this.labelsForTextField = labelsForTextField;
            this.numberColumns = numberColumns;
        }
        /**
         * CMPFieldDialog constructor comment.
         */
        @Override
        public Control createDialogArea(Composite parent) {

            Composite composite = (Composite) super.createDialogArea(parent);
            getShell().setText(windowTitle);

            GridLayout layout = new GridLayout();
            layout.numColumns = numberColumns;
            composite.setLayout(layout);
            GridData data = new GridData();
            data.verticalAlignment = GridData.FILL;
            data.horizontalAlignment = GridData.FILL;
            data.widthHint = widthHint;
            composite.setLayoutData(data);

            int n = labelsForTextField.length;
            texts = new Text[n];
            for (int i = 0; i < n; i++) {
                texts[i] = createField(composite, i);
            }

            createErrorMessageGroup(composite);
            
            // set focus
            texts[0].setFocus();
            Dialog.applyDialogFont(parent);
            return composite;
        }

        protected void setWidthHint(int hint) {
            this.widthHint = hint;
        }

        protected Text createField(Composite composite, int index) {
            Label label = new Label(composite, SWT.LEFT);
            label.setText(labelsForTextField[index]);
            label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
            final Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
            
            GridData data = new GridData(GridData.FILL_HORIZONTAL);
            data.widthHint = 100;
            text.setLayoutData(data);
            new Label(composite, SWT.NONE);
            return text;
        }

        @Override
        protected Control createContents(Composite parent) {
            Composite composite = (Composite) super.createContents(parent);

            for (int i = 0; i < texts.length; i++) {
                texts[i].addModifyListener(this);
            }

            updateOKButton( false );

            return composite;
        }

        @Override
        protected void okPressed() {
            stringArray = callback.retrieveResultStrings(texts);
            super.okPressed();
        }

        public String[] getStringArray() {
            return stringArray;
        }

        @Override
        public void modifyText(ModifyEvent e) 
        {
        	boolean validate = true;
        	for (Text text : texts) 
        	{
    			String input = text.getText();
    		
    			if ( CoreUtil.isNullOrEmpty( input ) )
    			{
    				errorMessageLabel.setVisible( true );
    				errorMessageLabel.setText( " Property and Value field can not be empty!" );
    				validate = validate && false;
    			}
    			else
    			{
    				validate = validate && !CoreUtil.isNullOrEmpty( input );
    			}
			}
        	
        	if ( validate == true )
			{
        		errorMessageLabel.setVisible( false );
			}
        	
        	updateOKButton( validate );
        }

        private void updateOKButton(final boolean validate) 
        {
            getButton(IDialogConstants.OK_ID).setEnabled(callback.validate(texts) && validate );
        }

    }
	
    protected class AddPropertyOverrideDialog extends AddStringArrayDialog
    {
        protected String[] buttonLabels;

        protected Boolean[] enables;

        protected String[] defaultValues;
        
        protected String selectPropertyDialoTtitle;
        
        protected String selectPropertyDialoMessage;      

		public AddPropertyOverrideDialog(Shell shell, String windowTitle,
				String[] labelsForTextField, String[] buttonLabels,
				Boolean[] enables, String[] defaultValues,
				String selectPropertyDialoTtitle,
				String selectPropertyDialoMessage,
				int numberColumns){

            super( shell, windowTitle, labelsForTextField, numberColumns );

            setShellStyle( getShellStyle() | SWT.RESIZE );

            this.buttonLabels = buttonLabels;

            this.enables = enables;

            this.defaultValues = defaultValues;
            
            this.selectPropertyDialoTtitle = selectPropertyDialoTtitle;
            
            this.selectPropertyDialoMessage = selectPropertyDialoMessage;

            setWidthHint( 450 );
        }

        @Override
        protected Text createField( Composite parent, final int index )
        {
            Label label = new Label( parent, SWT.LEFT );
            label.setText( labelsForTextField[index] );
            label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

            final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );

            GridData data = new GridData( GridData.FILL_HORIZONTAL );
            // data.widthHint = 200;

            text.setLayoutData( data );

            text.setEnabled( enables[index] );
            
            if ( defaultValues[index] != null )
            {
                text.setText( defaultValues[index] );
            }

            if( buttonLabels != null && buttonLabels[index] != null )
            {
                Composite buttonComposite = new Composite( parent, SWT.NONE );

                String[] buttonLbls = buttonLabels[index].split( "," ); //$NON-NLS-1$

                GridLayout gl = new GridLayout( buttonLbls.length, true );
                gl.marginWidth = 0;
                gl.horizontalSpacing = 1;

                buttonComposite.setLayout( gl );

                for( final String lbl : buttonLbls )
                {
                    Button button = new Button( buttonComposite, SWT.PUSH );
                    button.setText( lbl );
                    button.addSelectionListener( new SelectionAdapter()
                    {

                        @Override
                        public void widgetSelected( SelectionEvent e )
                        {
                            handleArrayDialogButtonSelected( index, lbl, text );
                        }

                    } );

                    button.setEnabled( enables[index] );
                }
            }

            return text;
        }

        protected void handleArrayDialogButtonSelected( int index, String label, Text text )
        {
            handleSelectPropertyButton( index, text );
        }

        private void handleSelectPropertyButton( int index, Text text )
        {
            String[] hookProperties = loadProperties();
            
            if ( hookProperties == null )
            {
            	return;
            }

            PropertiesFilteredDialog dialog = new PropertiesFilteredDialog( getParentShell() );
            dialog.setTitle( selectPropertyDialoTtitle );
            dialog.setMessage( selectPropertyDialoMessage );
            dialog.setInput( hookProperties );

            if( dialog.open() == Window.OK )
            {
                Object[] selected = dialog.getResult();

                text.setText( selected[0].toString() );
            }
        }
    }
	
    protected class EditStringArrayDialog extends AddStringArrayDialog {
        protected String[] valuesForTextField;
        /**
         * CMPFieldDialog constructor comment.
         */
        public EditStringArrayDialog(Shell shell, String windowTitle, String[] labelsForTextField, String[] valuesForTextField,
        		int numberColumns){
            super(shell, windowTitle, labelsForTextField, numberColumns);
            this.valuesForTextField = valuesForTextField;

        }
        /**
         * CMPFieldDialog constructor comment.
         */
        @Override
        public Control createDialogArea(Composite parent) {

            Composite composite = (Composite) super.createDialogArea(parent);

            int n = valuesForTextField.length;
            for (int i = 0; i < n; i++) {
                texts[i].setText(valuesForTextField[i]);
            }

            return composite;
        }
    }

    
    public class EditPropertyOverrideDialog extends EditStringArrayDialog
    {
        protected String[] buttonLabels;

        protected Boolean[] enables;

        protected String[] defaultValues;
        
        protected String selectPropertyDialoTtitle;
        
        protected String selectPropertyDialoMessage;

		public EditPropertyOverrideDialog(Shell shell, String windowTitle, String[] labelsForTextField, 
				String[] buttonLabels,String[] valuesForTextField, Boolean[] enables,
				String[] defaultValues,String selectPropertyDialoTtitle, String selectPropertyDialoMessage,int numberColumns )        {

            super( shell, windowTitle, labelsForTextField, valuesForTextField,numberColumns );

            setShellStyle( getShellStyle() | SWT.RESIZE );

            this.buttonLabels = buttonLabels;

            this.enables = enables;

            this.defaultValues = defaultValues;
            
            this.selectPropertyDialoTtitle = selectPropertyDialoTtitle;
            
            this.selectPropertyDialoMessage = selectPropertyDialoMessage;

            setWidthHint( 450 );
        }

        @Override
        protected Text createField( Composite parent, final int index )
        {
            Label label = new Label( parent, SWT.LEFT );
            label.setText( labelsForTextField[index] );
            label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING ) );

            final Text text = new Text( parent, SWT.SINGLE | SWT.BORDER );

            GridData data = new GridData( GridData.FILL_HORIZONTAL );
            // data.widthHint = 200;

            text.setLayoutData( data );

            if ( defaultValues[index] != null )
            {
                text.setText( defaultValues[index] );
            }

            text.setEnabled( enables[index] );

            if( buttonLabels != null && buttonLabels[index] != null )
            {
                Composite buttonComposite = new Composite( parent, SWT.NONE );

                String[] buttonLbls = buttonLabels[index].split( "," ); //$NON-NLS-1$

                GridLayout gl = new GridLayout( buttonLbls.length, true );
                gl.marginWidth = 0;
                gl.horizontalSpacing = 1;

                buttonComposite.setLayout( gl );

                for( final String lbl : buttonLbls )
                {
                    Button button = new Button( buttonComposite, SWT.PUSH );
                    button.setText( lbl );
                    button.addSelectionListener( new SelectionAdapter()
                    {

                        @Override
                        public void widgetSelected( SelectionEvent e )
                        {
                            handleArrayDialogButtonSelected( index, lbl, text );
                        }

                    } );

                    button.setEnabled( enables[index] );
                }
            }

            return text;
        }

        protected void handleArrayDialogButtonSelected( int index, String label, Text text )
        {
            handleSelectPropertyButton( index, text );
        }

        private void handleSelectPropertyButton( int index,Text text )
        {
            String[] hookProperties = loadProperties();
            
            if ( hookProperties == null )
            {
            	return;
            }

            PropertiesFilteredDialog dialog = new PropertiesFilteredDialog( getParentShell() );
            dialog.setTitle( selectPropertyDialoTtitle );
            dialog.setMessage( selectPropertyDialoMessage );
            dialog.setInput( hookProperties );

            if( dialog.open() == Window.OK )
            {
                Object[] selected = dialog.getResult();

                text.setText( selected[0].toString() );
            }
        }        

    }
	
    @SuppressWarnings( { "rawtypes" } )
    public void setInput(List input) {
        viewer.setInput(input);
    }

    @SuppressWarnings( { "rawtypes" } )
    public void editStringArray(String[] oldStringArray, String[] newStringArray)
    {
        if (newStringArray == null)
            return;

        List valueList = doEdit(oldStringArray, newStringArray);

        setInput(valueList);
    }
    
    protected void handleEditButtonSelected(final Composite composite) {
        ISelection s = viewer.getSelection();
        if (!(s instanceof IStructuredSelection))
            return;
        IStructuredSelection selection = (IStructuredSelection) s;
        if (selection.size() != 1)
            return;

        Object selectedObj = selection.getFirstElement();
        String[] valuesForText = (String[]) selectedObj;

        EditPropertyOverrideDialog dialog = getEditPropertyOverrideDialog(composite.getShell(), valuesForText);
        dialog.open();
        String[] stringArray = dialog.getStringArray();
        editStringArray(valuesForText, stringArray);
    }
	
    @SuppressWarnings( { "rawtypes" } )
    public void addStringArray( String[] stringArray )
    {
        if( stringArray == null )
            return;

        List valueList = doAdd(stringArray);

        setInput( valueList );
    }
    
    protected void handleAddButtonSelected(final Composite composite) {
        AddPropertyOverrideDialog dialog = getAddPropertyOverrideDialog(composite.getShell());
        dialog.open();
        String[] stringArray = dialog.getStringArray();
        addStringArray(stringArray);
    }
    
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void removeStringArrays(Collection selectedStringArrays) {
        List valueList = (List) viewer.getInput();
        valueList.removeAll(selectedStringArrays);
        doRemove(selectedStringArrays);
        setInput(valueList);
    }

    
    @SuppressWarnings( "rawtypes" )
    protected void handleRemoveButtonSelected(final Composite composite) {
        ISelection selection = viewer.getSelection();
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
            return;
        List selectedObj = ((IStructuredSelection) selection).toList();
        removeStringArrays(selectedObj);
    }
    
	
    
    @SuppressWarnings( "rawtypes" )
    protected class StringArrayListContentProvider implements IStructuredContentProvider {
        public boolean isDeleted(Object element) {
            return false;
        }

        @Override
        public Object[] getElements(Object element) {
            if (element instanceof List) {
                return ((List) element).toArray();
            }
            return new Object[0];
        }
        @Override
        public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
            //Default nothing
        }
        @Override
        public void dispose() {
            //Default nothing
        }
    }

    protected class StringArrayListLabelProvider extends ColumnLabelProvider implements ITableLabelProvider
    {
        private TableColumn column;
        private int columnIndex;

        public StringArrayListLabelProvider( TableColumn column, int columnIndex )
        {
            super();
            this.column = column;
            this.columnIndex = columnIndex;
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return null;
            }
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            String[] array = (String[]) element;
            return array[columnIndex];
        }

        @Override
        public void update(ViewerCell cell)
        {
            super.update(cell);
            column.pack();
        }

        @Override
        public String getText(Object element) {
            String[] array = (String[]) element;
            return array[columnIndex];
        }

    }
    

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private List<String> doAdd( String[] stringArray )
    {
        List valueList = (List) viewer.getInput();

        if ( stringArray == null )
        {
            return valueList;
        }

        if ( valueList == null )
        {
            valueList = new ArrayList<String>();
        }

        final String keyValue = stringArray[1];

        ElementList<PropertyKey> propertyKeys = op().getPropertyKeys();

        for( PropertyKey propertyKey : propertyKeys )
        {
            String keyValueContent = propertyKey.getKeyValue().content();
            if ( keyValueContent.equals( keyValue ) )
            {
                return valueList;
            }
        }

        PropertyKey propertyKey = propertyKeys.insert();
        propertyKey.setKeyName( stringArray[0] );
        propertyKey.setKeyValue( stringArray[1] );

        valueList.add( stringArray );

        return valueList;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private List<String> doEdit( String[] oldStringArray, String[] newStringArray )
    {
        List valueList = (List) viewer.getInput();

        if ( newStringArray == null )
        {
            return valueList;
        }

        ElementList<PropertyKey> propertyKeys = op().getPropertyKeys();

        final String oldKeyValue = oldStringArray[1];

        for( int i = 0; i < propertyKeys.size(); i++ )
        {
            PropertyKey propertyKey = propertyKeys.get( i );
            String keyValueContent = propertyKey.getKeyValue().content();

            if ( keyValueContent.equals( oldKeyValue ) )
            {
                propertyKeys.remove( i );
                valueList.set(i, newStringArray);
            }
        }

        PropertyKey newPropertyKey = propertyKeys.insert();
        newPropertyKey.setKeyName( newStringArray[0] );
        newPropertyKey.setKeyValue( newStringArray[1] );

        return valueList;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private List<String> doRemove( Collection<String> selectedStringArrays )
    {
        List valueList = (List) viewer.getInput();

        Object[] selectArrays = selectedStringArrays.toArray();

        for( Object selected : selectArrays )
        {
            final String[] removeList = (String[]) (selected);

            final String keyValue = removeList[1];

            ElementList<PropertyKey> propertyKeys = op().getPropertyKeys();

            for( int i = 0; i < propertyKeys.size(); i++ )
            {
                PropertyKey propertyKey = propertyKeys.get( i );
                String keyValueContent = propertyKey.getKeyValue().content();

                if ( keyValueContent.equals( keyValue ) )
                {
                    propertyKeys.remove( i );
                    valueList.removeAll(selectedStringArrays);
                }
            }
        }
        return valueList;
    }
	
}
