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

trait Observer[S]
{
  def receiveUpdate(subject: S)
}

trait Subject[S]
{
  this: S =>
  private var observers: List[Observer[S]] = Nil

  def addObserver(observer: Observer[S]) = observers = observer :: observers

  def notifyObservers() = observers.foreach(_.receiveUpdate(this))
}