#!/usr/bin/env bash
export AMPLXE_EXPERIMENTAL=1
/opt/intel/vtune_amplifier_xe/bin64/amplxe-cl -collect hotspots java -classpath target/classes/:target/test-classes/ com.alibaba.druid.benckmark.sql.MySqlPerfMain_visitor
