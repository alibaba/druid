/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.support.console;

import java.io.PrintStream;

public class Option {

    public static final int DATA_SOURCE = 1 ;
    public static final int SQL         = 2 ;
    public static final int ACTIVE_CONN = 4 ;

    private int printDataType = 0;
    private int pid = -1;
    private int id = -1;
	private int interval = -1;
	private boolean detailPrint;

	private PrintStream printStream = System.out;

    public void addPrintDataType(int newValue) {
        this.printDataType= this.printDataType |  newValue;
    }

    public static boolean isPrintHelp(String[] args) {
        if (args == null ) {
            return true;
        }
        for (String arg: args) {
            if (arg.equals("-help") ) {
                return true;
            }
        }
        return false;
    }

    public boolean printSqlData() {
        return ((printDataType & SQL) ==  SQL);
    }
    public boolean printDataSourceData() {
        return ( (printDataType & DATA_SOURCE) == DATA_SOURCE);
    }
    public boolean printActiveConn() {
        return ( (printDataType & ACTIVE_CONN) == ACTIVE_CONN);
    }

    public int getPrintDataType() {
        return this.printDataType;
    }


    public void setPid(int pid) {
        this.pid=pid;
    }
    public int getPid() {
        return this.pid;
    }

    public static String getUrl(int dataType) {
        switch (dataType) {
            case SQL:
                return "/sql.json";
            case DATA_SOURCE:
                return "/datasource.json";
            case ACTIVE_CONN:
                return "/activeConnectionStackTrace.json";
            default:
                return null;
        }
    }

	private static int parsePositiveInt(String v) {
		try {
            return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

    public static Option parseOptions(String[] args) throws OptionParseException{
        Option option = new Option();
		int i = 0;
        if (args.length < 1 ) {
            throw new OptionParseException("not enough arguments!");
        }

        while (i < args.length) {
			int v1 = parsePositiveInt(args[i]);

			//check last two arguments
            if ( (i == args.length - 2) && v1 > 0) {
				int v2 = parsePositiveInt(args[i+1]);
				if ( v2 > 0) {
					option.setPid(v1);
					option.setInterval(v2);
				} else {
		             throw new OptionParseException("请在参数的最后位置上 指定 pid 和 refresh-interval");
				}
				break;
            } else if ( i == args.length -1 ) {
				option.setPid(v1);
			}
		
            if (args[i].equals("-sql")) {
                option.addPrintDataType(SQL);
            } else if (args[i].equals("-ds")) {
                option.addPrintDataType(DATA_SOURCE);
            }  else if (args[i].equals("-act")) {
                option.addPrintDataType(ACTIVE_CONN);
            }  else if (args[i].equals("-detail")) {
				option.setDetailPrint(true);
            } else if (args[i].equals("-id")) {
            	 try {
                     int id = Integer.parseInt(args[i+1]);
                     option.setId(id);
					 i++;
                 } catch (NumberFormatException e) {
                     throw new OptionParseException("id参数必须是整数");
                 }
            }
            i++;
		}
        
        if (option.getPrintDataType() == 0) {
            throw new OptionParseException("请在{'-sql','-ds','-act'}参数中选择一个或多个");
        }
        if (option.getPid() == -1 ) {
            throw new OptionParseException("请在参数中指定 pid");
        }
        return option;
    }

    public static void printHelp(String errorMsg) {
		printHelp(System.out, errorMsg);
	}

    public static void printHelp() {
		printHelp(System.out, null);
	}

    public static void printHelp(PrintStream out, String errorMsg) {
		if (errorMsg != null ) {
			out.println(errorMsg);
			out.println();
		}
        out.println("Usage: druidStat -help | -sql -ds -act [-detail] [-id id] <pid> [refresh-interval]");
        out.println();
        out.println("参数: ");
        out.println("  -help             打印此帮助信息"); 
        out.println("  -sql              打印SQL统计数据"); 
        out.println("  -ds               打印DataSource统计数据"); 
        out.println("  -act              打印活动连接的堆栈信息"); 
        out.println("  -detail           打印统计数据的全部字段信息");
        out.println("  -id id            要打印的数据的具体id值" );
        out.println("  pid               使用druid连接池的jvm进程id"); 
        out.println("  refresh-interval  自动刷新时间间隔, 以秒为单位" );

        out.println();
        out.println("说明: ");
        out.println("  -sql,-ds,-act参数中要至少指定一种数据进行打印, 可以");
        out.println("    组合使用, 比如 -sql -ds 一起的话就打印两种统计数据");
        out.println("  -id id可以跟 -sql 或-ds组合, 比如  -sql -id 5 或 -ds -id 1086752");
        out.println("  pid必需指定, refresh-interval可选, 如不指定,则打印数据后退出");
        out.println("  pid和refresh-interval参数必需放在命令行的最后, 否则解析会出错");

        out.println();
        out.println("例子: ");
        out.println("  打印3983进程的sql 统计数据.");
        out.println("      >druidStat -sql 3983");
        out.println("  打印3983进程的ds统计数据.");
        out.println("      >druidStat -ds 3983");
        out.println("  打印3983进程的sql的id为10的详细统计数据.");
        out.println("      >druidStat -sql -id 10 -detail 3983");
        out.println("  打印3983进程的当前活动连接的堆栈信息");
        out.println("      >druidStat -act 3983");
        out.println("  打印3983进程的ds,sql,和act信息");
        out.println("      >druidStat -ds -sql -act 3983");
        out.println("  每隔5秒自动打印ds统计数据");
        out.println("      >druidStat -ds 3983 5");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDetailPrint(boolean detailPrint) {
		this.detailPrint=detailPrint;
	}
	public boolean isDetailPrint() {
		return this.detailPrint;
	}

	public void setInterval(int interval) {
		this.interval=interval;
	}
	public int getInterval() {
		return this.interval;
	}


	public void setPrintStream(PrintStream printStream) {
		this.printStream=printStream;
	}
	public PrintStream getPrintStream() {
		return this.printStream;
	}

}
