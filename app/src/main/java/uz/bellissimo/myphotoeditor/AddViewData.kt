package uz.bellissimo.myphotoeditor

import android.net.Uri

sealed interface AddViewData {
    data class EmojiData(
        val imageResID : Int,
        val defHeight: Int,
        val defWidth: Int
    ) : AddViewData

    data class TextData(
        val st : String,
        val defTextSize: Int
    ): AddViewData

//    data class ImageData(
//        val uri: Uri?
//    ): AddViewData
}

