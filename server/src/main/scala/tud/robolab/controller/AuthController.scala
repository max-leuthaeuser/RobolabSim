/*
 * RobolabSim
 * Copyright (C) 2014  Max Leuthaeuser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package tud.robolab.controller

import spray.routing.authentication.UserPass
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import tud.robolab.utils.HashString
import tud.robolab.Config

/**
 * Case class representing and authenticated user with password and username.
 *
 * @param username the username as String
 * @param password the password as String
 */
case class AuthUser(
  username: String,
  password: String
  )

/**
 * The authentication controller handling the basic authentication with
 * SHA-512. See [[tud.robolab.utils.HashString]].
 */
object AuthController
{
  val ADMIN = "admin"

  val admin = AuthUser(
    username = ADMIN,
    password = Config.ADMIN
  )

  /**
   * Handling the user authentication based on the SHA-512 encrypted password.
   *
   * @param userPass user credentials
   * @return a Future containing `admin` if the user could be authenticated as admin.
   */
  def userPassAuthenticator(userPass: Option[UserPass]): Future[Option[String]] =
    Future {
      if (userPass.exists(up => {
        val user = up.user
        val pass = HashString.hash(up.pass)
        (user == admin.username) && (pass == admin.password)
      })) {
        Some(ADMIN)
      }
      else {
        None
      }
    }
}
