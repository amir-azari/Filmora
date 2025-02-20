package azari.amirhossein.filmora.models.detail


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseReviews(
    @SerializedName("id")
    val id: Int?, // 839033
    @SerializedName("page")
    val page: Int?, // 1
    @SerializedName("results")
    val results: List<Result?>?,
    @SerializedName("total_pages")
    val totalPages: Int?, // 1
    @SerializedName("total_results")
    val totalResults: Int? // 1
) : Parcelable {
    @Parcelize
    data class Result(
        @SerializedName("author")
        val author: String?, // CinemaSerf
        @SerializedName("author_details")
        val authorDetails: AuthorDetails?,
        @SerializedName("content")
        val content: String?, // When the King decides to decline an offer for a strategic betrothal for his daughter "Héra", a duel ensues that sees the would-be suitor "Wulf" swear vengeance on the house of "Helm Hammerhand" and the kingdom of "Rohan" before he flees to the mountains. Before too long, he has made his presence felt amongst the usually warring hill tribes and is soon a force to be reckoned with by the King. With his capital being attacked, "Helm" must lead from the front - but can he trust all of his nobles? It turns out that his daughter and this rebel were childhood friends but it's not just him she doesn't want to marry, she has far more independent designs for her life that don't include marriage at all, and these are actively encouraged by her handmaiden - herself a woman to be reckoned with. With duplicity and tragedy all around her, she must lead her people to the safety of the "Hornberg" to avoid the harshness of both her enemy and the brutal winter, but can they survive the ordeal that awaits them. This is a perfectly watchable animated fantasy adventure with the odd snippet of Howard Shore's original "Lord of the Rings - Two Towers" (2002) score to pep it up, but the standard of the animation is really quite underwhelming. It's all very two-dimensional and it seemed to me that virtually no effort has been made to synchronise the lips and the voices. The story is adequate enough, but stretched really quite thinly as it tops the two hour mark without anywhere near enough going on. There's isn't so much a war as a siege, and after about half an hour it felt more like this was an introduction to a character who is going to - whether JRR Tolkien likes it or not - deliver some more Middle Earth adventures for sequels galore. It really underplays the characterful mythology of the topic and takes a very disappointingly "Janet and John" approach to the mysteries and evils of that realm which was so well captured twenty-odd years ago by Peter Jackson.
        @SerializedName("created_at")
        val createdAt: String?, // 2024-12-17T12:53:12.960Z
        @SerializedName("id")
        val id: String?, // 676174387bf386051518a493
        @SerializedName("updated_at")
        val updatedAt: String?, // 2024-12-17T12:53:13.088Z
        @SerializedName("url")
        val url: String? // https://www.themoviedb.org/review/676174387bf386051518a493
    ) : Parcelable {
        @Parcelize
        data class AuthorDetails(
            @SerializedName("avatar_path")
            val avatarPath: String?, // /yz2HPme8NPLne0mM8tBnZ5ZWJzf.jpg
            @SerializedName("name")
            val name: String?, // CinemaSerf
            @SerializedName("rating")
            val rating: Double?, // 6
            @SerializedName("username")
            val username: String? // Geronimo1967
        ) : Parcelable
    }
}