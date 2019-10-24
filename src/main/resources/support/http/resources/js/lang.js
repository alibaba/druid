$.namespace("druid.lang");
druid.lang = function() {
	var LANG_EN = 0;
	var LANG_CN = 1;
	var lang = {
		'Index' : ['Index','首页'],
		'DataSource' : ['DataSource', '数据源'],
		'SQL' : ['SQL','SQL监控'],
		'Wall' : ['Wall' , 'SQL防火墙'],
		'WebApp' : ['WebApp' , 'Web应用'],
		'WebURI' : ['WebURI' , 'URI监控'],
		'Web Session' : ['Web Session' , 'Session监控'],
		'Spring' : ['Spring' , 'Spring监控'],
		'JSON API' : ['JSON API' , 'JSON API'],
		'ResetAll' : ['Reset All' , '重置'],
		'LogAndReset' : ['Log And Reset' , '记录日志并重置'],

		'StatIndex' : ['Stat Index', '统计索引'],
		'ViewJSONAPI' : ['View JSON API','查看JSON API'],
		'Version' : ['Version' , '版本'],
		'Drivers' : ['Drivers' , '驱动'],
		'ResetEnable' : ['ResetEnable' , '是否允许重置'],
		'ResetCount' : ['ResetCount' , '重置次数'],
		'JavaVersion' : ['JavaVersion' , 'Java版本'],
		'JavaVMName' : ['JavaVMName' , 'JVM名称'],
		'JavaClassPath' : ['JavaClassPath' , 'classpath路径'],
		'StartTime' : ['StartTime' , '启动时间'],
		
		'DataSourceStatList' : ['DataSourceStat List' , '数据源列表'],
		'UserName' : ['UserName', '用户名'],
		'URL' : ['URL', '连接地址'],
		'DbType' : ['DbType', '数据库类型'],
		'DriverClassName' : ['DriverClassName', '驱动类名'],
		'FilterClassNames' : ['FilterClassNames', 'filter类名'],
		'TestOnBorrow' : ['TestOnBorrow', '获取连接时检测'],
		'TestWhileIdle' : ['TestWhileIdle', '空闲时检测'],
		'TestOnReturn' : ['TestOnReturn', '连接放回连接池时检测'],
		'InitialSize' : ['InitialSize', '初始化连接大小'],
		'MinIdle' : ['MinIdle', '最小空闲连接数'],
		'MaxActive' : ['MaxActive', '最大连接数'],
		'QueryTimeout' : ['QueryTimeout', '查询超时时间'],
		'TransactionQueryTimeout' : ['TransactionQueryTimeout', '事务查询超时时间'],//
		'LoginTimeout' : ['LoginTimeout', '登录超时时间'],//
		'ValidConnectionCheckerClassName' : ['ValidConnectionCheckerClassName', '连接有效性检查类名'],
		'ExceptionSorterClassName' : ['ExceptionSorterClassName', 'ExceptionSorter类名'],//
		'DefaultAutoCommit' : ['DefaultAutoCommit', '默认autocommit设置'],
		'DefaultReadOnly' : ['DefaultReadOnly', '默认只读设置'],
		'DefaultTransactionIsolation' : ['DefaultTransactionIsolation', '默认事务隔离'],//
		'NotEmptyWaitCount' : ['NotEmptyWaitCount', '累计总次数'],//
		'NotEmptyWaitMillis' : ['NotEmptyWaitMillis', '等待总时长'],//
		'WaitThreadCount' : ['WaitThreadCount', '等待线程数量'],
		'StartTransactionCount' : ['StartTransactionCount', '事务启动数'],//
		'TransactionHistogram' : ['TransactionHistogram', '事务时间分布'],//
		'PoolingCount' : ['PoolingCount', '池中连接数'],//
		'PoolingPeak' : ['PoolingPeak', '池中连接数峰值'],//
		'PoolingPeakTime' : ['PoolingPeakTime', '池中连接数峰值时间'],////
		'ActiveCount' : ['ActiveCount', '活跃连接数'],
		'ActivePeak' : ['ActivePeak', '活跃连接数峰值'],//
		'ActivePeakTime' : ['ActivePeakTime', '活跃连接数峰值时间'],
		'LogicConnectCount' : ['LogicConnectCount', '逻辑连接打开次数'],
		'LogicCloseCount' : ['LogicCloseCount', '逻辑连接关闭次数'],
		'LogicConnectErrorCount' : ['LogicConnectErrorCount', '逻辑连接错误次数'],
		'PhysicalConnectCount' : ['PhysicalConnectCount', '物理连接打开次数'],
		'PhysicalCloseCount' : ['PhysicalCloseCount', '物理关闭数量'],
		'PhysicalConnectErrorCount' : ['PhysicalConnectErrorCount', '物理连接错误次数'],
        'DiscardCount' : ['DiscardCount', '校验失败废弃连接数'],
		'ExecuteCount' : ['ExecuteCount (Total)', '执行数(总共)'],
		'ErrorCount' : ['ErrorCount', '错误数'],
		'CommitCount' : ['CommitCount', '提交数'],
		'RollbackCount' : ['RollbackCount', '回滚数'],
		'PSCacheAccessCount' : ['PSCacheAccessCount', 'PSCache访问次数'],
		'PSCacheHitCount' : ['PSCacheHitCount', 'PSCache命中次数'],
		'PSCacheMissCount' : ['PSCacheMissCount', 'PSCache不命中次数'],//
		'ConnectionHoldTimeHistogram' : ['ConnectionHoldTimeHistogram', '连接持有时间分布'],
		'ClobOpenCount' : ['ClobOpenCount', 'Clob打开次数'],
		'BlobOpenCount' : ['BlobOpenCount', 'Blob打开次数'],
		'KeepAliveCheckCount' : ['KeepAliveCheckCount', 'KeepAlive检测次数'],
		'ActiveConnectionStackTrace' : ['ActiveConnection StackTrace', '活跃连接堆栈查看'],
		'PollingConnectionInfo' : ['PollingConnection Info', '连接池中连接信息'],
		'SQLList' : ['SQL List', 'sql列表'],
				
		'UserNameDesc' : ['Specify the username used when creating a new connection.', '指定建立连接时使用的用户名'],
		'URLDesc' : ['The JDBC driver connection URL', 'JDBC连接字符串'],
		'DbTypeDesc' : ['database type', '数据库类型'],
		'DriverClassNameDesc' : ['The fully qualifed name of the JDBC driver class', 'JDBC驱动的类名'],
		'FilterClassNamesDesc' : ['All the fully qualifed name of the filter classes', 'filter的类名'],
		'TestOnBorrowDesc' : ['	Test or not when borrow a connection', '是否在获得连接后检测其可用性'],
		'TestWhileIdleDesc' : ['Test or not when a connection is idle for a while', '是否在连接空闲一段时间后检测其可用性'],
		'TestOnReturnDesc' : ['Test or not when return a connection', '是否在连接放回连接池后检测其可用性'],
		'InitialSizeDesc' : ['The size of datasource connections to create when initial a datasource', '连接池建立时创建的初始化连接数'],
		'MinIdleDesc' : ['The minimum number of connections a pool should hold. ', '连接池中最小的活跃连接数'],
		'MaxActiveDesc' : ['The maximum number of connections for a pool', '连接池中最大的活跃连接数'],
		'QueryTimeoutDesc' : ['', '查询超时时间'],
		'TransactionQueryTimeoutDesc' : ['', '事务查询超时时间'],
		'LoginTimeoutDesc' : ['', ''],///
		'ValidConnectionCheckerClassNameDesc' : ['', ''],
		'ExceptionSorterClassNameDesc' : ['', ''],
		'DefaultAutoCommitDesc' : ['', ''],
		'DefaultReadOnlyDesc' : ['', ''],
		'DefaultTransactionIsolationDesc' : ['', ''],
		'NotEmptyWaitCountDesc' : ['Total times for wait to get a connection', '获取连接时累计等待多少次'],//
		'NotEmptyWaitMillisDesc' : ['Total millis for wait to get a connection', '获取连接时累计等待多长时间'],
		'WaitThreadCountDesc' : ['The current waiting thread count', '当前等待获取连接的线程数'],
		'StartTransactionCountDesc' : ['The count of start transaction', '事务开始的个数'],
		'TransactionHistogramDesc' : ['The histogram values of transaction time, [0-1 ms, 1-10 ms, 10-100 ms, 100-1 s, 1-10 s, 10-100 s, >100 s]', '事务运行时间分布，分布区间为[0-1 ms, 1-10 ms, 10-100 ms, 100-1 s, 1-10 s, 10-100 s, >100 s]'],
		'PoolingCountDesc' : ['The current usefull connection count', '当前连接池中的数目'],//
		'PoolingPeakDesc' : ['The usefull connection peak count', '连接池中数目的峰值'],
		'PoolingPeakTimeDesc' : ['The usefull connection peak time', '连接池数目峰值出现的时间'],
		'ActiveCountDesc' : ['The current active connection count', '当前连接池中活跃连接数'],
		'ActivePeakDesc' : ['The current active connection peak count', '连接池中活跃连接数峰值'],
		'ActivePeakTimeDesc' : ['The active connection peak time', '活跃连接池峰值出现的时间'],
		'LogicConnectCountDesc' : ['Total connect times from datasource', '产生的逻辑连接建立总数'],
		'LogicCloseCountDesc' : ['Total close connect times from datasource', '产生的逻辑连接关闭总数'],
		'LogicConnectErrorCountDesc' : ['Total connect error times', '产生的逻辑连接出错总数'],
		'RecycleErrorCount' : ['Logic Connection Recycle Count', '逻辑连接回收重用次数'],
		'PhysicalConnectCountDesc' : ['Create physical connnection count', '产生的物理连接建立总数'],
		'PhysicalCloseCountDesc' : ['Close physical connnection count', '产生的物理关闭总数'],
        'DiscardCountDesc' : ['Discard connection count with validate fail', '校验连接失败丢弃连接次数'],
		'PhysicalConnectErrorCountDesc' : ['Total physical connect error times', '产生的物理连接失败总数'],
		'ExecuteCountDesc' : ['', ''],
		'ErrorCountDesc' : ['', ''],
		'CommitCountDesc' : ['', '事务提交次数'],
		'RollbackCountDesc' : ['', '事务回滚次数'],
		'PSCacheAccessCountDesc' : ['PerpareStatement access count', 'PSCache访问总数'],
		'PSCacheHitCountDesc' : ['PerpareStatement hit count', 'PSCache命中次数'],
		'PSCacheMissCountDesc' : ['PerpareStatement miss count', 'PSCache不命中次数'],//
		'PreparedStatementOpenCount' : ['Real PreparedStatement Open Count', '真实PreparedStatement打开次数'],//
		'PreparedStatementClosedCount' : ['Real PreparedStatement Closed Count', '真实PreparedStatement关闭次数'],//
		'ConnectionHoldTimeHistogramDesc' : ['The histogram values of connection hold time, [0-1 ms, 1-10 ms, 10-100 ms, 100ms-1s, 1-10 s, 10-100 s, 100-1000 s, >1000 s]', '连接持有时间分布，分布区间为[0-1 ms, 1-10 ms, 10-100 ms, 100ms-1s, 1-10 s, 10-100 s, 100-1000 s, >1000 s]'],
		'ClobOpenCountDesc' : ['', 'Clob打开数'],
		'BlobOpenCountDesc' : ['', 'Blob打开数'],
		/**spring-detail*/
		'Class' : ['Class', 'Class'],
		'Method' : ['Method', 'Method'],
		'ExecuteErrorCount' : ['ExecuteErrorCount', '执行出错数'],
		'ExecuteTimeMillis' : ['ExecuteTimeMillis', '执行时间'],
		'RunningCount' : ['RunningCount', '执行中'],
		'ConcurrentMax' : ['ConcurrentMax', '最大并发'],
		'JdbcExecuteCount' : ['JdbcExecuteCount', 'Jdbc执行数'],
		'JdbcExecuteErrorCount' : ['JdbcExecErrorCount', 'Jdbc出错数'],
		'JdbcExecuteTimeMillis' : ['JdbcExecTimeMillis', 'Jdbc时间'],
		'JdbcCommitCount' : ['CommitCount', '事务提交数'],
		'JdbcRollbackCount' : ['RollbackCount', '事务回滚数'],
		'JdbcFetchRowCount' : ['FetchRowCount', '读取行数'],
		'JdbcUpdateCount' : ['UpdateCount', '更新行数'],
		'JdbcPoolConnectionOpenCount' : ['JdbcPoolConnectionOpenCount', '连接池获取连接次数'],
		'JdbcPoolConnectionCloseCount' : ['JdbcPoolConnectionCloseCount', '连接池关闭连接次数'],
		'JdbcResultSetOpenCount' : ['JdbcResultSetOpenCount', 'ResultSet打开次数'],
		'JdbcResultSetCloseCount' : ['JdbcResultSetCloseCount', 'ResultSet关闭次数'],
		/**sql-detail*/
		'ParseView' : ['ParseView', '解析信息'],//
		'Tables' : [' Tables', '表'],
		'Fields' : ['Fields', '字段'],
		'Coditions' : ['Coditions', '条件'],
		'Relationships' : ['Relationships', '关联'],
		'OrderByColumns' : ['OrderByColumns', '排序字段'],
		'LastSlowView' : ['LastSlowView', '最后慢查询'],
		'MaxTimespan' : ['MaxTimespan', '最慢'],
		'MaxTimespanOccurTime' : ['MaxTimespanOccurTime', '最慢发生时间'],
		'LastSlowParameters' : ['LastSlowParameters', '最后慢查询参数'],
		'LastErrorView' : ['LastErrorView', '最后错误视图'],
		'LastErrorMessage' : ['LastErrorMessage', '最后错误信息'],
		'LastErrorClass' : ['LastErrorClass', '最后错误类'],
		'LastErrorTime' : ['LastErrorTime', '最后错误时间'],
		'LastErrorStackTrace' : ['LastErrorStackTrace', '最后错误堆栈'],
		'OtherView' : ['OtherView', '其他信息'],
		'BatchSizeMax' : ['BatchSizeMax', '批处理最大值'],////
		'BatchSizeTotal' : ['BatchSizeTotal', '批处理总数'],//
		'ReaderOpenCount' : ['ReaderOpenCount', 'reader打开次数'],//
		'InputStreamOpenCount' : ['InputStreamOpenCount', 'inputstream打开次数'],
		'ReadStringLength' : ['ReadStringLength', '读取字符串长度'],//
		'ReadBytesLength' : ['ReadBytesLength', '读取字节长度'],//
		
		'ExecHisto':['ExecHisto','执行时间分布'],
		'ExecRsHisto':['ExecRsHisto','执行+RS时分布'],
		'FetchRowHisto':['FetchRowHisto','读取行分布'],
		'UpdateHisto':['UpdateHisto','更新行分布'],
		'InTransactionCount' : ['Txn','事务执行'],
		'MaxTimespanDesc' : ['Execute Time Millis Max','最慢的执行耗时'],
		'InTransactionCountDesc' : ['Execute In Transaction Count','在事务中运行的次数'],
		
		'count1ms':['count of 0-1 ms','0-1毫秒次数'],
		'count10ms':['count of 1-10 ms','1-10毫秒次数'],
		'count100ms':['count of 10-100 ms','10-100毫秒次数'],
		'count1s':['count of 100ms-1s','100-1000毫秒次数'],
		'count10s':['count of 1-10 s','1-10秒次数'],
		'count100s':['count of 10-100 s','10-100秒次数'],
		'count1000s':['count of 100-1000 s','100-1000秒次数'],
		'countBg1000s':['count of >1000 s','大于1000秒次数'],
		
		'fetch0':['count of 0 FetchRow','读取行数为0'],
		'fetch9':['count of 1-9 FetchRow','读取行数1-9'],
		'fetch99':['count of 10-99 FetchRow','读取行数10-99'],
		'fetch999':['count of 100-999 FetchRow','读取行数100-999'],
		'fetch9999':['count of 1000-9999 FetchRow','读取行数1000-9999'],
		'fetch99999':['count of >9999 FetchRow','读取行数大于9999'],
		
		'update0':['count of 0 UpdateCount','更新行数为0'],
		'update9':['count of 1-9 UpdateCount','更新行数1-9'],
		'update99':['count of 10-99 UpdateCount','更新行数10-99'],
		'update999':['count of 100-999 UpdateCount','更新行数100-999'],
		'update9999':['count of 1000-9999 UpdateCount','更新行数1000-9999'],
		'update99999':['count of >9999 UpdateCount','更新行数大于9999'],
		/**wall*/
		'CheckCount':['CheckCount', '检查次数'],
		'HardCheckCount':['HardCheckCount', '硬检查次数'],
		'ViolationCount':['ViolationCount', '非法次数'],
		'BlackListHitCount':['BlackListHitCount', '黑名单命中次数'],
		'BlackListSize':['BlackListSize', '黑名单长度'],
		'WhiteListHitCount':['WhiteListHitCount', '白名单命中次数'],
		'WhiteListSize':['WhiteListSize', '白名单长度'],
		'SyntaxErrrorCount':['SyntaxErrrorCount', '语法错误次数'],
		'TableName':['TableName', '表名'],
		'TableNumber':['Number','序号'],
		
		'Sample':['Sample','样本'],
		'ExecuteCount':['ExecuteCount','执行数'],
		'FetchRowCount':['FetchRowCount','读取行数'],
		'SQLUpdateCount':['UpdateCount','更新行数'],
		
		'SelectCount':['SelectCount', 'Select数'],
		'SelectIntoCount':['SelectIntoCount', 'SelectInto数'],
		'InsertCount':['InsertCount', 'Insert数'],
		'UpdateCount':['UpdateCount', 'Update数'],
		'DeleteCount':['DeleteCount', 'Delete数'],
		'TruncateCount':['TruncateCount', 'Truncate数'],
		'CreateCount':['CreateCount', 'Create数'],
		'AlterCount':['AlterCount', 'Alter数'],
		'DropCount':['DropCount', 'Drop数'],
		'ReplaceCount':['ReplaceCount', 'Replace数'],
		'DeleteDataCount':['DeleteDataCount', '删除数据行数'],
		'UpdateDataCount':['UpdateDataCount', '更新数据行数'],
		'FetchRowCount':['FetchRowCount', '读取行数'],
		'WallStat':['Wall Stat', '防御统计'],
		'TableStat':['Table Stat', '表访问统计'],
		'FunctionStat':['Function Stat', '函数调用统计'],
		'SQLStatWhiteList':['SQL Stat - White List', 'SQL防御统计 - 白名单'],
		'SQLStatBlackList':['SQL Stat - Black List', 'SQL防御统计 - 黑名单'],
		/**session-detail*/
		'PrincipalOnly':['Principal Only', 'Principal过滤'],
		'SESSIONID':['SESSIONID', 'SESSIONID'],
		'UserAgent':['UserAgent', 'UserAgent'],
		'Principal':['Principal', 'Principal'],
		'CreateTime':['CreateTime', '创建时间'],
		'LastAccessTime':['LastAccessTime', '最后访问时间'],
		'RemoteAddress':['RemoteAddress', '访问ip地址'],
		'RequestCount':['RequestCount', '请求次数'],
		'RequestTimeMillisTotal':['RequestTimeMillisTotal', '总共请求时间'],
		'RequestInterval':['RequestInterval', '请求间隔'],
		/**weburi-detail*/
		'RefreshPeriod':['Refresh Period', '刷新时间'],
		'SuspendRefresh':['Suspend Refresh', '暂停刷新'],
		'RequestTimeMillis':['RequestTimeMillisTotal', '请求时间（和）'],
		'RequestTimeMillisMax':['RequestTimeMillisMax', '请求最慢（单次）'],
		'RequestTimeMillisMaxOccurTime':['RequestTimeMillisMaxOccurTime', '请求最慢发生时间'],
		'JdbcExecutePeak':['JdbcExecutePeak', 'jdbc执行峰值'],
		'JdbcFetchRowPeak':['JdbcFetchRowPeak', 'jdbc查询取回行数峰值'],
		'JdbcUpdatePeak':['JdbcUpdatePeak', 'jdbc更新峰值'],
		'Histogram':['Histogram','区间分布']
		
	};
	
	var COOKIE_LANG_NAME = "cookie_lang";
	
	function log(str) {
		if (typeof (console) != 'undefined' && typeof(console.log) != 'undefined') {
			console.log(str);
		} else {
			$('body').append('<input type="hidden" value="' + str + " />");
		}
	}
	
	function setCookie(name,value,expires,path,domain,secure)
	{
		var expDays = expires*24*60*60*1000;
		var expDate = new Date();
		expDate.setTime(expDate.getTime()+expDays);
		var expString = ((expires==null) ? "": (";expires="+expDate.toGMTString()));
		var pathString = ((path==null) ? "": (";path="+path));
		var domainString = ((domain==null) ? "": (";domain="+domain));
		var secureString = ((secure==true) ? ";secure": "");
		document.cookie = name + "="+ escape(value) + expString + pathString + domainString + secureString;
	}

	function getCookie(name)
	{
		var result = null;
		var myCookie = document.cookie + ";";
		var searchName = name + "=";
		var startOfCookie = myCookie.indexOf(searchName);
		var endOfCookie;
		if (startOfCookie != -1)
		{
			startOfCookie += searchName.length;
			endOfCookie = myCookie.indexOf(";",startOfCookie);
			result = unescape(myCookie.substring(startOfCookie,endOfCookie));
		}
		return result;
	}
	
	function setText($obj) {
		var key = $obj.attr('langKey');
		if (typeof(lang[key]) != 'undefined') {
			var text = lang[key][druid.lang.langNow];
			$obj.text(lang[key][druid.lang.langNow]);
		} else {
			log('key [' + key + '] not found');
		}
	}
	function setTitle($obj) {
		var key = $obj.attr('langKey');
		if (typeof(lang[key]) != 'undefined') {
			var title = lang[key][druid.lang.langNow];
			$obj.attr('title', title);
		} else {
			log('key [' + key + '] not found');
		}
	}
	
	return {
		langNow : LANG_CN,
		EVENT_LOAD_FINISHED : 'loadFinished',
		init : function(langNow) {
			if (typeof(langNow) != 'undefined') {
				this.setLangType(langNow);
			} else {
				var langInCookie = getCookie(COOKIE_LANG_NAME);
				if (langInCookie == LANG_CN || langInCookie == LANG_EN) {
					this.setLangType(langInCookie);
				}
			}
			$(document).on(this.EVENT_LOAD_FINISHED, '.lang', function() {
				log('load lang');
				setText($(this));
			});
			$(document).on(this.EVENT_LOAD_FINISHED, '.langTitle', function() {
				log('load title');
				setTitle($(this));
			});
			this.trigger();
			
			$(document).on('click','.langSelector',function() {
				var langSelected = $(this).attr('langNow');
				druid.lang.setLangType(langSelected);
				druid.lang.trigger();
				return false;
			});
		},
		setLangType : function (langNow) {
			this.langNow = langNow;
			setCookie(COOKIE_LANG_NAME,langNow,30,'/');
		},
		getLangType : function () {
			return this.langNow;
		},
		show : function($parent) {
			var $obj;
			var $objTitle;
			if ($parent) {
				$obj = $parent.find('.lang');
				$objTitle = $parent.find('.langTitle');
			} else {
				$obj = $('.lang');
				$objTitle = $('.langTitle');
			}
			$obj.each(function() {
				setText($(this));
			});
			$objTitle.each(function() {
				setTitle($(this));
			});
		},
		trigger : function() {
			log('to load lang now');
			$('.lang').trigger(this.EVENT_LOAD_FINISHED);//触发语言显示事件
			$('.langTitle').trigger(this.EVENT_LOAD_FINISHED);//触发语言显示事件
		}
	}
}();
