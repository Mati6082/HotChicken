package pl.matpakla.shootergame

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.matpakla.shootergame.databinding.ActivityGameBinding
import kotlin.properties.Delegates

class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private var hoverStartTime: Long? = null
    private var targetLifeTime: Float = 5000f
    private var marginTop by Delegates.notNull<Float>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        marginTop = 75 * resources.displayMetrics.density
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //TODO
        // setting:
        //  target icon
        //  difficulty levels
        // gameplay:
        //  points record
        //  game over menu
        //  restart
        // graphics:
        //  coin icon

        binding.main.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                    val x = event.x.coerceIn(
                        binding.crosshair.width / 2f,
                        v.width - binding.crosshair.width / 2f
                    )
                    val y = event.y.coerceIn(
                        binding.crosshair.height / 2f + marginTop,
                        v.height - binding.crosshair.height / 2f
                    )

                    binding.crosshair.x = x - binding.crosshair.width / 2f
                    binding.crosshair.y = y - binding.crosshair.height / 2f
                }
            }
            true
        }
        binding.crosshair.setOnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                    val x = (v.x + event.x - v.width / 2).coerceIn(
                        0f,
                        (binding.main.width - v.width).toFloat()
                    )
                    val y = (v.y + event.y - v.height / 2).coerceIn(
                        marginTop,
                        (binding.main.height - v.height).toFloat()
                    )

                    v.x = x
                    v.y = y
                }
            }
            true
        }

        // MAIN SCOPE
        lifecycleScope.launch {
            var alpha = 1f
            var money = 0
            var lives = 3
            while (isActive && lives > 0) {
                val targetRect = Rect()
                binding.target.getGlobalVisibleRect(targetRect)

                if (isCrosshairOverTarget(targetRect)) {
                    if (hoverStartTime == null) {
                        hoverStartTime = System.currentTimeMillis()
                    }
                    if (System.currentTimeMillis() - hoverStartTime!! >= 100) {
                        money++
                        binding.money.text = money.toString()

                        when (targetLifeTime.toInt()) {
                            in 4000..5000 -> targetLifeTime *= 0.85f
                            in 3000..4000 -> targetLifeTime *= 0.9f
                            in 2000..3000 -> targetLifeTime *= 0.94f
                            in 1000..2000 -> targetLifeTime *= 0.97f
                            in 500..1000 -> targetLifeTime *= 0.99f
                            else -> targetLifeTime = 500f
                        }

                        changeTargetPos()

                        alpha = 1f
                        hoverStartTime = null
                    }
                } else {
                    hoverStartTime = null
                }

                if (alpha < 0f) {
                    alpha = 1f
                    changeTargetPos()
                    lives -= 1
                    changeLives(lives)
                }

                alpha -= 50 / targetLifeTime
                binding.target.alpha = alpha

                delay(50)
            }
            binding.target.visibility = View.INVISIBLE
            Log.d("STATUS", "onCreate: GAME OVER")
        }

    }

    private fun changeLives(lives: Int) {
        when (lives) {
            2 -> {
                binding.life3.setImageResource(R.drawable.heart2)
            }

            1 -> {
                binding.life2.setImageResource(R.drawable.heart2)
            }

            0 -> {
                binding.life1.setImageResource(R.drawable.heart2)
            }
        }
    }

    private fun changeTargetPos() {
        binding.target.x = (0..binding.main.width - binding.target.width).random().toFloat()
        binding.target.y =
            (marginTop.toInt()..binding.main.height - binding.target.height).random().toFloat()
    }

    // CHECK IF CROSSHAIR IS OVER TARGET
    private fun isCrosshairOverTarget(targetRect: Rect): Boolean {
        val crosshairCenterX = binding.crosshair.x + binding.crosshair.width / 2
        val crosshairCenterY = binding.crosshair.y + binding.crosshair.height / 2
        return targetRect.contains(crosshairCenterX.toInt(), crosshairCenterY.toInt())
    }
}