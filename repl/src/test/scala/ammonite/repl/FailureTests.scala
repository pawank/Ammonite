package ammonite.repl

import utest._

import scala.collection.{immutable => imm}

object FailureTests extends TestSuite{
  val tests = TestSuite{
    val check = new Checker()
    'compileFailure {
      check.session("""
        @ doesnt_exist
        error: not found: value doesnt_exist

        @ java
        error: package java is not a value

        @ def def
        error: identifier expected but 'def' found
      """)
    }
    'compilerCrash{
      // Make sure compiler crashes provide the appropiate error
      // messaging, and the REPL continues functioning after
      check.session("""
        @ val x = 1
        x: Int = 1

        @ /* trigger compiler crash */ trait Bar { super[Object].hashCode }
        error: java.lang.AssertionError: assertion failed

        @ 1 + x
        res1: Int = 2
      """)
    }
    'exceptionHandling{
      check.fail("""throw new Exception("lol", new Exception("hoho"))""", x =>
        // It contains the things we want
        x.contains("java.lang.Exception: lol") &&
        x.contains("java.lang.Exception: hoho") &&
        // and none of the stuff we don't want
        x.lines.length == 10 &&
        !x.contains("Something unexpected went wrong =(")
      )
    }
  }
}
