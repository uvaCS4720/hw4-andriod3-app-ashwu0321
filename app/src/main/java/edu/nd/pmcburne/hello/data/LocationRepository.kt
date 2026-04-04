package edu.nd.pmcburne.hello.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRepository(
    private val dao: LocationDao,
    private val api: PlacemarksApi
) {
    fun getAllLocations(): Flow<List<Location>> = flow {
        val entities = dao.getAllLocations()
        emit(entities.map { it.toLocation() })
    }

    fun getLocationsByTag(tag: String): Flow<List<Location>> = flow {
        val allLocations = dao.getAllLocations()
        emit(allLocations
            .filter { entity ->
                entity.tags.contains("\"$tag\"")
            }
            .map { it.toLocation() }
        )
    }

    fun getAllUniqueTags(): Flow<List<String>> = flow {
        val allLocations = dao.getAllLocations()
        val tags = mutableSetOf<String>()
        
        allLocations.forEach { entity ->
            val tagList = try {
                kotlinx.serialization.json.Json.decodeFromString<List<String>>(entity.tags)
            } catch (e: Exception) {
                emptyList()
            }
            tags.addAll(tagList)
        }
        
        emit(tags.toList().sorted())
    }

    suspend fun syncLocations() {
        try {
            val fetchedLocations = api.getLocations()
            val entities = fetchedLocations.map { dto ->
                LocationEntity(
                    id = dto.id,
                    name = dto.name,
                    tags = kotlinx.serialization.json.Json.encodeToString(dto.tag_list),
                    description = dto.description,
                    latitude = dto.visual_center.latitude,
                    longitude = dto.visual_center.longitude
                )
            }
            dao.insertLocations(entities)
        } catch (e: Exception) {
            // Silently handle error, use cached data
        }
    }
}

private fun LocationEntity.toLocation(): Location {
    val tagList = try {
        kotlinx.serialization.json.Json.decodeFromString<List<String>>(tags)
    } catch (e: Exception) {
        emptyList()
    }
    
    return Location(
        id = id,
        name = name,
        tags = tagList,
        description = description,
        latitude = latitude,
        longitude = longitude
    )
}
