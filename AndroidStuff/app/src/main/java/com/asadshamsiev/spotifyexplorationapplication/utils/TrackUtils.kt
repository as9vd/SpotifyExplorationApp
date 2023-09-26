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

        /**
         * Splits a song of given length into sample segments.
         * If the song's length is 45 seconds or less, the entire duration of the song is returned as a single segment.
         * Otherwise, the song is divided into thirds, and a random segment from each third is sampled and returned.
         *
         * @param totalLengthMillis The total length of the song in milliseconds.
         *
         * @return A list of sampled segments as pairs (ArrayList<Pair<String, String>>),
         * where each pair represents the start and end time of the segment in a readable format.
         */
        fun sampleSong(totalLengthMillis: Int): ArrayList<Pair<String, String>> {
             if (totalLengthMillis <= 50000) { // If it's less than 50 seconds, just listen to half of it mate.
                 val returnVal = ArrayList<Pair<String, String>>()
                 returnVal.add(Pair(msToDuration(0), msToDuration(totalLengthMillis / 2)))
                 return returnVal
             }

            val numPeriod = 3

            val targetLength =
                ((totalLengthMillis - 5000) * 0.53).toInt() // A little offset so doesn't go to end.
            val segmentLength = targetLength / numPeriod
            val thirdOfSong = totalLengthMillis / numPeriod

            val periods = mutableListOf<Period>()

            for (i in 0 until numPeriod) {
                val thirdStart = i * thirdOfSong
                val thirdEnd = thirdStart + thirdOfSong

                val start = if (i == 0) 0 else Random.nextInt(thirdStart, thirdEnd - segmentLength)
                val end = start + segmentLength

                // 5 seconds of breathing room at end.
                if (i == numPeriod - 1 && end > totalLengthMillis - 5000) {
                    periods.add(Period(start, totalLengthMillis - 5000))
                } else {
                    periods.add(Period(start, end))
                }
            }

            val returnVal = ArrayList<Pair<String, String>>()
            periods.forEach { period ->
                returnVal.add(Pair(msToDuration(period.start), msToDuration(period.end)))
            }

            return returnVal
        }
    }
}