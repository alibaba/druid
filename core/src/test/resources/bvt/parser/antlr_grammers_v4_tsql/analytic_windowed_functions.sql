USE AdventureWorks2012;
GO

SELECT FIRST_VALUE(SalesOrderNumber) OVER(PARTITION BY CustomerID ORDER BY OrderDate) 
	AS FirstSONumberPerCustomer
FROM Sales.SalesOrderHeader;


SELECT LAST_VALUE(SalesOrderNumber) OVER(PARTITION BY CustomerID ORDER BY OrderDate ROWS UNBOUNDED PRECEDING) 
	AS FirstSONumberPerCustomer
FROM Sales.SalesOrderHeader;

SELECT LAG(PurchaseOrderNumber,2) OVER(PARTITION BY CustomerID ORDER BY OrderDate) AS PrevPONumberOffset2
FROM sales.SalesOrderHeader;

SELECT LEAD(PurchaseOrderNumber) OVER(PARTITION BY CustomerID ORDER BY OrderDate) AS NextPONumber
FROM sales.SalesOrderHeader;
