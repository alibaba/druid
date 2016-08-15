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
package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

/**
 * SQLServerWallTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class MySqlWallTest75 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setCommentAllow(true);

        Assert.assertFalse(provider.checkValid(//
        "UPDATE friends_a SET requests='-^B}q^A(X \\0\\0\\0176403924cdatetime\\ndatetime\\nq^BU\\n^G--\\Z^K:5^Hw.-Rq^CX \\0\\0\\0942515122h^BU\\n^G--^X^G^^$\\n^NӅRq^DX \\0\\0\\0760857294h^BU\\n^G-- ^F8+ ---Rq^EX \\0\\0\\0207000491h^BU\\n^G--^X^E^_^L^F-$-Rq^FX \\0\\0\\0281067699h^BU\\n^G--^B^O^C-^A<13>Rq^GX \\0\\0\\0941678014h^BU\\n^G--^\\^W^N^W^F-$-Rq^HX \\0\\0\\0840070155h^BU\\n^G--^\\^L\\n6^D*ʅRq X \\0\\0\\0468440035h^BU\\n^G--^V^W*^N^Bp^K-Rq\\nU 169240315h^BU\\n^G--^W^U^W1^D-m-Rq^KX \\0\\0\\0199411251h^BU\\n^G--^V^W%^^^A---Rq^LU 210660648h^BU\\n^G--^\\^W^Y-^F\\Zd-Rq\\rU 262672217h^BU\\n^G--\\Z^V2:^O^U--Rq^NX \\0\\0\\0952838443h^BU\\n^G--^\\^W!7\\r-{-Rq^OX \\0\\0\\0263642777h^BU\\n^G--^B^U/^D^G-̅Rq^PX \\0\\0\\0286685152h^BU\\n^G--^W^T3,^Ggs-Rq^QU 290976173h^BU\\n^G--^\\^V)^X^D---Rq^RX \\0\\0\\0825427842h^BU\\n^G--\\Z^V;^Q^N- -Rq^SX \\0\\0\\0399352674h^BU\\n^G--^\\^V-0^KC\\0-Rq^TX \\0\\0\\0429293778h^BU\\n^G--^Y^U ^]^C-��Rq^UX \\0\\0\\0796702973h^BU\\n^G--^Y^S^U#^F\\\\^W-Rq^Vu.'"//
                + ",friends='-^B}q^A(X \\0\\0\\0288854421cdatetime\\ndatetime\\nq^BU\\n^G--^[^N38^L6---q^CX \\0\\0\\0307943786h^BU\\n^G--^F^V7 ^D---Rq^DX \\0\\0\\0290783072NX \\0\\0\\0498070760NX \\0\\0\\0457575155NX \\0\\0\\0304215892h^BU\\n^G--^F^W^L+^L---Rq^EX \\0\\0\\0300254457h^BU\\n^G--^F^G$)^A---Rq^FX \\0\\0\\0252042226h^BU\\n^G-- ^R8\\r ----q^GX \\0\\0\\0697110711NX \\0\\0\\0809118053h^BU\\n^G-- ^L^H^O\\0ɲ-Rq^HX \\0\\0\\0293303495h^BU\\n^G-- ^T!.^B/ʅRq X \\0\\0\\0302651538h^BU\\n^G--^G^P)^C^Fn---q\\nU 888879887h^BU\\n^G--^H^W.*^G---Rq^KX \\0\\0\\0240865621h^BU\\n^G--^G\\n2;\\n---Rq^LU 300728616h^BU\\n^G--^A^L^N8\\0\\'" //
                + "--Rq\\rX \\0\\0\\0856456443NX \\0\\0\\0302371154h^BU\\n^G--^A^Q^R^^\\0---Rq^NX \\0\\0\\0696458616h^BU\\n^G--^G\\n98\\n---Rq^OU 297082613NX \\0\\0\\0811281930h^BU\\n^G--\\n^Q^P^X^L^OɅRq^PU 300986758h^BU\\n^G--^F^F\\r3^G-$-Rq^QU 276325435h^BU\\n^G--^B^P^P^T^E^N8-Rq^RX \\0\\0\\0299082034h^BU\\n^G--^H^W^_^Q^D<--Rq^SX \\0\\0\\0171238051h^BU\\n^G--\\n\\r)^S^Dܢ-Rq^TX \\0\\0\\0780724792h^BU\\n^G--\\n^N+ ^F*>-Rq^UX \\0\\0\\0893552392h^BU\\n^G--\\n^N^_-^K^L--Rq^VX \\0\\0\\0590290136h^BU\\n^G-- \\r^Y0\\r --Rq^WX \\0\\0\\0302913387h^BU\\n^G--^C^K#,\\0^X9-Rq^XX \\0\\0\\0252736446NX \\0\\0\\0302360033h^BU\\n^G--^C^O^A^^^H-[-Rq^YU 276564368h^BU\\n^G--\\n\\r:+^K-q-Rq\\ZX \\0\\0\\0296693715h^BU\\n^G-- ^G^[/^A-F-Rq^[X \\0\\0\\0223225019h^BU\\n^G-- ^S^X^C\\07\\Z-Rq^\\X \\0\\0\\0232453764h^BU\\n^G--^_\\r3^V\\0---Rq^]U 297276051h^BU\\n^G--^C^K/4^K-؅Rq^^X \\0\\0\\0184978889NX \\0\\0\\0813351784h^BU\\n^G--^H^H%^X^E&^S-Rq^_X \\0\\0\\03028705"));

        Assert.assertEquals(0, provider.getTableStats().size());
    }

}
