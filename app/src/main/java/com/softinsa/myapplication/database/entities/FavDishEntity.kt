package com.softinsa.myapplication.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// adicionar o plug in no gradle " id 'kotlin-parcelize'"
@Parcelize
@Entity(tableName = "fav_dishes_table")
data class FavDishEntity(

    @ColumnInfo val image: String,
    @ColumnInfo val imageSource: String,
    @ColumnInfo val title: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val ingredients: String,
    // Override column name with "coocking_time" instead of variable name ""
    // Na BD não deve haver maiusculas. Alterar nome da coluna para usar "_"
    @ColumnInfo(name = "coocking_time") val cookingTime: String,
    @ColumnInfo(name = "instructions") val directionToCook: String,
    @ColumnInfo(name = "favorite_dish") var favoriteDish: Boolean = false,

    @PrimaryKey(autoGenerate = true) val id: Int = 0,

): Parcelable
