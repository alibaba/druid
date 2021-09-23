package com.alibaba.druid.support.opds.udf;

import com.aliyun.odps.udf.UDF;
import org.apache.commons.lang.StringEscapeUtils;

public class MetaSqlExtract  extends UDF {
    public String evaluate(String xml) {
        if (xml == null || xml.length() == 0) {
            return null;
        }

        int p0 = xml.indexOf("<Query>");
        if (p0 == -1) {
            return null;
        }
        p0 += "<Query>".length();

        int p1 = xml.indexOf("</Query>", p0);
        if (p1 == -1) {
            return null;
        }

        String sql = xml.substring(p0, p1);
        int p2 = xml.indexOf("<![CDATA[");
        if (p2 != -1) {
            if (sql.length() > "<![CDATA[".length() + 3) {
                return sql.substring("<![CDATA[".length(), sql.length() - 3);
            } else {
                return null;
            }
        }
        return StringEscapeUtils.unescapeXml(sql);
    }

}
