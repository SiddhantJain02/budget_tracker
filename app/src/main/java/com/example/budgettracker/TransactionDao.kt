package com.example.budgettracker

import androidx.room.*
//TRANSACTION DATA
@Dao
interface TransactionDao {
   @Query("SELECT * from transactions")
   fun getAll(): List<Transaction>

   @Insert
   fun insertAll(vararg transaction: Transaction)

   @Delete
   fun delete(transaction: Transaction)

   @Query("DELETE FROM transactions")
   fun deleteAll()

   @Update
   fun update(vararg transaction: Transaction)
}

