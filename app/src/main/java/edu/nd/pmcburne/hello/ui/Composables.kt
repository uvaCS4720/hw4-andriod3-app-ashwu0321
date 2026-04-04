package edu.nd.pmcburne.hello.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import edu.nd.pmcburne.hello.MainViewModel
import edu.nd.pmcburne.hello.data.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDropdown(
    availableTags: List<String>,
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedTag,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by Tag") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableTags.forEach { tag ->
                    DropdownMenuItem(
                        text = { Text(tag) },
                        onClick = {
                            onTagSelected(tag)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
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
    // Default to UVA's center if no locations are present
    val uvaCenter = LatLng(38.0356, -78.5034)
    
    val cameraPositionState = rememberCameraPositionState {
        val initialLocation = locations.firstOrNull()
        position = CameraPosition.fromLatLngZoom(
            if (initialLocation != null) LatLng(initialLocation.latitude, initialLocation.longitude) else uvaCenter,
            15f
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { location ->
            key(location.id) {
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(location.latitude, location.longitude)
                    ),
                    title = location.name,
                    snippet = location.description
                )
            }
        }
    }
}

@Composable
fun CampusMapScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = modifier.fillMaxSize()) {
        // Tag Dropdown
        TagDropdown(
            availableTags = uiState.availableTags,
            selectedTag = uiState.selectedTag,
            onTagSelected = { viewModel.selectTag(it) },
            modifier = Modifier.padding(16.dp)
        )

        uiState.error?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        // Map view
        Box(modifier = Modifier.weight(1f)) {
            CampusMapView(
                locations = uiState.filteredLocations
            )

            if (uiState.isLoading) {
                // You could add a CircularProgressIndicator here
            }
        }
    }
}
