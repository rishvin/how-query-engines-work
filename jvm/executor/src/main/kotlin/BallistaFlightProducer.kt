// Copyright 2020 Andy Grove
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.andygrove.kquery.executor

import java.lang.IllegalStateException
import org.apache.arrow.flight.*
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.*
import io.andygrove.kquery.execution.ExecutionContext
import io.andygrove.kquery.logical.format

class BallistaFlightProducer : FlightProducer {

  override fun getStream(
      context: FlightProducer.CallContext?,
      ticket: Ticket?,
      listener: FlightProducer.ServerStreamListener?
  ) {

    if (listener == null) {
      throw IllegalArgumentException()
    }

    try {

      if (ticket != null) {
        println(" Received ticket: ${String(ticket.bytes)}")
      }

//      val logicalPlanNode =
//          io.andygrove.kquery.protobuf.LogicalPlanNode
//              .parseFrom(ticket?.bytes ?: throw IllegalArgumentException(ticket.toString()))
//      val logicalPlan = io.andygrove.kquery.protobuf.ProtobufDeserializer().fromProto(logicalPlanNode)
//      println(logicalPlan.pretty())


      // TODO get from protobuf request
      val settings = mapOf<String, String>()

      val ctx = ExecutionContext(settings)

      val df = ticket?.let { String(it.bytes) }?.let { ctx.sql(it) }

      val results = requireNotNull(df?.let { ctx.execute(it.logicalPlan()) })

      val schema = requireNotNull(df?.logicalPlan()?.schema())
      println(schema)

      val allocator = RootAllocator(Long.MAX_VALUE)

      val root = VectorSchemaRoot.create(schema.toArrow(), allocator)
      listener.start(root, null)

      // val loader = VectorLoader(root)
      var counter = 0
      results.iterator().forEach { batch ->
        root.clear()

        val rowCount = batch.rowCount()
        println("Received batch with $rowCount rows")

        var batchSize = rowCount
        root.fieldVectors.forEach { it.setInitialCapacity(batchSize) }
        root.allocateNew()

        (0 until schema.fields.size).forEach { columnIndex ->
          val sourceVector = batch.fields[columnIndex]
          val v = root.fieldVectors[columnIndex]

          // TODO this is brute force copying that can be optimized if the underlying data is
          // already in Arrow format
          when (v) {
            is TinyIntVector -> {
              (0 until rowCount).forEach { rowIndex ->
                val value = sourceVector.getValue(rowIndex)
                if (value == null) {
                  v.setNull(rowIndex)
                } else {
                  v.set(rowIndex, value as Byte)
                }
              }
            }
            is SmallIntVector -> {
              (0 until rowCount).forEach { rowIndex ->
                val value = sourceVector.getValue(rowIndex)
                if (value == null) {
                  v.setNull(rowIndex)
                } else {
                  v.set(rowIndex, value as Short)
                }
              }
            }
            is IntVector -> {
              (0 until rowCount).forEach { rowIndex ->
                val value = sourceVector.getValue(rowIndex)
                if (value == null) {
                  v.setNull(rowIndex)
                } else {
                  v.set(rowIndex, value as Int)
                }
              }
            }
            is BigIntVector -> {
              (0 until rowCount).forEach { rowIndex ->
                val value = sourceVector.getValue(rowIndex)
                if (value == null) {
                  v.setNull(rowIndex)
                } else {
                  v.set(rowIndex, value as Long)
                }
              }
            }
            is Float4Vector -> {
              (0 until rowCount).forEach { rowIndex ->
                val value = sourceVector.getValue(rowIndex)
                if (value == null) {
                  v.setNull(rowIndex)
                } else {
                  v.set(rowIndex, value as Float)
                }
              }
            }
            is Float8Vector -> {
              (0 until rowCount).forEach { rowIndex ->
                val value = sourceVector.getValue(rowIndex)
                if (value == null) {
                  v.setNull(rowIndex)
                } else {
                  v.set(rowIndex, value as Double)
                }
              }
            }
            is VarCharVector -> {
              (0 until rowCount).forEach { ri ->
                val value = sourceVector.getValue(ri)
                if (value == null) {
                  v.setNull(ri)
                } else {
                  val byteArray = (value as String).toByteArray()
                  v.set(ri, byteArray)
                }
              }
            }
            else -> throw IllegalStateException(v.javaClass.name)

          }
          v.valueCount = rowCount
         // println("The value of v is: $v")

        }

        root.rowCount = rowCount
        listener.putNext()

        counter++
      }

      root.close()
      listener.completed()
    } catch (ex: Exception) {
      ex.printStackTrace()
      listener.error(ex)
    }
  }

  override fun listFlights(
      context: FlightProducer.CallContext?,
      criteria: Criteria?,
      listener: FlightProducer.StreamListener<FlightInfo>?
  ) {
    TODO("not implemented") // To change body of created functions use File | Settings | File
    // Templates.
  }

  override fun getFlightInfo(
      context: FlightProducer.CallContext?, descriptor: FlightDescriptor?
  ): FlightInfo {
    TODO("not implemented") // To change body of created functions use File | Settings | File
    // Templates.
  }

  override fun listActions(
      context: FlightProducer.CallContext?, listener: FlightProducer.StreamListener<ActionType>?
  ) {
    TODO("not implemented") // To change body of created functions use File | Settings | File
    // Templates.
  }

  override fun acceptPut(
      context: FlightProducer.CallContext?,
      flightStream: FlightStream?,
      ackStream: FlightProducer.StreamListener<PutResult>?
  ): Runnable {
    TODO("not implemented") // To change body of created functions use File | Settings | File
    // Templates.
  }

  override fun doAction(
      context: FlightProducer.CallContext?,
      action: Action?,
      listener: FlightProducer.StreamListener<Result>?
  ) {
    TODO("not implemented") // To change body of created functions use File | Settings | File
    // Templates.
  }
}
