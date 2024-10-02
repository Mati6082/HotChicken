package pl.matpakla.shootergame

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.matpakla.shootergame.databinding.ActivityGameBinding

class Game : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.crosshair.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_MOVE) {
                binding.crosshair.x += e.x - binding.crosshair.width / 2f
                binding.crosshair.y += e.y - binding.crosshair.height / 2f
            }
            true
        }

        val imageView = binding.coin
        val matrix = ColorMatrix()
        val valueAnimator = ValueAnimator.ofFloat(0f, 2f) // Animate red value from 0 to 2
        valueAnimator.duration = 1000 // 1 second duration

        valueAnimator.addUpdateListener { animator ->
            val redValue = animator.animatedValue as Float
            matrix.set(
                floatArrayOf(
                    redValue, 0f, 0f, 0f, 0f, // Red
                    0f, 1f, 0f, 0f, 0f, // Green
                    0f, 0f, 1f, 0f, 0f, // Blue
                    0f, 0f, 0f, 1f, 0f  // Alpha
                )
            )
            val filter = ColorMatrixColorFilter(matrix)
            imageView.colorFilter = filter
        }

        valueAnimator.start()
    }
}