select (systimestamp - order_date) day(9) to second from orders
where order_id = 2458
