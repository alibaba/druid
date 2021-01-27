-- database: presto; groups: tpch; tables: lineitem
SELECT sum(l_extendedprice * l_discount) AS revenue
FROM
  lineitem
WHERE
  l_shipdate >= DATE '1994-01-01'
  AND l_shipdate < DATE '1994-01-01' + INTERVAL '1' YEAR
AND l_discount BETWEEN decimal '0.06' - decimal '0.01' AND decimal '0.06' + decimal '0.01'
AND l_quantity < 24
