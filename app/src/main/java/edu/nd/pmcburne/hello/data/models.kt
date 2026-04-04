package edu.nd.pmcburne.hello.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class VisualCenter(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val tag_list: List<String>,
    val description: String,
    val visual_center: VisualCenter
)

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val tags: String, // JSON string of tag list
    val description: String,
    val latitude: Double,
    val longitude: Double
)

data class Location(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

class ListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun toString(value: List<String>): String {
        return Json.encodeToString(value)
    }
}
