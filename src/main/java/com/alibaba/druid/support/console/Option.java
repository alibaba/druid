/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

public class Option {

    public static enum PrintStyle {HORIZONTAL , VERTICAL };
    public static final int DATA_SOURCE = 1 ;
    public static final int SQL         = 2 ;
    public static final int ACTIVE_CONN = 4 ;

    private PrintStyle style = PrintStyle.HORIZONTAL;
    private int printDataType = 0;
    private int vmid = -1;

    public void addPrintDataType(int newValue) {
        this.printDataType= this.printDataType |  newValue;
    }

    public static boolean isPrintHelp(String[] args) {
        if (args == null ) return true;
        for (String arg: args) {
            if (arg.equals("-h") || arg.equals("--help")) {
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

    public void setStyle(PrintStyle style) {
        this.style=style;
    }
    public PrintStyle getStyle() {
        return this.style;
    }

    public void setVmid(int vmid) {
        this.vmid=vmid;
    }
    public int getVmid() {
        return this.vmid;
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

    public static Option parseOptions(String[] args) throws OptionParseException{
        Option option = new Option();
		int i = 0;
        if (args.length < 1 ) {
            throw new OptionParseException("not enough arguments!");
        }

        while (i < args.length) {
            if ( i == args.length -1 ) {
                if (args[i].startsWith("-")) throw new OptionParseException("please specify vmid in last arguments!");
                try {
                    int vmid = Integer.parseInt(args[i]);
                    option.setVmid(vmid);
                } catch (NumberFormatException e) {
                    throw new OptionParseException("vmid argument is not a integer!");
                }
            }
            if (!args[i].startsWith("-")) {
                i++;
                continue;
            }
            if (args[i].equals("-sql")) {
                option.addPrintDataType(SQL);
            } else if (args[i].equals("-ds")) {
                option.addPrintDataType(DATA_SOURCE);
            }  else if (args[i].equals("-act")) {
                option.addPrintDataType(ACTIVE_CONN);
            }  else if (args[i].equals("-s1")) {
                option.setStyle(PrintStyle.HORIZONTAL);
            }  else if (args[i].equals("-s2")) {
                option.setStyle(PrintStyle.VERTICAL);
            }
            i++;
		}
        if (option.getPrintDataType() == 0) {
            throw new OptionParseException("please specify one or more arguments in {'-sql','-ds','-act'}.");
        }
        return option;
    }

    public static void printHelp() {
        System.out.println("Usage: druidStat -help | -sql -ds -act -s1 -s2 vmid");
        System.out.println();
        System.out.println("arguments: ");
        System.out.println("  vmid       the process id of jvm that running druid"); 
        System.out.println("  -sql       print sql stat data"); 
        System.out.println("  -ds        print datasource stat data"); 
        System.out.println("  -act       print active connection stacktrace data"); 
        System.out.println("  -s1        print field name in first row");
        System.out.println("  -s2        print field name in first column");
    }

    

}
