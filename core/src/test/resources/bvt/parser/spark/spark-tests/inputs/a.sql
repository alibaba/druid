SELECT ( WITH cte(foo) AS ( VALUES(id) )
         SELECT (SELECT foo FROM cte) )
FROM t;