package com.asadshamsiev.spotifyexplorationapplication.utils

import kotlin.random.Random

class TrackUtils {
    companion object {

        data class Period(val start: Int, val end: Int)

        fun msToDuration(ms: Int): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

        fun durationToMs(duration: String): Long {
            val split = duration.split(":")
            val minutes = split[0].toLong()
            val seconds = split[1].toLong()
            return (minutes * 60 + seconds) * 1000
        }

        fun sampleSong(totalLengthMillis: Int): ArrayList<Pair<String, String>> {
            if (totalLengthMillis <= 45000) { // If it's less than 45 seconds, why interval?
                val returnVal = ArrayList<Pair<String, String>>()
                returnVal.add(Pair(msToDuration(0), msToDuration(totalLengthMillis)))

                return returnVal
            }

            val numPeriod = 3

            val targetLength =
                ((totalLengthMillis - 2000) * 0.51).toInt() // A little offset so doesn't go to end.
            val segmentLength = targetLength / numPeriod
            val thirdOfSong = totalLengthMillis / numPeriod

            val periods = mutableListOf<Period>()

            for (i in 0 until numPeriod) {
                val thirdStart = i * thirdOfSong
                val thirdEnd = thirdStart + thirdOfSong

                val start = Random.nextInt(thirdStart, thirdEnd - segmentLength)
                val end = start + segmentLength

                periods.add(Period(start, end))
            }

            val returnVal = ArrayList<Pair<String, String>>()
            periods.forEach { period ->
                returnVal.add(Pair(msToDuration(period.start), msToDuration(period.end)))
            }

            return returnVal
        }
    }
}