@file:Suppress("unused", "UNUSED_PARAMETER")

package london.kotlin.night

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.StringEscapeUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.elasticsearch.index.query.QueryBuilders.*
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Test
import java.util.concurrent.Executors

/*


in big text:

10 Things I'm Loving About Kotlin

my email & title

[v quick intro about Kaleida]





figlet -f doh 1 | cowsay -n


 ____________
/            \
|  @@@@@@    |
| @@@@@@@@   |
|      @@@   |
|     @!@    |
|    !!@     |
|   !!:      |
|  !:!       |
| :!:        |
| :: :::::   |
| :: : :::   |
\            /
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||



 ______________________
/                      \
|  222222222222222     |
| 2:::::::::::::::22   |
| 2::::::222222:::::2  |
| 2222222     2:::::2  |
|             2:::::2  |
|             2:::::2  |
|          2222::::2   |
|     22222::::::22    |
|   22::::::::222      |
|  2:::::22222         |
| 2:::::2              |
| 2:::::2              |
| 2:::::2       222222 |
| 2::::::2222222:::::2 |
| 2::::::::::::::::::2 |
| 22222222222222222222 |
\                      /
 ----------------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||
*/

/* Opening poll */
val percentUsingKotlinInProduction = 10
val percentPlayedWithKotlin = 30
val percentPrimarilyJavaDevelopers = 80
val percentPrimarilyOtherJvmLanguages = 20








class AudienceProfileTest {
    @Test
    fun haveIGuessedTheAudienceCorrectly() {

        val softly = SoftAssertions()

        softly.assertThat(percentUsingKotlinInProduction)
            .withFailMessage("Opps this may be teaching you to suck eggs, " +
                "but at least the talk should be quick")
            .isLessThan(50)

        softly.assertThat(percentPlayedWithKotlin)
            .withFailMessage("I expected more to have experimented!")
            .isGreaterThan(25)

        softly.assertThat(percentPrimarilyJavaDevelopers)
            .withFailMessage("Sorry I've assumed java knowledge, so take a nap if needs be")
            .isGreaterThan(75)

        softly.assertThat(percentPrimarilyOtherJvmLanguages)
            .withFailMessage("I've primarily focused on Kotlin & Java, " +
                "talk to me afterwards if you want comparisons")
            .isLessThan(40)

        softly.assertAll()

        println("""
            |Great I guessed the audience right.
            |Any issues with this talk are due to my incompetence in other areas.
            """.trimMargin()
        )
    }
}


/*

Number 1: Sensible type inference

 */

val name = "John Smith"
val splitName = name.split(' ')

// compile error:
//val halfAName = name / 2

fun lastName(name: String) = name.split(' ').last()


class KotlinWelcomeClass(val name: String) {
    fun sayHello() = "hello $name"
}

/*
equivalent to this java class:

public class HelloJava {
    private String name;

    public HelloJava(String name) {
        this.name = name;
    }

    public String sayHello() {
        return "hello " + name;
    }

    public String getName() {
        return name;
    }
}

 */

/*

Number 2: Data Classes

 */

data class Publisher(
    val id: String,
    val name: String,
    val domains: List<String>,
    val summary: SummaryStats?
)

data class SummaryStats
(
    val articleCount: Int,
    val topicCount: Int,
    val socialInteractions: Int
)

/*

$ javap SummaryStats.class
Compiled from "Talk.kt"
public final class london.kotlin.night.SummaryStats {
  public final int getArticleCount();
  public final int getTopicCount();
  public final int getSocialInteractions();
  public london.kotlin.night.SummaryStats(int, int, int);
  public final int component1();
  public final int component2();
  public final int component3();
  public final london.kotlin.night.SummaryStats copy(int, int, int);
  public static london.kotlin.night.SummaryStats copy$default(london.kotlin.night.SummaryStats, int, int, int, int, java.lang.Object);
  public java.lang.String toString();
  public int hashCode();
  public boolean equals(java.lang.Object);
}

 */


val baseStats = SummaryStats(
    articleCount = 17,
    topicCount = 4,
    socialInteractions = 23000
)

class DataClassTest {
    @Test
    fun equalityToStringAndCopyMethodsAreGenerated() {

        assertThat(baseStats.toString())
            .isEqualTo("SummaryStats(articleCount=17, topicCount=4, socialInteractions=23000)")

        val sameStatsDifferentObject = SummaryStats(
            articleCount = 17,
            topicCount = 4,
            socialInteractions = 23000
        )

        assertThat(baseStats)
            .isEqualTo(sameStatsDifferentObject)

        val updated = sameStatsDifferentObject
            .copy(articleCount = 18)

        assertThat(baseStats)
            .isNotEqualTo(updated)
    }
}


/*

Number 3: Java Library Integration (i)


 */

class JsonSerialisationTest {
    @Test
    fun dataClassesJustWorkWithJackson() {
        val mapper = ObjectMapper()
        val json = mapper.writeValueAsString(baseStats)
        assertThat(json).isEqualTo(
            """{"articleCount":17,"topicCount":4,"socialInteractions":23000}"""
        )
    }
}


/*

Number 4: Null Checks (with smart casts)

 */

fun calculateStatsForArticle(url: String): SummaryStats {

    // [go fetch some urls, call some apis and parse the results]

    // this generates a compile error:
    //   return null

    return SummaryStats(
        articleCount = 1,
        topicCount = 1,
        socialInteractions = 15)
}

fun callCalculateStats() {
    // this generates a compile error:
    //calculateStatsForArticle(null)

    calculateStatsForArticle("https://example.org/some-article")
}


fun dumpStats(stats: SummaryStats?) {
    // generates a compile error:
    //println(stats.topicCount)

    if (stats != null) {
        println(stats.topicCount)
    }
}


/*

Number 5: Collections

 */

class CollectionTest {
    @Test
    fun simpleCollectionStuff() {
        val list = listOf(1, 2, 3, 4, 5)
        val squared = list.map { i -> i * i }
        assertThat(squared).containsExactly(1, 4, 9, 16, 25)
    }
}


data class SearchResult(val highlight: String, val score: Float, val id: String)

fun doSearch(q: String): List<SearchResult> {
    val response = Elasticsearch.client.prepareSearch("articles")
        .addHighlightedField("headline")
        .setQuery(
            boolQuery()
                .must(simpleQueryStringQuery("brexit").field("headline"))
                .filter(idsQuery("Topic").ids("topic_one", "topic_two"))
        )
        .get()

    return response.hits.mapNotNull { h ->
        val highlight = h.highlightFields["headline"]?.fragments

        highlight?.let {
            val combinedString = highlight.map { it.string() }.joinToString(" … ")

            SearchResult(
                highlight = combinedString,
                score = h.score,
                id = h.id
            )
        }
    }
}


/*

Number 6: SAM (Single Abstract Method) support
TODO: this should more generally be "first class functions"
 */

data class Event(
    val id: String,
    val type: String
)

class InProcessEventSender() {
    private val executor = Executors.newWorkStealingPool()

    fun send(event: Event) {
        executor.submit {
            println("processing event id ${event.id} of type ${event.type}")

            // do some actual work with the event
        }
    }
}



/*

Number 7: Extension methods

 */

fun String.htmlEntityDecode(): String =
    StringEscapeUtils.unescapeHtml4(this)

class ExtensionMethodTest1() {
    @Test
    fun shouldBeAbleToDecodeEntities() {
        assertThat("Colombia&#039;s FARC rebels".htmlEntityDecode())
            .isEqualTo("Colombia's FARC rebels")
    }
}





fun String?.forceNullIfBlank(): String? =
    if (this == null || this.isBlank()) null
    else this.trim()

class ExtensionMethodTest2() {
    @Test
    fun forceNullIfBlankWorks() {
        val nullString: String? = null

        assertThat(nullString.forceNullIfBlank()).isNull()
        assertThat("orange".forceNullIfBlank()).isNotNull().isEqualTo("orange")
        assertThat("banana ".forceNullIfBlank()).isNotNull().isEqualTo("banana")
        assertThat("  ".forceNullIfBlank()).isNull()
    }
}


/*

Number 8: let / use / apply

*/

// TODO: examples

/*

Number 9: java language integration (ii)

 */

val timeLeft = Duration.standardMinutes(2)
val expectedEndTime = DateTime.now().plus(timeLeft)




/*

Number 10: great IDE support

 */

// SEE ABOVE!!




/*

SUMMARY

The quality and variety of the java ecosystem is a key reason to develop on the JVM.

Kotlin doesn't try to invent its own sub-ecosystem,

TODO




 */

fun main(args: Array<String>) {
    System.setProperty("jansi.passthrough", "true")
    AnsiConsole.systemInstall()

    AnsiConsole.out.println(Ansi.ansi().eraseScreen().render("@|red Hello|@ @|green World|@"))
    println(Ansi.ansi().bg(Ansi.Color.RED).a("Hello"))
}