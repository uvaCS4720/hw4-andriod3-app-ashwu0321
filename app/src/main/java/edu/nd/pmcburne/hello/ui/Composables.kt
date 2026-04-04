package edu.nd.pmcburne.hello.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import edu.nd.pmcburne.hello.MainViewModel
import edu.nd.pmcburne.hello.data.Location

@Composable
fun TagDropdown(
    availableTags: List<String>,
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tag: $selectedTag")
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LazyColumn {
                items(availableTags) { tag ->
                    DropdownMenuItem(
                        text = { Text(tag) },
                        onClick = {
                            onTagSelected(tag)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CampusMapView(
    locations: List<Location>,
    modifier: Modifier = Modifier
) {
    if (locations.isEmpty()) {
        return
    }

    // Calculate initial camera position based on first location
    val initialLat = locations.firstOrNull()?.latitude ?: 38.0
    val initialLng = locations.firstOrNull()?.longitude ?: -78.0
    
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            LatLng(initialLat, initialLng),
            14f
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxWidth().height(600.dp),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { location ->
            Marker(
                state = rememberMarkerState(position = LatLng(location.latitude, location.longitude)),
                title = location.name,
                snippet = location.description
            )
        }
    }
}

@Composable
fun CampusMapScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Tag Dropdown
        TagDropdown(
            availableTags = uiState.availableTags,
            selectedTag = uiState.selectedTag,
            onTagSelected = { viewModel.selectTag(it) },
            modifier = Modifier.padding(16.dp)
        )
        
        // Map view
        if (uiState.filteredLocations.isNotEmpty()) {
            CampusMapView(
                locations = uiState.filteredLocations,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        } else {
            Text(
                "No locations found for tag: ${uiState.selectedTag}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
