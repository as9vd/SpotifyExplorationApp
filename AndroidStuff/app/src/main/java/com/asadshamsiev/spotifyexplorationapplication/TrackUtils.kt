package com.asadshamsiev.spotifyexplorationapplication

import kotlin.random.Random

class TrackUtils {
    data class Period(val start: Int, val end: Int)

    fun msToDuration(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    fun sampleSong(totalLengthMillis: Int): ArrayList<Pair<String, String>> {
        val targetLength = (totalLengthMillis * 0.51).toInt()
        val numPeriods = Random.nextInt(4, 8)

        val periods = mutableListOf<Period>()

        var currentLength = 0
        var lastEnd = 0

        for (i in 0 until numPeriods - 1) {
            val remainingPeriods = numPeriods - periods.size
            val remainingLength = targetLength - currentLength
            val averageRemainingLength = remainingLength / remainingPeriods

            val maxStart = totalLengthMillis - averageRemainingLength - (remainingPeriods - 1) * averageRemainingLength
            val start = Random.nextInt(lastEnd, maxStart.coerceAtLeast(lastEnd))
            val end = start + averageRemainingLength

            periods.add(Period(start, end))
            currentLength += averageRemainingLength
            lastEnd = end
        }

        // Adjust last period to match target length.
        val lastPeriodStart = Random.nextInt(lastEnd, totalLengthMillis - (targetLength - currentLength))
        periods.add(Period(lastPeriodStart, lastPeriodStart + (targetLength - currentLength)))

        val returnVal = ArrayList<Pair<String,String>>()
        periods.forEach { period ->
            returnVal.add(Pair(msToDuration(period.start), msToDuration(period.end)))
        }

        return returnVal
    }
}