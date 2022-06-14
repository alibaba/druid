with frequent_ss_items as
         (select substr(i_item_desc, 1, 30) itemdesc, i_item_sk item_sk, d_date solddate, count(*) cnt
          from store_sales
             , date_dim
             , item
          where ss_sold_date_sk = d_date_sk
            and ss_item_sk = i_item_sk
            and d_year in (2000, 2000 + 1, 2000 + 2, 2000 + 3)
          group by substr(i_item_desc, 1, 30), i_item_sk, d_date
          having count(*) > 4),
     max_store_sales as
         (select max(csales) tpcds_cmax
          from (select c_customer_sk, sum(ss_quantity * ss_sales_price) csales
                from store_sales
                   , customer
                   , date_dim
                where ss_customer_sk = c_customer_sk
                  and ss_sold_date_sk = d_date_sk
                  and d_year in (2000, 2000 + 1, 2000 + 2, 2000 + 3)
                group by c_customer_sk)),
     best_ss_customer as
         (select c_customer_sk, sum(ss_quantity * ss_sales_price) ssales
          from store_sales
             , customer
          where ss_customer_sk = c_customer_sk
          group by c_customer_sk
          having sum(ss_quantity * ss_sales_price) > (50 / 100.0) * (select *
                                                                     from max_store_sales))
select sum(sales)
from (select cs_quantity * cs_list_price sales
      from catalog_sales
         , date_dim
      where d_year = 2000
        and d_moy = 2
        and cs_sold_date_sk = d_date_sk
        and cs_item_sk in (select item_sk from frequent_ss_items)
        and cs_bill_customer_sk in (select c_customer_sk from best_ss_customer)
      union all
      select ws_quantity * ws_list_price sales
      from web_sales
         , date_dim
      where d_year = 2000
        and d_moy = 2
        and ws_sold_date_sk = d_date_sk
        and ws_item_sk in (select item_sk from frequent_ss_items)
        and ws_bill_customer_sk in (select c_customer_sk from best_ss_customer)) limit 100;
with frequent_ss_items as
         (select substr(i_item_desc, 1, 30) itemdesc, i_item_sk item_sk, d_date solddate, count(*) cnt
          from store_sales
             , date_dim
             , item
          where ss_sold_date_sk = d_date_sk
            and ss_item_sk = i_item_sk
            and d_year in (2000, 2000 + 1, 2000 + 2, 2000 + 3)
          group by substr(i_item_desc, 1, 30), i_item_sk, d_date
          having count(*) > 4),
     max_store_sales as
         (select max(csales) tpcds_cmax
          from (select c_customer_sk, sum(ss_quantity * ss_sales_price) csales
                from store_sales
                   , customer
                   , date_dim
                where ss_customer_sk = c_customer_sk
                  and ss_sold_date_sk = d_date_sk
                  and d_year in (2000, 2000 + 1, 2000 + 2, 2000 + 3)
                group by c_customer_sk)),
     best_ss_customer as
         (select c_customer_sk, sum(ss_quantity * ss_sales_price) ssales
          from store_sales
             , customer
          where ss_customer_sk = c_customer_sk
          group by c_customer_sk
          having sum(ss_quantity * ss_sales_price) > (50 / 100.0) * (select *
                                                                     from max_store_sales))
select c_last_name, c_first_name, sales
from (select c_last_name, c_first_name, sum(cs_quantity * cs_list_price) sales
      from catalog_sales
         , customer
         , date_dim
      where d_year = 2000
        and d_moy = 2
        and cs_sold_date_sk = d_date_sk
        and cs_item_sk in (select item_sk from frequent_ss_items)
        and cs_bill_customer_sk in (select c_customer_sk from best_ss_customer)
        and cs_bill_customer_sk = c_customer_sk
      group by c_last_name, c_first_name
      union all
      select c_last_name, c_first_name, sum(ws_quantity * ws_list_price) sales
      from web_sales
         , customer
         , date_dim
      where d_year = 2000
        and d_moy = 2
        and ws_sold_date_sk = d_date_sk
        and ws_item_sk in (select item_sk from frequent_ss_items)
        and ws_bill_customer_sk in (select c_customer_sk from best_ss_customer)
        and ws_bill_customer_sk = c_customer_sk
      group by c_last_name, c_first_name)
order by c_last_name, c_first_name, sales limit 100
