/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;


public class OracleSelectTest131 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from ( select * from ( \n" +
                "select bbb . id , bbb . isdel , bbb . dataversion , bbb . lrr_sfzh , bbb . lrsj , bbb . xgr_sfzh , bbb . xgsj , bbb . wszt , bbb . cbqy_bh , bbb . ajbh , bbb . cbdw_bh , bbb . cbdw_mc \n" +
                ", bbb . cbdw_jc , bbb . cbr_sfzh , bbb . cbr_xm , bbb . tfsj , bbb . wsh , bbb . ajmc , bbb . rybh \n" +
                ", bbb . ryxm , bbb . dwbh , bbb . dwmc , bbb . ryxx , bbb . wfss , bbb . zj , bbb . xzcfjd , bbb . lxfs , bbb . fyjg , bbb . rmfy , bbb . qdlx , bbb . qdfs , bbb . qzsj , bbb . signname , bbb . cflx , bbb . gajgname_bt , bbb . memo \n" +
                ", bbb . cfjg , bbb . cqcz , bbb . zxqk , bbb . qd , bbb . qd1 , bbb . wfss1 , bbb . zs , bbb . zj1 \n" +
                ", bbb . psignname , bbb . cbqy_mc , bbb . spsj , bbb . sprxm , bbb . tfsj1 , bbb . fr_xm \n" +
                ", case when bbb . tyshxydm = '*' then 'xxxxxxxxxxxxxxxxxx' when bbb . tyshxydm = '无' then 'xxxxxxxxxxxxxxxxxx' \n" +
                "   when bbb . tyshxydm = '0' then 'xxxxxxxxxxxxxxxxxx' when bbb . tyshxydm is null then 'xxxxxxxxxxxxxxxxxx' \n" +
                "   else bbb . tyshxydm end as tyshxydm , bbb . zjhm , bbb . wszh , bbb . flyj , decode ( bbb . cfjgmx , null , bbb . cfjg , bbb . cfjgmx ) cfjgmx , bbb . aybh , bbb . aymc \n" +
                "   , row_number ( ) over ( partition by bbb . id , bbb . cfjgmx order by bbb . xgsj desc ) rn from ( select aaa . * , decode ( bb . flyj , null , ( select xx . flyj from case_xz_cfjg_mx xx where xx . ajbh = aaa . ajbh and xx . flyj is not null and rownum = ? ) , bb . flyj ) flyj , bb . aybh , bb . aymc \n" +
                ", case when ( bb . jg = '1' and bb . fk is null and bb . jl is null and ( bb . zltcty is null or bb . zltcty = '0' ) and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '警告' when ( bb . jg = '1' and bb . fk is not null and bb . jl is null \n" +
                "   and ( bb . zltcty is null or bb . zltcty = '0' ) and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '警告并处罚款' || f_num_zi ( bb . fk ) || '元' when ( bb . jg = '1' and bb . jl is not null and bb . fk is null and ( bb . zltcty is null or bb . zltcty = '0' ) and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '行政拘留' || f_num_zi ( bb . jl ) || '日并警告' when ( bb . jg = '1' and bb . qtcfyj is not null and bb . fk is not null and bb . jl is null and ( bb . zltcty is null or bb . zltcty = '0' ) \n" +
                "and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . jd is null ) then '警告并' || bb . qtcfyj when ( bb . fk is not null and ( bb . jg is null or bb . jg = '0' ) and bb . jl is null and ( bb . zltcty is null or bb . zltcty = '0' ) \n" +
                "and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '罚款' || f_num_zi ( bb . fk ) || '元' when ( bb . fk is not null and bb . jl is not null and ( bb . jg is null or bb . jg = '0' ) \n" +
                "and ( bb . zltcty is null or bb . zltcty = '0' ) and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '行政拘留' || f_num_zi ( bb . jl ) || '日并罚款' || f_num_zi ( bb . fk ) || '元' \n" +
                "when ( bb . fk is not null and bb . zltcty = '1' and ( bb . jg is null or bb . jg = '0' ) and bb . jl is null and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '罚款' || f_num_zi ( bb . fk ) || '元并责令停产停业' when ( bb . fk is not null and bb . dxxkz = '1' and ( bb . jg is null or bb . jg = '0' ) and bb . jl is null and ( bb . zltcty is null or bb . zltcty = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '罚款' || f_num_zi ( bb . fk ) || '元并吊销公安机关发放的许可证' when ( bb . fk is not null and bb . qtcfyj is not null and ( bb . jg is null or bb . jg = '0' ) and bb . jl is null and ( bb . zltcty is null or bb . zltcty = '0' ) and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . jd is null ) then '罚款' || f_num_zi ( bb . fk ) || '元并' || bb . qtcfyj when ( bb . jl is not null and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null and ( bb . zltcty is null or bb . zltcty = '0' ) and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null \n" +
                "and bb . jd is null ) then '行政拘留' || f_num_zi ( bb . jl ) || '日' when ( bb . jl is not null and bb . jd is not null and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null and ( bb . zltcty is null or bb . zltcty = '0' ) \n" +
                "and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null ) then '行政拘留' || f_num_zi ( bb . jl ) || '日并刑拘折抵' || f_num_zi ( bb . jdyj ) || '日' when ( bb . zltcty = '1' and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null and bb . jl is null and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . qtcfyj is null and bb . jd is null ) then '责令停产停业' when ( bb . zltcty = '1' and bb . qtcfyj is not null and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null and bb . jl is null and ( bb . dxxkz is null or bb . dxxkz = '0' ) and bb . jd is null ) then '责令停产停业并' || bb . qtcfyj when ( bb . dxxkz = '1' and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null \n" +
                "and ( bb . zltcty is null or bb . zltcty = '0' ) and bb . jl is null and bb . jd is null and bb . qtcfyj is null ) then '吊销公安机关发放的许可证' when ( bb . dxxkz = '1' and bb . qtcfyj is not null and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null and ( bb . zltcty is null or bb . zltcty = '0' ) and bb . jl is null and bb . jd is null ) then '吊销公安机关发放的许可证并' || bb . qtcfyj when ( bb . qtcfyj is not null and ( bb . jg is null or bb . jg = '0' ) and bb . fk is null and ( bb . zltcty is null or bb . zltcty = '0' ) and bb . jl is null and bb . jd is null and ( bb . dxxkz is null or bb . dxxkz = '0' ) ) then qtcfyj else '' end cfjgmx from ( select a . id , a . isdel , a . dataversion , a . lrr_sfzh , a . lrsj , a . xgr_sfzh , a . xgsj , a . wszt , a . cbqy_bh , a . ajbh , a . cbdw_bh , a . cbdw_mc , a . cbdw_jc , a . cbr_sfzh , a . cbr_xm , a . tfsj , a . wsh , a . ajmc , a . rybh , a . ryxm , a . dwbh , a . dwmc , a . ryxx , a . wfss , a . zj , a . xzcfjd , a . lxfs , a . fyjg , a . rmfy , a . qdlx , a . qdfs \n" +
                ", a . qzsj , a . signname , a . cflx , a . gajgname_bt , a . memo , a . cfjg , a . cqcz , a . zxqk , a . qd , a . qd1 , a . wfss1 , a . zs , a . zj1 , a . psignname , a . cbqy_mc , a . spsj , a . sprxm , a . tfsj1 , b . fr_xm , b . tyshxydm \n" +
                ", c . sfzh zjhm , ( select d . wszh from case_gg_yyws d where d . zjz = a . id and d . ws_bm = ? and rownum = ? ) wszh from case_xz_xzcfjds a left join case_gg_dwxx b on b . dwbh = a . rybh left join case_gg_xyryxx c on c . rybh = a . rybh left join case_gg_ajxx d on a . ajbh = d . ajbh where a . wszt = ? and a . cfjg not like ? and a . wfss is not null and d . isdel = ? and d . ajlx = ? and not exists ( select 1 from ( select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b \n" +
                "where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) \n" +
                "or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) \n" +
                "and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? \n" +
                "and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) \n" +
                "or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null \n" +
                "and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) \n" +
                "and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null \n" +
                "and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) union all select * from case_xz_cfjg_mx a where a . isdel = ? and a . flyj like ? and not exists ( select 1 from case_gl_zaba b where a . ajbh = b . ajbh and b . isdel = ? and b . lx = ? ) and ( ( a . jg = ? and a . jl is null and a . fk is null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( ( a . jg = ? or a . jg is null ) and a . jl is null and a . fk is not null and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) or ( a . jg = ? and a . fk is not null and a . jl is null \n" +
                "and ( a . zltcty is null or a . zltcty = ? ) and ( a . dxxkz is null or a . dxxkz = ? ) and a . qtcfyj is null and a . jd is null ) ) ) tt where tt . rybh = a . rybh ) ) aaa join case_xz_cfspb aa on aa . ajbh = aaa . ajbh and aa . sfgk = ? and aa . wszt = ? join ( select distinct ajbh , rybh , flyj , jg , fk , jl , zltcty , dxxkz , qtcfyj , jd , jdyj , aybh , aymc from ( select ajbh , rybh , flyj , jg , fk , jl , zltcty , dxxkz , qtcfyj , jd , jdyj , AYBH , AYMC , rank ( ) over ( partition by ajbh , rybh order by xgsj desc ) rn from case_xz_cfjg_mx where flyj is not null ) where rn = ? ) bb on bb . ajbh = aaa . ajbh and bb . rybh = aaa . rybh ) bbb join ( select rybh from ( select a . rybh , a . ay_mc , a . ay_bh , trunc ( MONTHS_BETWEEN ( to_date ( to_char ( a . lrsj , 'yyyy-mm-dd' ) , 'yyyy-mm-dd' ) , to_date ( to_char ( a . csrq , 'yyyy-mm-dd' ) , 'yyyy-mm-dd' ) ) / 12 ) as nnl from case_gg_xyryxx a where ( length ( regexp_replace ( a . sfzh , ? ) ) >= ? ) and a . rybh not like ? and a . gj = ? and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc != ? \n" +
                "and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc not like ? and a . ay_mc not like ? and a . ay_mc not like ? and a . ay_mc not like ? and a . ay_mc != ? and ( a . sf not in ( ? , ? , ? ) or a . sf is null ) and a . isdel = ? \n" +
                "union all select a . rybh , a . ay_mc , a . ay_bh , trunc ( MONTHS_BETWEEN ( to_date ( to_char ( a . lrsj , 'yyyy-mm-dd' ) , 'yyyy-mm-dd' ) , to_date ( to_char ( a . csrq , 'yyyy-mm-dd' ) , 'yyyy-mm-dd' ) ) / 12 ) as nnl \n" +
                "from case_gg_xyryxx a \n" +
                "where （ a . sfzh is null or a . sfzh = ? or a . sfzh = ? or a . sfzh = ? ） and a . rybh not like ? and a . gj = ? and a . qtzjlx1 in ( ? , ? , ? , ? , ? ) and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc != ? and a . ay_mc not like ? and a . ay_mc not like ? and a . ay_mc not like ? and a . ay_mc not like ? and a . ay_mc != ? and ( a . sf not in ( ? , ? , ? ) or a . sf is null ) and a . isdel = ? ) where nnl >= ? union all select a . dwbh as rybh from case_gg_dwxx a where a . isdel = ? ) ccc on bbb . rybh = ccc . rybh ) where rn = ? ) where cfjgmx not like ? order by lrsj desc";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT *\n" +
                "FROM (\n" +
                "\tSELECT *\n" +
                "\tFROM (\n" +
                "\t\tSELECT bbb.id, bbb.isdel, bbb.dataversion, bbb.lrr_sfzh, bbb.lrsj\n" +
                "\t\t\t, bbb.xgr_sfzh, bbb.xgsj, bbb.wszt, bbb.cbqy_bh, bbb.ajbh\n" +
                "\t\t\t, bbb.cbdw_bh, bbb.cbdw_mc, bbb.cbdw_jc, bbb.cbr_sfzh, bbb.cbr_xm\n" +
                "\t\t\t, bbb.tfsj, bbb.wsh, bbb.ajmc, bbb.rybh, bbb.ryxm\n" +
                "\t\t\t, bbb.dwbh, bbb.dwmc, bbb.ryxx, bbb.wfss, bbb.zj\n" +
                "\t\t\t, bbb.xzcfjd, bbb.lxfs, bbb.fyjg, bbb.rmfy, bbb.qdlx\n" +
                "\t\t\t, bbb.qdfs, bbb.qzsj, bbb.signname, bbb.cflx, bbb.gajgname_bt\n" +
                "\t\t\t, bbb.memo, bbb.cfjg, bbb.cqcz, bbb.zxqk, bbb.qd\n" +
                "\t\t\t, bbb.qd1, bbb.wfss1, bbb.zs, bbb.zj1, bbb.psignname\n" +
                "\t\t\t, bbb.cbqy_mc, bbb.spsj, bbb.sprxm, bbb.tfsj1, bbb.fr_xm\n" +
                "\t\t\t, CASE \n" +
                "\t\t\t\tWHEN bbb.tyshxydm = '*' THEN 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\tWHEN bbb.tyshxydm = '无' THEN 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\tWHEN bbb.tyshxydm = '0' THEN 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\tWHEN bbb.tyshxydm IS NULL THEN 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\tELSE bbb.tyshxydm\n" +
                "\t\t\tEND AS tyshxydm, bbb.zjhm, bbb.wszh, bbb.flyj\n" +
                "\t\t\t, decode(bbb.cfjgmx, NULL, bbb.cfjg, bbb.cfjgmx) AS cfjgmx\n" +
                "\t\t\t, bbb.aybh, bbb.aymc, row_number() OVER (PARTITION BY bbb.id, bbb.cfjgmx ORDER BY bbb.xgsj DESC) AS rn\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT aaa.*\n" +
                "\t\t\t\t, decode(bb.flyj, NULL, (\n" +
                "\t\t\t\t\tSELECT xx.flyj\n" +
                "\t\t\t\t\tFROM case_xz_cfjg_mx xx\n" +
                "\t\t\t\t\tWHERE xx.ajbh = aaa.ajbh\n" +
                "\t\t\t\t\t\tAND xx.flyj IS NOT NULL\n" +
                "\t\t\t\t\t\tAND rownum = ?\n" +
                "\t\t\t\t), bb.flyj) AS flyj\n" +
                "\t\t\t\t, bb.aybh, bb.aymc\n" +
                "\t\t\t\t, CASE \n" +
                "\t\t\t\t\tWHEN bb.jg = '1'\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '警告'\n" +
                "\t\t\t\t\tWHEN bb.jg = '1'\n" +
                "\t\t\t\t\t\tAND bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '警告并处罚款' || f_num_zi(bb.fk) || '元'\n" +
                "\t\t\t\t\tWHEN bb.jg = '1'\n" +
                "\t\t\t\t\t\tAND bb.jl IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '行政拘留' || f_num_zi(bb.jl) || '日并警告'\n" +
                "\t\t\t\t\tWHEN bb.jg = '1'\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '警告并' || bb.qtcfyj\n" +
                "\t\t\t\t\tWHEN bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '罚款' || f_num_zi(bb.fk) || '元'\n" +
                "\t\t\t\t\tWHEN bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.jl IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '行政拘留' || f_num_zi(bb.jl) || '日并罚款' || f_num_zi(bb.fk) || '元'\n" +
                "\t\t\t\t\tWHEN bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.zltcty = '1'\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '罚款' || f_num_zi(bb.fk) || '元并责令停产停业'\n" +
                "\t\t\t\t\tWHEN bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.dxxkz = '1'\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '罚款' || f_num_zi(bb.fk) || '元并吊销公安机关发放的许可证'\n" +
                "\t\t\t\t\tWHEN bb.fk IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '罚款' || f_num_zi(bb.fk) || '元并' || bb.qtcfyj\n" +
                "\t\t\t\t\tWHEN bb.jl IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '行政拘留' || f_num_zi(bb.jl) || '日'\n" +
                "\t\t\t\t\tWHEN bb.jl IS NOT NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\tTHEN '行政拘留' || f_num_zi(bb.jl) || '日并刑拘折抵' || f_num_zi(bb.jdyj) || '日'\n" +
                "\t\t\t\t\tWHEN bb.zltcty = '1'\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '责令停产停业'\n" +
                "\t\t\t\t\tWHEN bb.zltcty = '1'\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '责令停产停业并' || bb.qtcfyj\n" +
                "\t\t\t\t\tWHEN bb.dxxkz = '1'\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NULL\n" +
                "\t\t\t\t\tTHEN '吊销公安机关发放的许可证'\n" +
                "\t\t\t\t\tWHEN bb.dxxkz = '1'\n" +
                "\t\t\t\t\t\tAND bb.qtcfyj IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\tTHEN '吊销公安机关发放的许可证并' || bb.qtcfyj\n" +
                "\t\t\t\t\tWHEN bb.qtcfyj IS NOT NULL\n" +
                "\t\t\t\t\t\tAND (bb.jg IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.jg = '0')\n" +
                "\t\t\t\t\t\tAND bb.fk IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tAND bb.jl IS NULL\n" +
                "\t\t\t\t\t\tAND bb.jd IS NULL\n" +
                "\t\t\t\t\t\tAND (bb.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\tOR bb.dxxkz = '0')\n" +
                "\t\t\t\t\tTHEN qtcfyj\n" +
                "\t\t\t\t\tELSE NULL\n" +
                "\t\t\t\tEND AS cfjgmx\n" +
                "\t\t\tFROM (\n" +
                "\t\t\t\tSELECT a.id, a.isdel, a.dataversion, a.lrr_sfzh, a.lrsj\n" +
                "\t\t\t\t\t, a.xgr_sfzh, a.xgsj, a.wszt, a.cbqy_bh, a.ajbh\n" +
                "\t\t\t\t\t, a.cbdw_bh, a.cbdw_mc, a.cbdw_jc, a.cbr_sfzh, a.cbr_xm\n" +
                "\t\t\t\t\t, a.tfsj, a.wsh, a.ajmc, a.rybh, a.ryxm\n" +
                "\t\t\t\t\t, a.dwbh, a.dwmc, a.ryxx, a.wfss, a.zj\n" +
                "\t\t\t\t\t, a.xzcfjd, a.lxfs, a.fyjg, a.rmfy, a.qdlx\n" +
                "\t\t\t\t\t, a.qdfs, a.qzsj, a.signname, a.cflx, a.gajgname_bt\n" +
                "\t\t\t\t\t, a.memo, a.cfjg, a.cqcz, a.zxqk, a.qd\n" +
                "\t\t\t\t\t, a.qd1, a.wfss1, a.zs, a.zj1, a.psignname\n" +
                "\t\t\t\t\t, a.cbqy_mc, a.spsj, a.sprxm, a.tfsj1, b.fr_xm\n" +
                "\t\t\t\t\t, b.tyshxydm, c.sfzh AS zjhm\n" +
                "\t\t\t\t\t, (\n" +
                "\t\t\t\t\t\tSELECT d.wszh\n" +
                "\t\t\t\t\t\tFROM case_gg_yyws d\n" +
                "\t\t\t\t\t\tWHERE d.zjz = a.id\n" +
                "\t\t\t\t\t\t\tAND d.ws_bm = ?\n" +
                "\t\t\t\t\t\t\tAND rownum = ?\n" +
                "\t\t\t\t\t) AS wszh\n" +
                "\t\t\t\tFROM case_xz_xzcfjds a\n" +
                "\t\t\t\tLEFT JOIN case_gg_dwxx b ON b.dwbh = a.rybh \n" +
                "\t\t\t\tLEFT JOIN case_gg_xyryxx c ON c.rybh = a.rybh \n" +
                "\t\t\t\t\tLEFT JOIN case_gg_ajxx d ON a.ajbh = d.ajbh \n" +
                "\t\t\t\tWHERE a.wszt = ?\n" +
                "\t\t\t\t\tAND a.cfjg NOT LIKE ?\n" +
                "\t\t\t\t\tAND a.wfss IS NOT NULL\n" +
                "\t\t\t\t\tAND d.isdel = ?\n" +
                "\t\t\t\t\tAND d.ajlx = ?\n" +
                "\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\tFROM (\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\t\t\tSELECT *\n" +
                "\t\t\t\t\t\t\tFROM case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tAND a.flyj LIKE ?\n" +
                "\t\t\t\t\t\t\t\tAND NOT EXISTS (\n" +
                "\t\t\t\t\t\t\t\t\tSELECT 1\n" +
                "\t\t\t\t\t\t\t\t\tFROM case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\tWHERE a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tAND ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.jg IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL)\n" +
                "\t\t\t\t\t\t\t\t\tOR (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.fk IS NOT NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jl IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.zltcty IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND (a.dxxkz IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\t\tOR a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.qtcfyj IS NULL\n" +
                "\t\t\t\t\t\t\t\t\t\tAND a.jd IS NULL))\n" +
                "\t\t\t\t\t\t) tt\n" +
                "\t\t\t\t\t\tWHERE tt.rybh = a.rybh\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t) aaa\n" +
                "\t\t\tJOIN case_xz_cfspb aa ON aa.ajbh = aaa.ajbh\n" +
                "\t\t\t\tAND aa.sfgk = ?\n" +
                "\t\t\t\tAND aa.wszt = ? \n" +
                "\t\t\t\tJOIN (\n" +
                "\t\t\t\t\tSELECT DISTINCT ajbh, rybh, flyj, jg, fk\n" +
                "\t\t\t\t\t\t, jl, zltcty, dxxkz, qtcfyj, jd\n" +
                "\t\t\t\t\t\t, jdyj, aybh, aymc\n" +
                "\t\t\t\t\tFROM (\n" +
                "\t\t\t\t\t\tSELECT ajbh, rybh, flyj, jg, fk\n" +
                "\t\t\t\t\t\t\t, jl, zltcty, dxxkz, qtcfyj, jd\n" +
                "\t\t\t\t\t\t\t, jdyj, AYBH, AYMC, rank() OVER (PARTITION BY ajbh, rybh ORDER BY xgsj DESC) AS rn\n" +
                "\t\t\t\t\t\tFROM case_xz_cfjg_mx\n" +
                "\t\t\t\t\t\tWHERE flyj IS NOT NULL\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t\tWHERE rn = ?\n" +
                "\t\t\t\t) bb ON bb.ajbh = aaa.ajbh\n" +
                "\t\t\t\tAND bb.rybh = aaa.rybh \n" +
                "\t\t) bbb\n" +
                "\t\t\tJOIN (\n" +
                "\t\t\t\tSELECT rybh\n" +
                "\t\t\t\tFROM (\n" +
                "\t\t\t\t\tSELECT a.rybh, a.ay_mc, a.ay_bh\n" +
                "\t\t\t\t\t\t, trunc(MONTHS_BETWEEN(to_date(to_char(a.lrsj, 'yyyy-mm-dd'), 'yyyy-mm-dd'), to_date(to_char(a.csrq, 'yyyy-mm-dd'), 'yyyy-mm-dd')) / 12) AS nnl\n" +
                "\t\t\t\t\tFROM case_gg_xyryxx a\n" +
                "\t\t\t\t\tWHERE length(regexp_replace(a.sfzh, ?)) >= ?\n" +
                "\t\t\t\t\t\tAND a.rybh NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.gj = ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND (a.sf NOT IN (?, ?, ?)\n" +
                "\t\t\t\t\t\t\tOR a.sf IS NULL)\n" +
                "\t\t\t\t\t\tAND a.isdel = ?\n" +
                "\t\t\t\t\tUNION ALL\n" +
                "\t\t\t\t\tSELECT a.rybh, a.ay_mc, a.ay_bh\n" +
                "\t\t\t\t\t\t, trunc(MONTHS_BETWEEN(to_date(to_char(a.lrsj, 'yyyy-mm-dd'), 'yyyy-mm-dd'), to_date(to_char(a.csrq, 'yyyy-mm-dd'), 'yyyy-mm-dd')) / 12) AS nnl\n" +
                "\t\t\t\t\tFROM case_gg_xyryxx a\n" +
                "\t\t\t\t\tWHERE (a.sfzh IS NULL\n" +
                "\t\t\t\t\t\t\tOR a.sfzh = ?\n" +
                "\t\t\t\t\t\t\tOR a.sfzh = ?\n" +
                "\t\t\t\t\t\t\tOR a.sfzh = ?)\n" +
                "\t\t\t\t\t\tAND a.rybh NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.gj = ?\n" +
                "\t\t\t\t\t\tAND a.qtzjlx1 IN (?, ?, ?, ?, ?)\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc NOT LIKE ?\n" +
                "\t\t\t\t\t\tAND a.ay_mc != ?\n" +
                "\t\t\t\t\t\tAND (a.sf NOT IN (?, ?, ?)\n" +
                "\t\t\t\t\t\t\tOR a.sf IS NULL)\n" +
                "\t\t\t\t\t\tAND a.isdel = ?\n" +
                "\t\t\t\t)\n" +
                "\t\t\t\tWHERE nnl >= ?\n" +
                "\t\t\t\tUNION ALL\n" +
                "\t\t\t\tSELECT a.dwbh AS rybh\n" +
                "\t\t\t\tFROM case_gg_dwxx a\n" +
                "\t\t\t\tWHERE a.isdel = ?\n" +
                "\t\t\t) ccc ON bbb.rybh = ccc.rybh \n" +
                "\t)\n" +
                "\tWHERE rn = ?\n" +
                ")\n" +
                "WHERE cfjgmx NOT LIKE ?\n" +
                "ORDER BY lrsj DESC", stmt.toString());

        assertEquals("select *\n" +
                "from (\n" +
                "\tselect *\n" +
                "\tfrom (\n" +
                "\t\tselect bbb.id, bbb.isdel, bbb.dataversion, bbb.lrr_sfzh, bbb.lrsj\n" +
                "\t\t\t, bbb.xgr_sfzh, bbb.xgsj, bbb.wszt, bbb.cbqy_bh, bbb.ajbh\n" +
                "\t\t\t, bbb.cbdw_bh, bbb.cbdw_mc, bbb.cbdw_jc, bbb.cbr_sfzh, bbb.cbr_xm\n" +
                "\t\t\t, bbb.tfsj, bbb.wsh, bbb.ajmc, bbb.rybh, bbb.ryxm\n" +
                "\t\t\t, bbb.dwbh, bbb.dwmc, bbb.ryxx, bbb.wfss, bbb.zj\n" +
                "\t\t\t, bbb.xzcfjd, bbb.lxfs, bbb.fyjg, bbb.rmfy, bbb.qdlx\n" +
                "\t\t\t, bbb.qdfs, bbb.qzsj, bbb.signname, bbb.cflx, bbb.gajgname_bt\n" +
                "\t\t\t, bbb.memo, bbb.cfjg, bbb.cqcz, bbb.zxqk, bbb.qd\n" +
                "\t\t\t, bbb.qd1, bbb.wfss1, bbb.zs, bbb.zj1, bbb.psignname\n" +
                "\t\t\t, bbb.cbqy_mc, bbb.spsj, bbb.sprxm, bbb.tfsj1, bbb.fr_xm\n" +
                "\t\t\t, case \n" +
                "\t\t\t\twhen bbb.tyshxydm = '*' then 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\twhen bbb.tyshxydm = '无' then 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\twhen bbb.tyshxydm = '0' then 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\twhen bbb.tyshxydm is null then 'xxxxxxxxxxxxxxxxxx'\n" +
                "\t\t\t\telse bbb.tyshxydm\n" +
                "\t\t\tend as tyshxydm, bbb.zjhm, bbb.wszh, bbb.flyj\n" +
                "\t\t\t, decode(bbb.cfjgmx, null, bbb.cfjg, bbb.cfjgmx) as cfjgmx\n" +
                "\t\t\t, bbb.aybh, bbb.aymc, row_number() over (partition by bbb.id, bbb.cfjgmx order by bbb.xgsj desc) as rn\n" +
                "\t\tfrom (\n" +
                "\t\t\tselect aaa.*\n" +
                "\t\t\t\t, decode(bb.flyj, null, (\n" +
                "\t\t\t\t\tselect xx.flyj\n" +
                "\t\t\t\t\tfrom case_xz_cfjg_mx xx\n" +
                "\t\t\t\t\twhere xx.ajbh = aaa.ajbh\n" +
                "\t\t\t\t\t\tand xx.flyj is not null\n" +
                "\t\t\t\t\t\tand rownum = ?\n" +
                "\t\t\t\t), bb.flyj) as flyj\n" +
                "\t\t\t\t, bb.aybh, bb.aymc\n" +
                "\t\t\t\t, case \n" +
                "\t\t\t\t\twhen bb.jg = '1'\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '警告'\n" +
                "\t\t\t\t\twhen bb.jg = '1'\n" +
                "\t\t\t\t\t\tand bb.fk is not null\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '警告并处罚款' || f_num_zi(bb.fk) || '元'\n" +
                "\t\t\t\t\twhen bb.jg = '1'\n" +
                "\t\t\t\t\t\tand bb.jl is not null\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '行政拘留' || f_num_zi(bb.jl) || '日并警告'\n" +
                "\t\t\t\t\twhen bb.jg = '1'\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is not null\n" +
                "\t\t\t\t\t\tand bb.fk is not null\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '警告并' || bb.qtcfyj\n" +
                "\t\t\t\t\twhen bb.fk is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '罚款' || f_num_zi(bb.fk) || '元'\n" +
                "\t\t\t\t\twhen bb.fk is not null\n" +
                "\t\t\t\t\t\tand bb.jl is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '行政拘留' || f_num_zi(bb.jl) || '日并罚款' || f_num_zi(bb.fk) || '元'\n" +
                "\t\t\t\t\twhen bb.fk is not null\n" +
                "\t\t\t\t\t\tand bb.zltcty = '1'\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '罚款' || f_num_zi(bb.fk) || '元并责令停产停业'\n" +
                "\t\t\t\t\twhen bb.fk is not null\n" +
                "\t\t\t\t\t\tand bb.dxxkz = '1'\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '罚款' || f_num_zi(bb.fk) || '元并吊销公安机关发放的许可证'\n" +
                "\t\t\t\t\twhen bb.fk is not null\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '罚款' || f_num_zi(bb.fk) || '元并' || bb.qtcfyj\n" +
                "\t\t\t\t\twhen bb.jl is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '行政拘留' || f_num_zi(bb.jl) || '日'\n" +
                "\t\t\t\t\twhen bb.jl is not null\n" +
                "\t\t\t\t\t\tand bb.jd is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\tthen '行政拘留' || f_num_zi(bb.jl) || '日并刑拘折抵' || f_num_zi(bb.jdyj) || '日'\n" +
                "\t\t\t\t\twhen bb.zltcty = '1'\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '责令停产停业'\n" +
                "\t\t\t\t\twhen bb.zltcty = '1'\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '责令停产停业并' || bb.qtcfyj\n" +
                "\t\t\t\t\twhen bb.dxxkz = '1'\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is null\n" +
                "\t\t\t\t\tthen '吊销公安机关发放的许可证'\n" +
                "\t\t\t\t\twhen bb.dxxkz = '1'\n" +
                "\t\t\t\t\t\tand bb.qtcfyj is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\tthen '吊销公安机关发放的许可证并' || bb.qtcfyj\n" +
                "\t\t\t\t\twhen bb.qtcfyj is not null\n" +
                "\t\t\t\t\t\tand (bb.jg is null\n" +
                "\t\t\t\t\t\t\tor bb.jg = '0')\n" +
                "\t\t\t\t\t\tand bb.fk is null\n" +
                "\t\t\t\t\t\tand (bb.zltcty is null\n" +
                "\t\t\t\t\t\t\tor bb.zltcty = '0')\n" +
                "\t\t\t\t\t\tand bb.jl is null\n" +
                "\t\t\t\t\t\tand bb.jd is null\n" +
                "\t\t\t\t\t\tand (bb.dxxkz is null\n" +
                "\t\t\t\t\t\t\tor bb.dxxkz = '0')\n" +
                "\t\t\t\t\tthen qtcfyj\n" +
                "\t\t\t\t\telse null\n" +
                "\t\t\t\tend as cfjgmx\n" +
                "\t\t\tfrom (\n" +
                "\t\t\t\tselect a.id, a.isdel, a.dataversion, a.lrr_sfzh, a.lrsj\n" +
                "\t\t\t\t\t, a.xgr_sfzh, a.xgsj, a.wszt, a.cbqy_bh, a.ajbh\n" +
                "\t\t\t\t\t, a.cbdw_bh, a.cbdw_mc, a.cbdw_jc, a.cbr_sfzh, a.cbr_xm\n" +
                "\t\t\t\t\t, a.tfsj, a.wsh, a.ajmc, a.rybh, a.ryxm\n" +
                "\t\t\t\t\t, a.dwbh, a.dwmc, a.ryxx, a.wfss, a.zj\n" +
                "\t\t\t\t\t, a.xzcfjd, a.lxfs, a.fyjg, a.rmfy, a.qdlx\n" +
                "\t\t\t\t\t, a.qdfs, a.qzsj, a.signname, a.cflx, a.gajgname_bt\n" +
                "\t\t\t\t\t, a.memo, a.cfjg, a.cqcz, a.zxqk, a.qd\n" +
                "\t\t\t\t\t, a.qd1, a.wfss1, a.zs, a.zj1, a.psignname\n" +
                "\t\t\t\t\t, a.cbqy_mc, a.spsj, a.sprxm, a.tfsj1, b.fr_xm\n" +
                "\t\t\t\t\t, b.tyshxydm, c.sfzh as zjhm\n" +
                "\t\t\t\t\t, (\n" +
                "\t\t\t\t\t\tselect d.wszh\n" +
                "\t\t\t\t\t\tfrom case_gg_yyws d\n" +
                "\t\t\t\t\t\twhere d.zjz = a.id\n" +
                "\t\t\t\t\t\t\tand d.ws_bm = ?\n" +
                "\t\t\t\t\t\t\tand rownum = ?\n" +
                "\t\t\t\t\t) as wszh\n" +
                "\t\t\t\tfrom case_xz_xzcfjds a\n" +
                "\t\t\t\tleft join case_gg_dwxx b on b.dwbh = a.rybh \n" +
                "\t\t\t\tleft join case_gg_xyryxx c on c.rybh = a.rybh \n" +
                "\t\t\t\t\tleft join case_gg_ajxx d on a.ajbh = d.ajbh \n" +
                "\t\t\t\twhere a.wszt = ?\n" +
                "\t\t\t\t\tand a.cfjg not like ?\n" +
                "\t\t\t\t\tand a.wfss is not null\n" +
                "\t\t\t\t\tand d.isdel = ?\n" +
                "\t\t\t\t\tand d.ajlx = ?\n" +
                "\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\tfrom (\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t\tunion all\n" +
                "\t\t\t\t\t\t\tselect *\n" +
                "\t\t\t\t\t\t\tfrom case_xz_cfjg_mx a\n" +
                "\t\t\t\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t\t\t\t\t\tand a.flyj like ?\n" +
                "\t\t\t\t\t\t\t\tand not exists (\n" +
                "\t\t\t\t\t\t\t\t\tselect 1\n" +
                "\t\t\t\t\t\t\t\t\tfrom case_gl_zaba b\n" +
                "\t\t\t\t\t\t\t\t\twhere a.ajbh = b.ajbh\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.isdel = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand b.lx = ?\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\tand ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor ((a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.jg is null)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null)\n" +
                "\t\t\t\t\t\t\t\t\tor (a.jg = ?\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.fk is not null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jl is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.zltcty is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.zltcty = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand (a.dxxkz is null\n" +
                "\t\t\t\t\t\t\t\t\t\t\tor a.dxxkz = ?)\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.qtcfyj is null\n" +
                "\t\t\t\t\t\t\t\t\t\tand a.jd is null))\n" +
                "\t\t\t\t\t\t) tt\n" +
                "\t\t\t\t\t\twhere tt.rybh = a.rybh\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t) aaa\n" +
                "\t\t\tjoin case_xz_cfspb aa on aa.ajbh = aaa.ajbh\n" +
                "\t\t\t\tand aa.sfgk = ?\n" +
                "\t\t\t\tand aa.wszt = ? \n" +
                "\t\t\t\tjoin (\n" +
                "\t\t\t\t\tselect distinct ajbh, rybh, flyj, jg, fk\n" +
                "\t\t\t\t\t\t, jl, zltcty, dxxkz, qtcfyj, jd\n" +
                "\t\t\t\t\t\t, jdyj, aybh, aymc\n" +
                "\t\t\t\t\tfrom (\n" +
                "\t\t\t\t\t\tselect ajbh, rybh, flyj, jg, fk\n" +
                "\t\t\t\t\t\t\t, jl, zltcty, dxxkz, qtcfyj, jd\n" +
                "\t\t\t\t\t\t\t, jdyj, AYBH, AYMC, rank() over (partition by ajbh, rybh order by xgsj desc) as rn\n" +
                "\t\t\t\t\t\tfrom case_xz_cfjg_mx\n" +
                "\t\t\t\t\t\twhere flyj is not null\n" +
                "\t\t\t\t\t)\n" +
                "\t\t\t\t\twhere rn = ?\n" +
                "\t\t\t\t) bb on bb.ajbh = aaa.ajbh\n" +
                "\t\t\t\tand bb.rybh = aaa.rybh \n" +
                "\t\t) bbb\n" +
                "\t\t\tjoin (\n" +
                "\t\t\t\tselect rybh\n" +
                "\t\t\t\tfrom (\n" +
                "\t\t\t\t\tselect a.rybh, a.ay_mc, a.ay_bh\n" +
                "\t\t\t\t\t\t, trunc(MONTHS_BETWEEN(to_date(to_char(a.lrsj, 'yyyy-mm-dd'), 'yyyy-mm-dd'), to_date(to_char(a.csrq, 'yyyy-mm-dd'), 'yyyy-mm-dd')) / 12) as nnl\n" +
                "\t\t\t\t\tfrom case_gg_xyryxx a\n" +
                "\t\t\t\t\twhere length(regexp_replace(a.sfzh, ?)) >= ?\n" +
                "\t\t\t\t\t\tand a.rybh not like ?\n" +
                "\t\t\t\t\t\tand a.gj = ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand (a.sf not in (?, ?, ?)\n" +
                "\t\t\t\t\t\t\tor a.sf is null)\n" +
                "\t\t\t\t\t\tand a.isdel = ?\n" +
                "\t\t\t\t\tunion all\n" +
                "\t\t\t\t\tselect a.rybh, a.ay_mc, a.ay_bh\n" +
                "\t\t\t\t\t\t, trunc(MONTHS_BETWEEN(to_date(to_char(a.lrsj, 'yyyy-mm-dd'), 'yyyy-mm-dd'), to_date(to_char(a.csrq, 'yyyy-mm-dd'), 'yyyy-mm-dd')) / 12) as nnl\n" +
                "\t\t\t\t\tfrom case_gg_xyryxx a\n" +
                "\t\t\t\t\twhere (a.sfzh is null\n" +
                "\t\t\t\t\t\t\tor a.sfzh = ?\n" +
                "\t\t\t\t\t\t\tor a.sfzh = ?\n" +
                "\t\t\t\t\t\t\tor a.sfzh = ?)\n" +
                "\t\t\t\t\t\tand a.rybh not like ?\n" +
                "\t\t\t\t\t\tand a.gj = ?\n" +
                "\t\t\t\t\t\tand a.qtzjlx1 in (?, ?, ?, ?, ?)\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc not like ?\n" +
                "\t\t\t\t\t\tand a.ay_mc != ?\n" +
                "\t\t\t\t\t\tand (a.sf not in (?, ?, ?)\n" +
                "\t\t\t\t\t\t\tor a.sf is null)\n" +
                "\t\t\t\t\t\tand a.isdel = ?\n" +
                "\t\t\t\t)\n" +
                "\t\t\t\twhere nnl >= ?\n" +
                "\t\t\t\tunion all\n" +
                "\t\t\t\tselect a.dwbh as rybh\n" +
                "\t\t\t\tfrom case_gg_dwxx a\n" +
                "\t\t\t\twhere a.isdel = ?\n" +
                "\t\t\t) ccc on bbb.rybh = ccc.rybh \n" +
                "\t)\n" +
                "\twhere rn = ?\n" +
                ")\n" +
                "where cfjgmx not like ?\n" +
                "order by lrsj desc", stmt.toLowerCaseString());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(8, visitor.getTables().size());
        assertEquals(91, visitor.getColumns().size());
        assertEquals(47, visitor.getConditions().size());
        assertEquals(8, visitor.getRelationships().size());
        assertEquals(1, visitor.getOrderByColumns().size());

//        assertTrue(visitor.containsColumn("srm1.CONSIGNEE_ADDRESS", "id"));
    }

}