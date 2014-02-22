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

case class AuthUser(
  username: String,
  password: String)

object AuthController
{
  val admin = AuthUser(
    username = "admin",
    password = Config.ADMIN
  )

  def userPassAuthenticator(userPass: Option[UserPass]): Future[Option[String]] =
    Future {
      if (userPass.exists(up => {
        val user = up.user
        val pass = HashString.hash(up.pass)
        (user equals admin.username) && (pass equals admin.password)
      })) Some("admin")
      else None
    }
}