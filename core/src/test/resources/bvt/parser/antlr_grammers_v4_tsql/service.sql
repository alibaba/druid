CREATE SERVICE [//Adventure-Works.com/Expenses]  
    ON QUEUE [dbo].[ExpenseQueue]  
    ([//Adventure-Works.com/Expenses/ExpenseSubmission]) 
CREATE SERVICE [//Adventure-Works.com/Expenses] ON QUEUE ExpenseQueue
    ([//Adventure-Works.com/Expenses/ExpenseSubmission],
     [//Adventure-Works.com/Expenses/ExpenseProcessing]) ;
CREATE SERVICE [//Adventure-Works.com/Expenses] ON QUEUE ExpenseQueue ;
