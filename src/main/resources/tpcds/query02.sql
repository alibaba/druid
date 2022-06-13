WITH wscs AS (SELECT sold_date_sk, sales_price
              FROM (SELECT ws_sold_date_sk AS sold_date_sk, ws_ext_sales_price AS sales_price
                    FROM web_sales
                    UNION ALL
                    SELECT cs_sold_date_sk AS sold_date_sk, cs_ext_sales_price AS sales_price
                    FROM catalog_sales) x),
     wswscs AS (SELECT d_week_seq
                     , sum(CASE
                               WHEN d_day_name = 'Sunday' THEN sales_price
                               ELSE NULL
             END) AS sun_sales
                     , sum(CASE
                               WHEN d_day_name = 'Monday' THEN sales_price
                               ELSE NULL
             END) AS mon_sales
                     , sum(CASE
                               WHEN d_day_name = 'Tuesday' THEN sales_price
                               ELSE NULL
             END) AS tue_sales
                     , sum(CASE
                               WHEN d_day_name = 'Wednesday' THEN sales_price
                               ELSE NULL
             END) AS wed_sales
                     , sum(CASE
                               WHEN d_day_name = 'Thursday' THEN sales_price
                               ELSE NULL
             END) AS thu_sales
                     , sum(CASE
                               WHEN d_day_name = 'Friday' THEN sales_price
                               ELSE NULL
             END) AS fri_sales
                     , sum(CASE
                               WHEN d_day_name = 'Saturday' THEN sales_price
                               ELSE NULL
             END) AS sat_sales
                FROM wscs,
                     date_dim
                WHERE d_date_sk = sold_date_sk
                GROUP BY d_week_seq)
SELECT d_week_seq1
     , round(sun_sales1 / sun_sales2, 2)
     , round(mon_sales1 / mon_sales2, 2)
     , round(tue_sales1 / tue_sales2, 2)
     , round(wed_sales1 / wed_sales2, 2)
     , round(thu_sales1 / thu_sales2, 2)
     , round(fri_sales1 / fri_sales2, 2)
     , round(sat_sales1 / sat_sales2, 2)
FROM (SELECT wswscs.d_week_seq AS d_week_seq1
           , sun_sales         AS sun_sales1
           , mon_sales         AS mon_sales1
           , tue_sales         AS tue_sales1
           , wed_sales         AS wed_sales1
           , thu_sales         AS thu_sales1
           , fri_sales         AS fri_sales1
           , sat_sales         AS sat_sales1
      FROM wswscs,
           date_dim
      WHERE date_dim.d_week_seq = wswscs.d_week_seq
        AND d_year = 2001) y,
     (SELECT wswscs.d_week_seq AS d_week_seq2
           , sun_sales         AS sun_sales2
           , mon_sales         AS mon_sales2
           , tue_sales         AS tue_sales2
           , wed_sales         AS wed_sales2
           , thu_sales         AS thu_sales2
           , fri_sales         AS fri_sales2
           , sat_sales         AS sat_sales2
      FROM wswscs,
           date_dim
      WHERE date_dim.d_week_seq = wswscs.d_week_seq
        AND d_year = 2001 + 1) z
WHERE d_week_seq1 = d_week_seq2 - 53
ORDER BY d_week_seq1