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

package tud.robolab.model

import java.util.Observable

import tud.robolab.view.{Interface, SimulationView}
import scala.collection.concurrent.TrieMap

/** Handling all active sessions.
  * They are basically a tuple (Session -> associated View).
  */
class SessionPool extends Observable
{
  private val peer = TrieMap[Session, Option[SimulationView]]()

  /**
   * @param s a [[tud.robolab.model.Session]] you want to get the [[tud.robolab.view.SimulationView]] for.
   * @return the [[tud.robolab.view.SimulationView]] associated to that session.
   */
  def get(s: Session): Option[SimulationView] = peer(s)

  /**
   * @return all sessions and views.
   */
  def all: TrieMap[Session, Option[SimulationView]] = peer

  /**
   * Set the new [[tud.robolab.model.Session]] `s` and its [[tud.robolab.view.SimulationView]] `v`.
   * @param s the new [[tud.robolab.model.Session]] to add
   * @param v the new [[tud.robolab.view.SimulationView]] to add
   */
  def set(
    s: Session,
    v: Option[SimulationView]
    )
  {
    peer(s) = v
    setChanged()
    notifyObservers()
  }

  /**
   * @param s the [[tud.robolab.model.Session]] to remove.
   */
  def remove(s: Session)
  {
    get(s).foreach(Interface.removeSimTap)
    peer.remove(s)
    setChanged()
    notifyObservers()
  }

  /**
   * @param s the [[tud.robolab.model.Session]] to block or unblock depending on `block`.
   * @param block `true` means block the [[tud.robolab.model.Session]] `s`, false means unblock it.
   */
  def block(
    s: Session,
    block: Boolean
    )
  {
    s.client.blocked = block
    setChanged()
    notifyObservers()
  }
}