package com.pinao.panchitaapp.utils

import org.junit.Test
import kotlin.system.measureTimeMillis

class CommonBenchmarkTest {
    @Test
    fun benchmarkSinAcento() {
        val common = Common()
        val testString = "ÁÉÍÓÚÜáéíóúü abcdefghijklmnopqrstuvwxyz ÁÉÍÓÚÜáéíóúü".repeat(100)

        // Warmup
        with(common) {
            for (i in 1..1000) {
                testString.sinAcento()
            }
        }

        // Measure
        var totalTime = 0L
        val iterations = 10000
        with(common) {
            totalTime = measureTimeMillis {
                for (i in 1..iterations) {
                    testString.sinAcento()
                }
            }
        }

        println("Benchmark time for ${iterations} iterations: ${totalTime} ms")
    }
}
