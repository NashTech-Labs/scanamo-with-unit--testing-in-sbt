package repository

import com.amazonaws.services.dynamodbv2.model.{BatchWriteItemResult, ConditionalCheckFailedException, DeleteItemResult, PutItemResult}
import com.gu.scanamo.error.{DynamoReadError, ScanamoError}
import models.{Code, Employee}
import scala.concurrent.Future

trait EmployeeRepository
{

    def put(employee : Employee) : Future[Option[Either[DynamoReadError, Employee]]]

    def get(name : String, id : Long) : Future[Option[Either[DynamoReadError, Employee]]]

    def delete(name : String, id : Long) : Future[DeleteItemResult]

    def scan : Future[List[Either[DynamoReadError, Employee]]]

    def update(employee : Employee) : Future[Either[DynamoReadError, Employee]]

    def query(name : String) : Future[List[Either[DynamoReadError, Employee]]]

    def putAll(employees : Set[Employee]) : Future[List[BatchWriteItemResult]]

    def getAll(names : Set[String]) : Future[Set[Either[DynamoReadError, Employee]]]

    def deleteAll(names : Set[String]) : Future[List[BatchWriteItemResult]]

    def putIfNotExist(employee : Employee) : Future[Either[ConditionalCheckFailedException, PutItemResult]]

    def deleteIfExist(employee : Employee) : Future[Either[ConditionalCheckFailedException, DeleteItemResult]]

    def updateIfExist(employee : Employee) : Future[Either[ScanamoError, Employee]]

    def scanWithId(id : Long) : Future[List[Either[DynamoReadError, Employee]]]

    def queryWithId(code : String) : Future[List[Either[DynamoReadError, Employee]]]

    def scanWithIndex(code : Code) : Future[List[Either[DynamoReadError, Employee]]]
}
