# Filmora 🎬

Filmora is a modern Android application that provides users with detailed information about movies, TV shows, and celebrities using [The Movie Database (TMDB) API](https://developer.themoviedb.org/). With a clean and modular architecture, along with an engaging user interface, Filmora offers a seamless and up-to-date entertainment discovery experience.

## Technologies Used

- **Kotlin**: The primary programming language for the app.
- **Architecture Pattern**: MVVM (Model-View-ViewModel) combined with a Single Activity approach for streamlined navigation and activity management.
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
## Key Features

### Discover and Explore

- **Detailed Information:**  
  Access comprehensive details about movies, TV shows, and celebrities.

- **Extensive Browsing Options:**  
  Easily explore various categories including:
  - **Movies:** Trending, Popular, Upcoming, Now Playing in Theatres, and Top-Rated.
  - **TV Shows:** Trending, Popular, On TV, Airing Today, and Top-Rated.

- **Advanced Search:**  
  Quickly find movies, TV shows, and actors with robust search functionality.

### Personalized Experience

- **Tailored Recommendations:**  
  Receive personalized suggestions and "You May Like" recommendations based on your viewing preferences.

- **Profile Management:**  
  Enjoy a customized experience by managing your favorites, watchlists, and ratings.

### Movie and TV Show Details

- **High-Quality Visuals:**  
  Browse high-resolution poster galleries and immersive backdrop displays.

- **Video Trailers:**  
  Watch movie trailers seamlessly via YouTube integration.

- **Cast & Crew Information:**  
  View detailed information about cast and crew members, with clear department categorization (e.g., Acting, Directing, Writing).

- **and** ...

### Celebrity Career Overviews

- **In-Depth Filmography:**  
  Explore career overviews with clear categorization between acting roles and production contributions.

- **Department-Specific Contributions:**  
  Discover detailed contributions in specific areas such as Directing, Writing, Cinematography, etc.

- **Biographical Insights:**  
  Get essential details including age, birthday, birthplace, and a full biography.

- **Iconic Roles Highlighted:**  
  Review the "Known For" section that showcases each celebrity’s most memorable roles.

- **Direct Work Connections:**  
  Easily navigate to the celebrity’s works within the app for a more integrated experience.

### Enhanced User Experience

- **Offline Caching:**  
  Benefit from offline caching to ensure a smooth and uninterrupted user experience.

- **Theme Support:**  
  Switch between Dark and Light themes according to your preference, enhancing visual comfort in any setting.

- **and** ...
---
## Screenshots & Demo

### App Screenshots
<div align="center">
    <img src="/docs/screenshots/login screen.png" alt="Login Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/home screen.png" alt="Home Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/celebrities screen.png" alt="Celebrities Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/tv show details.png" alt="TV Show Details Screen" width="24%" height="auto" />
</div>
<details>
<summary><b>📱 Click to Show More Screenshots</b></summary>
<div align="center">
    <img src="/docs/screenshots/movies screen.png" alt="Movies Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/tv shows screen.png" alt="TV Shows Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/search screen.png" alt="Search Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/movie details.png" alt="Movie Details Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/people deatils.png" alt="People Details Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/profile screen.png" alt="Profile Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/movies taste screen.png" alt="Movies Taste Screen" width="24%" height="auto" />
    <img src="/docs/screenshots/series taste screen.png" alt="Series Taste Screen" width="24%" height="auto" />
</div>
</details>

### Interactive Demos

<details>
<summary><b>🎬 Click to View Demos</b></summary>

<div align="center">
    <div>
        <p><b>Authentication & Personalization</b></p>
        <img src="/docs/demo/login.gif" alt="Login Demo" width="24%" height="auto" />
        <img src="/docs/demo/select%20taste%20movies.gif" alt="Movie Preferences Demo" width="24%" height="auto" />
        <img src="/docs/demo/select%20taste%20sreies.gif" alt="Series Preferences Demo" width="24%" height="auto" />
    </div>
    <br/>
    <div>
        <p><b>Content Browsing</b></p>
        <img src="/docs/demo/home%20screen.gif" alt="Home Screen Demo" width="24%" height="auto" />
        <img src="/docs/demo/movie%20screen.gif" alt="Movie Screen Demo" width="24%" height="auto" />
        <img src="/docs/demo/tv%20show%20screen.gif" alt="TV Show Screen Demo" width="24%" height="auto" />
        <img src="/docs/demo/celebrities%20screen.gif" alt="Celebrities Screen Demo" width="24%" height="auto" />
    </div>
    <br/>
    <div>
        <p><b>User Features</b></p>
        <img src="/docs/demo/moviedetails.gif" alt="Movie Details Demo" width="24%" height="auto" />
        <img src="/docs/demo/profile%20screen.gif" alt="Profile Screen Demo" width="24%" height="auto" />
        <img src="/docs/demo/search%20screen.gif" alt="Search Screen Demo" width="24%" height="auto" />
    </div>
</div>
</details>
