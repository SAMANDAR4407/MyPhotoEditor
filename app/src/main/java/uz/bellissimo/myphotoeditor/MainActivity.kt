package uz.bellissimo.myphotoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import uz.bellissimo.myphotoeditor.databinding.ActivityMainBinding
import uz.bellissimo.myphotoeditor.databinding.ContainerViewBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var addViewData: AddViewData? = null
    private var lastSelectView: ContainerViewBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addGlasses.setOnClickListener {
            addViewData = AddViewData.EmojiData(R.drawable.glasses, 60.px, 30.px)
        }
        binding.addText.setOnClickListener {
            addViewData = AddViewData.TextData("Hello world", 16.px)
        }

        binding.editor.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                when (addViewData) {
                    is AddViewData.EmojiData -> {
                        addView(event.x, event.y)
                    }

                    is AddViewData.TextData -> {
                        addView(event.x, event.y)
                    }

                    null -> {
                        unSelect()
                    }
                }
                addViewData = null
            }
            return@setOnTouchListener true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addView(targetX: Float, targetY: Float) {
        val _view: View = when (addViewData!!) {
            is AddViewData.EmojiData -> {
                ImageView(this).apply {
                    setImageResource((addViewData as AddViewData.EmojiData).imageResID)
                }
            }

            is AddViewData.TextData -> {
                TextView(this).apply {
                    text = (addViewData as AddViewData.TextData).st
                    textSize = (addViewData as AddViewData.TextData).defTextSize.toFloat()
                }
            }
        }

        val containerBinding = ContainerViewBinding.inflate(layoutInflater, binding.editor, false)

        containerBinding.root.x = targetX - 50.px
        containerBinding.root.y = targetY - 30.px

        containerBinding.viewContainer.addView(_view)
        binding.editor.addView(containerBinding.root, 100.px, 60.px)
        selectView(containerBinding)

        containerBinding.buttonCancel.setOnClickListener {
            binding.editor.removeView(containerBinding.root)
        }

        var lastPoint = PointF()
        var firstPoint = PointF()
        var secondPoint : PointF?= null
        var oldLength = 0.0

        containerBinding.root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    selectView(containerBinding)
                    lastPoint = PointF(event.x, event.y)
                    firstPoint = PointF(event.x, event.y)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        val distanceX = event.x - lastPoint.x
                        val distanceY = event.y - lastPoint.y

                        containerBinding.root.x += distanceX
                        containerBinding.root.y += distanceY
                    } else {
                        val index0 = event.findPointerIndex(0)
                        val index1 = event.findPointerIndex(1)

                        val x0 = event.getX(index0)
                        val y0 = event.getY(index0)

                        val x1 = event.getX(index1)
                        val y1 = event.getY(index1)

                        if (secondPoint == null) {
                            secondPoint = PointF(x1, y1)
                            oldLength = lineLength(firstPoint, secondPoint!!)
                        }

                        val newLength = lineLength(PointF(x0, y0), PointF(x1, y1))
                        val k = newLength / oldLength

                        if (containerBinding.root.scaleX * k > 0.1) {
                            containerBinding.root.scaleX *= k.toFloat()
                            containerBinding.root.scaleY *= k.toFloat()
                        }

//                        containerBinding.root.rotation += alpha
                    }
                }
            }
            return@setOnTouchListener true

//            binding.editor.draw()
        }
    }

    private fun selectView(view: ContainerViewBinding) {
        if (lastSelectView != view) unSelect()
        lastSelectView = view
        lastSelectView!!.apply {
            this.viewContainer.isSelected = true
            this.buttonCancel.visibility = View.VISIBLE
        }
    }

    private fun unSelect() {
        lastSelectView?.let {
            it.viewContainer.isSelected = false
            it.buttonCancel.visibility = View.GONE
        }
    }

    private fun pickImageFromGalery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,1000)
    }
}


