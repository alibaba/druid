select *
from   (select customer_id, product_code, quantity
        from   pivot_test)
pivot xml (sum(quantity) as sum_quantity for (product_code) in (select distinct product_code
                                                                from   pivot_test))
