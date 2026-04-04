package edu.nd.pmcburne.hello.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocationRepository(
    private val dao: LocationDao,
    private val api: PlacemarksApi
) {
    fun getAllLocations(): Flow<List<Location>> = flow {
        val entities = dao.getAllLocations()
        emit(entities.map { it.toLocation() })
    }.flowOn(Dispatchers.IO)

    fun getLocationsByTag(tag: String): Flow<List<Location>> = flow {
        val allLocations = dao.getAllLocations()
        emit(
            allLocations
                .filter { entity ->
                    val tagList = try {
                        Json.decodeFromString<List<String>>(entity.tags)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    tag in tagList
                }
                .map { it.toLocation() }
        )
    }.flowOn(Dispatchers.IO)

    fun getAllUniqueTags(): Flow<List<String>> = flow {
        val allLocations = dao.getAllLocations()
        val tags = mutableSetOf<String>()
        allLocations.forEach { entity ->
            val tagList = try {
                Json.decodeFromString<List<String>>(entity.tags)
            } catch (e: Exception) {
                emptyList()
            }
            tags.addAll(tagList)
        }
        emit(tags.toList().sorted())
    }.flowOn(Dispatchers.IO)

    /** Loads and filters on a background thread (not the main dispatcher). */
    suspend fun getLocationsByTagSync(tag: String): List<Location> =
        withContext(Dispatchers.IO) {
            dao.getAllLocations()
                .filter { entity ->
                    val tagList = try {
                        Json.decodeFromString<List<String>>(entity.tags)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    tag in tagList
                }
                .map { it.toLocation() }
        }

    suspend fun syncLocations() {
        try {
            val fetchedLocations = api.getLocations()
            val entities = fetchedLocations.map { dto ->
                LocationEntity(
                    id = dto.id,
                    name = dto.name,
                    tags = Json.encodeToString<List<String>>(dto.tag_list),
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
        Json.decodeFromString<List<String>>(tags)
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
