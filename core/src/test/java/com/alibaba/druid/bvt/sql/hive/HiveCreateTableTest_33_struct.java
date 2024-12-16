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
package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class HiveCreateTableTest_33_struct extends OracleTest {
    public void test_0() throws Exception {
        String sql = //
                "CREATE EXTERNAL TABLE user_snp_test_4 (\n" +
                        "variant struct<\n" +
                        "  contigName:string,\n" +
                        "  start:bigint,\n" +
                        "  end:bigint,\n" +
                        "  names:array<string>,\n" +
                        "  splitFromMultiAllelic:boolean,\n" +
                        "  referenceAllele:string,\n" +
                        "  alternateAllele:string,\n" +
                        "  quality:double,\n" +
                        "  filtersApplied:boolean,\n" +
                        "  filtersPassed:boolean,\n" +
                        "  filtersFailed:array<string>,\n" +
                        "  annotation:struct<\n" +
                        "        ancestralAllele:string,\n" +
                        "        alleleCount:int,\n" +
                        "        readDepth:int,\n" +
                        "        forwardReadDepth:int,\n" +
                        "        reverseReadDepth:int,\n" +
                        "        referenceReadDepth:int,\n" +
                        "        referenceForwardReadDepth:int,\n" +
                        "        referenceReverseReadDepth:int,\n" +
                        "        alleleFrequency:float,\n" +
                        "        cigar:string,\n" +
                        "        dbSnp:boolean,\n" +
                        "        hapMap2:boolean,\n" +
                        "        hapMap3:boolean,\n" +
                        "        validated:boolean,\n" +
                        "        thousandGenomes:boolean,\n" +
                        "        somatic:boolean,\n" +
                        "        transcriptEffects:array<struct<\n" +
                        "            alternateAllele:string,\n" +
                        "            effects:array<string>,\n" +
                        "            geneName:string,\n" +
                        "            geneId:string,\n" +
                        "            featureType:string,\n" +
                        "            featureId:string,\n" +
                        "            biotype:string,\n" +
                        "            rank:int,\n" +
                        "            total:int,\n" +
                        "            genomicHgvs:string,\n" +
                        "            transcriptHgvs:string,\n" +
                        "            proteinHgvs:string,\n" +
                        "            cdnaPosition:int,\n" +
                        "            cdnaLength:int,\n" +
                        "            cdsPosition:int,\n" +
                        "            cdsLength:int,\n" +
                        "            proteinPosition:int,\n" +
                        "            proteinLength:int,\n" +
                        "            distance:int,\n" +
                        "            messages:array<string>\n" +
                        "        >>,\n" +
                        "        attributes:map<string,string>\n" +
                        "    >\n" +
                        ">, \n" +
                        "    contigName string, \n" +
                        "    start bigint, \n" +
                        "    end bigint, \n" +
                        "    variantCallingAnnotations struct<filtersApplied:boolean,filtersPassed:boolean,filtersFailed:array<string>,downsampled:boolean,baseQRankSum:float,fisherStrandBiasPValue:float,rmsMapQ:float,mapq0Reads:int,mqRankSum:float,readPositionRankSum:float,genotypePriors:array<float>,genotypePosteriors:array<float>,vqslod:float,culprit:string,attributes:map<string,string>>, \n" +
                        "    sampleId string, \n" +
                        "    sampleDescription string, \n" +
                        "    processingDescription string, \n" +
                        "    alleles array<string>, \n" +
                        "    expectedAlleleDosage float, \n" +
                        "    referenceReadDepth int, \n" +
                        "    alternateReadDepth int, \n" +
                        "    readDepth int, \n" +
                        "    minReadDepth int, \n" +
                        "    genotypeQuality int, \n" +
                        "    genotypeLikelihoods array<double>, \n" +
                        "    nonReferenceLikelihoods array<double>, \n" +
                        "    strandBiasComponents array<int>, \n" +
                        "    splitFromMultiAllelic boolean, \n" +
                        "    phased boolean, \n" +
                        "    phaseSetId int, \n" +
                        "    phaseQuality int\n" +
                        "    ) \n" +
                        "STORED AS PARQUET \n" +
                        "LOCATION 'oss://wegene-genomics-api-test/parquet_data/WGSDANAL098612/';"; //

        List<SQLStatement> statementList = SQLUtils.toStatementList(sql, JdbcConstants.HIVE);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt.toString());

        Assert.assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.HIVE);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toSQLString(stmt, JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE user_snp_test_4 (\n" +
                    "\tvariant STRUCT<\n" +
                    "\t\tcontigName:string,\n" +
                    "\t\tstart:bigint,\n" +
                    "\t\tend:bigint,\n" +
                    "\t\tnames:ARRAY<string>,\n" +
                    "\t\tsplitFromMultiAllelic:boolean,\n" +
                    "\t\treferenceAllele:string,\n" +
                    "\t\talternateAllele:string,\n" +
                    "\t\tquality:double,\n" +
                    "\t\tfiltersApplied:boolean,\n" +
                    "\t\tfiltersPassed:boolean,\n" +
                    "\t\tfiltersFailed:ARRAY<string>,\n" +
                    "\t\tannotation:STRUCT<\n" +
                    "\t\t\tancestralAllele:string,\n" +
                    "\t\t\talleleCount:int,\n" +
                    "\t\t\treadDepth:int,\n" +
                    "\t\t\tforwardReadDepth:int,\n" +
                    "\t\t\treverseReadDepth:int,\n" +
                    "\t\t\treferenceReadDepth:int,\n" +
                    "\t\t\treferenceForwardReadDepth:int,\n" +
                    "\t\t\treferenceReverseReadDepth:int,\n" +
                    "\t\t\talleleFrequency:float,\n" +
                    "\t\t\tcigar:string,\n" +
                    "\t\t\tdbSnp:boolean,\n" +
                    "\t\t\thapMap2:boolean,\n" +
                    "\t\t\thapMap3:boolean,\n" +
                    "\t\t\tvalidated:boolean,\n" +
                    "\t\t\tthousandGenomes:boolean,\n" +
                    "\t\t\tsomatic:boolean,\n" +
                    "\t\t\ttranscriptEffects:ARRAY<STRUCT<\n" +
                    "\t\t\t\talternateAllele:string,\n" +
                    "\t\t\t\teffects:ARRAY<string>,\n" +
                    "\t\t\t\tgeneName:string,\n" +
                    "\t\t\t\tgeneId:string,\n" +
                    "\t\t\t\tfeatureType:string,\n" +
                    "\t\t\t\tfeatureId:string,\n" +
                    "\t\t\t\tbiotype:string,\n" +
                    "\t\t\t\trank:int,\n" +
                    "\t\t\t\ttotal:int,\n" +
                    "\t\t\t\tgenomicHgvs:string,\n" +
                    "\t\t\t\ttranscriptHgvs:string,\n" +
                    "\t\t\t\tproteinHgvs:string,\n" +
                    "\t\t\t\tcdnaPosition:int,\n" +
                    "\t\t\t\tcdnaLength:int,\n" +
                    "\t\t\t\tcdsPosition:int,\n" +
                    "\t\t\t\tcdsLength:int,\n" +
                    "\t\t\t\tproteinPosition:int,\n" +
                    "\t\t\t\tproteinLength:int,\n" +
                    "\t\t\t\tdistance:int,\n" +
                    "\t\t\t\tmessages:ARRAY<string>\n" +
                    "\t\t\t>>,\n" +
                    "\t\t\tattributes:MAP<string, string>\n" +
                    "\t\t>\n" +
                    "\t>,\n" +
                    "\tcontigName string,\n" +
                    "\tstart bigint,\n" +
                    "\tend bigint,\n" +
                    "\tvariantCallingAnnotations STRUCT<\n" +
                    "\t\tfiltersApplied:boolean,\n" +
                    "\t\tfiltersPassed:boolean,\n" +
                    "\t\tfiltersFailed:ARRAY<string>,\n" +
                    "\t\tdownsampled:boolean,\n" +
                    "\t\tbaseQRankSum:float,\n" +
                    "\t\tfisherStrandBiasPValue:float,\n" +
                    "\t\trmsMapQ:float,\n" +
                    "\t\tmapq0Reads:int,\n" +
                    "\t\tmqRankSum:float,\n" +
                    "\t\treadPositionRankSum:float,\n" +
                    "\t\tgenotypePriors:ARRAY<float>,\n" +
                    "\t\tgenotypePosteriors:ARRAY<float>,\n" +
                    "\t\tvqslod:float,\n" +
                    "\t\tculprit:string,\n" +
                    "\t\tattributes:MAP<string, string>\n" +
                    "\t>,\n" +
                    "\tsampleId string,\n" +
                    "\tsampleDescription string,\n" +
                    "\tprocessingDescription string,\n" +
                    "\talleles ARRAY<string>,\n" +
                    "\texpectedAlleleDosage float,\n" +
                    "\treferenceReadDepth int,\n" +
                    "\talternateReadDepth int,\n" +
                    "\treadDepth int,\n" +
                    "\tminReadDepth int,\n" +
                    "\tgenotypeQuality int,\n" +
                    "\tgenotypeLikelihoods ARRAY<double>,\n" +
                    "\tnonReferenceLikelihoods ARRAY<double>,\n" +
                    "\tstrandBiasComponents ARRAY<int>,\n" +
                    "\tsplitFromMultiAllelic boolean,\n" +
                    "\tphased boolean,\n" +
                    "\tphaseSetId int,\n" +
                    "\tphaseQuality int\n" +
                    ")\n" +
                    "STORED AS PARQUET\n" +
                    "LOCATION 'oss://wegene-genomics-api-test/parquet_data/WGSDANAL098612/';", text);
        }
        {
            String text = SQLUtils.toSQLString(stmt.clone(), JdbcConstants.HIVE);

            assertEquals("CREATE EXTERNAL TABLE user_snp_test_4 (\n" +
                    "\tvariant STRUCT<\n" +
                    "\t\tcontigName:string,\n" +
                    "\t\tstart:bigint,\n" +
                    "\t\tend:bigint,\n" +
                    "\t\tnames:ARRAY<string>,\n" +
                    "\t\tsplitFromMultiAllelic:boolean,\n" +
                    "\t\treferenceAllele:string,\n" +
                    "\t\talternateAllele:string,\n" +
                    "\t\tquality:double,\n" +
                    "\t\tfiltersApplied:boolean,\n" +
                    "\t\tfiltersPassed:boolean,\n" +
                    "\t\tfiltersFailed:ARRAY<string>,\n" +
                    "\t\tannotation:STRUCT<\n" +
                    "\t\t\tancestralAllele:string,\n" +
                    "\t\t\talleleCount:int,\n" +
                    "\t\t\treadDepth:int,\n" +
                    "\t\t\tforwardReadDepth:int,\n" +
                    "\t\t\treverseReadDepth:int,\n" +
                    "\t\t\treferenceReadDepth:int,\n" +
                    "\t\t\treferenceForwardReadDepth:int,\n" +
                    "\t\t\treferenceReverseReadDepth:int,\n" +
                    "\t\t\talleleFrequency:float,\n" +
                    "\t\t\tcigar:string,\n" +
                    "\t\t\tdbSnp:boolean,\n" +
                    "\t\t\thapMap2:boolean,\n" +
                    "\t\t\thapMap3:boolean,\n" +
                    "\t\t\tvalidated:boolean,\n" +
                    "\t\t\tthousandGenomes:boolean,\n" +
                    "\t\t\tsomatic:boolean,\n" +
                    "\t\t\ttranscriptEffects:ARRAY<STRUCT<\n" +
                    "\t\t\t\talternateAllele:string,\n" +
                    "\t\t\t\teffects:ARRAY<string>,\n" +
                    "\t\t\t\tgeneName:string,\n" +
                    "\t\t\t\tgeneId:string,\n" +
                    "\t\t\t\tfeatureType:string,\n" +
                    "\t\t\t\tfeatureId:string,\n" +
                    "\t\t\t\tbiotype:string,\n" +
                    "\t\t\t\trank:int,\n" +
                    "\t\t\t\ttotal:int,\n" +
                    "\t\t\t\tgenomicHgvs:string,\n" +
                    "\t\t\t\ttranscriptHgvs:string,\n" +
                    "\t\t\t\tproteinHgvs:string,\n" +
                    "\t\t\t\tcdnaPosition:int,\n" +
                    "\t\t\t\tcdnaLength:int,\n" +
                    "\t\t\t\tcdsPosition:int,\n" +
                    "\t\t\t\tcdsLength:int,\n" +
                    "\t\t\t\tproteinPosition:int,\n" +
                    "\t\t\t\tproteinLength:int,\n" +
                    "\t\t\t\tdistance:int,\n" +
                    "\t\t\t\tmessages:ARRAY<string>\n" +
                    "\t\t\t>>,\n" +
                    "\t\t\tattributes:MAP<string, string>\n" +
                    "\t\t>\n" +
                    "\t>,\n" +
                    "\tcontigName string,\n" +
                    "\tstart bigint,\n" +
                    "\tend bigint,\n" +
                    "\tvariantCallingAnnotations STRUCT<\n" +
                    "\t\tfiltersApplied:boolean,\n" +
                    "\t\tfiltersPassed:boolean,\n" +
                    "\t\tfiltersFailed:ARRAY<string>,\n" +
                    "\t\tdownsampled:boolean,\n" +
                    "\t\tbaseQRankSum:float,\n" +
                    "\t\tfisherStrandBiasPValue:float,\n" +
                    "\t\trmsMapQ:float,\n" +
                    "\t\tmapq0Reads:int,\n" +
                    "\t\tmqRankSum:float,\n" +
                    "\t\treadPositionRankSum:float,\n" +
                    "\t\tgenotypePriors:ARRAY<float>,\n" +
                    "\t\tgenotypePosteriors:ARRAY<float>,\n" +
                    "\t\tvqslod:float,\n" +
                    "\t\tculprit:string,\n" +
                    "\t\tattributes:MAP<string, string>\n" +
                    "\t>,\n" +
                    "\tsampleId string,\n" +
                    "\tsampleDescription string,\n" +
                    "\tprocessingDescription string,\n" +
                    "\talleles ARRAY<string>,\n" +
                    "\texpectedAlleleDosage float,\n" +
                    "\treferenceReadDepth int,\n" +
                    "\talternateReadDepth int,\n" +
                    "\treadDepth int,\n" +
                    "\tminReadDepth int,\n" +
                    "\tgenotypeQuality int,\n" +
                    "\tgenotypeLikelihoods ARRAY<double>,\n" +
                    "\tnonReferenceLikelihoods ARRAY<double>,\n" +
                    "\tstrandBiasComponents ARRAY<int>,\n" +
                    "\tsplitFromMultiAllelic boolean,\n" +
                    "\tphased boolean,\n" +
                    "\tphaseSetId int,\n" +
                    "\tphaseQuality int\n" +
                    ")\n" +
                    "STORED AS PARQUET\n" +
                    "LOCATION 'oss://wegene-genomics-api-test/parquet_data/WGSDANAL098612/';", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(22, visitor.getColumns().size());
        assertEquals(0, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

        assertTrue(visitor.containsTable("user_snp_test_4"));

    }

}
