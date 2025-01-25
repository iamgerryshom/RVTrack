# RVTrack

RVTrack is a custom Android library that provides an interactive indicator system to visually track the horizontal scroll position of a `RecyclerView`. It dynamically updates the position of indicators based on the scroll state of the `RecyclerView` and is designed to work with `LinearLayoutManager` as of the moment.

## Features

- Syncs indicator positions with the horizontal scrolling of a `RecyclerView`.
- Displays active and inactive circle indicators.
- Supports customizable indicator radius and gap size.
- Automatically adjusts to the number of items in the `RecyclerView`.
- Easy integration by attaching to a `RecyclerView`.

## Getting Started

To use RVTrackView in your project:

1. **Add the following dependency to your `build.gradle` file:**
   ```gradle
   dependencies {
	        implementation 'com.github.iamgerryshom:RVTrack:1.7.0'
	}
   ```
3. **Include it in your layout XML file as follows:**
   ```xml
   <com.wid.rvtrack.RVTrackView
    android:id="@+id/rvTrackView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:inactive_indicator_color="#717171"
    app:active_indicator_color="#FFFFFF"
    app:circle_indicator_radius="6dp"
    app:recycler_view="@id/recyclerView" />
   
4. **Alternatively, to set it programmatically:**

   You can attach the `RVTrackView` to your `RecyclerView` directly in your Java or Kotlin code like this:

   ```java
   final RecyclerView recyclerView = findViewById(R.id.recyclerView);
   final RVTrackView rvTrackView = findViewById(R.id.rvTrackView);
   rvTrackView.attachToRecyclerView(recyclerView);
   ```

## Demo Video

You can watch the demo video here:

![Demo Video](./RVTrack/assets/demo_video.mp4)


