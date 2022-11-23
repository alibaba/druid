select country,prod,year,s
  from sales_view_ref
  model
    partition by (country)
    dimension by (prod, year)
    measures (sale s)
    ignore nav
    unique dimension
    rules upsert sequential order
    (
      s[prod='mouse pad'] = 1,
      s['standard mouse'] = 2
    )
  order by country, prod, year

