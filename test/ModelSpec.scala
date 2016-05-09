import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

  import models._

  "Cat model" should {

    "be retrieved by id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(kitto) = Cat.findById(3)

        kitto.name must equalTo("Kitto")
        kitto.color must equalTo("green")

      }
    }

    "be updated if needed" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        Cat.update(3, Cat(name = "The Kitto", color = "green", breed = "haskY", gender = true))

        val Some(kitto) = Cat.findById(3)

        kitto.name must equalTo("The Kitto")
        kitto.breed must equalTo("haskY")

      }
    }

  }

}