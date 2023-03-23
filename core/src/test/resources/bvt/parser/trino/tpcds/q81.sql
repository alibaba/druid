-- database: presto_tpcds; groups: tpcds; requires: io.trino.tempto.fulfillment.table.hive.tpcds.ImmutableTpcdsTablesRequirements
WITH
  customer_total_return AS (
   SELECT
     "cr_returning_customer_sk" "ctr_customer_sk"
   , "ca_state" "ctr_state"
   , "sum"("cr_return_amt_inc_tax") "ctr_total_return"
   FROM
     catalog_returns
   , date_dim
   , customer_address
   WHERE ("cr_returned_date_sk" = "d_date_sk")
      AND ("d_year" = 2000)
      AND ("cr_returning_addr_sk" = "ca_address_sk")
   GROUP BY "cr_returning_customer_sk", "ca_state"
)
SELECT
  "c_customer_id"
, "c_salutation"
, "c_first_name"
, "c_last_name"
, "ca_street_number"
, "ca_street_name"
, "ca_street_type"
, "ca_suite_number"
, "ca_city"
, "ca_county"
, "ca_state"
, "ca_zip"
, "ca_country"
, "ca_gmt_offset"
, "ca_location_type"
, "ctr_total_return"
FROM
  customer_total_return ctr1
, customer_address
, customer
WHERE ("ctr1"."ctr_total_return" > (
      SELECT ("avg"("ctr_total_return") * DECIMAL '1.2')
      FROM
        customer_total_return ctr2
      WHERE ("ctr1"."ctr_state" = "ctr2"."ctr_state")
   ))
   AND ("ca_address_sk" = "c_current_addr_sk")
   AND ("ca_state" = 'GA')
   AND ("ctr1"."ctr_customer_sk" = "c_customer_sk")
ORDER BY "c_customer_id" ASC, "c_salutation" ASC, "c_first_name" ASC, "c_last_name" ASC, "ca_street_number" ASC, "ca_street_name" ASC, "ca_street_type" ASC, "ca_suite_number" ASC, "ca_city" ASC, "ca_county" ASC, "ca_state" ASC, "ca_zip" ASC, "ca_country" ASC, "ca_gmt_offset" ASC, "ca_location_type" ASC, "ctr_total_return" ASC
LIMIT 100
