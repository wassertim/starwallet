package specs

import org.specs2.mutable.Specification
import utility.{closable, EmailClient}

class EmailTest extends Specification {
  "Test email" should {
    "email" in {
      closable(new EmailClient("", "")) {
        emailClient =>
          val activationUrl = emailClient.getActivationUrl("")
          1 must beEqualTo(1)
      }
    }
  }
}
