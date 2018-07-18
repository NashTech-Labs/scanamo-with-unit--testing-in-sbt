package repository.impl

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import models._

class EmployeeRepositoryBatchTest extends TestTrait {

    private val employeeRepository = new EmployeeRepositoryImpl(alpakkaClient)

    override def beforeAll() : Unit = {
        super.beforeAll()
        LocalDynamoDB.createTable(client)("Employee")('name -> S)
    }

    it should "putAll and scan the employee" in {
        val employee1 = Employee("name1", 12, Code(List("abc12", "456we")))
        val employee2 = Employee("name2", 22, Code(List("abc12", "456we")))
        val futureRes = for {
            _ <- employeeRepository.putAll(Set(employee1, employee2))
            res <- employeeRepository.scan
        } yield res

        futureRes
            .map(
                res => assert(res === List(
                    Right(Employee("name1", 12, Code(List("abc12", "456we")))),
                    Right(Employee("name2", 22, Code(List("abc12", "456we"))))))
            )
    }

    it should "getAll the employee" in {
        val employee1 = Employee("name3", 12, Code(List("abc12", "456we")))
        val futureRes = for {
            _ <- employeeRepository.putAll(Set(employee1))
            res <- employeeRepository.getAll(Set("name1", "name2"))
        } yield res

        futureRes
            .map(
                res => assert(res === Set(
                    Right(Employee("name1", 12, Code(List("abc12", "456we")))),
                    Right(Employee("name2", 22, Code(List("abc12", "456we"))))))
            )
    }

    it should "delete all the employee by name" in {
        val futureRes = for {
            _ <- employeeRepository.deleteAll(Set("name1", "name2"))
            res <- employeeRepository.scan
        } yield res

        futureRes
            .map(
                res => assert(res === List(
                    Right(Employee("name3", 12, Code(List("abc12", "456we"))))))
            )
    }

    override def afterAll() : Unit = {
        super.afterAll()
    }
}

