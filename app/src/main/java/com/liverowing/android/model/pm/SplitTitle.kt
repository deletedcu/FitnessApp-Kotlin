package com.liverowing.android.model.pm

enum class SplitType {
    Number, Time, StrokeRate, DPS, Dist, RestTime, DragFactor, Watts, TimeDist, HeartRate, Pace, RestDist, Cals, StrokeCount, DriveLength
}

data class SplitTitle(val type: SplitType,
                      val name: String,
                      val shortName: String) {
    companion object {
        fun fullData(): List<SplitTitle> {
            mutableMapOf<SplitType, Number>()
            var data = mutableListOf<SplitTitle>()
            data.add(SplitTitle(SplitType.Time, "TIME", "Time"))
            data.add(SplitTitle(SplitType.StrokeRate, "RATE", "Rate"))
            data.add(SplitTitle(SplitType.DPS, "DIST./STROKE", "DPS"))
            data.add(SplitTitle(SplitType.Dist, "DISTANCE", "Dist"))
            data.add(SplitTitle(SplitType.RestTime, "REST TIME", "RestTime"))
            data.add(SplitTitle(SplitType.DragFactor, "DRAG", "DragFactor"))
            data.add(SplitTitle(SplitType.Watts, "POWER", "Power"))
            data.add(SplitTitle(SplitType.TimeDist, "TIME DISTANCE", "TimeDist"))
            data.add(SplitTitle(SplitType.HeartRate, "HEART RATE", "HeartRate"))
            data.add(SplitTitle(SplitType.Pace, "PACE", "Pace"))
            data.add(SplitTitle(SplitType.RestDist, "REST DISTANCE", "RestDist"))
            data.add(SplitTitle(SplitType.Cals, "CALORIES", "Cals"))
            data.add(SplitTitle(SplitType.StrokeCount, "STROKES", "Strokes"))
            data.add(SplitTitle(SplitType.DriveLength, "DRIVE LENGTH", "DriveLength"))

            return data
        }

        fun defaultData(): MutableList<SplitTitle> {
            var data = mutableListOf<SplitTitle>()
            data.add(SplitTitle(SplitType.Dist, "DISTANCE", "Dist"))
            data.add(SplitTitle(SplitType.Time, "TIME", "Time"))
            data.add(SplitTitle(SplitType.Pace, "PACE", "Pace"))
            data.add(SplitTitle(SplitType.StrokeRate, "RATE", "Rate"))

            return data
        }
    }

}

