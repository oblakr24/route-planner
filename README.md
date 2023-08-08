# Route Planner

A demo app displaying a list of bus routes as well as route details with step-by-step routing information on a map.

Made using the latest Android development stack and architectural and UI development patterns, including Jetpack Compose and Material Design 3.

The app will load pages automatically once the user scrolls far enough (close enough to the last item). 
The pages are "faked" on the app-side to accommodate enough data for pagination, as the current API does not support pagination (and only returns two items).

## Video



## Features

1. **Listing routes:** Bus routes are listed in an infinitely scrollable list, with pages loaded dynamically. Scroll to-refresh to refresh the data.

2. **Route details:** The route can be opened to reveal the details, with the route displayed on a full-screen map, and routing info in an expandable bottom sheet.

3. **Routing:** Expand individual leg sections for step-by-step routing instructions, students to pick up and click on the corresponding button to pan to that stop on the map.

4. **Dark mode:** Toggle to override system settings. When toggled, the preference is persisted.

## Technologies

**This app is made using:**

- Jetpack Compose for UI with Material 3
- A mix of MVVM/MVI clean architecture with unidirectional data flows
- Datastore for preferences persistence
- Standardized design and typography to match Material 3 and easy customization

**Stack:**
- MVVM architecture (mix of MVVM and MVI)
- [Jetpack Compose](https://developer.android.com/jetpack/compose) and [Compose Navigation](https://developer.android.com/jetpack/compose/navigation) for UI rendering
- [Maps Compose](https://developers.google.com/maps/documentation/android-sdk/maps-compose) for map display
- [Geoapify](https://www.geoapify.com/routing-api) for routing calculation
- [Hilt](https://dagger.dev/hilt/) for dependency injection
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) for serialization and deserialization of models into and from files
- [Retrofit](https://github.com/square/retrofit) for network requests
- [Extended Material icons](https://developer.android.com/jetpack/androidx/releases/compose-material) for vector images
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for persisting user preferences
- [Compose Shimmer](https://github.com/valentinilk/compose-shimmer) for loading item shimmer support
- [MockK](https://mockk.io/) for mocking in tests
- [Turbine](https://github.com/cashapp/turbine) for testing Flows

## Screenshots

<b>Routes Listing</b>



<b>Route details</b>


