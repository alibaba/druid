SELECT h1.CRTYPE , 
CASE 'month'
          WHEN 'week' THEN TO_CHAR(h1.DateFrom, 'YYYYIW') 
          ELSE to_char(h1.DateFrom,'YYYYMM') 
END 
FROM CQ_CHANGEREQUESTS h1