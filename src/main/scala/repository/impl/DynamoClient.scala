package repository.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.dynamodb.impl.DynamoSettings
import akka.stream.alpakka.dynamodb.scaladsl.DynamoClient

class DynamoDBClient {

    implicit val system = ActorSystem("Scanamo-System")
    implicit val materializer = ActorMaterializer()

    val settings = DynamoSettings(system)
    val alpakkaClient = DynamoClient(settings)
}
