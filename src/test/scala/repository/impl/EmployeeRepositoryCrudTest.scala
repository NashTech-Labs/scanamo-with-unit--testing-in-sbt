package repository.impl

import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import models._

class EmployeeRepositoryCrudTest extends TestTrait {

    private val employeeRepository = new EmployeeRepositoryImpl(alpakkaClient)

    override def beforeAll() : Unit = {
        super.beforeAll()
        LocalDynamoDB.createTable(client)("Employee")('name -> S, 'id -> N)
    }

    it should "put the employee" in {
            val id = scala.util.Random.nextInt()
            val employee = Employee("name", id , Code(List("code1")))
            val futureRes = employeeRepository.put(employee)
            futureRes.map(res => assert(res === None))
        }

    it should "put and get the employee" in {
        val employee = Employee("name1", 12, Code(List("abc12", "456we")))
        val futureRes = for {
            _ <- employeeRepository.put(employee)
            res <- employeeRepository.get("name1", 12)
        } yield res
        futureRes.map(res => assert(res === Some(Right(Employee("name1", 12, Code(List("abc12", "456we")))))))
    }

    it should "put and query the employee" in {
        val employee1 = Employee("name1", -21, Code(List("abc12", "456we")))
        val employee2 = Employee("name1", 22, Code(List("abc12", "456we")))
        val employee3 = Employee("name2", -21, Code(List("abc12", "456we")))
        val futureRes = for {
            _ <- employeeRepository.put(employee1)
            _ <- employeeRepository.put(employee2)
            _ <- employeeRepository.put(employee3)
            res <- employeeRepository.query("name1")
        } yield res

        futureRes
            .map(
                res => assert(res === List(
            Right(Employee("name1", 12, Code(List("abc12", "456we")))),
            Right(Employee("name1", 22, Code(List("abc12", "456we"))))))
            )
    }

    it should "put & get the employee" in {
            val id = scala.util.Random.nextInt()
            val employee = Employee("name", id,  Code(List("code2")))
            val futureRes = for
                {
                _ <- employeeRepository.put(employee)
                res <- employeeRepository.get("name", id)
            } yield res
            futureRes.map(res => assert(res === Some(Right(Employee("name", id,  Code(List("code2")))))))
        }

    it should "delete the employee" in {
            val id = scala.util.Random.nextInt()
            val employee = Employee("name", id,  Code(List("code3")))
            val futureRes = for
                {
                _ <- employeeRepository.put(employee)
                resBeforeDelete <- employeeRepository.get("name", id)
                _ <- employeeRepository.delete("name", id)
                resAfterDelete <- employeeRepository.get("name", id)
            } yield (resBeforeDelete, resAfterDelete)

            futureRes.map
            {
                case (resBeforeDelete, resAfterDelete) => assert(resBeforeDelete === Some(Right(Employee("name", id,  Code(List("code3")))))
                    && resAfterDelete === None)
            }
        }

    it should "scan the employees" in {
            val futureRes = employeeRepository.scan
            futureRes.map(list => assert(list.nonEmpty))
        }

    it should "update the employee" in {
        val employee1 = Employee("name47", 47, Code(List("abc12", "456we")))
        val employee2 = Employee("name47", 47, Code(List("abc47", "45647we")))
        val futureRes = for {
            _ <- employeeRepository.put(employee1)
            res <- employeeRepository.update(employee2)
        } yield res

        futureRes
            .map(
                res => assert(res === Right(Employee("name47", 47, Code(List("abc47", "45647we")))))
            )
    }

    override def afterAll() : Unit = {
        super.afterAll()
    }
}
