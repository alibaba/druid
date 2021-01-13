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
                    "\tvariant STRUCT<contigName:string, start:bigint, end:bigint, names:ARRAY<string>, splitFromMultiAllelic:boolean, referenceAllele:string, alternateAllele:string, quality:double, filtersApplied:boolean, filtersPassed:boolean, filtersFailed:ARRAY<string>, annotation:STRUCT<ancestralAllele:string, alleleCount:int, readDepth:int, forwardReadDepth:int, reverseReadDepth:int, referenceReadDepth:int, referenceForwardReadDepth:int, referenceReverseReadDepth:int, alleleFrequency:float, cigar:string, dbSnp:boolean, hapMap2:boolean, hapMap3:boolean, validated:boolean, thousandGenomes:boolean, somatic:boolean, transcriptEffects:ARRAY<STRUCT<alternateAllele:string, effects:ARRAY<string>, geneName:string, geneId:string, featureType:string, featureId:string, biotype:string, rank:int, total:int, genomicHgvs:string, transcriptHgvs:string, proteinHgvs:string, cdnaPosition:int, cdnaLength:int, cdsPosition:int, cdsLength:int, proteinPosition:int, proteinLength:int, distance:int, messages:ARRAY<string>>>, attributes:MAP<string, string>>>,\n" +
                    "\tcontigName string,\n" +
                    "\tstart bigint,\n" +
                    "\tend bigint,\n" +
                    "\tvariantCallingAnnotations STRUCT<filtersApplied:boolean, filtersPassed:boolean, filtersFailed:ARRAY<string>, downsampled:boolean, baseQRankSum:float, fisherStrandBiasPValue:float, rmsMapQ:float, mapq0Reads:int, mqRankSum:float, readPositionRankSum:float, genotypePriors:ARRAY<float>, genotypePosteriors:ARRAY<float>, vqslod:float, culprit:string, attributes:MAP<string, string>>,\n" +
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
                    "\tvariant STRUCT<contigName:string, start:bigint, end:bigint, names:ARRAY<string>, splitFromMultiAllelic:boolean, referenceAllele:string, alternateAllele:string, quality:double, filtersApplied:boolean, filtersPassed:boolean, filtersFailed:ARRAY<string>, annotation:STRUCT<ancestralAllele:string, alleleCount:int, readDepth:int, forwardReadDepth:int, reverseReadDepth:int, referenceReadDepth:int, referenceForwardReadDepth:int, referenceReverseReadDepth:int, alleleFrequency:float, cigar:string, dbSnp:boolean, hapMap2:boolean, hapMap3:boolean, validated:boolean, thousandGenomes:boolean, somatic:boolean, transcriptEffects:ARRAY<STRUCT<alternateAllele:string, effects:ARRAY<string>, geneName:string, geneId:string, featureType:string, featureId:string, biotype:string, rank:int, total:int, genomicHgvs:string, transcriptHgvs:string, proteinHgvs:string, cdnaPosition:int, cdnaLength:int, cdsPosition:int, cdsLength:int, proteinPosition:int, proteinLength:int, distance:int, messages:ARRAY<string>>>, attributes:MAP<string, string>>>,\n" +
                    "\tcontigName string,\n" +
                    "\tstart bigint,\n" +
                    "\tend bigint,\n" +
                    "\tvariantCallingAnnotations STRUCT<filtersApplied:boolean, filtersPassed:boolean, filtersFailed:ARRAY<string>, downsampled:boolean, baseQRankSum:float, fisherStrandBiasPValue:float, rmsMapQ:float, mapq0Reads:int, mqRankSum:float, readPositionRankSum:float, genotypePriors:ARRAY<float>, genotypePosteriors:ARRAY<float>, vqslod:float, culprit:string, attributes:MAP<string, string>>,\n" +
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
                    "LOCATION 'oss://wegene-genomics-api-test/parquet_data/WGSDANAL098612/'", text);
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
