package repository.impl

import com.amazonaws.services.dynamodbv2.model.{BatchWriteItemResult, ConditionalCheckFailedException, DeleteItemResult, PutItemResult}
import com.gu.scanamo.error.{DynamoReadError, ScanamoError}
import com.gu.scanamo.syntax._
import com.gu.scanamo.{ScanamoAlpakka, Table}
import models.{Code, Employee}
import repository.EmployeeRepository
import repository.impl.EmployeeRepositoryImpl._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Dynamo table called 'Employee' has only 'id' as it hash key or partition key
  **/
object EmployeeRepositoryImpl
{
    val TableName = "Employee"
    val codeIndexName = "code-index"
}

class EmployeeRepositoryImpl(dbClient : DynamoDBClient) extends EmployeeRepository
{
    private val client = dbClient.alpakkaClient
    private val table = Table[Employee](TableName)
    private val codeIndex = table.index(codeIndexName)

    /**
      * CRUD
      **/

    def put(employee : Employee) : Future[Option[Either[DynamoReadError, Employee]]] =
        ScanamoAlpakka.put[Employee](client)(TableName)(employee)

    def get(name : String, id : Long) : Future[Option[Either[DynamoReadError, Employee]]] =
        ScanamoAlpakka.get[Employee](client)(TableName)('name -> name and 'id -> id)

    def delete(name : String, id : Long) : Future[DeleteItemResult] =
        ScanamoAlpakka.delete[Employee](client)(TableName)('name -> name and 'id -> id)

    def scan : Future[List[Either[DynamoReadError, Employee]]] = //also there is method called scan with limits
        ScanamoAlpakka.scan[Employee](client)(TableName)

    def query(name : String) : Future[List[Either[DynamoReadError, Employee]]] =
        ScanamoAlpakka.query[Employee](client)(TableName)('name -> name and 'id > 0)

    def update(employee : Employee) : Future[Either[DynamoReadError, Employee]] =
        ScanamoAlpakka.update[Employee](client)(TableName)(
            'name -> employee.name and 'id -> employee.id,
            set('code -> employee.code)
        )

    /**
      * Batch Operations*/

    def putAll(employees : Set[Employee]) : Future[List[BatchWriteItemResult]] =
        ScanamoAlpakka.putAll[Employee](client)(TableName)(employees)

    def getAll(names : Set[String]) : Future[Set[Either[DynamoReadError, Employee]]] =
        ScanamoAlpakka.getAll[Employee](client)(TableName)('name -> names)

    def deleteAll(names : Set[String]) : Future[List[BatchWriteItemResult]] =
        ScanamoAlpakka.deleteAll(client)(TableName)('name -> names)

    /**
      * Conditional*/

    def putIfNotExist(employee : Employee) : Future[Either[ConditionalCheckFailedException, PutItemResult]] = {
        ScanamoAlpakka.exec(client)(table.given(not(attributeExists('name)))
            .put(employee))
    }

    def deleteIfExist(employee : Employee) : Future[Either[ConditionalCheckFailedException, DeleteItemResult]] = {
        ScanamoAlpakka.exec(client)(table.given('id > 0)
            .delete('name -> employee.name))
    }

    def updateIfExist(employee : Employee) : Future[Either[ScanamoError, Employee]] = {
        ScanamoAlpakka.exec(client)(table.given('id > 0)
            .update('name -> employee.name,
                set('code -> employee.code)))
    }

    /**
      * Filters*/

    def scanWithId(id : Long) : Future[List[Either[DynamoReadError, Employee]]] = {
        ScanamoAlpakka.exec(client)(table
            .filter('id -> id)
            .scan())
    }

    def queryWithId(code : String) : Future[List[Either[DynamoReadError, Employee]]] = {
        ScanamoAlpakka.exec(client)(table
            .filter('code -> Code(List(code)))
            .query('name -> "name"))
    }

    /**
      * Indexes*/

    def scanWithIndex(code : Code) : Future[List[Either[DynamoReadError, Employee]]] = {
        ScanamoAlpakka.exec(client)(codeIndex.filter().query()
        )
    }
}
