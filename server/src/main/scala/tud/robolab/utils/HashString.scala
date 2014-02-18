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

package tud.robolab.utils

import java.security.MessageDigest

object HashString
{
  def hash(password: String): String =
  {
    val md = MessageDigest.getInstance("SHA-512")
    val bytes = md.digest(password.getBytes)
    val sb = new StringBuffer()
    bytes.foreach(b => sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1)))
    sb.toString
  }
}
