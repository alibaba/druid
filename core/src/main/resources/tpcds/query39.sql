with inv as
         (select w_warehouse_name
               , w_warehouse_sk
               , i_item_sk
               , d_moy
               , stdev
               , mean
               , case mean when 0 then null else stdev / mean end cov
          from (select w_warehouse_name
                     , w_warehouse_sk
                     , i_item_sk
                     , d_moy
                     , stddev_samp(inv_quantity_on_hand) stdev
                     , avg(inv_quantity_on_hand)         mean
                from inventory
                   , item
                   , warehouse
                   , date_dim
                where inv_item_sk = i_item_sk
                  and inv_warehouse_sk = w_warehouse_sk
                  and inv_date_sk = d_date_sk
                  and d_year = 2001
                group by w_warehouse_name, w_warehouse_sk, i_item_sk, d_moy) foo
          where case mean when 0 then 0 else stdev / mean end > 1)
select inv1.w_warehouse_sk
     , inv1.i_item_sk
     , inv1.d_moy
     , inv1.mean
     , inv1.cov
     , inv2.w_warehouse_sk as w_warehouse_sk_2
     , inv2.i_item_sk      as i_item_sk_2
     , inv2.d_moy          as d_moy_2
     , inv2.mean           as mean_2
     , inv2.cov            as cov_2
from inv inv1,
     inv inv2
where inv1.i_item_sk = inv2.i_item_sk
  and inv1.w_warehouse_sk = inv2.w_warehouse_sk
  and inv1.d_moy = 1
  and inv2.d_moy = 1 + 1
order by inv1.w_warehouse_sk, inv1.i_item_sk, inv1.d_moy, inv1.mean, inv1.cov
       , d_moy_2, mean_2, cov_2
;
with inv as
         (select w_warehouse_name
               , w_warehouse_sk
               , i_item_sk
               , d_moy
               , stdev
               , mean
               , case mean when 0 then null else stdev / mean end cov
          from (select w_warehouse_name
                     , w_warehouse_sk
                     , i_item_sk
                     , d_moy
                     , stddev_samp(inv_quantity_on_hand) stdev
                     , avg(inv_quantity_on_hand)         mean
                from inventory
                   , item
                   , warehouse
                   , date_dim
                where inv_item_sk = i_item_sk
                  and inv_warehouse_sk = w_warehouse_sk
                  and inv_date_sk = d_date_sk
                  and d_year = 2001
                group by w_warehouse_name, w_warehouse_sk, i_item_sk, d_moy) foo
          where case mean when 0 then 0 else stdev / mean end > 1)
select inv1.w_warehouse_sk
     , inv1.i_item_sk
     , inv1.d_moy
     , inv1.mean
     , inv1.cov
     , inv2.w_warehouse_sk as w_warehouse_sk_2
     , inv2.i_item_sk      as i_item_sk_2
     , inv2.d_moy          as d_moy_2
     , inv2.mean           as mean_2
     , inv2.cov            as cov_2
from inv inv1,
     inv inv2
where inv1.i_item_sk = inv2.i_item_sk
  and inv1.w_warehouse_sk = inv2.w_warehouse_sk
  and inv1.d_moy = 1
  and inv2.d_moy = 1 + 1
  and inv1.cov > 1.5
order by inv1.w_warehouse_sk, inv1.i_item_sk, inv1.d_moy, inv1.mean, inv1.cov
       , d_moy_2, mean_2, cov_2
