-- database: presto_tpcds; groups: tpcds; requires: io.trino.tempto.fulfillment.table.hive.tpcds.ImmutableTpcdsTablesRequirements
SELECT
  "s_store_name"
, "i_item_desc"
, "sc"."revenue"
, "i_current_price"
, "i_wholesale_cost"
, "i_brand"
FROM
  store
, item
, (
   SELECT
     "ss_store_sk"
   , "avg"("revenue") "ave"
   FROM
     (
      SELECT
        "ss_store_sk"
      , "ss_item_sk"
      , "sum"("ss_sales_price") "revenue"
      FROM
        store_sales
      , date_dim
      WHERE ("ss_sold_date_sk" = "d_date_sk")
         AND ("d_month_seq" BETWEEN 1176 AND (1176 + 11))
      GROUP BY "ss_store_sk", "ss_item_sk"
   )  sa
   GROUP BY "ss_store_sk"
)  sb
, (
   SELECT
     "ss_store_sk"
   , "ss_item_sk"
   , "sum"("ss_sales_price") "revenue"
   FROM
     store_sales
   , date_dim
   WHERE ("ss_sold_date_sk" = "d_date_sk")
      AND ("d_month_seq" BETWEEN 1176 AND (1176 + 11))
   GROUP BY "ss_store_sk", "ss_item_sk"
)  sc
WHERE ("sb"."ss_store_sk" = "sc"."ss_store_sk")
   AND ("sc"."revenue" <= (DECIMAL '0.1' * "sb"."ave"))
   AND ("s_store_sk" = "sc"."ss_store_sk")
   AND ("i_item_sk" = "sc"."ss_item_sk")
ORDER BY "s_store_name" ASC, "i_item_desc" ASC
LIMIT 100
