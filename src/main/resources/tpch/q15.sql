WITH revenue0 AS
(
SELECT l_suppkey AS supplier_no, sum(l_extendedprice * (1 - l_discount)) AS total_revenue
FROM lineitem
WHERE date_parse(l_shipdate, '%Y-%m-%d %H:%i:%s') >= DATE '1993-01-01'
AND date_parse(l_shipdate, '%Y-%m-%d %H:%i:%s') < DATE '1993-01-01' + interval '3' month
GROUP BY l_suppkey
)
SELECT s_suppkey, s_name, s_address, s_phone, total_revenue
FROM supplier_rcfile_string, revenue0
WHERE s_suppkey = supplier_no
AND total_revenue IN (
    SELECT max(total_revenue)
    FROM revenue0 )
ORDER BY s_suppkey