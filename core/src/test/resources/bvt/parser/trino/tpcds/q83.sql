-- database: presto_tpcds; groups: tpcds; requires: io.trino.tempto.fulfillment.table.hive.tpcds.ImmutableTpcdsTablesRequirements
WITH
  sr_items AS (
   SELECT
     "i_item_id" "item_id"
   , "sum"("sr_return_quantity") "sr_item_qty"
   FROM
     store_returns
   , item
   , date_dim
   WHERE ("sr_item_sk" = "i_item_sk")
      AND ("d_date" IN (
      SELECT "d_date"
      FROM
        date_dim
      WHERE ("d_week_seq" IN (
         SELECT "d_week_seq"
         FROM
           date_dim
         WHERE ("d_date" IN (CAST('2000-06-30' AS DATE)         , CAST('2000-09-27' AS DATE)         , CAST('2000-11-17' AS DATE)))
      ))
   ))
      AND ("sr_returned_date_sk" = "d_date_sk")
   GROUP BY "i_item_id"
)
, cr_items AS (
   SELECT
     "i_item_id" "item_id"
   , "sum"("cr_return_quantity") "cr_item_qty"
   FROM
     catalog_returns
   , item
   , date_dim
   WHERE ("cr_item_sk" = "i_item_sk")
      AND ("d_date" IN (
      SELECT "d_date"
      FROM
        date_dim
      WHERE ("d_week_seq" IN (
         SELECT "d_week_seq"
         FROM
           date_dim
         WHERE ("d_date" IN (CAST('2000-06-30' AS DATE)         , CAST('2000-09-27' AS DATE)         , CAST('2000-11-17' AS DATE)))
      ))
   ))
      AND ("cr_returned_date_sk" = "d_date_sk")
   GROUP BY "i_item_id"
)
, wr_items AS (
   SELECT
     "i_item_id" "item_id"
   , "sum"("wr_return_quantity") "wr_item_qty"
   FROM
     web_returns
   , item
   , date_dim
   WHERE ("wr_item_sk" = "i_item_sk")
      AND ("d_date" IN (
      SELECT "d_date"
      FROM
        date_dim
      WHERE ("d_week_seq" IN (
         SELECT "d_week_seq"
         FROM
           date_dim
         WHERE ("d_date" IN (CAST('2000-06-30' AS DATE)         , CAST('2000-09-27' AS DATE)         , CAST('2000-11-17' AS DATE)))
      ))
   ))
      AND ("wr_returned_date_sk" = "d_date_sk")
   GROUP BY "i_item_id"
)
SELECT
  "sr_items"."item_id"
, "sr_item_qty"
, CAST(((("sr_item_qty" / ((CAST("sr_item_qty" AS DECIMAL(9,4)) + "cr_item_qty") + "wr_item_qty")) / DECIMAL '3.0') * 100) AS DECIMAL(7,2)) "sr_dev"
, "cr_item_qty"
, CAST(((("cr_item_qty" / ((CAST("sr_item_qty" AS DECIMAL(9,4)) + "cr_item_qty") + "wr_item_qty")) / DECIMAL '3.0') * 100) AS DECIMAL(7,2)) "cr_dev"
, "wr_item_qty"
, CAST(((("wr_item_qty" / ((CAST("sr_item_qty" AS DECIMAL(9,4)) + "cr_item_qty") + "wr_item_qty")) / DECIMAL '3.0') * 100) AS DECIMAL(7,2)) "wr_dev"
, ((("sr_item_qty" + "cr_item_qty") + "wr_item_qty") / DECIMAL '3.00') "average"
FROM
  sr_items
, cr_items
, wr_items
WHERE ("sr_items"."item_id" = "cr_items"."item_id")
   AND ("sr_items"."item_id" = "wr_items"."item_id")
ORDER BY "sr_items"."item_id" ASC, "sr_item_qty" ASC
LIMIT 100
