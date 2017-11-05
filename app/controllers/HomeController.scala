package controllers

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton
import models.SiteInfo
import play.api.db.Database
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import play.api.data.Form
import play.api.data.Forms._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (cc: ControllerComponents, db: Database)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val userForm = Form(
    mapping(
      "name" -> text)(SiteInfo.apply)(SiteInfo.unapply))
  def index = Action.async {
    // access "default" database
    Future({

      db.withConnection { conn =>
        // do whatever you need with the connection
        try {
          val stmt = conn.createStatement
          val rs = stmt.executeQuery("SELECT * from site_info ")
          var lst: ListBuffer[SiteInfo] = new ListBuffer[SiteInfo]()

          while (rs.next()) {
            /* val outString = rs.getString("name")*/
            val dataMap = Map("name" -> rs.getString("name"))
            lst += new SiteInfo(rs.getString("name"))
          }

          val jstr = Json.toJson(
            lst.map { t =>
              Map("SiteName" -> t.name)
            })

          Ok(views.html.index(lst.toList))
        } finally {

        }
      }

    })(exec)

  }

  def getSites = Action.async {
    // access "default" database
    Future({

      db.withConnection { conn =>
        // do whatever you need with the connection
        try {
          val stmt = conn.createStatement
          val rs = stmt.executeQuery("SELECT * from site_info ")
          var lst: ListBuffer[SiteInfo] = new ListBuffer[SiteInfo]()

          while (rs.next()) {
            /* val outString = rs.getString("name")*/
            val dataMap = Map("name" -> rs.getString("name"))
            lst += new SiteInfo(rs.getString("name"))
          }

          val jstr = Json.toJson(
            lst.map { t =>
              Map("SiteName" -> t.name)
            })

          Ok(jstr.toString())
        } finally {

        }
      }

    })(exec)

  }

  def addSite = Action.async { implicit request =>
    // access "default" database
    Future({

      db.withTransaction { conn =>
        // do whatever you need with the connection

        val sql = "INSERT INTO site_info(name) VALUES(?)";
        try {
          val abc = userForm.bindFromRequest.get
          val preparedStmt = conn.prepareStatement(sql)
          preparedStmt.setString(1, abc.name)
          preparedStmt.executeUpdate()

          println(abc)

          Redirect(routes.HomeController.index())
        } finally {

        }
      }

    })(exec)

  }

}
