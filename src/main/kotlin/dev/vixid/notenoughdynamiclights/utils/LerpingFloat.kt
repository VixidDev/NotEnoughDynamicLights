package dev.vixid.notenoughdynamiclights.utils

/**
 * Taken from NEU
 *
 * https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/util/LerpingFloat.java
 */
class LerpingFloat(
    var initialValue: Float,
    var timeToReachTarget: Int
) {
    private var timeSpent = 0
    private var lastMillis: Long = 0

    private var lerpValue = initialValue
    private var targetValue = lerpValue

    fun tick() {
        val lastTimeSpent = timeSpent
        timeSpent += (System.currentTimeMillis() - lastMillis).toInt()
        val lastDistPercentToTarget = lastTimeSpent / timeToReachTarget.toFloat()
        val distPercentToTarget = timeSpent / timeToReachTarget.toFloat()
        val fac = (1 - lastDistPercentToTarget) / lastDistPercentToTarget
        val startValue = lerpValue - (targetValue - lerpValue) / fac
        val dist = targetValue - startValue
        if (dist == 0f) return
        val oldLerpValue = lerpValue
        lerpValue = if (distPercentToTarget >= 1) {
            targetValue
        } else {
            startValue + dist * distPercentToTarget
        }
        if (lerpValue == oldLerpValue) {
            timeSpent = lastTimeSpent
        } else {
            lastMillis = System.currentTimeMillis()
        }
    }

    fun resetTimer() {
        timeSpent = 0
        lastMillis = System.currentTimeMillis()
    }

    fun setTarget(targetValue: Float) {
        this.targetValue = targetValue
    }

    fun setValue(value: Float) {
        lerpValue = value
        targetValue = lerpValue
    }

    fun getValue(): Float {
        return lerpValue
    }

    fun getTarget(): Float {
        return targetValue
    }
}