package com.appbytes.pharma_manager.presentation.inventory.global

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*


abstract class MySwipeHelper(
    context: Context?,
    private val recyclerView: RecyclerView,
    buttonWidth: Int
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    var buttonWidth: Int
    private lateinit var buttonList: MutableList<MyButton>
    private lateinit var gestureDetector: GestureDetector
    private var swipePosition = -1
    private var swipeThreshold = 1f
    private val buttonBuffer: MutableMap<Int, MutableList<MyButton>>
    private lateinit var removeQueue: Queue<Int>
    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            for (button in buttonList) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }
    private val onTouchListener =
        OnTouchListener { view, motionEvent ->
            if (swipePosition < 0) return@OnTouchListener false
            val point = Point(
                motionEvent.rawX.toInt(),
                motionEvent.rawY.toInt()
            )
            val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
            val swipedItem = Objects.requireNonNull(swipeViewHolder)?.itemView
            val rect = Rect()
            swipedItem?.getGlobalVisibleRect(rect)
            if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y) gestureDetector.onTouchEvent(
                    motionEvent
                ) else {
                    removeQueue.add(swipePosition)
                    swipePosition = -1
                    recoverSwipedItem()
                }
            }
            false
        }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!removeQueue.isEmpty()) {
            val pos = removeQueue.poll()
            if (pos > -1) recyclerView.adapter!!.notifyItemChanged(pos)
        }
    }

    inner class MyButton(
        private val context: Context,
        private val text: String,
        private val textSize: Int,
        private val imageResId: Int,
        private val color: Int,
        private val myButtonClickListener: MyButtonClickListener
    ) {
        private var pos = 0
        private var clickRegion: RectF? = null
        private val resources: Resources
        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                myButtonClickListener.onClick(pos)
                return true
            }
            return false
        }

        fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
            val p = Paint()
            p.color = color
            c.drawRect(rectF, p)
            //text
            p.color = Color.WHITE
            p.textSize = textSize.toFloat()
            val r = Rect()
            val cHeight = rectF.height()
            val cWidth = rectF.width()
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)
            var x = 0f
            var y = 0f
            if (imageResId == 0) //If just show Text
            {
                x = cWidth / 2f - r.width() / 2f - r.left
                y = cHeight / 2f - r.height() / 2f - r.bottom
                c.drawText(text, rectF.left + x, rectF.bottom - y, p)
            } else {
                val d = ContextCompat.getDrawable(
                    context,
                    imageResId
                )
                val bitmap = drawableToBitmap(d)
                c.drawBitmap(
                    bitmap,
                    (rectF.left + rectF.right) / 2,
                    (rectF.top + rectF.bottom) / 2,
                    p
                )
            }
            clickRegion = rectF
            this.pos = pos
        }

        init {
            resources = context.resources
        }
    }

    private fun drawableToBitmap(d: Drawable?): Bitmap {
        if (d is BitmapDrawable) return d.bitmap
        val bitmap = Bitmap.createBitmap(
            d!!.intrinsicWidth,
            d.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipePosition != pos) removeQueue.add(swipePosition)
        swipePosition = pos
        if (buttonBuffer.containsKey(swipePosition)) buttonList =
            buttonBuffer[swipePosition]!! else buttonList.clear()
        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttonList.size * buttonWidth
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.5f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 10.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipePosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<MyButton> = java.util.ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size * buttonWidth / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: List<MyButton>,
        pos: Int,
        translationX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * translationX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c, RectF(
                    left, itemView.top.toFloat(), right,
                    itemView.bottom.toFloat()
                ), pos
            )
            right = left
        }
    }

    protected abstract fun instantiateMyButton(
        viewHolder: RecyclerView.ViewHolder?,
        buffer: MutableList<MyButton>?
    )

    init {
        buttonList = java.util.ArrayList()
        gestureDetector = GestureDetector(context, gestureListener)
        recyclerView.setOnTouchListener(onTouchListener)
        buttonBuffer = HashMap()
        this.buttonWidth = buttonWidth
        removeQueue = object : LinkedList<Int>() {
            override fun add(integer: Int): Boolean {
                return if (contains(integer)) false else super.add(integer)
            }
        }
        attachSwipe()
    }
}