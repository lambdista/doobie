// Copyright (c) 2013-2018 Rob Norris and Contributors
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package doobie.free

import cats.~>
import cats.effect.{ Async, ContextShift, ExitCase }
import cats.free.{ Free => FF } // alias because some algebras have an op called Free
import scala.concurrent.ExecutionContext

import java.io.InputStream
import java.io.Reader
import java.lang.Class
import java.lang.String
import java.math.BigDecimal
import java.net.URL
import java.sql.Blob
import java.sql.CallableStatement
import java.sql.Clob
import java.sql.Connection
import java.sql.Date
import java.sql.NClob
import java.sql.ParameterMetaData
import java.sql.Ref
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.RowId
import java.sql.SQLType
import java.sql.SQLWarning
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.sql.{ Array => SqlArray }
import java.util.Calendar
import java.util.Map

@SuppressWarnings(Array("org.wartremover.warts.Overloading"))
object callablestatement { module =>

  // Algebra of operations for CallableStatement. Each accepts a visitor as an alternative to pattern-matching.
  sealed trait CallableStatementOp[A] {
    def visit[F[_]](v: CallableStatementOp.Visitor[F]): F[A]
  }

  // Free monad over CallableStatementOp.
  type CallableStatementIO[A] = FF[CallableStatementOp, A]

  // Module of instances and constructors of CallableStatementOp.
  object CallableStatementOp {

    // Given a CallableStatement we can embed a CallableStatementIO program in any algebra that understands embedding.
    implicit val CallableStatementOpEmbeddable: Embeddable[CallableStatementOp, CallableStatement] =
      new Embeddable[CallableStatementOp, CallableStatement] {
        def embed[A](j: CallableStatement, fa: FF[CallableStatementOp, A]) = Embedded.CallableStatement(j, fa)
      }

    // Interface for a natural transformation CallableStatementOp ~> F encoded via the visitor pattern.
    // This approach is much more efficient than pattern-matching for large algebras.
    trait Visitor[F[_]] extends (CallableStatementOp ~> F) {
      final def apply[A](fa: CallableStatementOp[A]): F[A] = fa.visit(this)

      // Common
      def raw[A](f: CallableStatement => A): F[A]
      def embed[A](e: Embedded[A]): F[A]
      def delay[A](a: () => A): F[A]
      def handleErrorWith[A](fa: CallableStatementIO[A], f: Throwable => CallableStatementIO[A]): F[A]
      def raiseError[A](e: Throwable): F[A]
      def async[A](k: (Either[Throwable, A] => Unit) => Unit): F[A]
      def asyncF[A](k: (Either[Throwable, A] => Unit) => CallableStatementIO[Unit]): F[A]
      def bracketCase[A, B](acquire: CallableStatementIO[A])(use: A => CallableStatementIO[B])(release: (A, ExitCase[Throwable]) => CallableStatementIO[Unit]): F[B]
      def shift: F[Unit]
      def evalOn[A](ec: ExecutionContext)(fa: CallableStatementIO[A]): F[A]

      // CallableStatement
      def addBatch: F[Unit]
      def addBatch(a: String): F[Unit]
      def cancel: F[Unit]
      def clearBatch: F[Unit]
      def clearParameters: F[Unit]
      def clearWarnings: F[Unit]
      def close: F[Unit]
      def closeOnCompletion: F[Unit]
      def execute: F[Boolean]
      def execute(a: String): F[Boolean]
      def execute(a: String, b: Array[Int]): F[Boolean]
      def execute(a: String, b: Array[String]): F[Boolean]
      def execute(a: String, b: Int): F[Boolean]
      def executeBatch: F[Array[Int]]
      def executeLargeBatch: F[Array[Long]]
      def executeLargeUpdate: F[Long]
      def executeLargeUpdate(a: String): F[Long]
      def executeLargeUpdate(a: String, b: Array[Int]): F[Long]
      def executeLargeUpdate(a: String, b: Array[String]): F[Long]
      def executeLargeUpdate(a: String, b: Int): F[Long]
      def executeQuery: F[ResultSet]
      def executeQuery(a: String): F[ResultSet]
      def executeUpdate: F[Int]
      def executeUpdate(a: String): F[Int]
      def executeUpdate(a: String, b: Array[Int]): F[Int]
      def executeUpdate(a: String, b: Array[String]): F[Int]
      def executeUpdate(a: String, b: Int): F[Int]
      def getArray(a: Int): F[SqlArray]
      def getArray(a: String): F[SqlArray]
      def getBigDecimal(a: Int): F[BigDecimal]
      def getBigDecimal(a: Int, b: Int): F[BigDecimal]
      def getBigDecimal(a: String): F[BigDecimal]
      def getBlob(a: Int): F[Blob]
      def getBlob(a: String): F[Blob]
      def getBoolean(a: Int): F[Boolean]
      def getBoolean(a: String): F[Boolean]
      def getByte(a: Int): F[Byte]
      def getByte(a: String): F[Byte]
      def getBytes(a: Int): F[Array[Byte]]
      def getBytes(a: String): F[Array[Byte]]
      def getCharacterStream(a: Int): F[Reader]
      def getCharacterStream(a: String): F[Reader]
      def getClob(a: Int): F[Clob]
      def getClob(a: String): F[Clob]
      def getConnection: F[Connection]
      def getDate(a: Int): F[Date]
      def getDate(a: Int, b: Calendar): F[Date]
      def getDate(a: String): F[Date]
      def getDate(a: String, b: Calendar): F[Date]
      def getDouble(a: Int): F[Double]
      def getDouble(a: String): F[Double]
      def getFetchDirection: F[Int]
      def getFetchSize: F[Int]
      def getFloat(a: Int): F[Float]
      def getFloat(a: String): F[Float]
      def getGeneratedKeys: F[ResultSet]
      def getInt(a: Int): F[Int]
      def getInt(a: String): F[Int]
      def getLargeMaxRows: F[Long]
      def getLargeUpdateCount: F[Long]
      def getLong(a: Int): F[Long]
      def getLong(a: String): F[Long]
      def getMaxFieldSize: F[Int]
      def getMaxRows: F[Int]
      def getMetaData: F[ResultSetMetaData]
      def getMoreResults: F[Boolean]
      def getMoreResults(a: Int): F[Boolean]
      def getNCharacterStream(a: Int): F[Reader]
      def getNCharacterStream(a: String): F[Reader]
      def getNClob(a: Int): F[NClob]
      def getNClob(a: String): F[NClob]
      def getNString(a: Int): F[String]
      def getNString(a: String): F[String]
      def getObject(a: Int): F[AnyRef]
      def getObject[T](a: Int, b: Class[T]): F[T]
      def getObject(a: Int, b: Map[String, Class[_]]): F[AnyRef]
      def getObject(a: String): F[AnyRef]
      def getObject[T](a: String, b: Class[T]): F[T]
      def getObject(a: String, b: Map[String, Class[_]]): F[AnyRef]
      def getParameterMetaData: F[ParameterMetaData]
      def getQueryTimeout: F[Int]
      def getRef(a: Int): F[Ref]
      def getRef(a: String): F[Ref]
      def getResultSet: F[ResultSet]
      def getResultSetConcurrency: F[Int]
      def getResultSetHoldability: F[Int]
      def getResultSetType: F[Int]
      def getRowId(a: Int): F[RowId]
      def getRowId(a: String): F[RowId]
      def getSQLXML(a: Int): F[SQLXML]
      def getSQLXML(a: String): F[SQLXML]
      def getShort(a: Int): F[Short]
      def getShort(a: String): F[Short]
      def getString(a: Int): F[String]
      def getString(a: String): F[String]
      def getTime(a: Int): F[Time]
      def getTime(a: Int, b: Calendar): F[Time]
      def getTime(a: String): F[Time]
      def getTime(a: String, b: Calendar): F[Time]
      def getTimestamp(a: Int): F[Timestamp]
      def getTimestamp(a: Int, b: Calendar): F[Timestamp]
      def getTimestamp(a: String): F[Timestamp]
      def getTimestamp(a: String, b: Calendar): F[Timestamp]
      def getURL(a: Int): F[URL]
      def getURL(a: String): F[URL]
      def getUpdateCount: F[Int]
      def getWarnings: F[SQLWarning]
      def isCloseOnCompletion: F[Boolean]
      def isClosed: F[Boolean]
      def isPoolable: F[Boolean]
      def isWrapperFor(a: Class[_]): F[Boolean]
      def registerOutParameter(a: Int, b: Int): F[Unit]
      def registerOutParameter(a: Int, b: Int, c: Int): F[Unit]
      def registerOutParameter(a: Int, b: Int, c: String): F[Unit]
      def registerOutParameter(a: Int, b: SQLType): F[Unit]
      def registerOutParameter(a: Int, b: SQLType, c: Int): F[Unit]
      def registerOutParameter(a: Int, b: SQLType, c: String): F[Unit]
      def registerOutParameter(a: String, b: Int): F[Unit]
      def registerOutParameter(a: String, b: Int, c: Int): F[Unit]
      def registerOutParameter(a: String, b: Int, c: String): F[Unit]
      def registerOutParameter(a: String, b: SQLType): F[Unit]
      def registerOutParameter(a: String, b: SQLType, c: Int): F[Unit]
      def registerOutParameter(a: String, b: SQLType, c: String): F[Unit]
      def setArray(a: Int, b: SqlArray): F[Unit]
      def setAsciiStream(a: Int, b: InputStream): F[Unit]
      def setAsciiStream(a: Int, b: InputStream, c: Int): F[Unit]
      def setAsciiStream(a: Int, b: InputStream, c: Long): F[Unit]
      def setAsciiStream(a: String, b: InputStream): F[Unit]
      def setAsciiStream(a: String, b: InputStream, c: Int): F[Unit]
      def setAsciiStream(a: String, b: InputStream, c: Long): F[Unit]
      def setBigDecimal(a: Int, b: BigDecimal): F[Unit]
      def setBigDecimal(a: String, b: BigDecimal): F[Unit]
      def setBinaryStream(a: Int, b: InputStream): F[Unit]
      def setBinaryStream(a: Int, b: InputStream, c: Int): F[Unit]
      def setBinaryStream(a: Int, b: InputStream, c: Long): F[Unit]
      def setBinaryStream(a: String, b: InputStream): F[Unit]
      def setBinaryStream(a: String, b: InputStream, c: Int): F[Unit]
      def setBinaryStream(a: String, b: InputStream, c: Long): F[Unit]
      def setBlob(a: Int, b: Blob): F[Unit]
      def setBlob(a: Int, b: InputStream): F[Unit]
      def setBlob(a: Int, b: InputStream, c: Long): F[Unit]
      def setBlob(a: String, b: Blob): F[Unit]
      def setBlob(a: String, b: InputStream): F[Unit]
      def setBlob(a: String, b: InputStream, c: Long): F[Unit]
      def setBoolean(a: Int, b: Boolean): F[Unit]
      def setBoolean(a: String, b: Boolean): F[Unit]
      def setByte(a: Int, b: Byte): F[Unit]
      def setByte(a: String, b: Byte): F[Unit]
      def setBytes(a: Int, b: Array[Byte]): F[Unit]
      def setBytes(a: String, b: Array[Byte]): F[Unit]
      def setCharacterStream(a: Int, b: Reader): F[Unit]
      def setCharacterStream(a: Int, b: Reader, c: Int): F[Unit]
      def setCharacterStream(a: Int, b: Reader, c: Long): F[Unit]
      def setCharacterStream(a: String, b: Reader): F[Unit]
      def setCharacterStream(a: String, b: Reader, c: Int): F[Unit]
      def setCharacterStream(a: String, b: Reader, c: Long): F[Unit]
      def setClob(a: Int, b: Clob): F[Unit]
      def setClob(a: Int, b: Reader): F[Unit]
      def setClob(a: Int, b: Reader, c: Long): F[Unit]
      def setClob(a: String, b: Clob): F[Unit]
      def setClob(a: String, b: Reader): F[Unit]
      def setClob(a: String, b: Reader, c: Long): F[Unit]
      def setCursorName(a: String): F[Unit]
      def setDate(a: Int, b: Date): F[Unit]
      def setDate(a: Int, b: Date, c: Calendar): F[Unit]
      def setDate(a: String, b: Date): F[Unit]
      def setDate(a: String, b: Date, c: Calendar): F[Unit]
      def setDouble(a: Int, b: Double): F[Unit]
      def setDouble(a: String, b: Double): F[Unit]
      def setEscapeProcessing(a: Boolean): F[Unit]
      def setFetchDirection(a: Int): F[Unit]
      def setFetchSize(a: Int): F[Unit]
      def setFloat(a: Int, b: Float): F[Unit]
      def setFloat(a: String, b: Float): F[Unit]
      def setInt(a: Int, b: Int): F[Unit]
      def setInt(a: String, b: Int): F[Unit]
      def setLargeMaxRows(a: Long): F[Unit]
      def setLong(a: Int, b: Long): F[Unit]
      def setLong(a: String, b: Long): F[Unit]
      def setMaxFieldSize(a: Int): F[Unit]
      def setMaxRows(a: Int): F[Unit]
      def setNCharacterStream(a: Int, b: Reader): F[Unit]
      def setNCharacterStream(a: Int, b: Reader, c: Long): F[Unit]
      def setNCharacterStream(a: String, b: Reader): F[Unit]
      def setNCharacterStream(a: String, b: Reader, c: Long): F[Unit]
      def setNClob(a: Int, b: NClob): F[Unit]
      def setNClob(a: Int, b: Reader): F[Unit]
      def setNClob(a: Int, b: Reader, c: Long): F[Unit]
      def setNClob(a: String, b: NClob): F[Unit]
      def setNClob(a: String, b: Reader): F[Unit]
      def setNClob(a: String, b: Reader, c: Long): F[Unit]
      def setNString(a: Int, b: String): F[Unit]
      def setNString(a: String, b: String): F[Unit]
      def setNull(a: Int, b: Int): F[Unit]
      def setNull(a: Int, b: Int, c: String): F[Unit]
      def setNull(a: String, b: Int): F[Unit]
      def setNull(a: String, b: Int, c: String): F[Unit]
      def setObject(a: Int, b: AnyRef): F[Unit]
      def setObject(a: Int, b: AnyRef, c: Int): F[Unit]
      def setObject(a: Int, b: AnyRef, c: Int, d: Int): F[Unit]
      def setObject(a: Int, b: AnyRef, c: SQLType): F[Unit]
      def setObject(a: Int, b: AnyRef, c: SQLType, d: Int): F[Unit]
      def setObject(a: String, b: AnyRef): F[Unit]
      def setObject(a: String, b: AnyRef, c: Int): F[Unit]
      def setObject(a: String, b: AnyRef, c: Int, d: Int): F[Unit]
      def setObject(a: String, b: AnyRef, c: SQLType): F[Unit]
      def setObject(a: String, b: AnyRef, c: SQLType, d: Int): F[Unit]
      def setPoolable(a: Boolean): F[Unit]
      def setQueryTimeout(a: Int): F[Unit]
      def setRef(a: Int, b: Ref): F[Unit]
      def setRowId(a: Int, b: RowId): F[Unit]
      def setRowId(a: String, b: RowId): F[Unit]
      def setSQLXML(a: Int, b: SQLXML): F[Unit]
      def setSQLXML(a: String, b: SQLXML): F[Unit]
      def setShort(a: Int, b: Short): F[Unit]
      def setShort(a: String, b: Short): F[Unit]
      def setString(a: Int, b: String): F[Unit]
      def setString(a: String, b: String): F[Unit]
      def setTime(a: Int, b: Time): F[Unit]
      def setTime(a: Int, b: Time, c: Calendar): F[Unit]
      def setTime(a: String, b: Time): F[Unit]
      def setTime(a: String, b: Time, c: Calendar): F[Unit]
      def setTimestamp(a: Int, b: Timestamp): F[Unit]
      def setTimestamp(a: Int, b: Timestamp, c: Calendar): F[Unit]
      def setTimestamp(a: String, b: Timestamp): F[Unit]
      def setTimestamp(a: String, b: Timestamp, c: Calendar): F[Unit]
      def setURL(a: Int, b: URL): F[Unit]
      def setURL(a: String, b: URL): F[Unit]
      def setUnicodeStream(a: Int, b: InputStream, c: Int): F[Unit]
      def unwrap[T](a: Class[T]): F[T]
      def wasNull: F[Boolean]

    }

    // Common operations for all algebras.
    final case class Raw[A](f: CallableStatement => A) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.raw(f)
    }
    final case class Embed[A](e: Embedded[A]) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.embed(e)
    }
    final case class Delay[A](a: () => A) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.delay(a)
    }
    final case class HandleErrorWith[A](fa: CallableStatementIO[A], f: Throwable => CallableStatementIO[A]) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.handleErrorWith(fa, f)
    }
    final case class RaiseError[A](e: Throwable) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.raiseError(e)
    }
    final case class Async1[A](k: (Either[Throwable, A] => Unit) => Unit) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.async(k)
    }
    final case class AsyncF[A](k: (Either[Throwable, A] => Unit) => CallableStatementIO[Unit]) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.asyncF(k)
    }
    final case class BracketCase[A, B](acquire: CallableStatementIO[A], use: A => CallableStatementIO[B], release: (A, ExitCase[Throwable]) => CallableStatementIO[Unit]) extends CallableStatementOp[B] {
      def visit[F[_]](v: Visitor[F]) = v.bracketCase(acquire)(use)(release)
    }
    final case object Shift extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.shift
    }
    final case class EvalOn[A](ec: ExecutionContext, fa: CallableStatementIO[A]) extends CallableStatementOp[A] {
      def visit[F[_]](v: Visitor[F]) = v.evalOn(ec)(fa)
    }

    // CallableStatement-specific operations.
    final case object AddBatch extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.addBatch
    }
    final case class  AddBatch1(a: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.addBatch(a)
    }
    final case object Cancel extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.cancel
    }
    final case object ClearBatch extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.clearBatch
    }
    final case object ClearParameters extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.clearParameters
    }
    final case object ClearWarnings extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.clearWarnings
    }
    final case object Close extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.close
    }
    final case object CloseOnCompletion extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.closeOnCompletion
    }
    final case object Execute extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.execute
    }
    final case class  Execute1(a: String) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.execute(a)
    }
    final case class  Execute2(a: String, b: Array[Int]) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.execute(a, b)
    }
    final case class  Execute3(a: String, b: Array[String]) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.execute(a, b)
    }
    final case class  Execute4(a: String, b: Int) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.execute(a, b)
    }
    final case object ExecuteBatch extends CallableStatementOp[Array[Int]] {
      def visit[F[_]](v: Visitor[F]) = v.executeBatch
    }
    final case object ExecuteLargeBatch extends CallableStatementOp[Array[Long]] {
      def visit[F[_]](v: Visitor[F]) = v.executeLargeBatch
    }
    final case object ExecuteLargeUpdate extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.executeLargeUpdate
    }
    final case class  ExecuteLargeUpdate1(a: String) extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.executeLargeUpdate(a)
    }
    final case class  ExecuteLargeUpdate2(a: String, b: Array[Int]) extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.executeLargeUpdate(a, b)
    }
    final case class  ExecuteLargeUpdate3(a: String, b: Array[String]) extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.executeLargeUpdate(a, b)
    }
    final case class  ExecuteLargeUpdate4(a: String, b: Int) extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.executeLargeUpdate(a, b)
    }
    final case object ExecuteQuery extends CallableStatementOp[ResultSet] {
      def visit[F[_]](v: Visitor[F]) = v.executeQuery
    }
    final case class  ExecuteQuery1(a: String) extends CallableStatementOp[ResultSet] {
      def visit[F[_]](v: Visitor[F]) = v.executeQuery(a)
    }
    final case object ExecuteUpdate extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.executeUpdate
    }
    final case class  ExecuteUpdate1(a: String) extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.executeUpdate(a)
    }
    final case class  ExecuteUpdate2(a: String, b: Array[Int]) extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.executeUpdate(a, b)
    }
    final case class  ExecuteUpdate3(a: String, b: Array[String]) extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.executeUpdate(a, b)
    }
    final case class  ExecuteUpdate4(a: String, b: Int) extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.executeUpdate(a, b)
    }
    final case class  GetArray(a: Int) extends CallableStatementOp[SqlArray] {
      def visit[F[_]](v: Visitor[F]) = v.getArray(a)
    }
    final case class  GetArray1(a: String) extends CallableStatementOp[SqlArray] {
      def visit[F[_]](v: Visitor[F]) = v.getArray(a)
    }
    final case class  GetBigDecimal(a: Int) extends CallableStatementOp[BigDecimal] {
      def visit[F[_]](v: Visitor[F]) = v.getBigDecimal(a)
    }
    final case class  GetBigDecimal1(a: Int, b: Int) extends CallableStatementOp[BigDecimal] {
      def visit[F[_]](v: Visitor[F]) = v.getBigDecimal(a, b)
    }
    final case class  GetBigDecimal2(a: String) extends CallableStatementOp[BigDecimal] {
      def visit[F[_]](v: Visitor[F]) = v.getBigDecimal(a)
    }
    final case class  GetBlob(a: Int) extends CallableStatementOp[Blob] {
      def visit[F[_]](v: Visitor[F]) = v.getBlob(a)
    }
    final case class  GetBlob1(a: String) extends CallableStatementOp[Blob] {
      def visit[F[_]](v: Visitor[F]) = v.getBlob(a)
    }
    final case class  GetBoolean(a: Int) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.getBoolean(a)
    }
    final case class  GetBoolean1(a: String) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.getBoolean(a)
    }
    final case class  GetByte(a: Int) extends CallableStatementOp[Byte] {
      def visit[F[_]](v: Visitor[F]) = v.getByte(a)
    }
    final case class  GetByte1(a: String) extends CallableStatementOp[Byte] {
      def visit[F[_]](v: Visitor[F]) = v.getByte(a)
    }
    final case class  GetBytes(a: Int) extends CallableStatementOp[Array[Byte]] {
      def visit[F[_]](v: Visitor[F]) = v.getBytes(a)
    }
    final case class  GetBytes1(a: String) extends CallableStatementOp[Array[Byte]] {
      def visit[F[_]](v: Visitor[F]) = v.getBytes(a)
    }
    final case class  GetCharacterStream(a: Int) extends CallableStatementOp[Reader] {
      def visit[F[_]](v: Visitor[F]) = v.getCharacterStream(a)
    }
    final case class  GetCharacterStream1(a: String) extends CallableStatementOp[Reader] {
      def visit[F[_]](v: Visitor[F]) = v.getCharacterStream(a)
    }
    final case class  GetClob(a: Int) extends CallableStatementOp[Clob] {
      def visit[F[_]](v: Visitor[F]) = v.getClob(a)
    }
    final case class  GetClob1(a: String) extends CallableStatementOp[Clob] {
      def visit[F[_]](v: Visitor[F]) = v.getClob(a)
    }
    final case object GetConnection extends CallableStatementOp[Connection] {
      def visit[F[_]](v: Visitor[F]) = v.getConnection
    }
    final case class  GetDate(a: Int) extends CallableStatementOp[Date] {
      def visit[F[_]](v: Visitor[F]) = v.getDate(a)
    }
    final case class  GetDate1(a: Int, b: Calendar) extends CallableStatementOp[Date] {
      def visit[F[_]](v: Visitor[F]) = v.getDate(a, b)
    }
    final case class  GetDate2(a: String) extends CallableStatementOp[Date] {
      def visit[F[_]](v: Visitor[F]) = v.getDate(a)
    }
    final case class  GetDate3(a: String, b: Calendar) extends CallableStatementOp[Date] {
      def visit[F[_]](v: Visitor[F]) = v.getDate(a, b)
    }
    final case class  GetDouble(a: Int) extends CallableStatementOp[Double] {
      def visit[F[_]](v: Visitor[F]) = v.getDouble(a)
    }
    final case class  GetDouble1(a: String) extends CallableStatementOp[Double] {
      def visit[F[_]](v: Visitor[F]) = v.getDouble(a)
    }
    final case object GetFetchDirection extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getFetchDirection
    }
    final case object GetFetchSize extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getFetchSize
    }
    final case class  GetFloat(a: Int) extends CallableStatementOp[Float] {
      def visit[F[_]](v: Visitor[F]) = v.getFloat(a)
    }
    final case class  GetFloat1(a: String) extends CallableStatementOp[Float] {
      def visit[F[_]](v: Visitor[F]) = v.getFloat(a)
    }
    final case object GetGeneratedKeys extends CallableStatementOp[ResultSet] {
      def visit[F[_]](v: Visitor[F]) = v.getGeneratedKeys
    }
    final case class  GetInt(a: Int) extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getInt(a)
    }
    final case class  GetInt1(a: String) extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getInt(a)
    }
    final case object GetLargeMaxRows extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.getLargeMaxRows
    }
    final case object GetLargeUpdateCount extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.getLargeUpdateCount
    }
    final case class  GetLong(a: Int) extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.getLong(a)
    }
    final case class  GetLong1(a: String) extends CallableStatementOp[Long] {
      def visit[F[_]](v: Visitor[F]) = v.getLong(a)
    }
    final case object GetMaxFieldSize extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getMaxFieldSize
    }
    final case object GetMaxRows extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getMaxRows
    }
    final case object GetMetaData extends CallableStatementOp[ResultSetMetaData] {
      def visit[F[_]](v: Visitor[F]) = v.getMetaData
    }
    final case object GetMoreResults extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.getMoreResults
    }
    final case class  GetMoreResults1(a: Int) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.getMoreResults(a)
    }
    final case class  GetNCharacterStream(a: Int) extends CallableStatementOp[Reader] {
      def visit[F[_]](v: Visitor[F]) = v.getNCharacterStream(a)
    }
    final case class  GetNCharacterStream1(a: String) extends CallableStatementOp[Reader] {
      def visit[F[_]](v: Visitor[F]) = v.getNCharacterStream(a)
    }
    final case class  GetNClob(a: Int) extends CallableStatementOp[NClob] {
      def visit[F[_]](v: Visitor[F]) = v.getNClob(a)
    }
    final case class  GetNClob1(a: String) extends CallableStatementOp[NClob] {
      def visit[F[_]](v: Visitor[F]) = v.getNClob(a)
    }
    final case class  GetNString(a: Int) extends CallableStatementOp[String] {
      def visit[F[_]](v: Visitor[F]) = v.getNString(a)
    }
    final case class  GetNString1(a: String) extends CallableStatementOp[String] {
      def visit[F[_]](v: Visitor[F]) = v.getNString(a)
    }
    final case class  GetObject(a: Int) extends CallableStatementOp[AnyRef] {
      def visit[F[_]](v: Visitor[F]) = v.getObject(a)
    }
    final case class  GetObject1[T](a: Int, b: Class[T]) extends CallableStatementOp[T] {
      def visit[F[_]](v: Visitor[F]) = v.getObject(a, b)
    }
    final case class  GetObject2(a: Int, b: Map[String, Class[_]]) extends CallableStatementOp[AnyRef] {
      def visit[F[_]](v: Visitor[F]) = v.getObject(a, b)
    }
    final case class  GetObject3(a: String) extends CallableStatementOp[AnyRef] {
      def visit[F[_]](v: Visitor[F]) = v.getObject(a)
    }
    final case class  GetObject4[T](a: String, b: Class[T]) extends CallableStatementOp[T] {
      def visit[F[_]](v: Visitor[F]) = v.getObject(a, b)
    }
    final case class  GetObject5(a: String, b: Map[String, Class[_]]) extends CallableStatementOp[AnyRef] {
      def visit[F[_]](v: Visitor[F]) = v.getObject(a, b)
    }
    final case object GetParameterMetaData extends CallableStatementOp[ParameterMetaData] {
      def visit[F[_]](v: Visitor[F]) = v.getParameterMetaData
    }
    final case object GetQueryTimeout extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getQueryTimeout
    }
    final case class  GetRef(a: Int) extends CallableStatementOp[Ref] {
      def visit[F[_]](v: Visitor[F]) = v.getRef(a)
    }
    final case class  GetRef1(a: String) extends CallableStatementOp[Ref] {
      def visit[F[_]](v: Visitor[F]) = v.getRef(a)
    }
    final case object GetResultSet extends CallableStatementOp[ResultSet] {
      def visit[F[_]](v: Visitor[F]) = v.getResultSet
    }
    final case object GetResultSetConcurrency extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getResultSetConcurrency
    }
    final case object GetResultSetHoldability extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getResultSetHoldability
    }
    final case object GetResultSetType extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getResultSetType
    }
    final case class  GetRowId(a: Int) extends CallableStatementOp[RowId] {
      def visit[F[_]](v: Visitor[F]) = v.getRowId(a)
    }
    final case class  GetRowId1(a: String) extends CallableStatementOp[RowId] {
      def visit[F[_]](v: Visitor[F]) = v.getRowId(a)
    }
    final case class  GetSQLXML(a: Int) extends CallableStatementOp[SQLXML] {
      def visit[F[_]](v: Visitor[F]) = v.getSQLXML(a)
    }
    final case class  GetSQLXML1(a: String) extends CallableStatementOp[SQLXML] {
      def visit[F[_]](v: Visitor[F]) = v.getSQLXML(a)
    }
    final case class  GetShort(a: Int) extends CallableStatementOp[Short] {
      def visit[F[_]](v: Visitor[F]) = v.getShort(a)
    }
    final case class  GetShort1(a: String) extends CallableStatementOp[Short] {
      def visit[F[_]](v: Visitor[F]) = v.getShort(a)
    }
    final case class  GetString(a: Int) extends CallableStatementOp[String] {
      def visit[F[_]](v: Visitor[F]) = v.getString(a)
    }
    final case class  GetString1(a: String) extends CallableStatementOp[String] {
      def visit[F[_]](v: Visitor[F]) = v.getString(a)
    }
    final case class  GetTime(a: Int) extends CallableStatementOp[Time] {
      def visit[F[_]](v: Visitor[F]) = v.getTime(a)
    }
    final case class  GetTime1(a: Int, b: Calendar) extends CallableStatementOp[Time] {
      def visit[F[_]](v: Visitor[F]) = v.getTime(a, b)
    }
    final case class  GetTime2(a: String) extends CallableStatementOp[Time] {
      def visit[F[_]](v: Visitor[F]) = v.getTime(a)
    }
    final case class  GetTime3(a: String, b: Calendar) extends CallableStatementOp[Time] {
      def visit[F[_]](v: Visitor[F]) = v.getTime(a, b)
    }
    final case class  GetTimestamp(a: Int) extends CallableStatementOp[Timestamp] {
      def visit[F[_]](v: Visitor[F]) = v.getTimestamp(a)
    }
    final case class  GetTimestamp1(a: Int, b: Calendar) extends CallableStatementOp[Timestamp] {
      def visit[F[_]](v: Visitor[F]) = v.getTimestamp(a, b)
    }
    final case class  GetTimestamp2(a: String) extends CallableStatementOp[Timestamp] {
      def visit[F[_]](v: Visitor[F]) = v.getTimestamp(a)
    }
    final case class  GetTimestamp3(a: String, b: Calendar) extends CallableStatementOp[Timestamp] {
      def visit[F[_]](v: Visitor[F]) = v.getTimestamp(a, b)
    }
    final case class  GetURL(a: Int) extends CallableStatementOp[URL] {
      def visit[F[_]](v: Visitor[F]) = v.getURL(a)
    }
    final case class  GetURL1(a: String) extends CallableStatementOp[URL] {
      def visit[F[_]](v: Visitor[F]) = v.getURL(a)
    }
    final case object GetUpdateCount extends CallableStatementOp[Int] {
      def visit[F[_]](v: Visitor[F]) = v.getUpdateCount
    }
    final case object GetWarnings extends CallableStatementOp[SQLWarning] {
      def visit[F[_]](v: Visitor[F]) = v.getWarnings
    }
    final case object IsCloseOnCompletion extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.isCloseOnCompletion
    }
    final case object IsClosed extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.isClosed
    }
    final case object IsPoolable extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.isPoolable
    }
    final case class  IsWrapperFor(a: Class[_]) extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.isWrapperFor(a)
    }
    final case class  RegisterOutParameter(a: Int, b: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b)
    }
    final case class  RegisterOutParameter1(a: Int, b: Int, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter2(a: Int, b: Int, c: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter3(a: Int, b: SQLType) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b)
    }
    final case class  RegisterOutParameter4(a: Int, b: SQLType, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter5(a: Int, b: SQLType, c: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter6(a: String, b: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b)
    }
    final case class  RegisterOutParameter7(a: String, b: Int, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter8(a: String, b: Int, c: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter9(a: String, b: SQLType) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b)
    }
    final case class  RegisterOutParameter10(a: String, b: SQLType, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  RegisterOutParameter11(a: String, b: SQLType, c: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.registerOutParameter(a, b, c)
    }
    final case class  SetArray(a: Int, b: SqlArray) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setArray(a, b)
    }
    final case class  SetAsciiStream(a: Int, b: InputStream) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setAsciiStream(a, b)
    }
    final case class  SetAsciiStream1(a: Int, b: InputStream, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setAsciiStream(a, b, c)
    }
    final case class  SetAsciiStream2(a: Int, b: InputStream, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setAsciiStream(a, b, c)
    }
    final case class  SetAsciiStream3(a: String, b: InputStream) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setAsciiStream(a, b)
    }
    final case class  SetAsciiStream4(a: String, b: InputStream, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setAsciiStream(a, b, c)
    }
    final case class  SetAsciiStream5(a: String, b: InputStream, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setAsciiStream(a, b, c)
    }
    final case class  SetBigDecimal(a: Int, b: BigDecimal) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBigDecimal(a, b)
    }
    final case class  SetBigDecimal1(a: String, b: BigDecimal) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBigDecimal(a, b)
    }
    final case class  SetBinaryStream(a: Int, b: InputStream) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBinaryStream(a, b)
    }
    final case class  SetBinaryStream1(a: Int, b: InputStream, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBinaryStream(a, b, c)
    }
    final case class  SetBinaryStream2(a: Int, b: InputStream, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBinaryStream(a, b, c)
    }
    final case class  SetBinaryStream3(a: String, b: InputStream) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBinaryStream(a, b)
    }
    final case class  SetBinaryStream4(a: String, b: InputStream, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBinaryStream(a, b, c)
    }
    final case class  SetBinaryStream5(a: String, b: InputStream, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBinaryStream(a, b, c)
    }
    final case class  SetBlob(a: Int, b: Blob) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBlob(a, b)
    }
    final case class  SetBlob1(a: Int, b: InputStream) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBlob(a, b)
    }
    final case class  SetBlob2(a: Int, b: InputStream, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBlob(a, b, c)
    }
    final case class  SetBlob3(a: String, b: Blob) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBlob(a, b)
    }
    final case class  SetBlob4(a: String, b: InputStream) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBlob(a, b)
    }
    final case class  SetBlob5(a: String, b: InputStream, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBlob(a, b, c)
    }
    final case class  SetBoolean(a: Int, b: Boolean) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBoolean(a, b)
    }
    final case class  SetBoolean1(a: String, b: Boolean) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBoolean(a, b)
    }
    final case class  SetByte(a: Int, b: Byte) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setByte(a, b)
    }
    final case class  SetByte1(a: String, b: Byte) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setByte(a, b)
    }
    final case class  SetBytes(a: Int, b: Array[Byte]) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBytes(a, b)
    }
    final case class  SetBytes1(a: String, b: Array[Byte]) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setBytes(a, b)
    }
    final case class  SetCharacterStream(a: Int, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCharacterStream(a, b)
    }
    final case class  SetCharacterStream1(a: Int, b: Reader, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCharacterStream(a, b, c)
    }
    final case class  SetCharacterStream2(a: Int, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCharacterStream(a, b, c)
    }
    final case class  SetCharacterStream3(a: String, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCharacterStream(a, b)
    }
    final case class  SetCharacterStream4(a: String, b: Reader, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCharacterStream(a, b, c)
    }
    final case class  SetCharacterStream5(a: String, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCharacterStream(a, b, c)
    }
    final case class  SetClob(a: Int, b: Clob) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setClob(a, b)
    }
    final case class  SetClob1(a: Int, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setClob(a, b)
    }
    final case class  SetClob2(a: Int, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setClob(a, b, c)
    }
    final case class  SetClob3(a: String, b: Clob) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setClob(a, b)
    }
    final case class  SetClob4(a: String, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setClob(a, b)
    }
    final case class  SetClob5(a: String, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setClob(a, b, c)
    }
    final case class  SetCursorName(a: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setCursorName(a)
    }
    final case class  SetDate(a: Int, b: Date) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setDate(a, b)
    }
    final case class  SetDate1(a: Int, b: Date, c: Calendar) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setDate(a, b, c)
    }
    final case class  SetDate2(a: String, b: Date) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setDate(a, b)
    }
    final case class  SetDate3(a: String, b: Date, c: Calendar) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setDate(a, b, c)
    }
    final case class  SetDouble(a: Int, b: Double) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setDouble(a, b)
    }
    final case class  SetDouble1(a: String, b: Double) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setDouble(a, b)
    }
    final case class  SetEscapeProcessing(a: Boolean) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setEscapeProcessing(a)
    }
    final case class  SetFetchDirection(a: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setFetchDirection(a)
    }
    final case class  SetFetchSize(a: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setFetchSize(a)
    }
    final case class  SetFloat(a: Int, b: Float) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setFloat(a, b)
    }
    final case class  SetFloat1(a: String, b: Float) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setFloat(a, b)
    }
    final case class  SetInt(a: Int, b: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setInt(a, b)
    }
    final case class  SetInt1(a: String, b: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setInt(a, b)
    }
    final case class  SetLargeMaxRows(a: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setLargeMaxRows(a)
    }
    final case class  SetLong(a: Int, b: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setLong(a, b)
    }
    final case class  SetLong1(a: String, b: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setLong(a, b)
    }
    final case class  SetMaxFieldSize(a: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setMaxFieldSize(a)
    }
    final case class  SetMaxRows(a: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setMaxRows(a)
    }
    final case class  SetNCharacterStream(a: Int, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNCharacterStream(a, b)
    }
    final case class  SetNCharacterStream1(a: Int, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNCharacterStream(a, b, c)
    }
    final case class  SetNCharacterStream2(a: String, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNCharacterStream(a, b)
    }
    final case class  SetNCharacterStream3(a: String, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNCharacterStream(a, b, c)
    }
    final case class  SetNClob(a: Int, b: NClob) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNClob(a, b)
    }
    final case class  SetNClob1(a: Int, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNClob(a, b)
    }
    final case class  SetNClob2(a: Int, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNClob(a, b, c)
    }
    final case class  SetNClob3(a: String, b: NClob) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNClob(a, b)
    }
    final case class  SetNClob4(a: String, b: Reader) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNClob(a, b)
    }
    final case class  SetNClob5(a: String, b: Reader, c: Long) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNClob(a, b, c)
    }
    final case class  SetNString(a: Int, b: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNString(a, b)
    }
    final case class  SetNString1(a: String, b: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNString(a, b)
    }
    final case class  SetNull(a: Int, b: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNull(a, b)
    }
    final case class  SetNull1(a: Int, b: Int, c: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNull(a, b, c)
    }
    final case class  SetNull2(a: String, b: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNull(a, b)
    }
    final case class  SetNull3(a: String, b: Int, c: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setNull(a, b, c)
    }
    final case class  SetObject(a: Int, b: AnyRef) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b)
    }
    final case class  SetObject1(a: Int, b: AnyRef, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c)
    }
    final case class  SetObject2(a: Int, b: AnyRef, c: Int, d: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c, d)
    }
    final case class  SetObject3(a: Int, b: AnyRef, c: SQLType) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c)
    }
    final case class  SetObject4(a: Int, b: AnyRef, c: SQLType, d: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c, d)
    }
    final case class  SetObject5(a: String, b: AnyRef) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b)
    }
    final case class  SetObject6(a: String, b: AnyRef, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c)
    }
    final case class  SetObject7(a: String, b: AnyRef, c: Int, d: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c, d)
    }
    final case class  SetObject8(a: String, b: AnyRef, c: SQLType) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c)
    }
    final case class  SetObject9(a: String, b: AnyRef, c: SQLType, d: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setObject(a, b, c, d)
    }
    final case class  SetPoolable(a: Boolean) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setPoolable(a)
    }
    final case class  SetQueryTimeout(a: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setQueryTimeout(a)
    }
    final case class  SetRef(a: Int, b: Ref) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setRef(a, b)
    }
    final case class  SetRowId(a: Int, b: RowId) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setRowId(a, b)
    }
    final case class  SetRowId1(a: String, b: RowId) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setRowId(a, b)
    }
    final case class  SetSQLXML(a: Int, b: SQLXML) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setSQLXML(a, b)
    }
    final case class  SetSQLXML1(a: String, b: SQLXML) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setSQLXML(a, b)
    }
    final case class  SetShort(a: Int, b: Short) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setShort(a, b)
    }
    final case class  SetShort1(a: String, b: Short) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setShort(a, b)
    }
    final case class  SetString(a: Int, b: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setString(a, b)
    }
    final case class  SetString1(a: String, b: String) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setString(a, b)
    }
    final case class  SetTime(a: Int, b: Time) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTime(a, b)
    }
    final case class  SetTime1(a: Int, b: Time, c: Calendar) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTime(a, b, c)
    }
    final case class  SetTime2(a: String, b: Time) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTime(a, b)
    }
    final case class  SetTime3(a: String, b: Time, c: Calendar) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTime(a, b, c)
    }
    final case class  SetTimestamp(a: Int, b: Timestamp) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTimestamp(a, b)
    }
    final case class  SetTimestamp1(a: Int, b: Timestamp, c: Calendar) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTimestamp(a, b, c)
    }
    final case class  SetTimestamp2(a: String, b: Timestamp) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTimestamp(a, b)
    }
    final case class  SetTimestamp3(a: String, b: Timestamp, c: Calendar) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setTimestamp(a, b, c)
    }
    final case class  SetURL(a: Int, b: URL) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setURL(a, b)
    }
    final case class  SetURL1(a: String, b: URL) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setURL(a, b)
    }
    final case class  SetUnicodeStream(a: Int, b: InputStream, c: Int) extends CallableStatementOp[Unit] {
      def visit[F[_]](v: Visitor[F]) = v.setUnicodeStream(a, b, c)
    }
    final case class  Unwrap[T](a: Class[T]) extends CallableStatementOp[T] {
      def visit[F[_]](v: Visitor[F]) = v.unwrap(a)
    }
    final case object WasNull extends CallableStatementOp[Boolean] {
      def visit[F[_]](v: Visitor[F]) = v.wasNull
    }

  }
  import CallableStatementOp._

  // Smart constructors for operations common to all algebras.
  val unit: CallableStatementIO[Unit] = FF.pure[CallableStatementOp, Unit](())
  def pure[A](a: A): CallableStatementIO[A] = FF.pure[CallableStatementOp, A](a)
  def raw[A](f: CallableStatement => A): CallableStatementIO[A] = FF.liftF(Raw(f))
  def embed[F[_], J, A](j: J, fa: FF[F, A])(implicit ev: Embeddable[F, J]): FF[CallableStatementOp, A] = FF.liftF(Embed(ev.embed(j, fa)))
  def delay[A](a: => A): CallableStatementIO[A] = FF.liftF(Delay(() => a))
  def handleErrorWith[A](fa: CallableStatementIO[A], f: Throwable => CallableStatementIO[A]): CallableStatementIO[A] = FF.liftF[CallableStatementOp, A](HandleErrorWith(fa, f))
  def raiseError[A](err: Throwable): CallableStatementIO[A] = FF.liftF[CallableStatementOp, A](RaiseError(err))
  def async[A](k: (Either[Throwable, A] => Unit) => Unit): CallableStatementIO[A] = FF.liftF[CallableStatementOp, A](Async1(k))
  def asyncF[A](k: (Either[Throwable, A] => Unit) => CallableStatementIO[Unit]): CallableStatementIO[A] = FF.liftF[CallableStatementOp, A](AsyncF(k))
  def bracketCase[A, B](acquire: CallableStatementIO[A])(use: A => CallableStatementIO[B])(release: (A, ExitCase[Throwable]) => CallableStatementIO[Unit]): CallableStatementIO[B] = FF.liftF[CallableStatementOp, B](BracketCase(acquire, use, release))
  val shift: CallableStatementIO[Unit] = FF.liftF[CallableStatementOp, Unit](Shift)
  def evalOn[A](ec: ExecutionContext)(fa: CallableStatementIO[A]) = FF.liftF[CallableStatementOp, A](EvalOn(ec, fa))

  // Smart constructors for CallableStatement-specific operations.
  val addBatch: CallableStatementIO[Unit] = FF.liftF(AddBatch)
  def addBatch(a: String): CallableStatementIO[Unit] = FF.liftF(AddBatch1(a))
  val cancel: CallableStatementIO[Unit] = FF.liftF(Cancel)
  val clearBatch: CallableStatementIO[Unit] = FF.liftF(ClearBatch)
  val clearParameters: CallableStatementIO[Unit] = FF.liftF(ClearParameters)
  val clearWarnings: CallableStatementIO[Unit] = FF.liftF(ClearWarnings)
  val close: CallableStatementIO[Unit] = FF.liftF(Close)
  val closeOnCompletion: CallableStatementIO[Unit] = FF.liftF(CloseOnCompletion)
  val execute: CallableStatementIO[Boolean] = FF.liftF(Execute)
  def execute(a: String): CallableStatementIO[Boolean] = FF.liftF(Execute1(a))
  def execute(a: String, b: Array[Int]): CallableStatementIO[Boolean] = FF.liftF(Execute2(a, b))
  def execute(a: String, b: Array[String]): CallableStatementIO[Boolean] = FF.liftF(Execute3(a, b))
  def execute(a: String, b: Int): CallableStatementIO[Boolean] = FF.liftF(Execute4(a, b))
  val executeBatch: CallableStatementIO[Array[Int]] = FF.liftF(ExecuteBatch)
  val executeLargeBatch: CallableStatementIO[Array[Long]] = FF.liftF(ExecuteLargeBatch)
  val executeLargeUpdate: CallableStatementIO[Long] = FF.liftF(ExecuteLargeUpdate)
  def executeLargeUpdate(a: String): CallableStatementIO[Long] = FF.liftF(ExecuteLargeUpdate1(a))
  def executeLargeUpdate(a: String, b: Array[Int]): CallableStatementIO[Long] = FF.liftF(ExecuteLargeUpdate2(a, b))
  def executeLargeUpdate(a: String, b: Array[String]): CallableStatementIO[Long] = FF.liftF(ExecuteLargeUpdate3(a, b))
  def executeLargeUpdate(a: String, b: Int): CallableStatementIO[Long] = FF.liftF(ExecuteLargeUpdate4(a, b))
  val executeQuery: CallableStatementIO[ResultSet] = FF.liftF(ExecuteQuery)
  def executeQuery(a: String): CallableStatementIO[ResultSet] = FF.liftF(ExecuteQuery1(a))
  val executeUpdate: CallableStatementIO[Int] = FF.liftF(ExecuteUpdate)
  def executeUpdate(a: String): CallableStatementIO[Int] = FF.liftF(ExecuteUpdate1(a))
  def executeUpdate(a: String, b: Array[Int]): CallableStatementIO[Int] = FF.liftF(ExecuteUpdate2(a, b))
  def executeUpdate(a: String, b: Array[String]): CallableStatementIO[Int] = FF.liftF(ExecuteUpdate3(a, b))
  def executeUpdate(a: String, b: Int): CallableStatementIO[Int] = FF.liftF(ExecuteUpdate4(a, b))
  def getArray(a: Int): CallableStatementIO[SqlArray] = FF.liftF(GetArray(a))
  def getArray(a: String): CallableStatementIO[SqlArray] = FF.liftF(GetArray1(a))
  def getBigDecimal(a: Int): CallableStatementIO[BigDecimal] = FF.liftF(GetBigDecimal(a))
  def getBigDecimal(a: Int, b: Int): CallableStatementIO[BigDecimal] = FF.liftF(GetBigDecimal1(a, b))
  def getBigDecimal(a: String): CallableStatementIO[BigDecimal] = FF.liftF(GetBigDecimal2(a))
  def getBlob(a: Int): CallableStatementIO[Blob] = FF.liftF(GetBlob(a))
  def getBlob(a: String): CallableStatementIO[Blob] = FF.liftF(GetBlob1(a))
  def getBoolean(a: Int): CallableStatementIO[Boolean] = FF.liftF(GetBoolean(a))
  def getBoolean(a: String): CallableStatementIO[Boolean] = FF.liftF(GetBoolean1(a))
  def getByte(a: Int): CallableStatementIO[Byte] = FF.liftF(GetByte(a))
  def getByte(a: String): CallableStatementIO[Byte] = FF.liftF(GetByte1(a))
  def getBytes(a: Int): CallableStatementIO[Array[Byte]] = FF.liftF(GetBytes(a))
  def getBytes(a: String): CallableStatementIO[Array[Byte]] = FF.liftF(GetBytes1(a))
  def getCharacterStream(a: Int): CallableStatementIO[Reader] = FF.liftF(GetCharacterStream(a))
  def getCharacterStream(a: String): CallableStatementIO[Reader] = FF.liftF(GetCharacterStream1(a))
  def getClob(a: Int): CallableStatementIO[Clob] = FF.liftF(GetClob(a))
  def getClob(a: String): CallableStatementIO[Clob] = FF.liftF(GetClob1(a))
  val getConnection: CallableStatementIO[Connection] = FF.liftF(GetConnection)
  def getDate(a: Int): CallableStatementIO[Date] = FF.liftF(GetDate(a))
  def getDate(a: Int, b: Calendar): CallableStatementIO[Date] = FF.liftF(GetDate1(a, b))
  def getDate(a: String): CallableStatementIO[Date] = FF.liftF(GetDate2(a))
  def getDate(a: String, b: Calendar): CallableStatementIO[Date] = FF.liftF(GetDate3(a, b))
  def getDouble(a: Int): CallableStatementIO[Double] = FF.liftF(GetDouble(a))
  def getDouble(a: String): CallableStatementIO[Double] = FF.liftF(GetDouble1(a))
  val getFetchDirection: CallableStatementIO[Int] = FF.liftF(GetFetchDirection)
  val getFetchSize: CallableStatementIO[Int] = FF.liftF(GetFetchSize)
  def getFloat(a: Int): CallableStatementIO[Float] = FF.liftF(GetFloat(a))
  def getFloat(a: String): CallableStatementIO[Float] = FF.liftF(GetFloat1(a))
  val getGeneratedKeys: CallableStatementIO[ResultSet] = FF.liftF(GetGeneratedKeys)
  def getInt(a: Int): CallableStatementIO[Int] = FF.liftF(GetInt(a))
  def getInt(a: String): CallableStatementIO[Int] = FF.liftF(GetInt1(a))
  val getLargeMaxRows: CallableStatementIO[Long] = FF.liftF(GetLargeMaxRows)
  val getLargeUpdateCount: CallableStatementIO[Long] = FF.liftF(GetLargeUpdateCount)
  def getLong(a: Int): CallableStatementIO[Long] = FF.liftF(GetLong(a))
  def getLong(a: String): CallableStatementIO[Long] = FF.liftF(GetLong1(a))
  val getMaxFieldSize: CallableStatementIO[Int] = FF.liftF(GetMaxFieldSize)
  val getMaxRows: CallableStatementIO[Int] = FF.liftF(GetMaxRows)
  val getMetaData: CallableStatementIO[ResultSetMetaData] = FF.liftF(GetMetaData)
  val getMoreResults: CallableStatementIO[Boolean] = FF.liftF(GetMoreResults)
  def getMoreResults(a: Int): CallableStatementIO[Boolean] = FF.liftF(GetMoreResults1(a))
  def getNCharacterStream(a: Int): CallableStatementIO[Reader] = FF.liftF(GetNCharacterStream(a))
  def getNCharacterStream(a: String): CallableStatementIO[Reader] = FF.liftF(GetNCharacterStream1(a))
  def getNClob(a: Int): CallableStatementIO[NClob] = FF.liftF(GetNClob(a))
  def getNClob(a: String): CallableStatementIO[NClob] = FF.liftF(GetNClob1(a))
  def getNString(a: Int): CallableStatementIO[String] = FF.liftF(GetNString(a))
  def getNString(a: String): CallableStatementIO[String] = FF.liftF(GetNString1(a))
  def getObject(a: Int): CallableStatementIO[AnyRef] = FF.liftF(GetObject(a))
  def getObject[T](a: Int, b: Class[T]): CallableStatementIO[T] = FF.liftF(GetObject1(a, b))
  def getObject(a: Int, b: Map[String, Class[_]]): CallableStatementIO[AnyRef] = FF.liftF(GetObject2(a, b))
  def getObject(a: String): CallableStatementIO[AnyRef] = FF.liftF(GetObject3(a))
  def getObject[T](a: String, b: Class[T]): CallableStatementIO[T] = FF.liftF(GetObject4(a, b))
  def getObject(a: String, b: Map[String, Class[_]]): CallableStatementIO[AnyRef] = FF.liftF(GetObject5(a, b))
  val getParameterMetaData: CallableStatementIO[ParameterMetaData] = FF.liftF(GetParameterMetaData)
  val getQueryTimeout: CallableStatementIO[Int] = FF.liftF(GetQueryTimeout)
  def getRef(a: Int): CallableStatementIO[Ref] = FF.liftF(GetRef(a))
  def getRef(a: String): CallableStatementIO[Ref] = FF.liftF(GetRef1(a))
  val getResultSet: CallableStatementIO[ResultSet] = FF.liftF(GetResultSet)
  val getResultSetConcurrency: CallableStatementIO[Int] = FF.liftF(GetResultSetConcurrency)
  val getResultSetHoldability: CallableStatementIO[Int] = FF.liftF(GetResultSetHoldability)
  val getResultSetType: CallableStatementIO[Int] = FF.liftF(GetResultSetType)
  def getRowId(a: Int): CallableStatementIO[RowId] = FF.liftF(GetRowId(a))
  def getRowId(a: String): CallableStatementIO[RowId] = FF.liftF(GetRowId1(a))
  def getSQLXML(a: Int): CallableStatementIO[SQLXML] = FF.liftF(GetSQLXML(a))
  def getSQLXML(a: String): CallableStatementIO[SQLXML] = FF.liftF(GetSQLXML1(a))
  def getShort(a: Int): CallableStatementIO[Short] = FF.liftF(GetShort(a))
  def getShort(a: String): CallableStatementIO[Short] = FF.liftF(GetShort1(a))
  def getString(a: Int): CallableStatementIO[String] = FF.liftF(GetString(a))
  def getString(a: String): CallableStatementIO[String] = FF.liftF(GetString1(a))
  def getTime(a: Int): CallableStatementIO[Time] = FF.liftF(GetTime(a))
  def getTime(a: Int, b: Calendar): CallableStatementIO[Time] = FF.liftF(GetTime1(a, b))
  def getTime(a: String): CallableStatementIO[Time] = FF.liftF(GetTime2(a))
  def getTime(a: String, b: Calendar): CallableStatementIO[Time] = FF.liftF(GetTime3(a, b))
  def getTimestamp(a: Int): CallableStatementIO[Timestamp] = FF.liftF(GetTimestamp(a))
  def getTimestamp(a: Int, b: Calendar): CallableStatementIO[Timestamp] = FF.liftF(GetTimestamp1(a, b))
  def getTimestamp(a: String): CallableStatementIO[Timestamp] = FF.liftF(GetTimestamp2(a))
  def getTimestamp(a: String, b: Calendar): CallableStatementIO[Timestamp] = FF.liftF(GetTimestamp3(a, b))
  def getURL(a: Int): CallableStatementIO[URL] = FF.liftF(GetURL(a))
  def getURL(a: String): CallableStatementIO[URL] = FF.liftF(GetURL1(a))
  val getUpdateCount: CallableStatementIO[Int] = FF.liftF(GetUpdateCount)
  val getWarnings: CallableStatementIO[SQLWarning] = FF.liftF(GetWarnings)
  val isCloseOnCompletion: CallableStatementIO[Boolean] = FF.liftF(IsCloseOnCompletion)
  val isClosed: CallableStatementIO[Boolean] = FF.liftF(IsClosed)
  val isPoolable: CallableStatementIO[Boolean] = FF.liftF(IsPoolable)
  def isWrapperFor(a: Class[_]): CallableStatementIO[Boolean] = FF.liftF(IsWrapperFor(a))
  def registerOutParameter(a: Int, b: Int): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter(a, b))
  def registerOutParameter(a: Int, b: Int, c: Int): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter1(a, b, c))
  def registerOutParameter(a: Int, b: Int, c: String): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter2(a, b, c))
  def registerOutParameter(a: Int, b: SQLType): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter3(a, b))
  def registerOutParameter(a: Int, b: SQLType, c: Int): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter4(a, b, c))
  def registerOutParameter(a: Int, b: SQLType, c: String): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter5(a, b, c))
  def registerOutParameter(a: String, b: Int): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter6(a, b))
  def registerOutParameter(a: String, b: Int, c: Int): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter7(a, b, c))
  def registerOutParameter(a: String, b: Int, c: String): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter8(a, b, c))
  def registerOutParameter(a: String, b: SQLType): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter9(a, b))
  def registerOutParameter(a: String, b: SQLType, c: Int): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter10(a, b, c))
  def registerOutParameter(a: String, b: SQLType, c: String): CallableStatementIO[Unit] = FF.liftF(RegisterOutParameter11(a, b, c))
  def setArray(a: Int, b: SqlArray): CallableStatementIO[Unit] = FF.liftF(SetArray(a, b))
  def setAsciiStream(a: Int, b: InputStream): CallableStatementIO[Unit] = FF.liftF(SetAsciiStream(a, b))
  def setAsciiStream(a: Int, b: InputStream, c: Int): CallableStatementIO[Unit] = FF.liftF(SetAsciiStream1(a, b, c))
  def setAsciiStream(a: Int, b: InputStream, c: Long): CallableStatementIO[Unit] = FF.liftF(SetAsciiStream2(a, b, c))
  def setAsciiStream(a: String, b: InputStream): CallableStatementIO[Unit] = FF.liftF(SetAsciiStream3(a, b))
  def setAsciiStream(a: String, b: InputStream, c: Int): CallableStatementIO[Unit] = FF.liftF(SetAsciiStream4(a, b, c))
  def setAsciiStream(a: String, b: InputStream, c: Long): CallableStatementIO[Unit] = FF.liftF(SetAsciiStream5(a, b, c))
  def setBigDecimal(a: Int, b: BigDecimal): CallableStatementIO[Unit] = FF.liftF(SetBigDecimal(a, b))
  def setBigDecimal(a: String, b: BigDecimal): CallableStatementIO[Unit] = FF.liftF(SetBigDecimal1(a, b))
  def setBinaryStream(a: Int, b: InputStream): CallableStatementIO[Unit] = FF.liftF(SetBinaryStream(a, b))
  def setBinaryStream(a: Int, b: InputStream, c: Int): CallableStatementIO[Unit] = FF.liftF(SetBinaryStream1(a, b, c))
  def setBinaryStream(a: Int, b: InputStream, c: Long): CallableStatementIO[Unit] = FF.liftF(SetBinaryStream2(a, b, c))
  def setBinaryStream(a: String, b: InputStream): CallableStatementIO[Unit] = FF.liftF(SetBinaryStream3(a, b))
  def setBinaryStream(a: String, b: InputStream, c: Int): CallableStatementIO[Unit] = FF.liftF(SetBinaryStream4(a, b, c))
  def setBinaryStream(a: String, b: InputStream, c: Long): CallableStatementIO[Unit] = FF.liftF(SetBinaryStream5(a, b, c))
  def setBlob(a: Int, b: Blob): CallableStatementIO[Unit] = FF.liftF(SetBlob(a, b))
  def setBlob(a: Int, b: InputStream): CallableStatementIO[Unit] = FF.liftF(SetBlob1(a, b))
  def setBlob(a: Int, b: InputStream, c: Long): CallableStatementIO[Unit] = FF.liftF(SetBlob2(a, b, c))
  def setBlob(a: String, b: Blob): CallableStatementIO[Unit] = FF.liftF(SetBlob3(a, b))
  def setBlob(a: String, b: InputStream): CallableStatementIO[Unit] = FF.liftF(SetBlob4(a, b))
  def setBlob(a: String, b: InputStream, c: Long): CallableStatementIO[Unit] = FF.liftF(SetBlob5(a, b, c))
  def setBoolean(a: Int, b: Boolean): CallableStatementIO[Unit] = FF.liftF(SetBoolean(a, b))
  def setBoolean(a: String, b: Boolean): CallableStatementIO[Unit] = FF.liftF(SetBoolean1(a, b))
  def setByte(a: Int, b: Byte): CallableStatementIO[Unit] = FF.liftF(SetByte(a, b))
  def setByte(a: String, b: Byte): CallableStatementIO[Unit] = FF.liftF(SetByte1(a, b))
  def setBytes(a: Int, b: Array[Byte]): CallableStatementIO[Unit] = FF.liftF(SetBytes(a, b))
  def setBytes(a: String, b: Array[Byte]): CallableStatementIO[Unit] = FF.liftF(SetBytes1(a, b))
  def setCharacterStream(a: Int, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetCharacterStream(a, b))
  def setCharacterStream(a: Int, b: Reader, c: Int): CallableStatementIO[Unit] = FF.liftF(SetCharacterStream1(a, b, c))
  def setCharacterStream(a: Int, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetCharacterStream2(a, b, c))
  def setCharacterStream(a: String, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetCharacterStream3(a, b))
  def setCharacterStream(a: String, b: Reader, c: Int): CallableStatementIO[Unit] = FF.liftF(SetCharacterStream4(a, b, c))
  def setCharacterStream(a: String, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetCharacterStream5(a, b, c))
  def setClob(a: Int, b: Clob): CallableStatementIO[Unit] = FF.liftF(SetClob(a, b))
  def setClob(a: Int, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetClob1(a, b))
  def setClob(a: Int, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetClob2(a, b, c))
  def setClob(a: String, b: Clob): CallableStatementIO[Unit] = FF.liftF(SetClob3(a, b))
  def setClob(a: String, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetClob4(a, b))
  def setClob(a: String, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetClob5(a, b, c))
  def setCursorName(a: String): CallableStatementIO[Unit] = FF.liftF(SetCursorName(a))
  def setDate(a: Int, b: Date): CallableStatementIO[Unit] = FF.liftF(SetDate(a, b))
  def setDate(a: Int, b: Date, c: Calendar): CallableStatementIO[Unit] = FF.liftF(SetDate1(a, b, c))
  def setDate(a: String, b: Date): CallableStatementIO[Unit] = FF.liftF(SetDate2(a, b))
  def setDate(a: String, b: Date, c: Calendar): CallableStatementIO[Unit] = FF.liftF(SetDate3(a, b, c))
  def setDouble(a: Int, b: Double): CallableStatementIO[Unit] = FF.liftF(SetDouble(a, b))
  def setDouble(a: String, b: Double): CallableStatementIO[Unit] = FF.liftF(SetDouble1(a, b))
  def setEscapeProcessing(a: Boolean): CallableStatementIO[Unit] = FF.liftF(SetEscapeProcessing(a))
  def setFetchDirection(a: Int): CallableStatementIO[Unit] = FF.liftF(SetFetchDirection(a))
  def setFetchSize(a: Int): CallableStatementIO[Unit] = FF.liftF(SetFetchSize(a))
  def setFloat(a: Int, b: Float): CallableStatementIO[Unit] = FF.liftF(SetFloat(a, b))
  def setFloat(a: String, b: Float): CallableStatementIO[Unit] = FF.liftF(SetFloat1(a, b))
  def setInt(a: Int, b: Int): CallableStatementIO[Unit] = FF.liftF(SetInt(a, b))
  def setInt(a: String, b: Int): CallableStatementIO[Unit] = FF.liftF(SetInt1(a, b))
  def setLargeMaxRows(a: Long): CallableStatementIO[Unit] = FF.liftF(SetLargeMaxRows(a))
  def setLong(a: Int, b: Long): CallableStatementIO[Unit] = FF.liftF(SetLong(a, b))
  def setLong(a: String, b: Long): CallableStatementIO[Unit] = FF.liftF(SetLong1(a, b))
  def setMaxFieldSize(a: Int): CallableStatementIO[Unit] = FF.liftF(SetMaxFieldSize(a))
  def setMaxRows(a: Int): CallableStatementIO[Unit] = FF.liftF(SetMaxRows(a))
  def setNCharacterStream(a: Int, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetNCharacterStream(a, b))
  def setNCharacterStream(a: Int, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetNCharacterStream1(a, b, c))
  def setNCharacterStream(a: String, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetNCharacterStream2(a, b))
  def setNCharacterStream(a: String, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetNCharacterStream3(a, b, c))
  def setNClob(a: Int, b: NClob): CallableStatementIO[Unit] = FF.liftF(SetNClob(a, b))
  def setNClob(a: Int, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetNClob1(a, b))
  def setNClob(a: Int, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetNClob2(a, b, c))
  def setNClob(a: String, b: NClob): CallableStatementIO[Unit] = FF.liftF(SetNClob3(a, b))
  def setNClob(a: String, b: Reader): CallableStatementIO[Unit] = FF.liftF(SetNClob4(a, b))
  def setNClob(a: String, b: Reader, c: Long): CallableStatementIO[Unit] = FF.liftF(SetNClob5(a, b, c))
  def setNString(a: Int, b: String): CallableStatementIO[Unit] = FF.liftF(SetNString(a, b))
  def setNString(a: String, b: String): CallableStatementIO[Unit] = FF.liftF(SetNString1(a, b))
  def setNull(a: Int, b: Int): CallableStatementIO[Unit] = FF.liftF(SetNull(a, b))
  def setNull(a: Int, b: Int, c: String): CallableStatementIO[Unit] = FF.liftF(SetNull1(a, b, c))
  def setNull(a: String, b: Int): CallableStatementIO[Unit] = FF.liftF(SetNull2(a, b))
  def setNull(a: String, b: Int, c: String): CallableStatementIO[Unit] = FF.liftF(SetNull3(a, b, c))
  def setObject(a: Int, b: AnyRef): CallableStatementIO[Unit] = FF.liftF(SetObject(a, b))
  def setObject(a: Int, b: AnyRef, c: Int): CallableStatementIO[Unit] = FF.liftF(SetObject1(a, b, c))
  def setObject(a: Int, b: AnyRef, c: Int, d: Int): CallableStatementIO[Unit] = FF.liftF(SetObject2(a, b, c, d))
  def setObject(a: Int, b: AnyRef, c: SQLType): CallableStatementIO[Unit] = FF.liftF(SetObject3(a, b, c))
  def setObject(a: Int, b: AnyRef, c: SQLType, d: Int): CallableStatementIO[Unit] = FF.liftF(SetObject4(a, b, c, d))
  def setObject(a: String, b: AnyRef): CallableStatementIO[Unit] = FF.liftF(SetObject5(a, b))
  def setObject(a: String, b: AnyRef, c: Int): CallableStatementIO[Unit] = FF.liftF(SetObject6(a, b, c))
  def setObject(a: String, b: AnyRef, c: Int, d: Int): CallableStatementIO[Unit] = FF.liftF(SetObject7(a, b, c, d))
  def setObject(a: String, b: AnyRef, c: SQLType): CallableStatementIO[Unit] = FF.liftF(SetObject8(a, b, c))
  def setObject(a: String, b: AnyRef, c: SQLType, d: Int): CallableStatementIO[Unit] = FF.liftF(SetObject9(a, b, c, d))
  def setPoolable(a: Boolean): CallableStatementIO[Unit] = FF.liftF(SetPoolable(a))
  def setQueryTimeout(a: Int): CallableStatementIO[Unit] = FF.liftF(SetQueryTimeout(a))
  def setRef(a: Int, b: Ref): CallableStatementIO[Unit] = FF.liftF(SetRef(a, b))
  def setRowId(a: Int, b: RowId): CallableStatementIO[Unit] = FF.liftF(SetRowId(a, b))
  def setRowId(a: String, b: RowId): CallableStatementIO[Unit] = FF.liftF(SetRowId1(a, b))
  def setSQLXML(a: Int, b: SQLXML): CallableStatementIO[Unit] = FF.liftF(SetSQLXML(a, b))
  def setSQLXML(a: String, b: SQLXML): CallableStatementIO[Unit] = FF.liftF(SetSQLXML1(a, b))
  def setShort(a: Int, b: Short): CallableStatementIO[Unit] = FF.liftF(SetShort(a, b))
  def setShort(a: String, b: Short): CallableStatementIO[Unit] = FF.liftF(SetShort1(a, b))
  def setString(a: Int, b: String): CallableStatementIO[Unit] = FF.liftF(SetString(a, b))
  def setString(a: String, b: String): CallableStatementIO[Unit] = FF.liftF(SetString1(a, b))
  def setTime(a: Int, b: Time): CallableStatementIO[Unit] = FF.liftF(SetTime(a, b))
  def setTime(a: Int, b: Time, c: Calendar): CallableStatementIO[Unit] = FF.liftF(SetTime1(a, b, c))
  def setTime(a: String, b: Time): CallableStatementIO[Unit] = FF.liftF(SetTime2(a, b))
  def setTime(a: String, b: Time, c: Calendar): CallableStatementIO[Unit] = FF.liftF(SetTime3(a, b, c))
  def setTimestamp(a: Int, b: Timestamp): CallableStatementIO[Unit] = FF.liftF(SetTimestamp(a, b))
  def setTimestamp(a: Int, b: Timestamp, c: Calendar): CallableStatementIO[Unit] = FF.liftF(SetTimestamp1(a, b, c))
  def setTimestamp(a: String, b: Timestamp): CallableStatementIO[Unit] = FF.liftF(SetTimestamp2(a, b))
  def setTimestamp(a: String, b: Timestamp, c: Calendar): CallableStatementIO[Unit] = FF.liftF(SetTimestamp3(a, b, c))
  def setURL(a: Int, b: URL): CallableStatementIO[Unit] = FF.liftF(SetURL(a, b))
  def setURL(a: String, b: URL): CallableStatementIO[Unit] = FF.liftF(SetURL1(a, b))
  def setUnicodeStream(a: Int, b: InputStream, c: Int): CallableStatementIO[Unit] = FF.liftF(SetUnicodeStream(a, b, c))
  def unwrap[T](a: Class[T]): CallableStatementIO[T] = FF.liftF(Unwrap(a))
  val wasNull: CallableStatementIO[Boolean] = FF.liftF(WasNull)

  // CallableStatementIO is an Async
  implicit val AsyncCallableStatementIO: Async[CallableStatementIO] =
    new Async[CallableStatementIO] {
      val asyncM = FF.catsFreeMonadForFree[CallableStatementOp]
      def bracketCase[A, B](acquire: CallableStatementIO[A])(use: A => CallableStatementIO[B])(release: (A, ExitCase[Throwable]) => CallableStatementIO[Unit]): CallableStatementIO[B] = module.bracketCase(acquire)(use)(release)
      def pure[A](x: A): CallableStatementIO[A] = asyncM.pure(x)
      def handleErrorWith[A](fa: CallableStatementIO[A])(f: Throwable => CallableStatementIO[A]): CallableStatementIO[A] = module.handleErrorWith(fa, f)
      def raiseError[A](e: Throwable): CallableStatementIO[A] = module.raiseError(e)
      def async[A](k: (Either[Throwable,A] => Unit) => Unit): CallableStatementIO[A] = module.async(k)
      def asyncF[A](k: (Either[Throwable,A] => Unit) => CallableStatementIO[Unit]): CallableStatementIO[A] = module.asyncF(k)
      def flatMap[A, B](fa: CallableStatementIO[A])(f: A => CallableStatementIO[B]): CallableStatementIO[B] = asyncM.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => CallableStatementIO[Either[A, B]]): CallableStatementIO[B] = asyncM.tailRecM(a)(f)
      def suspend[A](thunk: => CallableStatementIO[A]): CallableStatementIO[A] = asyncM.flatten(module.delay(thunk))
    }

  // CallableStatementIO is a ContextShift
  implicit val ContextShiftCallableStatementIO: ContextShift[CallableStatementIO] =
    new ContextShift[CallableStatementIO] {
      def shift: CallableStatementIO[Unit] = module.shift
      def evalOn[A](ec: ExecutionContext)(fa: CallableStatementIO[A]) = module.evalOn(ec)(fa)
    }
}

