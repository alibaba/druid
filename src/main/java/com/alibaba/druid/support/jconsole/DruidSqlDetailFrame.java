package com.alibaba.druid.support.jconsole;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.management.MBeanServerConnection;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.alibaba.druid.support.jconsole.model.DruidTableCellRenderer;
import com.alibaba.druid.support.jconsole.model.DruidTableModel;
import com.alibaba.druid.support.jconsole.util.TableDataProcessor;
import com.alibaba.druid.support.jconsole.util.TableDataProcessor.ColumnData;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * sql语句的详细信息
 * 
 * 调用服务地址：/sql-{id}.json
 * 返回信息：
 * <pre>
 * {
    "ResultCode": 1,
    "Content": {
            "ExecuteCount": 15,
            "ReadStringLength": 0,
            "LastSlowParameters": null,
            "BlobOpenCount": 0,
            "InputStreamOpenCount": 0,
            "DbType": "mysql",
            "DataSource": null,
            "RunningCount": 0,
            "parsedRelationships": "[]",
            "ConcurrentMax": 1,
            "LastErrorClass": null,
            "MaxTimespan": 125,
            "InTransactionCount": 0,
            "ID": 3,
            "parsedFields": "[]",
            "EffectedRowCount": 0,
            "BatchSizeTotal": 0,
            "URL": null,
            "ExecuteAndResultSetHoldTime": 0,
            "LastErrorMessage": null,
            "FetchRowCountHistogram": [
                0,
                0,
                0,
                0,
                0,
                0
            ],
            "FetchRowCountMax": 0,
            "Histogram": [
                0,
                0,
                13,
                2,
                0,
                0,
                0,
                0
            ],
            "parsedOrderbycolumns": "[]",
            "ErrorCount": 0,
            "SQL": "DROP TABLE t_big",
            "ClobOpenCount": 0,
            "LastTime": "2012-09-14 21:35:10",
            "File": null,
            "LastErrorStackTrace": null,
            "LastError": null,
            "EffectedRowCountHistogram": [
                15,
                0,
                0,
                0,
                0,
                0
            ],
            "TotalTime": 742,
            "formattedSql": "DROP TABLE t_big",
            "Name": null,
            "MaxTimespanOccurTime": "2012/09/14 09:35:00:758",
            "parsedTable": "{t_big=Drop}",
            "BatchSizeMax": 0,
            "ReadBytesLength": 0,
            "EffectedRowCountMax": 0,
            "FetchRowCount": 0,
            "parsedConditions": "[]",
            "ResultSetHoldTime": 0,
            "ReaderOpenCount": 0,
            "ExecuteAndResultHoldTimeHistogram": [
                0,
                0,
                13,
                2,
                0,
                0,
                0,
                0
            ],
            "LastErrorTime": null
        }
    }
 * </pre>
 * */
public class DruidSqlDetailFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String BASE_URL = "/sql";
    private String id;
    private MBeanServerConnection conn;
    private static final ArrayList<String> PARESE_TITLE_LIST = new ArrayList<String>(){

        private static final long serialVersionUID = 1L;

        {
            add("parsedTable");
            add("parsedFields");
            add("parsedConditions");
            add("parsedRelationships");
            add("parsedOrderbycolumns");

        }
    };
    private static final ArrayList<String> LAST_SLOW_TITLE_LIST= new ArrayList<String>(){

        private static final long serialVersionUID = 1L;

        {
            add("MaxTimespan");
            add("MaxTimespanOccurTime");
            add("LastSlowParameters");
        }
    };
    private static final ArrayList<String> LAST_ERROR_TITLE_LIST= new ArrayList<String>(){

        private static final long serialVersionUID = 1L;

        {
            add("LastErrorMessage");
            add("LastErrorClass");
            add("LastErrorTime");
            add("LastErrorStackTrace");
        }
    };
    private static final ArrayList<String> OTHER_ERROR_TITLE_LIST= new ArrayList<String>(){

        private static final long serialVersionUID = 1L;

        {
            add("BatchSizeMax");
            add("BatchSizeTotal");
            add("BlobOpenCount");
            add("ClobOpenCount");
            add("ReaderOpenCount");
            add("InputStreamOpenCount");
            add("ReadStringLength");
            add("ReadBytesLength");
        }
    };
    private ArrayList<LinkedHashMap<String, Object>> parseData;
    private ArrayList<LinkedHashMap<String, Object>> lastSlowData;
    private ArrayList<LinkedHashMap<String, Object>> lastErrorData;
    private ArrayList<LinkedHashMap<String, Object>> otherData;
    private int maxListLen;
    private String formatSql;
    private String sql;
    
    private static final int WIDTH = 620;
    private static final int HEIGHT = 520;
    private final static Log    LOG                 = LogFactory.getLog(DruidSqlDetailFrame.class);
    
    public DruidSqlDetailFrame(String id, MBeanServerConnection conn) {
        this.id = id;
        this.conn = conn;
        getMaxListLen();
        init();
        start();
    }
    
    private void getMaxListLen() {
        maxListLen = PARESE_TITLE_LIST.size();
        int slowLen = LAST_SLOW_TITLE_LIST.size();
        int errLen = LAST_ERROR_TITLE_LIST.size();
        int otherLen = OTHER_ERROR_TITLE_LIST.size();
        if (maxListLen < slowLen) {
            maxListLen = slowLen;
        }
        if (maxListLen < errLen) {
            maxListLen = errLen;
        }
        if (maxListLen < otherLen) {
            maxListLen = otherLen;
        }
    }
    
    private void init() {
        String url = BASE_URL + "-" + id + ".json";
        
        try {
            ArrayList<LinkedHashMap<String, Object>> data = TableDataProcessor.parseData(TableDataProcessor.getData(url,conn));
            if (data != null) {
                LinkedHashMap<String, Object> contentEle = data.get(0);
                formatSql = (String)contentEle.remove("formattedSql");
                int parseLen = PARESE_TITLE_LIST.size();
                int slowLen = LAST_SLOW_TITLE_LIST.size();
                int errLen = LAST_ERROR_TITLE_LIST.size();
                int otherLen = OTHER_ERROR_TITLE_LIST.size();
                LinkedHashMap<String, Object> parseDataEle = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> slowDataEle = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> errDataEle = new LinkedHashMap<String, Object>();
                LinkedHashMap<String, Object> otherDataEle = new LinkedHashMap<String, Object>();
                parseData = new ArrayList<LinkedHashMap<String, Object>>(1);
                lastSlowData = new ArrayList<LinkedHashMap<String, Object>>(1);
                lastErrorData = new ArrayList<LinkedHashMap<String, Object>>(1);
                otherData = new ArrayList<LinkedHashMap<String, Object>>(1);
                for (Iterator<Entry<String, Object>> it = contentEle.entrySet().iterator(); it.hasNext();) {
                    Entry<String, Object> entry = it.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    for(int i=0;i<maxListLen;i++) {
                        if (i < parseLen && key.equals(PARESE_TITLE_LIST.get(i))) {
                            parseDataEle.put(key, value);
                        } else if (i < slowLen && key.equals(LAST_SLOW_TITLE_LIST.get(i))) {
                            slowDataEle.put(key, value); 
                        } else if (i < errLen && key.equals(LAST_ERROR_TITLE_LIST.get(i))) {
                            errDataEle.put(key, value);
                        } else if (i < otherLen && key.equals(OTHER_ERROR_TITLE_LIST.get(i))) {
                            otherDataEle.put(key, value);
                        }
                    }
                }
                
                parseData.add(parseDataEle);
                lastSlowData.add(slowDataEle);
                lastErrorData.add(errDataEle);
                otherData.add(otherDataEle);
            } else {
                LOG.warn("错误的json格式");
            }
            
        } catch (Exception e) {
            LOG.warn("获取数据时异常",e);
        }
    }
    
    private void addTable(JPanel contentPanel,String title,ArrayList<LinkedHashMap<String, Object>> data) {
        final JPanel content1 = new JPanel();
        content1.setLayout(new BorderLayout());        
        content1.setBorder((TitledBorder)BorderFactory.createTitledBorder(title));
        contentPanel.add(content1);
        
        ColumnData colData = TableDataProcessor.row2col(data);
        JTable table = new JTable();
        DruidTableModel tableModel = new DruidTableModel(colData.getDatas());
        table.setModel(tableModel);
        
        TableColumn col = table.getColumnModel().getColumn(0);
        col.setCellRenderer(new DruidTableCellRenderer());
        
        final JTableHeader  header1 = table.getTableHeader();
        content1.add(header1,BorderLayout.NORTH);        
        content1.add(table);        
    }
    
    private void addComponentsToPane(Container pane) {
        JScrollPane scrollPane = new JScrollPane();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(0,1));
        
        final JTextArea sqlField = new JTextArea(formatSql,15,20);
        final JScrollPane  content1 = new JScrollPane (sqlField);
     
        content1.setBorder((TitledBorder)BorderFactory.createTitledBorder("SQL语句"));
        contentPanel.add(content1);       
        
        addTable(contentPanel,"解析信息",parseData);
        addTable(contentPanel,"上次慢查询信息",lastSlowData);
        addTable(contentPanel,"上次错误查询信息",lastErrorData);
        addTable(contentPanel,"其他信息",otherData);
        
        scrollPane.setViewportView(contentPanel);
        pane.add(scrollPane,BorderLayout.CENTER);
    }
    
    private void start() {
        addComponentsToPane(getContentPane());
        pack();
        setSize(WIDTH, HEIGHT);
        double   width   =   Toolkit.getDefaultToolkit().getScreenSize().getWidth(); 
        double   height   =   Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        
        setLocation(   (int)   (width   -   getWidth())   /   2, 
                       (int)   (height   -   getHeight())   /   2); 
        setVisible(true);
    }

}
