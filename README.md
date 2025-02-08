# Filmora 🎬

Filmora is a modern Android application that provides users with detailed information about movies, TV shows, and celebrities using [The Movie Database (TMDB) API](https://developer.themoviedb.org/). With a clean and modular architecture, along with an engaging user interface, Filmora offers a seamless and up-to-date entertainment discovery experience.

## Technologies Used

- **Kotlin**: The primary programming language for the app.
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Kotlin Coroutines & Flow**: For asynchronous programming and data stream management.
- **Jetpack Components**:
    - ViewModel
    - LiveData
    - Room Database
    - Navigation Component
    - Paging 3
- **Retrofit**: For networking and API calls.
- **Hilt**: For dependency injection.
- **Coil**: For image loading.
- **Material Design**: For UI components and theming.
- **The Movie Database (TMDB) API**: For fetching data.

## Project Structure

The project follows a clean and modular architecture. Here's an overview of the directory structure:

```
amir-azari-filmora/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/azari/amirhossein/filmora/
│   │   │   │   ├── adapter/          # Adapters for RecyclerViews
│   │   │   │   ├── data/             # Data layer (database, network, repository)
│   │   │   │   ├── models/           # Data models and API responses
│   │   │   │   ├── paging/           # Paging sources for pagination
│   │   │   │   ├── ui/               # UI components (activities, fragments)
│   │   │   │   ├── utils/            # Utility classes and extensions
│   │   │   │   └── viewmodel/        # ViewModels for UI logic
│   │   │   └── res/                  # Resources (layouts, drawables, strings, etc.)
│   │   └── test/                     # Unit tests
│   └── build.gradle.kts              # App-level build configuration
├── gradle/                           # Gradle configuration files
└── build.gradle.kts                  # Project-level build configuration
```

---