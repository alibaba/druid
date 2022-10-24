select country, year, sale, csum
   from
   (select country, year, salex sale
    from sales_view_ref
    group by country, year
   ) m
   model dimension by (country, year)
         measures (sale, 0 csum)
         rules
            (
              s['standard mouse'] = 2
            )
   order by country, year

