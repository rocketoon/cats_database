import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import play.api.test.FakeApplication
import play.api.test.FakeRequest
import play.api.test.Helpers.BAD_REQUEST
import play.api.test.Helpers.OK
import play.api.test.Helpers.SEE_OTHER
import play.api.test.Helpers.contentAsString
import play.api.test.Helpers.defaultAwaitTimeout
import play.api.test.Helpers.flash
import play.api.test.Helpers.inMemoryDatabase
import play.api.test.Helpers.redirectLocation
import play.api.test.Helpers.running
import play.api.test.Helpers.status
import org.specs2.runner.files
import views.html.helper.form
import play.api.mvc.MultipartFormData



@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {


  val applicationController = new controllers.Application()

  "Application" should {

    "redirect to the cats list on /" in {

      val result = applicationController.index(FakeRequest())

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == "/cats")

    }

    "list cats on the the first page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val result = applicationController.list(0, 2, "")(FakeRequest())

        status(result) must equalTo(OK)
        contentAsString(result) must contain("15 cats found")

      }
    }

    "filter cat by name" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val result = applicationController.list(0, 2, "Bob")(FakeRequest())

        status(result) must equalTo(OK)
        contentAsString(result) must contain("4 cats found")

      }
    }

    /**
   *   You can't provide  multi-part testing nowadays 
   *   
   *   https://github.com/playframework/playframework/issues/2289#issuecomment-39409820
   *
   */
    
    
//    "create new cat information" in {
//      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
//
//        val badResult = applicationController.save(FakeRequest())
//
//        status(badResult.run) must equalTo(BAD_REQUEST)
//        
//        
//        
//        
//        val result = applicationController.save(
//          //FakeRequest().withMultipartFormDataBody(form: )
//          FakeRequest().withFormUrlEncodedBody("name" -> "Kitty", "color" -> "yellow", "breed" -> "hasky", "gender" -> "true"))
//
//        status(result.run) must equalTo(SEE_OTHER)
//        redirectLocation(result.run) must beSome.which(_ == "/cats")
//        flash(result.run).get("success") must beSome.which(_ == "Cat Kitty has been added")
//
//        val list = applicationController.list(0, 2, "Kitty")(FakeRequest())
//
//        status(list) must equalTo(OK)
//        contentAsString(list) must contain("One cat found")
//
//      }
//    }

  }

}