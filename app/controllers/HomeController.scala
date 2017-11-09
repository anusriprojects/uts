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
import models.SiteInfoVo

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
      "site_id" -> number,
      "name" -> text)(SiteInfo.apply)(SiteInfo.unapply))
  def index(page: Int) = Action.async { implicit request =>
    // access "default" database
    Future({

      db.withConnection { conn =>
        // do whatever you need with the connection
        try {
          val offset = (page - 1) * 5
          val stmt = conn.createStatement
          val rs = stmt.executeQuery(s"SELECT * from site_info  limit 5 offset $offset")

          val countStmt = conn.createStatement
          val countrs = countStmt.executeQuery("select count(*) as count from site_info")

          var count: Int = 0

          while (countrs.next()) {
            /* val outString = rs.getString("name")*/
            count = countrs.getInt("count")
          }
          var lst: ListBuffer[SiteInfo] = new ListBuffer[SiteInfo]()

          while (rs.next()) {
            /* val outString = rs.getString("name")*/
            val dataMap = Map("name" -> rs.getString("name"))
            lst += new SiteInfo(rs.getInt("site_id"), rs.getString("name"))
          }

          val retVal = new SiteInfoVo(lst.toList, page, if (count % 5.0 > 0) (count / 5) + 1 else count / 5)

          Ok(views.html.index(retVal))
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
