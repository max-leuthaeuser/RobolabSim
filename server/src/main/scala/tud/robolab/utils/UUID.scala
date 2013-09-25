/*
 * RobolabSim
 * Copyright (C) 2013  Max Leuthaeuser
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

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.security.{MessageDigest, NoSuchAlgorithmException}

object UUID {
  val alg = "SHA"

  def createHashcode(): String = {
    var mdg: MessageDigest = null
    val baos: ByteArrayOutputStream = new ByteArrayOutputStream()
    val dos: DataOutputStream = new DataOutputStream(baos)

    //filling byteArray with some stuff to make it unique
    try {
      dos.write(Thread.currentThread().hashCode())
      dos.write(InetAddress.getLocalHost().getAddress())
      dos.writeDouble(System.currentTimeMillis())
      dos.close();
    }
    catch {
      case e: IOException => e.printStackTrace()
    }

    try {
      mdg = MessageDigest.getInstance(alg)
    }
    catch {
      case e: NoSuchAlgorithmException => {
        e.printStackTrace()
        return null
      }
    }

    mdg.reset()
    mdg.update(baos.toByteArray())

    toHexString(mdg.digest())
  }

  private def toHexString(bArray: Array[Byte]): String = {
    val charBuff = new Array[Char](bArray.length)
    for (i <- 0 to bArray.length - 1) {
      charBuff(i) = "0123456789ABCDEF".charAt((bArray(i) >> 4) & 0x0f)
    }
    new String(charBuff)
  }
}
