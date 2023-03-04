-- start query 1 in stream 0 using template query40.tpl and seed 1819994127
select  
   w_state
  ,i_item_id
  ,sum(case when (cast(d_date as date) < cast ('2000-03-18' as date)) 
 		then cs_sales_price - coalesce(cr_refunded_cash,0) else 0 end) as sales_before
  ,sum(case when (cast(d_date as date) >= cast ('2000-03-18' as date)) 
 		then cs_sales_price - coalesce(cr_refunded_cash,0) else 0 end) as sales_after
 from
   catalog_sales left outer join catalog_returns on
       (cs_order_number = cr_order_number 
        and cs_item_sk = cr_item_sk)
  ,warehouse 
  ,item
  ,date_dim
 where
     i_current_price between 0.99 and 1.49
 and i_item_sk          = cs_item_sk
 and cs_warehouse_sk    = w_warehouse_sk 
 and cs_sold_date_sk    = d_date_sk
 and d_date between (cast ('2000-03-18' as date) - 30 days)
                and (cast ('2000-03-18' as date) + 30 days) 
 group by
    w_state,i_item_id
 order by w_state,i_item_id
limit 100;

-- end query 1 in stream 0 using template query40.tpl
