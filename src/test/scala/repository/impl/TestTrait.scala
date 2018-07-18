package repository.impl

import com.amazonaws.auth._
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import com.amazonaws.services.dynamodbv2._
import org.scalatest._

trait TestTrait extends AsyncFlatSpec with BeforeAndAfterAll {
    val alpakkaClient = new DynamoDBClient
    var client : AmazonDynamoDB = _
    var server : DynamoDBProxyServer = _

    override def beforeAll() : Unit =
    {
        Thread.sleep(2000)
        val localArgs = Array("-inMemory", "-sharedDb", "1")
        server = ServerRunner.createServerFromCommandLineArgs(localArgs)
        System.setProperty("sqlite4java.library.path", "native-libs")
        System.setProperty("aws.accessKeyId", "access_key_id") //for alpakka-client
        System.setProperty("aws.secretKey", "secret_access_key") //for alpakka-client
        val awsCredentials = new BasicAWSCredentials("dummy", "credentials")
        client = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", ""))
            .build()

        server.start()
    }

    override def afterAll() : Unit =
    {
        client.shutdown()
        server.stop()
    }
}
