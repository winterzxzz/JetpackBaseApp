package com.example.jetpackbaseapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jetpackbaseapp.database.entity.TaskEntity
import com.example.jetpackbaseapp.database.dao.TaskDao


private const val DATABASE_VERSION = 1

private const val DATABASE_NAME = "app_database"


@Database(
    entities=[TaskEntity::class],
    version=DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDB: RoomDatabase() {
    abstract fun taskDao(): TaskDao


    companion object {
        private var instance: AppDB? = null

        operator fun invoke(context: Context): AppDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }   
            }
        }

        private fun buildDatabase(context: Context): AppDB {
            return Room.databaseBuilder(
                context = context,
                AppDB::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATE_1_2)
                .build()
        }
    }
}

private val MIGRATE_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE task_collection ADD COLUMN sort_type INTEGER NOT NULL DEFAULT 0")
    }
}