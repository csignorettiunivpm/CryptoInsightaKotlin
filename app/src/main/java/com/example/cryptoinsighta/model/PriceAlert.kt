package com.example.cryptoinsighta.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "price_alerts",
    foreignKeys = [ForeignKey(
        entity = Asset::class,
        parentColumns = ["ASSE_Id"],
        childColumns = ["PRAS_Id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val PRAL_Id: Int = 0,
    val PRAS_Id: Int,              //è la foreign key per l'asset
    val PRAL_prezzoSoglia: Double,
    val PRAL_tipoSoglia: String,   // "ABOVE" o "BELOW"
    val PRAL_isAttivo: Boolean = true
)