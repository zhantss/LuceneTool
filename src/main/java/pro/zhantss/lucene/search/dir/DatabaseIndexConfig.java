package pro.zhantss.lucene.search.dir;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import pro.zhantss.lucene.store.database.config.DB2Config;
import pro.zhantss.lucene.store.database.config.DatabaseConfig;
import pro.zhantss.lucene.store.database.config.H2Config;
import pro.zhantss.lucene.store.database.config.HSQLConfig;
import pro.zhantss.lucene.store.database.config.InterbaseConfig;
import pro.zhantss.lucene.store.database.config.MySQLConfig;
import pro.zhantss.lucene.store.database.config.OracleConfig;
import pro.zhantss.lucene.store.database.config.PostgreSQLConfig;
import pro.zhantss.lucene.store.database.config.SQLServerConfig;
import pro.zhantss.lucene.store.database.config.SybaseConfig;
import pro.zhantss.lucene.store.database.datasource.TransactionAwareDataSourceProxy;

import pro.zhantss.lucene.search.DatabaseType;

public class DatabaseIndexConfig extends IndexConfig {
	
	private String driver;
	
	private String url;
	
	private String user;
	
	private String indexTableName;
	
	private DataSource control;
	
	private DatabaseConfig dc;

	public DatabaseIndexConfig(DataSource control, DatabaseType dt, String indexTableName) throws SQLException {
		super();
		if (control == null) {
			return;
		}
		Connection connection = control.getConnection();
		DatabaseMetaData dmd = connection.getMetaData();
		this.driver = dmd.getDriverName();
		this.user = dmd.getUserName();
		this.url = dmd.getURL();
		connection.close();
		
		TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(control);
		DatabaseConfig dc = null;
		switch (dt) {
			case MySQL:		dc = new MySQLConfig();		break;
			case Oracle:		dc = new OracleConfig();	break;
			case SQLServer:	dc = new SQLServerConfig();	break;
			case Db2:			dc = new DB2Config();		break;
			case H2:			dc = new H2Config();		break;
			case HSql:			dc = new HSQLConfig();		break;
			case Interbase:	dc = new InterbaseConfig();	break;
			case PostGresql:	dc = new PostgreSQLConfig();break;
			case Sybase:		dc = new SybaseConfig();	break;
			default:			dc = new OracleConfig();	break;
		}
		setLocation(DataLocation.DATABASE);
		this.control = proxy;
		this.dc = dc;
		this.indexTableName = indexTableName;
	}
	
	@Override
	public String serialCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(getLocation().toString());
		if (dc != null) {
			builder.append(dc.toString());
		}
		if (control != null) {
			builder.append(driver);
			builder.append(url);
			builder.append(user);
		}
		if (indexTableName != null) {
			builder.append(indexTableName);
		}
		return builder.toString();
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public DataSource getControl() {
		return control;
	}

	public void setControl(DataSource control) {
		this.control = control;
	}

	public DatabaseConfig getDc() {
		return dc;
	}

	public void setDc(DatabaseConfig dc) {
		this.dc = dc;
	}
	
	public String getIndexTableName() {
		return indexTableName;
	}
	
	public void setIndexTableName(String indexTableName) {
		this.indexTableName = indexTableName;
	}
	
}
