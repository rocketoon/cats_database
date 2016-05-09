package models

import java.util.{ Date }

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps
import java.io.InputStream
import java.io.File
import java.io.FileInputStream
import play.api.libs.Files.TemporaryFile

case class Cat(id: Option[Long] = None, name: String, color: String, breed: String, gender: String)

/**
 * Helper for pagination.
 */
case class Page[A](items: List[Cat], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Cat {

  // -- Parsers

  /**
   * Parse a Cat from a ResultSet
   */
  val simple = {
    get[Option[Long]]("cat.id") ~
      get[String]("cat.name") ~
      get[String]("cat.color") ~
      get[String]("cat.breed") ~
      get[String]("cat.gender") map {
        case id ~ name ~ color ~ breed ~ gender => Cat(id, name, color, breed, gender)
      }
  }

  // -- Queries

  /**
   * Retrieve a cat from the id.
   */
  def findById(id: Long): Option[Cat] = {
    DB.withConnection { implicit connection =>
      SQL("select * from cat where id = {id}").on('id -> id).as(Cat.simple.singleOpt)
    }
  }

  /**
   * Return a page of Cats.
   *
   * @param page Page to display
   * @param pageSize Number of computers per page
   * @param orderBy Computer property used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Cat)] = {

    val offest = pageSize * page

    DB.withConnection { implicit connection =>

      val cats = SQL(
        """
          select * from cat 
          where cat.name like {filter}
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy).as(Cat.simple.*)

      val totalRows = SQL(
        """
          select count(*) from cat 
          where cat.name like {filter}
        """).on(
          'filter -> filter).as(scalar[Long].single)

      Page(cats, page, offest, totalRows)

    }

  }

  /**
   * Update a cat information.
   *
   * @param id The cat id
   * @param cat The cat values.
   */
  def update(id: Long, cat: Cat, pic: Option[TemporaryFile]) = {

    DB.withConnection { implicit connection =>
      pic match {
        case Some(f) =>
          val istr: InputStream = new FileInputStream(f.file)

          SQL(
            """
                update cat
                set name = {name}, color= {color}, picture= {picture}, breed = {breed}, gender = {gender}
                where id = {id}
            """).on(
              'id -> id,
              'name -> cat.name,
              'color -> cat.color,
              'picture -> istr,
              'breed -> cat.breed,
              'gender -> cat.gender).executeUpdate()

        case None =>

          SQL(
            """
                update cat
                set name = {name}, color= {color}, breed = {breed}, gender = {gender}
                where id = {id}
            """).on(
              'id -> id,
              'name -> cat.name,
              'color -> cat.color,
              'breed -> cat.breed,
              'gender -> cat.gender).executeUpdate()

      }
    }
  }

  /**
   * Get cat image from database
   *
   * @param id The cat id
   */

  def getImage(id: Long) = {

    DB.withConnection { implicit connection =>
      val res = SQL(
        """
          SELECT picture FROM cat WHERE id = {id}
         """).on(
          'id -> id).map {
            case Row(picture: Array[Byte]) => picture
          }
      res.as(scalar[Array[Byte]].singleOpt)
    }

  }

  /**
   * Insert a new cat information.
   *
   * @param cat The cat values.
   * @param pic uploaded picture(as temporary file)
   */
  def insert(cat: Cat, pic: Option[TemporaryFile]) = {

    val istr: InputStream = pic.map(f => new FileInputStream(f.file)).getOrElse(null)

    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into cat values (
            (select next value for cat_seq), 
            {name}, {color}, {picture}, {breed}, {gender}        
          )
        """).on(
          'name -> cat.name,
          'color -> cat.color,
          'picture -> istr,
          'breed -> cat.breed,
          'gender -> cat.gender).executeUpdate()
    }
  }

  /**
   * Delete a cat.
   *
   * @param id Id of the cat to delete.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from cat where id = {id}").on('id -> id).executeUpdate()
    }
  }

}