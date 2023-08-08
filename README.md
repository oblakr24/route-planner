# Route Planner

A demo app displaying a list of bus routes as well as route details with step-by-step routing information on a map.

Made using the latest Android development stack and architectural and UI development patterns, including Jetpack Compose and Material Design 3.

The app will load pages automatically once the user scrolls far enough (close enough to the last item). 
The pages are "faked" on the app-side to accommodate enough data for pagination, as the current API does not support pagination (and only returns two items).

**NOTE**: When building the app, you need to provide two API keys to the build process by adding in your local.properties the following two lines:
MAPS_API_KEY=YOUR_MAPS_KEY_HERE
GEOAPIFY_API_KEY=YOUR_GEOAPIFY_KEY_HERE

Instructions:
[Geoapify API key instructions]([https://developer.android.com/jetpack/compose](https://www.geoapify.com/get-started-with-maps-api)
[Google Maps API key instructions](https://developers.google.com/maps/documentation/android-sdk/get-api-key)

## Video

![rec2](https://github.com/oblakr24/route-planner/assets/32245831/c16c83a2-2123-416c-8562-c50814f31928)

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
- [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) for serialization and deserialization of models into and from files
- [Retrofit](https://github.com/square/retrofit) for network requests
- [Extended Material icons](https://developer.android.com/jetpack/androidx/releases/compose-material) for vector images
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for persisting user preferences
- [Compose Shimmer](https://github.com/valentinilk/compose-shimmer) for loading item shimmer support
- [MockK](https://mockk.io/) for mocking in tests
- [Turbine](https://github.com/cashapp/turbine) for testing Flows

## Screenshots

<b>Routes Listing & Drawer</b>

<p align="center">
  <img src="https://github.com/oblakr24/route-planner/assets/32245831/f8c5c6db-eb71-4367-9adf-96a63e9fe3db" width="270" height="480">
  <img src="https://github.com/oblakr24/route-planner/assets/32245831/a6cb7ccd-6f0d-43e8-bc44-ae9738db52c1" width="270" height="480">
</p>

<b>Route details</b>

<p align="center">
  <img src="https://github.com/oblakr24/route-planner/assets/32245831/f13eceb4-2c55-4fe1-b201-545b257f5df5" width="270" height="480">
  <img src="https://github.com/oblakr24/route-planner/assets/32245831/c597cfeb-bf8d-48b3-b6a1-cbc55210dc77" width="270" height="480">
</p>
