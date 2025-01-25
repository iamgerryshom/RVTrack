# RVTrackView

RVTrackView is a custom Android View that provides an interactive indicator system to visually track the horizontal scroll position of a `RecyclerView`. It dynamically updates the position of indicators based on the scroll state of the `RecyclerView` and is designed to work with `LinearLayoutManager`.

## Features

- Syncs indicator positions with horizontal scrolling of a `RecyclerView`.
- Displays active and inactive circle indicators.
- Supports custom indicator radius and gap size.
- Automatically adjusts to the number of items in the `RecyclerView`.
- Allows easy integration by attaching to a `RecyclerView`.

## Getting Started

To use RVTrackView in your project:

1. Add the `RVTrackView` class to your project.
2. Include it in your layout XML file as follows:

```xml
<com.wid.rvtrack.RVTrackView
    android:id="@+id/rvTrackView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:recyclerView="@id/recyclerView" />
