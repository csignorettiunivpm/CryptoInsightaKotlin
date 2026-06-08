package com.example.cryptoinsighta.dao

import androidx.room.*
import com.example.cryptoinsighta.model.Asset
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets")
    //qua non ci va suspend perchè room sa già che questi dati devono viaggiare in background, viene già gestito tutto con flow
    suspend fun getAllAssets(): List<Asset>
    @Query("SELECT * FROM assets WHERE ASSE_Id = :id")
    suspend fun getAssetById(id: Int): Asset

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset):Long
    //suspend - azione immediata e pesante, serve per spostare momentaneamente il lavoro su un thread secondario, per far si che quando vengano inseriti i dati non si debba aspettare

    @Update
    suspend fun updateAsset(asset: Asset)

    @Delete
    suspend fun deleteAsset(asset: Asset)

    @Query("DELETE FROM assets")
    suspend fun svuotaTabella()
    @Query("SELECT * FROM assets WHERE ASSE_Id = :assetId")
    suspend fun getAssetByIdOnce(assetId: Int):Asset

}