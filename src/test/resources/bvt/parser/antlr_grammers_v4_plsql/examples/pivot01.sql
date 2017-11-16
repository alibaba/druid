select * from pivot_table
  unpivot (yearly_total for order_mode in (store as 'direct',
           internet as 'online'))
  order by year, order_mode


