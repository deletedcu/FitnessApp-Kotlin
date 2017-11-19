package com.liverowing.liverowing

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions


/**
 * Created by henrikmalmberg on 2017-11-09.
 */
@GlideModule
class LiveRowingGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.VERBOSE)
    }
}