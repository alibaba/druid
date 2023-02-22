-- database: presto_tpcds; groups: tpcds; requires: io.trino.tempto.fulfillment.table.hive.tpcds.ImmutableTpcdsTablesRequirements
WITH
  ss_items AS (
   SELECT
     "i_item_id" "item_id"
   , "sum"("ss_ext_sales_price") "ss_item_rev"
   FROM
     store_sales
   , item
   , date_dim
   WHERE ("ss_item_sk" = "i_item_sk")
      AND ("d_date" IN (
      SELECT "d_date"
      FROM
        date_dim
      WHERE ("d_week_seq" = (
            SELECT "d_week_seq"
            FROM
              date_dim
            WHERE ("d_date" = CAST('2000-01-03' AS DATE))
         ))
   ))
      AND ("ss_sold_date_sk" = "d_date_sk")
   GROUP BY "i_item_id"
)
, cs_items AS (
   SELECT
     "i_item_id" "item_id"
   , "sum"("cs_ext_sales_price") "cs_item_rev"
   FROM
     catalog_sales
   , item
   , date_dim
   WHERE ("cs_item_sk" = "i_item_sk")
      AND ("d_date" IN (
      SELECT "d_date"
      FROM
        date_dim
      WHERE ("d_week_seq" = (
            SELECT "d_week_seq"
            FROM
              date_dim
            WHERE ("d_date" = CAST('2000-01-03' AS DATE))
         ))
   ))
      AND ("cs_sold_date_sk" = "d_date_sk")
   GROUP BY "i_item_id"
)
, ws_items AS (
   SELECT
     "i_item_id" "item_id"
   , "sum"("ws_ext_sales_price") "ws_item_rev"
   FROM
     web_sales
   , item
   , date_dim
   WHERE ("ws_item_sk" = "i_item_sk")
      AND ("d_date" IN (
      SELECT "d_date"
      FROM
        date_dim
      WHERE ("d_week_seq" = (
            SELECT "d_week_seq"
            FROM
              date_dim
            WHERE ("d_date" = CAST('2000-01-03' AS DATE))
         ))
   ))
      AND ("ws_sold_date_sk" = "d_date_sk")
   GROUP BY "i_item_id"
)
SELECT
  "ss_items"."item_id"
, "ss_item_rev"
, CAST(((("ss_item_rev" / ((CAST("ss_item_rev" AS DECIMAL(16,7)) + "cs_item_rev") + "ws_item_rev")) / 3) * 100) AS DECIMAL(7,2)) "ss_dev"
, "cs_item_rev"
, CAST(((("cs_item_rev" / ((CAST("ss_item_rev" AS DECIMAL(16,7)) + "cs_item_rev") + "ws_item_rev")) / 3) * 100) AS DECIMAL(7,2)) "cs_dev"
, "ws_item_rev"
, CAST(((("ws_item_rev" / ((CAST("ss_item_rev" AS DECIMAL(16,7)) + "cs_item_rev") + "ws_item_rev")) / 3) * 100) AS DECIMAL(7,2)) "ws_dev"
, ((("ss_item_rev" + "cs_item_rev") + "ws_item_rev") / 3) "average"
FROM
  ss_items
, cs_items
, ws_items
WHERE ("ss_items"."item_id" = "cs_items"."item_id")
   AND ("ss_items"."item_id" = "ws_items"."item_id")
   AND ("ss_item_rev" BETWEEN (DECIMAL '0.9' * "cs_item_rev") AND (DECIMAL '1.1' * "cs_item_rev"))
   AND ("ss_item_rev" BETWEEN (DECIMAL '0.9' * "ws_item_rev") AND (DECIMAL '1.1' * "ws_item_rev"))
   AND ("cs_item_rev" BETWEEN (DECIMAL '0.9' * "ss_item_rev") AND (DECIMAL '1.1' * "ss_item_rev"))
   AND ("cs_item_rev" BETWEEN (DECIMAL '0.9' * "ws_item_rev") AND (DECIMAL '1.1' * "ws_item_rev"))
   AND ("ws_item_rev" BETWEEN (DECIMAL '0.9' * "ss_item_rev") AND (DECIMAL '1.1' * "ss_item_rev"))
   AND ("ws_item_rev" BETWEEN (DECIMAL '0.9' * "cs_item_rev") AND (DECIMAL '1.1' * "cs_item_rev"))
ORDER BY "ss_items"."item_id" ASC, "ss_item_rev" ASC
LIMIT 100
