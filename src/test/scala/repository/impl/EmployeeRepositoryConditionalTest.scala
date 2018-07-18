package repository.impl

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import models._

class EmployeeRepositoryConditionalTest extends TestTrait {

    private val employeeRepository = new EmployeeRepositoryImpl(alpakkaClient)

    override def beforeAll() : Unit = {
        super.beforeAll()
        LocalDynamoDB.createTable(client)("Employee")('name -> S)
    }

    private val id = scala.util.Random.nextInt()
    private val employee = Employee("name", -1 , Code(List("code1")))
    private val employee2 = Employee("name", id , Code(List("code2")))

    it should "put the employee if not exists" in {
        val futureRes = for{
         _ <- employeeRepository.putIfNotExist(employee)
            res <- employeeRepository.scan
        } yield res
        futureRes.map(res => assert(res === List(Right(employee))))
    }

    it should "not put the employee as it exists" in {
        val futureRes = for{
            _ <- employeeRepository.putIfNotExist(employee2)
            res <- employeeRepository.scan
        } yield res
        futureRes.map(res => assert(res === List(Right(employee))))
    }

    override def afterAll() : Unit = {
        super.afterAll()
    }
}
