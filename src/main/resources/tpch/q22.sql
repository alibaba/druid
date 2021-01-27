-- database: presto; groups: tpch; tables: orders,customer
SELECT
  cntrycode,
  count(*)       AS numcust,
  sum(c_acctbal) AS totacctbal
FROM (
       SELECT
         substr(c_phone, 1, 2) AS cntrycode,
         c_acctbal
       FROM
         customer
       WHERE
         substr(c_phone, 1, 2) IN
         ('13', '31', '23', '29', '30', '18', '17')
         AND c_acctbal > (
           SELECT avg(c_acctbal)
           FROM
             customer
           WHERE
             c_acctbal > 0.00
             AND substr(c_phone, 1, 2) IN
                 ('13', '31', '23', '29', '30', '18', '17')
         )
         AND NOT exists(
           SELECT *
           FROM
             orders
           WHERE
             o_custkey = c_custkey
         )
     ) AS custsale
GROUP BY
  cntrycode
ORDER BY
  cntrycode
