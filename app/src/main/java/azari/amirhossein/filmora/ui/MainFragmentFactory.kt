package azari.amirhossein.filmora.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import azari.amirhossein.filmora.ui.home.HomeFragment
import azari.amirhossein.filmora.ui.movies.MovieFragment
import azari.amirhossein.filmora.ui.people.PeopleFragment
import azari.amirhossein.filmora.ui.tvs.TvFragment

class MainFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            HomeFragment::class.java.name,
            MovieFragment::class.java.name,
            TvFragment::class.java.name,
            PeopleFragment::class.java.name -> {
                DummyFragment()
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}
