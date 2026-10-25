package com.superman.openworld

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.random.Random

class GameView(context: Context) : View(context) {

    private data class Building(var x: Float, var width: Float, var height: Float, var color: Int)
    private data class Cloud(var x: Float, var y: Float, var size: Float, var speed: Float)

    private val buildings = mutableListOf<Building>()
    private val clouds = mutableListOf<Cloud>()

    private val skyPaint = Paint().apply { color = Color.rgb(18, 32, 68) }
    private val buildingPaint = Paint()
    private val windowPaint = Paint().apply { color = Color.rgb(255, 224, 130) }
    private val cloudPaint = Paint().apply {
        color = Color.argb(180, 235, 240, 255)
    }
    private val heroPaint = Paint().apply { color = Color.rgb(20, 60, 160) }
    private val capePaint = Paint().apply { color = Color.rgb(190, 25, 35) }
    private val skinPaint = Paint().apply { color = Color.rgb(240, 200, 170) }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 54f
        isAntiAlias = true
    }

    private var heroX = 300f
    private var heroY = 400f
    private var worldX = 0f
    private var score = 0
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var touching = false

    init {
        repeat(30) {
            buildings.add(
                Building(
                    x = it * 260f,
                    width = Random.nextFloat() * 100f + 140f,
                    height = Random.nextFloat() * 320f + 220f,
                    color = Color.rgb(
                        Random.nextInt(35, 70),
                        Random.nextInt(45, 85),
                        Random.nextInt(75, 125)
                    )
                )
            )
        }

        repeat(18) {
            clouds.add(
                Cloud(
                    x = Random.nextFloat() * 3000f,
                    y = Random.nextFloat() * 500f + 60f,
                    size = Random.nextFloat() * 70f + 50f,
                    speed = Random.nextFloat() * 1.5f + 0.5f
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        canvas.drawRect(0f, 0f, w, h, skyPaint)

        clouds.forEach { cloud ->
            canvas.drawCircle(cloud.x, cloud.y, cloud.size, cloudPaint)
            canvas.drawCircle(cloud.x + cloud.size * 0.8f, cloud.y + 15f, cloud.size * 0.75f, cloudPaint)
            cloud.x -= cloud.speed
            if (cloud.x < -150f) cloud.x = w + Random.nextFloat() * 500f
        }

        buildings.forEach { building ->
            val screenX = building.x - worldX
            if (screenX > -400f && screenX < w + 400f) {
                buildingPaint.color = building.color
                val top = h - building.height
                canvas.drawRect(screenX, top, screenX + building.width, h, buildingPaint)

                var wx = screenX + 18f
                while (wx < screenX + building.width - 24f) {
                    var wy = top + 30f
                    while (wy < h - 50f) {
                        canvas.drawRect(wx, wy, wx + 16f, wy + 22f, windowPaint)
                        wy += 48f
                    }
                    wx += 38f
                }
            }
        }

        drawSuperman(canvas, heroX, heroY)
        canvas.drawText("Score: $score", 40f, 90f, textPaint)
        canvas.drawText("Fly by dragging anywhere", 40f, h - 60f, textPaint)

        worldX += 4f
        score = (worldX / 20f).toInt()

        if (worldX > 7800f) {
            buildings.forEach { it.x -= 7800f }
            worldX -= 7800f
        }

        postInvalidateOnAnimation()
    }

    private fun drawSuperman(canvas: Canvas, x: Float, y: Float) {
        canvas.drawRect(x - 55f, y + 18f, x - 5f, y + 48f, capePaint)
        canvas.drawOval(RectF(x - 28f, y - 35f, x + 28f, y + 35f), heroPaint)
        canvas.drawCircle(x, y - 48f, 20f, skinPaint)
        canvas.drawRect(x - 18f, y + 30f, x - 4f, y + 65f, heroPaint)
        canvas.drawRect(x + 4f, y + 30f, x + 18f, y + 65f, heroPaint)
        canvas.drawRect(x - 45f, y - 12f, x - 20f, y + 8f, heroPaint)
        canvas.drawRect(x + 20f, y - 12f, x + 45f, y + 8f, heroPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touching = true
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY

                heroX = (heroX + dx).coerceIn(80f, width - 80f)
                heroY = (heroY + dy).coerceIn(100f, height - 150f)

                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> touching = false
        }
        return true
    }
}
