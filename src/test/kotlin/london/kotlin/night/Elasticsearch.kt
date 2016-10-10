package london.kotlin.night

import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient

object Elasticsearch {
    val client: Client = TransportClient.Builder().build()
}