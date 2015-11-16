package example

import scala.scalajs.js
import org.scalajs.dom
import shared.SharedMessages
import scala.scalajs.js.Dynamic.{global => g}
import org.scalajs.jquery.{jQuery => JQ, JQueryXHR, JQueryAjaxSettings}

import scala.scalajs.js.annotation.JSExport

object ScalaJSExample extends js.JSApp {
  @JSExport
  def submitForm(): Unit = {
    val opponent = JQ("#opponent").value().asInstanceOf[String]
    val result = JQ("#result").value().asInstanceOf[String].toInt
    JQ.ajax(js.Dynamic.literal(
      url = s"game/$opponent/$result",
      success = { (data: js.Any, textStatus: String, jqXHR: JQueryXHR) =>
        g.location.reload()
      },
      `type` = "POST"
    ).asInstanceOf[JQueryAjaxSettings])
  }

  def main(): Unit = {
    if (g.userName != null) {
      def greetingText(rating: Option[Int]): String = {
        s"Hi, ${g.userName} (${rating.map(_.toString).getOrElse("???")})!"
      }

      def updateRating(rating: Int): Unit = {
        JQ("#greeting").text(greetingText(Some(rating)))
      }

      val content = JQ("#content")
      content.append("""<p><a href="logout?url='http://localhost:9000'">Logout</a>""")
      content.append(s"<p id='greeting'>" + greetingText(None))


      val facebookId = g.facebookId
      JQ.ajax(js.Dynamic.literal(
        url = "rating",
        data = js.Dynamic.literal(facebookId = facebookId),
        success = { (data: js.Any, textStatus: String, jqXHR: JQueryXHR) =>
          updateRating(data.asInstanceOf[js.Dynamic].rating.asInstanceOf[Int])
        },
        `type` = "GET"
      ).asInstanceOf[JQueryAjaxSettings])

      JQ("#addGameForm").submit(submitForm _)
    }
  }
}
