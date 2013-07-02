$.namespace("druid.lang");
druid.lang = function() {
	var LANG_EN = 0;
	var LANG_CN = 1;
	var lang = {
		'Index' : ['Index','首页'],
		'DataSource' : ['DataSource', '数据源'],
		'SQL' : ['SQL','SQL监控'],
		'Wall' : ['Wall' , '安全监控'],
		'WebApp' : ['WebApp' , 'web应用'],
		'WebURI' : ['WebURI' , 'URI监控'],
		'Web Session' : ['Web Session' , 'session监控'],
		'Spring' : ['Spring' , 'spring监控'],
		'JSON API' : ['JSON API' , 'json数据'],
		'ResetAll' : ['Reset All' , '重置'],
		
		'StatIndex' : ['Stat Index', '配置信息'],
		'ViewJSONAPI' : ['View JSON API','查看json数据'],
		'Version' : ['Version' , '版本'],
		'Drivers' : ['Drivers' , '驱动'],
		'ResetEnable' : ['ResetEnable' , '是否允许重置'],
		'ResetCount' : ['ResetCount' , '重置次数'],
		'JavaVersion' : ['JavaVersion' , 'java版本'],
		'JavaVMName' : ['JavaVMName' , 'jvm名称'],
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
		'NotEmptyWaitCount' : ['NotEmptyWaitCount', '等待次数'],//
		'NotEmptyWaitMillis' : ['NotEmptyWaitMillis', '等待最大时长'],//
		'WaitThreadCount' : ['WaitThreadCount', '等待线程个数'],
		'StartTransactionCount' : ['StartTransactionCount', '事务开始个数'],//
		'TransactionHistogram' : ['TransactionHistogram', '事务时间分布'],//
		'PoolingCount' : ['PoolingCount', '连接池个数'],//
		'PoolingPeak' : ['PoolingPeak', '连接池峰值'],//
		'PoolingPeakTime' : ['PoolingPeakTime', '连接池峰值时间'],////
		'ActiveCount' : ['ActiveCount', '活跃个数'],
		'ActivePeak' : ['ActivePeak', '活跃个数峰值'],//
		'ActivePeakTime' : ['ActivePeakTime', '活跃个数峰值时间'],
		'LogicConnectCount' : ['LogicConnectCount', '逻辑连接打开次数'],
		'LogicCloseCount' : ['LogicCloseCount', '逻辑连接关闭次数'],
		'LogicConnectErrorCount' : ['LogicConnectErrorCount', '逻辑连接错误次数'],
		'PhysicalConnectCount' : ['PhysicalConnectCount', '物理连接打开次数'],
		'PhysicalCloseCount' : ['PhysicalCloseCount', '物理关闭个数'],
		'PhysicalConnectErrorCount' : ['PhysicalConnectErrorCount', '物理连接错误次数'],
		'ExecuteCount' : ['ExecuteCount', '执行个数'],
		'ErrorCount' : ['ErrorCount', '出错个数'],
		'CommitCount' : ['CommitCount', '提交个数'],
		'RollbackCount' : ['RollbackCount', '回滚个数'],
		'PSCacheAccessCount' : ['PSCacheAccessCount', 'PSCache访问次数'],
		'PSCacheHitCount' : ['PSCacheHitCount', 'PSCache命中次数'],
		'PSCacheMissCount' : ['PSCacheMissCount', 'PSCache丢失次数'],//
		'ConnectionHoldTimeHistogram' : ['ConnectionHoldTimeHistogram', '连接持有时间分布'],
		'ClobOpenCount' : ['ClobOpenCount', 'Clob打开次数'],
		'BlobOpenCount' : ['BlobOpenCount', 'Blob打开次数'],
		
		'UserNameDesc' : ['Specify the username used when creating a new connection.', '指定建立连接是使用的用户名'],
		'URLDesc' : ['The JDBC driver connection URL', 'jdbc连接字符串'],
		'DbTypeDesc' : ['database type', '数据库类型'],
		'DriverClassNameDesc' : ['The fully qualifed name of the JDBC driver class', 'jdbc驱动的类名'],
		'FilterClassNamesDesc' : ['All the fully qualifed name of the filter classes', 'filter的类名'],
		'TestOnBorrowDesc' : ['	Test or not when borrow a connection', '获得连接后是否检测其可用性'],
		'TestWhileIdleDesc' : ['Test or not when a connection is idle for a while', '连接空闲时是否检测其可用性'],
		'TestOnReturnDesc' : ['Test or not when return a connection', '连接放回连接池后是否检测其可用性'],
		'InitialSizeDesc' : ['The size of datasource connections to create when initial a datasource', '连接池建立时创建的初始化连接的数目'],
		'MinIdleDesc' : ['The minimum number of connections a pool should hold. ', '连接池中最小的活跃连接数目'],
		'MaxActiveDesc' : ['The maximum number of connections for a pool', '连接池中最大的活跃连接数'],
		'QueryTimeoutDesc' : ['', '查询超时时间'],
		'TransactionQueryTimeoutDesc' : ['', '事务查询超时时间'],
		'LoginTimeoutDesc' : ['', ''],///
		'ValidConnectionCheckerClassNameDesc' : ['', ''],
		'ExceptionSorterClassNameDesc' : ['', ''],
		'DefaultAutoCommitDesc' : ['', ''],
		'DefaultReadOnlyDesc' : ['', ''],
		'DefaultTransactionIsolationDesc' : ['', ''],
		'NotEmptyWaitCountDesc' : ['Total times for wait to get a connection', '获取连接时最多等待多少次'],//
		'NotEmptyWaitMillisDesc' : ['Total millins for wait to get a connection', '获取连接时最多等待多长时间'],
		'WaitThreadCountDesc' : ['The current waiting thread count', '当前等待获取连接的线程数'],
		'StartTransactionCountDesc' : ['The count of start transaction', '事务开始的个数'],
		'TransactionHistogramDesc' : ['The histogram values of transaction time, [0-10 ms, 10-100 ms, 100-1 s, 1-10 s, 10-100 s, >100 s]', '事务运行时间分布，分布区间为[0-10 ms, 10-100 ms, 100-1 s, 1-10 s, 10-100 s, >100 s]'],
		'PoolingCountDesc' : ['The current usefull connection count', '当前连接池中的数目'],//
		'PoolingPeakDesc' : ['The usefull connection peak count', '连接池中数目的峰值'],
		'PoolingPeakTimeDesc' : ['The usefull connection peak time', '连接池数目峰值出现的时间'],
		'ActiveCountDesc' : ['The current active connection count', '当前连接池中活跃连接数'],
		'ActivePeakDesc' : ['The current active connection peak count', '连接池中活跃连接数峰值'],
		'ActivePeakTimeDesc' : ['The active connection peak time', '活跃连接池峰值出现的时间'],
		'LogicConnectCountDesc' : ['Total connect times from datasource', '产生的逻辑连接建立总数'],
		'LogicCloseCountDesc' : ['Total close connect times from datasource', '产生的逻辑连接关闭总数'],
		'LogicConnectErrorCountDesc' : ['Total connect error times', '产生的逻辑连接出错总数'],
		'PhysicalConnectCountDesc' : ['Create physical connnection count', '产生的物理连接建立总数'],
		'PhysicalCloseCountDesc' : ['Close physical connnection count', '产生的物理关闭总数'],
		'PhysicalConnectErrorCountDesc' : ['Total physical connect error times', '产生的物理连接失败总数'],
		'ExecuteCountDesc' : ['', ''],
		'ErrorCountDesc' : ['', ''],
		'CommitCountDesc' : ['', ''],
		'RollbackCountDesc' : ['', ''],
		'PSCacheAccessCountDesc' : ['PerpareStatement access count', 'PSCache访问总数'],
		'PSCacheHitCountDesc' : ['PerpareStatement hit count', 'PSCache命中次数'],
		'PSCacheMissCountDesc' : ['PerpareStatement miss count', 'PSCache丢失次数'],//
		'ConnectionHoldTimeHistogramDesc' : ['The histogram values of connection hold time, [0-1 ms, 1-10 ms, 10-100 ms, 100ms-1s, 1-10 s, 10-100 s, 100-1000 s, >1000 s]', '连接持有时间分布，分布区间为[0-1 ms, 1-10 ms, 10-100 ms, 100ms-1s, 1-10 s, 10-100 s, 100-1000 s, >1000 s]'],
		'ClobOpenCountDesc' : ['', ''],
		'BlobOpenCountDesc' : ['', ''],
		/**spring-detail*/
		'Class' : ['Class', 'Class'],
		'Method' : ['Method', 'Method'],
//		'ExecuteCount' : ['ExecuteCount', ''],
		'ExecuteErrorCount' : ['ExecuteErrorCount', '执行出错个数'],
		'ExecuteTimeMillis' : ['ExecuteTimeMillis', '执行时间'],
		'RunningCount' : ['RunningCount', '正运行次数'],
		'ConcurrentMax' : ['ConcurrentMax', '最大并发'],
		'JdbcExecuteCount' : ['JdbcExecuteCount', 'jdbc执行次数'],
		'JdbcExecuteErrorCount' : ['JdbcExecuteErrorCount', 'jdbc执行出错次数'],
		'JdbcExecuteTimeMillis' : ['JdbcExecuteTimeMillis', 'jdbc执行时间'],
		'JdbcCommitCount' : ['JdbcCommitCount', 'jdbc提交次数'],
		'JdbcRollbackCount' : ['JdbcRollbackCount', 'jdbc回滚次数'],
		'JdbcFetchRowCount' : ['JdbcFetchRowCount', 'jdbc查询次数'],
		'JdbcUpdateCount' : ['JdbcUpdateCount', 'jdbc更新次数'],
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
		'MaxTimespan' : ['MaxTimespan', '最大时长'],
		'MaxTimespanOccurTime' : ['MaxTimespanOccurTime', '最大时长出现时间'],
		'LastSlowParameters' : ['LastSlowParameters', '最后慢查询参数'],
		'LastErrorView' : ['LastErrorView', '最后错误'],
		'LastErrorMessage' : ['LastErrorMessage', '最后错误信息'],
		'LastErrorClass' : ['LastErrorClass', '最后错误类'],
		'LastErrorTime' : ['LastErrorTime', '最后错误时间'],
		'LastErrorStackTrace' : ['LastErrorStackTrace', '最后错误堆栈'],
		'OtherView' : ['OtherView', '其他信息'],
		'BatchSizeMax' : ['BatchSizeMax', '批处理最大长度'],////
		'BatchSizeTotal' : ['BatchSizeTotal', '批处理总共长度'],//
		// 'BlobOpenCount' : ['BlobOpenCount', ''],
		// 'ClobOpenCount' : ['ClobOpenCount', ''],
		'ReaderOpenCount' : ['ReaderOpenCount', 'reader打开次数'],//
		'InputStreamOpenCount' : ['InputStreamOpenCount', 'inputstream打开次数'],
		'ReadStringLength' : ['ReadStringLength', '读取字符串长度'],//
		'ReadBytesLength' : ['ReadBytesLength', '读取字节长度'],//
		
		'ExecHisto':['ExecHisto','执行时间分布'],
		'ExecRsHisto':['ExecRsHisto','执行和rs对象时间分布'],
		'FetchRowHisto':['FetchRowHisto','查询影响的行数'],
		'UpdateHisto':['UpdateHisto','更新影响的行数'],
		
		'count1ms':['count of 0-1 ms','0-1ms内的次数'],
		'count10ms':['count of 1-10 ms','1-10ms内的次数'],
		'count100ms':['count of 10-100 ms','10-100ms内的次数'],
		'count1s':['count of 100ms-1s','100-1000ms内的次数'],
		'count10s':['count of 1-10 s','1-10s内的次数'],
		'count100s':['count of 10-100 s','10-100s内的次数'],
		'count1000s':['count of 100-1000 s','100-1000s内的次数'],
		'countBg1000s':['count of >1000 s','大于1000s内的次数'],
		
		'fetch0':['count of 0 FetchRow','查询行数为0'],
		'fetch9':['count of 1-9 FetchRow','查询行数1-9之间'],
		'fetch99':['count of 10-99 FetchRow','查询行数10-99之间'],
		'fetch999':['count of 100-999 FetchRow','查询行数100-999之间'],
		'fetch9999':['count of 1000-9999 FetchRow','查询行数1000-9999之间'],
		'fetch99999':['count of >9999 FetchRow','查询行数大于9999'],
		
		'update0':['count of 0 UpdateCount','更新行数为0'],
		'update9':['count of 1-9 UpdateCount','更新行数1-9之间'],
		'update99':['count of 10-99 UpdateCount','更新行数10-99之间'],
		'update999':['count of 100-999 UpdateCount','更新行数100-999之间'],
		'update9999':['count of 1000-9999 UpdateCount','更新行数1000-9999之间'],
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
		
		'SelectCount':['SelectCount', 'select次数'],
		'InsertCount':['InsertCount', 'insert次数'],
		'UpdateCount':['UpdateCount', 'update次数'],
		'DeleteCount':['DeleteCount', 'delete次数'],
		'TruncateCount':['TruncateCount', 'truncate次数'],
		'CreateCount':['CreateCount', 'create次数'],
		'AlterCount':['AlterCount', 'alter次数'],
		'DropCount':['DropCount', 'drop次数'],
		'ReplaceCount':['ReplaceCount', 'relace次数'],
		'DeleteDataCount':['DeleteDataCount', '删除数据次数'],
		'UpdateDataCount':['UpdateDataCount', '更新数据次数'],
		'FetchRowCount':['FetchRowCount', '查询次数'],
		/**session-detail*/
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
//		'RequestCount':['RequestCount', ''],
		'RequestTimeMillis':['RequestTimeMillis', '请求时间'],
//		'LastAccessTime':['LastAccessTime', ''],
//		'RunningCount':['RunningCount', ''],
//		'ConcurrentMax':['ConcurrentMax', ''],
//		'JdbcExecuteCount':['JdbcExecuteCount', ''],
//		'JdbcExecuteErrorCount':['JdbcExecuteErrorCount', ''],
		'JdbcExecutePeak':['JdbcExecutePeak', 'jdbc执行峰值'],
//		'JdbcExecuteTimeMillis':['JdbcExecuteTimeMillis', ''],
//		'JdbcCommitCount':['JdbcCommitCount', ''],
//		'JdbcRollbackCount':['JdbcRollbackCount', ''],
//		'JdbcFetchRowCount':['JdbcFetchRowCount', ''],
		'JdbcFetchRowPeak':['JdbcFetchRowPeak', 'jdbc查询峰值'],
//		'JdbcUpdateCount':['JdbcUpdateCount', ''],
		'JdbcUpdatePeak':['JdbcUpdatePeak', 'jdbc更新峰值']
//		'JdbcPoolConnectionOpenCount':['JdbcPoolConnectionOpenCount', ''],
//		'JdbcPoolConnectionCloseCount':['JdbcPoolConnectionCloseCount', ''],
//		'JdbcResultSetOpenCount':['JdbcResultSetOpenCount', ''],
//		'JdbcResultSetCloseCount':['JdbcResultSetCloseCount', ''],
		
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
			log('没有找到key为[' + key + ']的说明');
		}
	}
	function setTitle($obj) {
		var key = $obj.attr('langKey');
		if (typeof(lang[key]) != 'undefined') {
			var title = lang[key][druid.lang.langNow];
			$obj.attr('title', title);
		} else {
			log('没有找到key为[' + key + ']的说明');
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