import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import org.fluentlenium.core.filter.FilterConstructor._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {
  
  "Application" should {
    
    "work from within a browser" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/")
        
        browser.$("header h1").first.getText must equalTo("Cats database")
        browser.$("section h1").first.getText must equalTo("15 cats found")
      
        browser.$("#pagination li.current").first.getText must equalTo("Displaying 1 to 10 of 15")
        browser.$("#pagination li.next a").click()
        browser.$("#pagination li.current").first.getText must equalTo("Displaying 11 to 15 of 15")
        
        browser.$("#searchbox").text("Bob")
        browser.$("#searchsubmit").click()
        browser.$("section h1").first.getText must equalTo("4 cats found")
        browser.$("a", withText("Bob")).first().click()       
        browser.$("section h1").first.getText must equalTo("Edit cat information")
        browser.$("#submit").click()   
        browser.$(".alert-message").first.getText must equalTo("Done! Cat Bob has been updated")

        browser.$("section h1").first.getText must equalTo("15 cats found")
        
        browser.$("a", withText("Guffy")).click() 
        browser.$("#deleteCat").click()
        browser.$("#confDeleteCat").click()
        browser.$(".alert-message").first.getText must equalTo("Done! Cat information has been deleted")
       
        browser.$("section h1").first.getText must equalTo("14 cats found")
        

      }
    }
    
  }
  
}