select country,prod,year,s
from sales_view_ref
model
partition by (country)
dimension by (prod, year)
measures (sale s)
ignore nav
-- cell_reference_options
unique dimension
-- here starts model_rules_clause
rules upsert sequential order
(
s[prod='mouse pad', year=2001] = s['mouse pad', 1999] + s['mouse pad', 2000],
s['standard mouse', 2002] = s['standard mouse', 2001]
)
order by country, prod, year


