package com.example.cryptoinsighta.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = Asset::class,
        parentColumns = ["ASSE_Id"],
        childColumns = ["TRAS_Id"],
        onDelete = ForeignKey.CASCADE  // sull'eliminazione cancello anche tutte le transazioni dell'utente associate all'asset
    )]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val TRAN_Id: Int = 0,
    val TRAS_Id: Int,              // è la foreign key per l'asset
    val TRAN_tipo: String,         // "BUY" o "SELL"
    val TRAN_qta: Double,
    val TRAN_prezzoUnitario: Double,
    val TRAN_dataOra: Long,
    val TRAN_note: String = ""
)
