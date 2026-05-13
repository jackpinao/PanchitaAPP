class Common {
    fun String.sinAcento(): String {
        val original = "ÁÉÍÓÚÜáéíóúü"
        val replacement = "AEIOUUaeiouu"
        val output = StringBuilder()
        for (c in this) {
            val index = original.indexOf(c)
            if (index >= 0) {
                output.append(replacement[index])
            } else {
                output.append(c)
            }
        }
        return output.toString()
    }
}

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
    totalTime = kotlin.system.measureTimeMillis {
        for (i in 1..iterations) {
            testString.sinAcento()
        }
    }
}
println("Baseline Benchmark time for ${iterations} iterations: ${totalTime} ms")
