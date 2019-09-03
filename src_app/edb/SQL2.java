package edb;

public class SQL {
	
	public static final String createUser ="CREATE TABLE users (user_login varchar(100) NOT NULL, "
			+ "user_password varchar(100) DEFAULT NULL, "
			+ "user_name varchar(200) DEFAULT NULL,"
			+ "user_role varchar(100) DEFAULT NULL,"
			+ "user_login_alt varchar(50) DEFAULT NULL,"
			+ "user_address varchar(255) DEFAULT NULL,"
			+ "user_ip_address varchar(50) DEFAULT NULL,"
			+ "date_registered date DEFAULT NULL,"
			+ "avatar varchar(255) DEFAULT NULL,"
			+ "db_name varchar(50) DEFAULT NULL,"
			+ "PRIMARY KEY (user_login))";
	
	public static final String insertUsers  =  "INSERT INTO users VALUES ('admin','0DPiKuNIrrVmD8IUCuw1hQxNqZc=','Administrator','root','','','','2007-09-01','',''),('anon','YVGPsXFatNaYrKMqeECsey5QfT4=','Webmaster Mode','anon',NULL,NULL,NULL,'2007-09-01',NULL,'')";
	
	public static final String createUserRole = "CREATE TABLE user_role ("
			+ "user_id varchar(50) NOT NULL, "
			+ "role_id varchar(50) NOT NULL, "
			+ "PRIMARY KEY (role_id,user_id))";


	public static final String createUserModuleTemplate = "CREATE TABLE user_module_template ("
			+ "tab_id varchar(100) NOT NULL, "
			+ "user_login varchar(100) NOT NULL, "
			+ "module_id varchar(100) NOT NULL, "
			+ "sequence int, "
			+ "module_custom_title varchar(100) DEFAULT NULL, "
			+ "column_number int, "
			+ "PRIMARY KEY (tab_id,user_login,module_id))";
	
	
	public static final String createUserModule = "CREATE TABLE user_module ("
			+ "tab_id varchar(100) NOT NULL,"
			+ "user_login varchar(100) NOT NULL,"
			+ "module_id varchar(100) NOT NULL,"
			+ "sequence int DEFAULT NULL,"
			+ "module_custom_title varchar(100) DEFAULT NULL,"
			+ "column_number int DEFAULT NULL,"
			+ "PRIMARY KEY (tab_id,user_login,module_id))";
	

	public static final String createUserCss = "CREATE TABLE user_css ("
			+ "user_login varchar(100) NOT NULL, "
			+ "css_name varchar(100) DEFAULT NULL, "
			+ "PRIMARY KEY (user_login))"; 
	
	public static final String createUserActivityLog = "CREATE TABLE user_activity_log ( "
			+ "id varchar(50) NOT NULL, "
			+ "dateTime date DEFAULT NULL, "
			+ "description varchar(255) DEFAULT NULL, "
			+ "module varchar(100) DEFAULT NULL,  "
			+ "remark varchar(5000),  "
			+ "remoteAddress varchar(50) DEFAULT NULL, "
			+ "userId varchar(50) DEFAULT NULL, "
			+ "PRIMARY KEY (id))";

	public static final String createUserTracker = "CREATE TABLE user_tracker ( "
			+ "user_login varchar(50) NOT NULL, "
			+ "log_year int DEFAULT NULL, "
			+ "log_month int DEFAULT NULL, "
			+ "log_day int DEFAULT NULL, "
			+ "module_id varchar(50) NOT NULL, "
			+ "module_class varchar(100) DEFAULT NULL, "
			+ "module_name varchar(100) DEFAULT NULL, "
			+ "time12 varchar(50) DEFAULT NULL, "
			+ "time24 varchar(50) DEFAULT NULL, "
			+ "str_date varchar(50) DEFAULT NULL, "
			+ "remote_add varchar(50) DEFAULT NULL, "
			+ "log_date date NOT NULL, "
			+ "PRIMARY KEY (user_login,module_id,log_date))";
	
	
	public static final String createWebLogger = "CREATE TABLE web_logger ( "
			+ "remote_add varchar(50) DEFAULT NULL, "
			+ "user_name varchar(50) DEFAULT NULL, "
			+ "log_year int DEFAULT NULL, "
			+ "log_month int DEFAULT NULL, "
			+ "log_day int DEFAULT NULL, "
			+ "log_string varchar(255) DEFAULT NULL, "
			+ "log_date date DEFAULT NULL)";
	
	
	public static final String createXmlModule = "CREATE TABLE xml_module ( "
			+ "module_id varchar(100) NOT NULL, "
			+ "xml varchar(200) DEFAULT NULL, "
			+ "xsl varchar(200) DEFAULT NULL, "
			+ "PRIMARY KEY (module_id))";
	
	public static final String createTabUser = "CREATE TABLE tab_user ( "
			+ "tab_id varchar(50) DEFAULT NULL, "
			+ "tab_title varchar(50) DEFAULT NULL, "
			+ "user_login varchar(50) DEFAULT NULL, "
			+ "sequence int DEFAULT NULL, "
			+ "display_type varchar(50) DEFAULT NULL, "
			+ "locked int DEFAULT NULL, "
			+ "hide int DEFAULT NULL)";
	
	public static final String createTabTemplate = "CREATE TABLE tab_template ( "
			+ "tab_id varchar(100) NOT NULL, "
			+ "tab_title varchar(100) DEFAULT NULL, "
			+ "user_login varchar(100) DEFAULT NULL, "
			+ "sequence int DEFAULT NULL, "
			+ "display_type varchar(100) DEFAULT NULL, "
			+ "locked int DEFAULT NULL, "
			+ "PRIMARY KEY (tab_id))";


	public static final String createTabs = "CREATE TABLE tabs ( "
			+ "tab_id varchar(100) NOT NULL, "
			+ "tab_title varchar(100) DEFAULT NULL, "
			+ "user_login varchar(100) DEFAULT NULL, "
			+ "sequence int DEFAULT NULL, "
			+ "display_type varchar(100) DEFAULT NULL, "
			+ "PRIMARY KEY (tab_id))";

	public static final String createPageCss = "CREATE TABLE page_css ( "
			+ "css_title varchar(100) DEFAULT NULL, "
			+ "css_name varchar(100) DEFAULT NULL)";

	
		public static final String createModule = "CREATE TABLE module ( "
			+ "module_id varchar(100) NOT NULL, "
			+ "module_title varchar(100) DEFAULT NULL, "
			+ "module_class varchar(100) DEFAULT NULL, "
			+ "module_group varchar(100) DEFAULT NULL, "
			+ "module_description varchar(200) DEFAULT NULL, "
			+ "PRIMARY KEY (module_id))";
	
	
	public static final String createModuleHtmlContainer = "CREATE TABLE module_htmlcontainer ( "
			+ "module_id varchar(100) NOT NULL, "
			+ "html_url varchar(100) DEFAULT NULL, "
			+ "PRIMARY KEY (module_id))";


	public static final String createAttrModuleData = "CREATE TABLE attr_module_data ( "
			+ "module_id varchar(100) DEFAULT NULL, "
			+ "attribute_name varchar(100) DEFAULT NULL, "
			+ "attribute_value varchar(255) DEFAULT NULL)";

	public static final String createForum = "CREATE TABLE forum ( "
			+ "id varchar(50),"
			+ "parent_id varchar(50) NOT NULL, "
			+ "category_id varchar(50) NOT NULL, "
			+ "member_id varchar(50) NOT NULL, "
			+ "posted_date date DEFAULT NULL, "
			+ "title varchar(255) DEFAULT NULL, "
			+ "description varchar(255) DEFAULT NULL, "
			+ "content varchar(5000) DEFAULT NULL, "
			+ "is_parent int DEFAULT NULL, "
			+ "is_delete int  DEFAULT NULL, "
			+ "message_text varchar(5000), "
			+ "subject_id varchar(50) DEFAULT NULL, "
			+ "classroom_id varchar(50) DEFAULT NULL, "
			+ "rate int, "
			+ "PRIMARY KEY (id,parent_id,category_id,member_id))";
	
	
	public static final String createForumAttachment = "CREATE TABLE forum_attachment ( "
			+ "forum_id varchar(50) NOT NULL, "
			+ "file_name varchar(255) DEFAULT NULL, "
			+ "directory varchar(255) DEFAULT NULL, "
			+ "PRIMARY KEY (forum_id))";


	public static final String createMemberSubject = "CREATE TABLE member_subject ( "
			+ "member_id varchar(50) NOT NULL, "
			+ "subject_id varchar(50) NOT NULL, "
			+ "role varchar(50) DEFAULT NULL, "
			+ "status varchar(50) DEFAULT NULL, "
			+ "date_apply date DEFAULT NULL, "
			+ "date_enroll date DEFAULT NULL, "
			+ "module_id varchar(50) NOT NULL, "
			+ "expiry_date date DEFAULT NULL, "
			+ "PRIMARY KEY (member_id,subject_id,module_id))";

	public static final String createRole = "CREATE TABLE role ( "
			+ "name varchar(100) NOT NULL, "
			+ "description varchar(255) DEFAULT NULL,"
			+ "PRIMARY KEY (name))";
	
	public static final String createRoleModule = "CREATE TABLE role_module ( "
			+ "module_id varchar(100) NOT NULL, "
			+ "user_role varchar(100) NOT NULL, "
			+ "PRIMARY KEY (module_id,user_role))";
	
	
	public static final String insertModule = "INSERT INTO module VALUES "
			+ "('lebah_app_users.UserAdministratorModule','User Manager','lebah.app.users.UserAdministratorModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_app_UpdateUserModule','Update User','lebah.app.UpdateUserModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_app_RegisterNewModule','Register Module','lebah.app.RegisterNewModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_app_RegisterUserModule','Register User','lebah.app.RegisterUserModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_app_RoleEditorModule','User Modules Access Privilleges','lebah.app.RoleEditorModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_app_RoleModule','Roles Manager','lebah.app.RoleModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_listeners_SessionListenerModule','Sessions Listener','lebah.listeners.SessionListenerModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_module_theme_PageStyleManagerModule','Themes Manager','lebah.module.theme.PageStyleManagerModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_util_RepositoryModule','Repository','lebah.util.RepositoryModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_portal_PrepareUserModule','Role Pages Template','lebah.portal.PrepareUserModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_portal_RenameGroupModule','Rename Modules Group','lebah.portal.RenameGroupModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_general_AppFileManagerModule','File Manager','lebah.general.AppFileManagerModule','PORTAL ADMINISTRATION','none'),"
			+ "('lebah_portal_db_DbInfoModule','Db Info','lebah.portal.db.DbInfoModule','PORTAL ADMINISTRATION',''),"
			+ "('lebah_app_ModuleEditor','List Modules','lebah.app.ModuleEditor','PORTAL ADMINISTRATION',''),"
			+ "('secondary_role','Users Secondary Roles','lebah.portal.SecondaryUserRoleModule','PORTAL ADMINISTRATION',''),"
			+ "('AnnouncementAdminRoleManager','Announcement Admin Roles','portal.module.AnnouncementAdminModule','PORTAL ADMINISTRATION',''),"
			+ "('user_administration','User List','lebah.app.users.UserAdministratorModule','PORTAL ADMINISTRATION',''),"
			+ "('lebah.app.UserListModule','User List','lebah.app.UserListModule','PORTAL ADMINISTRATION','')";

	public static final String insertRoleModule = "INSERT INTO role_module VALUES ('lebah_app_EmptyModule','root'),"
			+ "('lebah_general_EmailEnquiryModule','root'),('lebah_app_UserLogModule','root'),('lebah_app_UserLogStatModule','root'),"
			+ "('lebah_log_LogCountryModule','root'),('lebah_log_LogIPCountModule','root'),('lebah_log_UserLogStatModule','root'),"
			+ "('lebah_app_users_UserAdministratorModule','root'),('lebah_app_UpdateUserModule','root'),('lebah_app_RegisterNewModule','root'),"
			+ "('lebah_app_RegisterUserModule','root'),('lebah_app_RoleEditorModule','root'),('lebah_app_RoleModule','root'),"
			+ "('lebah_listeners_SessionListenerModule','root'),('lebah_module_theme_PageStyleManagerModule','root'),"
			+ "('lebah_util_RepositoryModule','root'),('lebah_portal_PrepareUserModule','root'),('lebah_portal_RenameGroupModule','root'),"
			+ "('lebah_general_AppFileManagerModule','root'),('lebah_app_UpdatePageStyleModule','root'),('lebah_mail_MailModule','root'),"
			+ "('lebah_portal_users_RegisterModule','root'),('lebah_util_CSSFileManagerModule','root'),('lebah_util_FileManagerModule','root'),"
			+ "('lebah_util_FilesRepositoryModule','root'),('lebah_prototype_LORepositoryModule','root'),"
			+ "('lebah_portal_mobile_ListRegisteredModule','root'),('lebah_portal_mobile_LoginModule','root'),"
			+ "('lebah_portal_mobile_LogoutModule','root'),('lebah_portal_mobile_SetupMobileModule','root'),"
			+ "('lebah_portal_db_DbInfoModule','root'),('lebah_app_ModuleEditor','root'),('lebah_app_content_ArticleModule','root'),"
			+ "('lebah_app_shout_ShoutModule','root'),('lebah_app_ForumModule','root'),('lebah_guestbook_GuestBookModule','root'),"
			+ "('lebah_app_PortalLoginModule','root'),('lebah_planner_PlannerModule','root'),('lebah_planner_CalendarModule','root'),"
			+ "('lebah_repository_ImageGalleryModule','root'),('lebah_repository_RepositoryModule','root'),"
			+ "('lebah.util.FCKEditorModule','root'),('lebah_app_MessageModule','root'),('lebah.app.shout.ShoutBoxModule','root'),"
			+ "('lebah_app_AssignmentModule','root'),('lebah_app_shout.ShoutModule','root'),('lebah.util.HtmlEditorModule','root'),"
			+ "('lebah.portal.XModule','root'),('lebah.app.UserListModule','root'),('lebah_app_UpdateUserProfileModule','root')";
}
