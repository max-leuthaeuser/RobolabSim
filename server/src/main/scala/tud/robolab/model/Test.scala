package tud.robolab.model

import tud.robolab.model.TestResult.TestResult

object TestResult extends Enumeration {
  type TestResult = Value
  val SUCCESS, FAILED = Value

  override def toString() = this match {
    case SUCCESS => "*** SUCCESS ***"
    case FAILED => "*** FAILED ***"
  }
}

object Test {
  def apply(result: String, status: Boolean): Test = {
    def st = status match {
      case true => TestResult.SUCCESS
      case false => TestResult.FAILED
    }
    Test(result, st)
  }
}

case class Test(result: String = "No tests done yet. (Reload this page if necessary!)", status: TestResult = TestResult.FAILED)