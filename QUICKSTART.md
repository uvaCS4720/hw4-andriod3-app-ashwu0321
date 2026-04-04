# Quick Start Checklist

## Before Running the App

### 1. ✓ Get Google Maps API Key
- [ ] Go to https://console.cloud.google.com/
- [ ] Create a new project or select existing one
- [ ] Enable "Maps SDK for Android"
- [ ] Create an API key (Credentials page)
- [ ] Restrict key to Android apps

### 2. ✓ Add API Key to App
- [ ] Open `app/src/main/AndroidManifest.xml`
- [ ] Find the line with `android:value="YOUR_GOOGLE_MAPS_API_KEY_HERE"`
- [ ] Replace it with your actual API key

### 3. ✓ Configure Android SDK (Command Line Only)
- [ ] Create `local.properties` file in project root
- [ ] Add `sdk.dir=C:\path\to\Android\Sdk` (Windows) or appropriate path for your OS

### 4. ✓ Build and Run
**Android Studio:**
- [ ] Open project in Android Studio
- [ ] Wait for Gradle sync
- [ ] Click Run → Run 'app'

**Command Line:**
- [ ] Run: `gradlew build`
- [ ] Run: `gradlew installDebug`

## What the App Does

✓ **First Launch:**
- Downloads location data from UVA placemarks API
- Stores in local SQLite database
- Shows locations tagged with "core"

✓ **Dropdown Filter:**
- Select any tag from alphabetical list
- Map updates automatically with filtered locations

✓ **Tap Markers:**
- Click on any map marker
- Info window shows building name and description

✓ **Configuration Changes:**
- Survives screen rotation
- Preserves selected tag and location data

## Architecture Overview

```
MainActivity
    ↓
CampusMapScreen (Composable)
    ├── TagDropdown
    └── CampusMapView
        ↓
MainViewModel
    ├── LocationRepository
    │   ├── LocationDatabase (Room)
    │   └── PlacemarksApi (Retrofit)
    ↓
app/src/main/
    ├── data/ (Models, DB, API, Repository)
    ├── ui/ (Composables, Theme)
    └── resources/
```

## Testing Checklist

- [ ] App launches without crashing
- [ ] Initial "core" tag is shown on startup
- [ ] Dropdown shows alphabetically sorted tags
- [ ] Selecting a tag updates markers on map
- [ ] Tapping a marker shows info window
- [ ] Screen rotation preserves state
- [ ] Launching app twice doesn't duplicate data in database
- [ ] Map displays all locations for selected tag

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "No API key" error | Check AndroidManifest.xml has correct key |
| Map is blank | Verify API key is enabled for Android |
| No locations showing | Ensure network access, API endpoint is reachable |
| App crashes | Check logcat: `adb logcat` |
| SDK error | Create local.properties with correct sdk.dir path |

## File Structure Summary

**Key Implementation Files:**
- `app/src/main/java/edu/nd/pmcburne/hello/data/` - Database & API
- `app/src/main/java/edu/nd/pmcburne/hello/ui/Composables.kt` - UI Components  
- `app/src/main/java/edu/nd/pmcburne/hello/MainViewModel.kt` - State Management

**Configuration:**
- `app/src/main/AndroidManifest.xml` - Permissions & API Key
- `app/build.gradle.kts` - Dependencies

See `SETUP.md` for detailed setup and architecture information.
