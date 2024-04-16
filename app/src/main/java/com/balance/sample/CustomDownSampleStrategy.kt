package com.balance.sample

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy

class CustomDownSampleStrategy(private val maxDimension: Int = com.blankj.utilcode.util.ScreenUtils.getScreenWidth()/2) :
    DownsampleStrategy() {
    override fun getScaleFactor(
        sourceWidth: Int,
        sourceHeight: Int,
        requestedWidth: Int,
        requestedHeight: Int
    ): Float {
        return if (sourceWidth > maxDimension || sourceHeight > maxDimension) {
            Math.min(maxDimension.toFloat() / sourceWidth, maxDimension.toFloat() / sourceHeight)
        } else {
            1f
        }
    }

    override fun getSampleSizeRounding(
        sourceWidth: Int,
        sourceHeight: Int,
        requestedWidth: Int,
        requestedHeight: Int
    ): SampleSizeRounding {
        return SampleSizeRounding.QUALITY
    }
}