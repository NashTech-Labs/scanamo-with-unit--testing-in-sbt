package repository.impl

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import models._

class EmployeeRepositoryFilterTest extends TestTrait {

    private val employeeRepository = new EmployeeRepositoryImpl(alpakkaClient)

    override def beforeAll() : Unit = {
        super.beforeAll()
        LocalDynamoDB.createTable(client)("Employee")('name -> S)
    }

    private val employee = Employee("name1", 2 , Code(List("code1")))
    private val employee2 = Employee("name", 2 , Code(List("code1")))

    it should "scan by id" in {
        val futureRes = for{
            _ <- employeeRepository.putAll(Set(employee, employee2))
            res <- employeeRepository.scanWithId(2)
        } yield res
        futureRes.map(res => assert(res === List(Right(employee2), Right(employee))))
    }

    it should "query by id" in {
        val futureRes = employeeRepository.queryWithId("code1")
        futureRes.map(res => assert(res === List(Right(employee2))))
    }

    override def afterAll() : Unit = {
        super.afterAll()
    }
}
