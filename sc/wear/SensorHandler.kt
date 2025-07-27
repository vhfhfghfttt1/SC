
package com.snooker.coach

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorHandler(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val rotVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var currentAccel = FloatArray(3)
    private var currentGyro = FloatArray(3)
    private var currentOrientation = FloatArray(3)

    var onMotionData: ((acc: FloatArray, gyro: FloatArray, orientation: FloatArray) -> Unit)? = null

    fun start() {
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, rotVectorSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> currentAccel = event.values.clone()
            Sensor.TYPE_GYROSCOPE -> currentGyro = event.values.clone()
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotMatrix, event.values)
                SensorManager.getOrientation(rotMatrix, currentOrientation)
            }
        }

        onMotionData?.invoke(currentAccel, currentGyro, currentOrientation)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
