select c_last_name,
       c_first_name,
       substr(s_city, 1, 30),
       ss_ticket_number,
       amt,
       profit
from (select ss_ticket_number
           , ss_customer_sk
           , store.s_city
           , sum(ss_coupon_amt) amt
           , sum(ss_net_profit) profit
      from store_sales,
           date_dim,
           store,
           household_demographics
      where store_sales.ss_sold_date_sk = date_dim.d_date_sk
        and store_sales.ss_store_sk = store.s_store_sk
        and store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
        and (household_demographics.hd_dep_count = 6 or household_demographics.hd_vehicle_count > 2)
        and date_dim.d_dow = 1
        and date_dim.d_year in (1999, 1999 + 1, 1999 + 2)
        and store.s_number_employees between 200 and 295
      group by ss_ticket_number, ss_customer_sk, ss_addr_sk, store.s_city) ms,
     customer
where ss_customer_sk = c_customer_sk
order by c_last_name, c_first_name, substr(s_city, 1, 30), profit limit 100
