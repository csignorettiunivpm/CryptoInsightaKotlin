package com.example.cryptoinsighta.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val ASSE_Id: Int = 0,
    val ASSE_nome: String,
    val ASSE_ticker: String,
    val ASSE_iconURL: String,
    val ASSE_descrizione: String = "",
    val ASSE_qtaPosseduta: Double = 0.0,
    val ASSE_categoria: String  // "Azione", "Crypto", "Materia Prima"
)