package es.soutullo.blitter.view.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min

/** This class is a modified version of the Google Cloud OCR example */
class CameraSourcePreview(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
    private val surfaceView = SurfaceView(context)
    private var cameraSource: CameraSource? = null
    private var graphicOverlay: GraphicOverlay<OcrGraphic>? = null
    private var surfaceAvailable = false
    private var startRequested = false

    init {
        this.surfaceView.holder.addCallback(SurfaceCallback())
        this.addView(this.surfaceView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var previewWidth = this.cameraSource?.previewSize?.width ?: 320
        var previewHeight = this.cameraSource?.previewSize?.height ?: 240

        if (this.isPortraitMode()) {
            previewHeight = previewWidth.also { previewWidth = previewHeight }
        }

        val viewWidth = right - left
        val viewHeight = bottom - top
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        val childWidth = if(widthRatio > heightRatio) viewWidth else (previewWidth.toFloat() * heightRatio).toInt()
        val childHeight = if(widthRatio > heightRatio) (previewHeight.toFloat() * widthRatio).toInt() else viewHeight
        val childXOffset = if(widthRatio > heightRatio) 0 else (childWidth - viewWidth) / 2
        val childYOffset = if(widthRatio > heightRatio) (childHeight - viewHeight) / 2 else 0

        for (i in 0 until childCount) {
            this.getChildAt(i).layout(-1 * childXOffset, -1 * childYOffset, childWidth - childXOffset, childHeight - childYOffset)
        }

        try {
            this.startIfReady()
        } catch (e: Exception) { }
    }

    fun start(cameraSource: CameraSource, graphicOverlay: GraphicOverlay<OcrGraphic>) {
        this.cameraSource = cameraSource
        this.graphicOverlay = graphicOverlay
        this.startRequested = true

        this.startIfReady()
    }

    fun stop() {
        this.cameraSource?.stop()
    }

    fun release() {
        this.cameraSource?.release()
        this.cameraSource = null
    }

    @SuppressLint("MissingPermission")
    private fun startIfReady() {
        if (this.startRequested && this.surfaceAvailable) {
            this.cameraSource?.start(this.surfaceView.holder)
            this.startRequested = false

            this.graphicOverlay?.let { graphicOverlay ->
                this.cameraSource?.let { cameraSource ->
                    val size = cameraSource.previewSize
                    val min = min(size.width, size.height)
                    val max = max(size.width, size.height)

                    if(this.isPortraitMode()) {
                        graphicOverlay.setCameraInfo(min, max, cameraSource.cameraFacing)
                    } else {
                        graphicOverlay.setCameraInfo(max, min, cameraSource.cameraFacing)
                    }

                    graphicOverlay.clear()
                }
            }
        }
    }

    private fun isPortraitMode() = this.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder?) {
            try {
                this@CameraSourcePreview.surfaceAvailable = true
                this@CameraSourcePreview.startIfReady()
            } catch (e: Exception) { }
        }

        override fun surfaceDestroyed(p0: SurfaceHolder?) {
            this@CameraSourcePreview.surfaceAvailable = false
        }

        override fun surfaceChanged(surface: SurfaceHolder?, format: Int, width: Int, height: Int) {}
    }
}