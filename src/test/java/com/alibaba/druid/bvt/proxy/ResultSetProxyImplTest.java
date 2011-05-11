package com.alibaba.druid.bvt.proxy;

import java.io.Reader;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;

public class ResultSetProxyImplTest extends TestCase {
	String sql = "SELECT * FROM PATROL";

	public void test_resultset() throws Exception {

		MockDriver driver = new MockDriver();
		DataSourceProxyConfig config = new DataSourceProxyConfig();
		config.setUrl("");
		DataSourceProxyImpl dataSource = new DataSourceProxyImpl(driver, config);

		{
			StatFilter filter = new StatFilter();
			filter.init(dataSource);
			config.getFilters().add(filter);
		}
		{
			Log4jFilter filter = new Log4jFilter();
			filter.init(dataSource);
			config.getFilters().add(filter);
		}

		Connection conn = dataSource.connect(null);
		
		conn.setClientInfo("name", null);

		Statement stmt = conn.createStatement();
		ResultSetProxy rs = (ResultSetProxy) stmt.executeQuery(sql);

		rs.insertRow();
		rs.refreshRow();
		rs.moveToInsertRow();
		rs.moveToCurrentRow();

		rs.getArray(1);
		rs.updateRef(1, null);
		rs.updateArray(1, null);
		rs.updateRowId(1, null);
		rs.updateNString(1, null);
		rs.updateNClob(1, (NClob) null);
		rs.updateNClob(1, (Reader) null);
		rs.updateNClob(1, (Reader) null, 0);
		rs.updateSQLXML(1, null);
		rs.updateNCharacterStream(1, null);
		rs.updateNCharacterStream(1, null, 0);

		rs.getArray("1");
		rs.updateRef("1", null);
		rs.updateArray("1", null);
		rs.updateRowId("1", null);
		rs.updateNString("1", null);
		rs.updateNClob("1", (NClob) null);
		rs.updateNClob("1", (Reader) null);
		rs.updateNClob("1", (Reader) null, 0);
		rs.updateSQLXML("1", null);
		rs.updateNCharacterStream("1", null);
		rs.updateNCharacterStream("1", null, 0);
	}

}
