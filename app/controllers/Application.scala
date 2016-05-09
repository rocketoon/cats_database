package controllers

import java.io.File

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import play.api.mvc.Request

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import anorm._

import views._
import models._
import java.io.InputStream
import scala.collection.Map

/**
 * Manage a database of cats
 */
class Application extends Controller {

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list(0, 2, ""))

  /**
   * Describe the cat form (used in both edit and create screens).
   */

  val catForm = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "name" -> nonEmptyText,
      "color" -> nonEmptyText,
      "breed" -> nonEmptyText,
      "gender" -> boolean)(Cat.apply)(Cat.unapply))

  // -- Actions

  /**
   * Handle default path requests, redirect to cats list
   */
  def index = Action { Home }

  /**
   * Display the paginated list of cats.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on computer names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Cat.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")),
      orderBy, filter))
  }

  /**
   * Display the 'edit form' of a existing Cat information.
   *
   * @param id Id of the cat to edit
   */
  def edit(id: Long) = Action {
    Cat.findById(id).map { cat =>
      Ok(html.editForm(id, catForm.fill(cat)))
    }.getOrElse(NotFound)
  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the cat to edit
   */
  def update(id: Long) = Action { implicit request =>
    catForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors)),
      cat => {
        Cat.update(id, cat)
        Home.flashing("success" -> "Cat %s has been updated".format(cat.name))
      })
  }

  /**
   * Display the 'new cat form'.
   */
  def create = Action {
    Ok(html.createForm(catForm))
  }

  /**
   * Handle the 'new cat form' submission.
   */
  def save = Action(parse.multipartFormData) { implicit request =>

    catForm.bindFromRequest().fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors)),
      cat => {

        val picture = request.body.file("picture").map { picture =>
          import java.io.File
          val filename = picture.filename
          val contentType = picture.contentType
          picture.ref
        }

        Cat.insert(cat, picture)
        Home.flashing("success" -> "Cat %s has been added".format(cat.name))
      })
  }
 
  
  /**
   * Handle cat picture displaing.
   */
  
  
  def catImage(id:Long) = Action { 
    Cat.getImage(id).map { img => Ok(img) }.getOrElse(NotFound)
  }
  
  
  def uploadFile = Action(parse.multipartFormData) { implicit request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"/tmp/$filename"))
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }

  /**
   * Handle cat deletion.
   */
  def delete(id: Long) = Action {
    Cat.delete(id)
    Home.flashing("success" -> "Cat information has been deleted")
  }

}
            
