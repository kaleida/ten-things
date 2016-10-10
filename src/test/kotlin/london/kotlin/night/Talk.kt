@file:Suppress("unused", "UNUSED_PARAMETER")

package london.kotlin.night

import com.amazonaws.services.kinesis.producer.KinesisProducer
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.HttpUrl
import org.apache.commons.lang3.StringEscapeUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.elasticsearch.index.query.QueryBuilders.*
import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.Test
import java.util.concurrent.Executors

/*









  1111111        000000000
 1::::::1      00:::::::::00
1:::::::1    00:::::::::::::00
111:::::1   0:::::::000:::::::0
   1::::1   0::::::0   0::::::0
   1::::1   0:::::0     0:::::0
   1::::1   0:::::0     0:::::0   things I'm loving about Kotlin
   1::::l   0:::::0 000 0:::::0
   1::::l   0:::::0 000 0:::::0
   1::::l   0:::::0     0:::::0
   1::::l   0:::::0     0:::::0
   1::::l   0::::::0   0::::::0
111::::::1110:::::::000:::::::0
1::::::::::1 00:::::::::::::00   Graham Tackley
1::::::::::1   00:::::::::00     Cofounder & CTO
111111111111     000000000       kaleida.com













*/

/* Opening poll */
val percentUsingKotlinInProduction = 5
val percentPlayedWithKotlin = 99
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
            .withFailMessage("Sorry I've assumed java knowledge, " +
                "so take a nap if needs be")
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




  1111111
 1::::::1
1:::::::1
111:::::1
   1::::1
   1::::1              sensible type inference
   1::::1
   1::::l
   1::::l
   1::::l
   1::::l
   1::::l
111::::::111
1::::::::::1
1::::::::::1
111111111111










 */

val name = "John Smith"
val splitName = name.split(' ')

val i = 7

// compile error:
//val halfAName = name / 2

fun lastName(name: String) = name.split(' ').last()


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

class KotlinWelcomeClass(val name: String) {
    fun sayHello() = "hello $name"
}

/*







 222222222222222
2:::::::::::::::22
2::::::222222:::::2
2222222     2:::::2
            2:::::2
            2:::::2
         2222::::2
    22222::::::22            data classes
  22::::::::222
 2:::::22222
2:::::2
2:::::2
2:::::2       222222
2::::::2222222:::::2
2::::::::::::::::::2
22222222222222222222









 */

data class Publisher(
    val id: String,
    val name: String,
    val domains: List<String>,
    val summary: SummaryStats?
)

data class SummaryStats(
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

 _____________________
/                     \
|  333333333333333    |
| 3:::::::::::::::33  |
| 3::::::33333::::::3 |
| 3333333     3:::::3 |
|             3:::::3 |
|             3:::::3 |
|     33333333:::::3  |     java library integration (i)
|     3:::::::::::3   |
|     33333333:::::3  |
|             3:::::3 |
|             3:::::3 |
|             3:::::3 |
| 3333333     3:::::3 |
| 3::::::33333::::::3 |
| 3:::::::::::::::33  |
|  333333333333333    |
\                     /
 ---------------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||




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



 ____________________
/                    \
|        444444444   |
|       4::::::::4   |
|      4:::::::::4   |
|     4::::44::::4   |
|    4::::4 4::::4   |
|   4::::4  4::::4   |
|  4::::4   4::::4   |         null checks (with smart casts)
| 4::::444444::::444 |
| 4::::::::::::::::4 |
| 4444444444:::::444 |
|           4::::4   |
|           4::::4   |
|           4::::4   |
|         44::::::44 |
|         4::::::::4 |
|         4444444444 |
\                    /
 --------------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||



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

 _____________________
/                     \
| 555555555555555555  |
| 5::::::::::::::::5  |
| 5::::::::::::::::5  |
| 5:::::555555555555  |
| 5:::::5             |
| 5:::::5             |           collections
| 5:::::5555555555    |
| 5:::::::::::::::5   |
| 555555555555:::::5  |
|             5:::::5 |
|             5:::::5 |
| 5555555     5:::::5 |
| 5::::::55555::::::5 |
|  55:::::::::::::55  |
|    55:::::::::55    |
|      555555555      |
\                     /
 ---------------------
        \   ^__^
         \  (..)\_______
            (__)\       )\/\
                ||----w |
                ||     ||


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

        if (highlight == null) null
        else {
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

 _____________________
/                     \
|         66666666    |
|        6::::::6     |
|       6::::::6      |
|      6::::::6       |
|     6::::::6        |
|    6::::::6         |
|   6::::::6          |       higher order functions
|  6::::::::66666     |         (with SAM support)
| 6::::::::::::::66   |
| 6::::::66666:::::6  |
| 6:::::6     6:::::6 |
| 6:::::6     6:::::6 |
| 6::::::66666::::::6 |
|  66:::::::::::::66  |
|    66:::::::::66    |
|      666666666      |
\                     /
 ---------------------
        \   ^__^
         \  (OO)\_______
            (__)\       )\/\
                ||----w |
                ||     ||


 */

interface ArticleUrlDetector {
    fun isArticle(url: HttpUrl): Boolean
}

class LastSlug(val f: (String) -> Boolean) : ArticleUrlDetector {
    override fun isArticle(url: HttpUrl): Boolean {
        val lastSlug = url.pathSegments().lastOrNull { it.isNotBlank() }
        return if (lastSlug == null) false else f(lastSlug)
    }
}

fun lastSlugEndsWithDigits() = LastSlug { Regex("""\d{6}$""") in it }



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

 ______________________
/                      \
| 77777777777777777777 |
| 7::::::::::::::::::7 |
| 7::::::::::::::::::7 |
| 777777777777:::::::7 |
|            7::::::7  |
|           7::::::7   |
|          7::::::7    |
|         7::::::7     |
|        7::::::7      |        extension methods
|       7::::::7       |
|      7::::::7        |
|     7::::::7         |
|    7::::::7          |
|   7::::::7           |
|  7::::::7            |
| 77777777             |
\                      /
 ----------------------
        \   ^__^
         \  (@@)\_______
            (__)\       )\/\
                ||----w |
                ||     ||

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

 _____________________
/                     \
|      888888888      |
|    88:::::::::88    |
|  88:::::::::::::88  |
| 8::::::88888::::::8 |
| 8:::::8     8:::::8 |
| 8:::::8     8:::::8 |
|  8:::::88888:::::8  |
|   8:::::::::::::8   |       let & apply
|  8:::::88888:::::8  |
| 8:::::8     8:::::8 |
| 8:::::8     8:::::8 |
| 8:::::8     8:::::8 |
| 8::::::88888::::::8 |
|  88:::::::::::::88  |
|    88:::::::::88    |
|      888888888      |
\                     /
 ---------------------
        \   ^__^
         \  (**)\_______
            (__)\       )\/\
             U  ||----w |
                ||     ||
*/

// public inline fun <T, R> T.let(block: (T) -> R): R = block(this)

fun doSearch2(q: String): List<SearchResult> {
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

// public inline fun <T> T.apply(block: T.() -> Unit): T { block(); return this }

val kinesisProducer = KinesisProducer(
    KinesisProducerConfiguration().apply {
        metricsLevel = "summary"
        metricsNamespace = "some-stream"
        region = "eu-west-1"
    }
)



/*

 _____________________
/                     \
|      999999999      |
|    99:::::::::99    |
|  99:::::::::::::99  |
| 9::::::99999::::::9 |
| 9:::::9     9:::::9 |
| 9:::::9     9:::::9 |      operators
|  9:::::99999::::::9 |       (java library integration ii)
|   99::::::::::::::9 |
|     99999::::::::9  |
|          9::::::9   |
|         9::::::9    |
|        9::::::9     |
|       9::::::9      |
|      9::::::9       |
|     9::::::9        |
|    99999999         |
\                     /
 ---------------------
        \   ^__^
         \  (--)\_______
            (__)\       )\/\
                ||----w |
                ||     ||

 */

val timeLeft = Duration.standardMinutes(2)
val expectedEndTime = DateTime.now().plus(timeLeft)

/*

 _________________________________
/                                 \
|   1111111        000000000      |
|  1::::::1      00:::::::::00    |
| 1:::::::1    00:::::::::::::00  |
| 111:::::1   0:::::::000:::::::0 |
|    1::::1   0::::::0   0::::::0 |
|    1::::1   0:::::0     0:::::0 |
|    1::::1   0:::::0     0:::::0 |        great IDE support
|    1::::l   0:::::0 000 0:::::0 |          (& fast compilation)
|    1::::l   0:::::0 000 0:::::0 |
|    1::::l   0:::::0     0:::::0 |
|    1::::l   0:::::0     0:::::0 |
|    1::::l   0::::::0   0::::::0 |
| 111::::::1110:::::::000:::::::0 |
| 1::::::::::1 00:::::::::::::00  |
| 1::::::::::1   00:::::::::00    |
| 111111111111     000000000      |
\                                 /
 ---------------------------------
        \   ^__^
         \  (xx)\_______
            (__)\       )\/\
             U  ||----w |
                ||     ||


 */

// SEE ABOVE!!


/*

 ___ _   _ _ __ ___  _ __ ___   __ _ _ __ _   _
/ __| | | | '_ ` _ \| '_ ` _ \ / _` | '__| | | |
\__ \ |_| | | | | | | | | | | | (_| | |  | |_| |
|___/\__,_|_| |_| |_|_| |_| |_|\__,_|_|   \__, |
                                           __/ |
                                          |___/

Kotlin addresses the needless verbosity of Java...

 ... while retaining fast compile times
 ... with great IDE support
 ... encouraging readability
 ... embracing the java ecosystem you use today
 ... without trying to create its own sub-ecosystem


 */
