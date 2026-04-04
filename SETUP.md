# Campus Maps App - Setup and Implementation Guide

## Overview
This app displays a map of UVA campus locations with filtering by tags. Data is fetched from the UVA placemarks API on first run and stored in a local SQLite database.

## Features Implemented
✓ Google Map integration using Jetpack Compose  
✓ Dropdown filter for location tags (alphabetically sorted)  
✓ Default tag set to "core" on startup  
✓ SQLite database for local data storage  
✓ API sync on first run (subsequent runs use cached data)  
✓ Markers with info windows showing location name and description  
✓ Configuration change handling (screen rotation)  

## Setup Instructions

### 1. Google Maps API Key
You need a Google Maps API key to use the Google Maps service.

**Get your API key:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable the Maps SDK for Android
4. Go to Credentials → Create API Key
5. Restrict the key to Android apps and add your app's package name and certificate

**Add your API key to the app:**
- Open `app/src/main/AndroidManifest.xml`
- Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

### 2. Android SDK Configuration
If you're building from command line:

Create `local.properties` in the root of your project:
```
sdk.dir=/path/to/your/Android/SDK
```

On Windows (example):
```
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

On Mac/Linux:
```
sdk.dir=/Users/YourUsername/Library/Android/sdk
```

### 3. Build and Run

**In Android Studio:**
- Open the project in Android Studio
- Wait for Gradle sync to complete
- Click "Run" or "Run on Device"

**From Command Line:**
```bash
# On Windows
gradlew.bat build

# On Mac/Linux
./gradlew build

# Run on emulator/device
gradlew.bat installDebug
```

## Project Structure

```
app/src/main/java/edu/nd/pmcburne/hello/
├── MainActivity.kt                 - Main activity
├── MainViewModel.kt                - ViewModel managing app state
├── data/
│   ├── models.kt                  - Data models and entities
│   ├── PlacemarksApi.kt           - Retrofit API client
│   ├── LocationDao.kt             - Room DAO interface
│   ├── LocationDatabase.kt        - Room database setup
│   └── LocationRepository.kt      - Repository for data access
└── ui/
    ├── Composables.kt            - UI components (Dropdown, Map)
    └── theme/                    - Theme and styling
```

## How It Works

### Data Flow
1. **On App Start:**
   - ViewModel loads from database
   - API sync runs (pulls latest data from UVA API)
   - Database is updated with new locations (duplicates are replaced)
   - UI displays locations for the "core" tag

2. **User Selects a Tag:**
   - ViewModel filters locations by selected tag
   - Map automatically updates with new markers

3. **User Taps a Marker:**
   - Info window appears showing location name and description

### Database
- Locations are stored in SQLite using Room ORM
- Tags are stored as JSON strings
- Each location has a unique ID (prevents duplicates)

### API
- Fetches from: `https://www.cs.virginia.edu/~wxt4gm/placemarks.json`
- Response is a JSON array of location objects
- Only fetched on app startup

## Permissions
The app requests the following permissions:
- `android.permission.INTERNET` - To fetch data from the API
- `android.permission.ACCESS_NETWORK_STATE` - To check network status
- `android.permission.ACCESS_FINE_LOCATION` - For location features (if implemented)
- `android.permission.ACCESS_COARSE_LOCATION` - For location features (if implemented)

## Troubleshooting

### "No API key found"
- Make sure you've added your Google Maps API key to AndroidManifest.xml
- Verify the key is valid and enabled for Android apps

### "SDK location not found"
- Create the `local.properties` file with the correct SDK path
- Or configure Android SDK location in Android Studio

### "No locations showing on map"
- Check that the API is returning data
- Verify the API URL is accessible
- Check that tags exist in the database

### App crashes on rotation
- The ViewModel is preserved across configuration changes
- State is maintained in StateFlow

## Dependencies
- **Jetpack Compose** - UI framework
- **Room** - Local SQLite database
- **Retrofit** - HTTP client for API calls
- **Google Maps Compose** - Map display
- **Kotlin Serialization** - JSON parsing
- **OkHttp** - HTTP client

## Notes
- The map is centered on the first location for the selected tag
- Zoom level is set to 14 (adjustable in Composables.kt)
- Info windows show the raw HTML description (as requested)
- The dropdown menu is scrollable for many tags
