package com.example.cryptoinsighta.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "price_history",
    foreignKeys = [ForeignKey(
        entity = Asset::class,
        parentColumns = ["ASSE_Id"],
        childColumns = ["PRAS_Id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PriceHistory(
    @PrimaryKey(autoGenerate = true)
    val PRHI_Id: Int = 0,
    val PRAS_Id: Int,              // è la foreing key per l'asset
    val PRHI_prezzo: Double,
    val PRHI_dataOra: Long,         // timestamp in millisecondi
)