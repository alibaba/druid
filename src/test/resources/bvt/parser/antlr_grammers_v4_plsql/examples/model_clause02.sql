select country, year, sale, csum
   from 
   (select country, year, sum(sale) sale
    from sales_view_ref
    group by country, year
   )
   model dimension by (country, year)
         measures (sale, 0 csum) 
         rules (csum[any, any]= 
                  sum(sale) over (partition by country 
                                  order by year 
                                  rows unbounded preceding) 
                )
   order by country, year


