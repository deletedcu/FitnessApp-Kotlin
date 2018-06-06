package com.liverowing.android.extensions

import com.kaopiz.kprogresshud.KProgressHUD

fun KProgressHUD.default(): KProgressHUD {
    return this
            .setCancellable(false)
            .setDimAmount(0.5f)
            .setGraceTime(0)
}