package com.example.openlog.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "category_with_logs")
data class CategoryWithLogs(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "category_owner_id"
    )
    val log: List<Log>
)
