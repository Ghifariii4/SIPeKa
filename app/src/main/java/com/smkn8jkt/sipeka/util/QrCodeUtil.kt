package com.smkn8jkt.sipeka.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

object QrCodeUtil {
    /**
     * Menghasilkan bitmap QR Code beresolusi tinggi untuk voucher pesanan pre-order siswa.
     */
    fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Membaca dan mendeteksi kode QR dari Bitmap kamera atau tangkapan layar.
     * Menggunakan pendekatan multi-binarizer bertingkat (HybridBinarizer -> GlobalHistogramBinarizer)
     * agar tangguh terhadap kondisi layar ponsel redup, silau, maupun foto yang agak miring.
     */
    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)

            val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
                put(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
                put(DecodeHintType.TRY_HARDER, java.lang.Boolean.TRUE)
                put(DecodeHintType.CHARACTER_SET, "UTF-8")
            }

            val reader = MultiFormatReader()
            reader.setHints(hints)

            // Percobaan 1: HybridBinarizer (optimal untuk kontras tajam)
            try {
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                val result = reader.decodeWithState(binaryBitmap)
                if (!result.text.isNullOrBlank()) {
                    return result.text
                }
            } catch (_: Exception) {
                reader.reset()
            }

            // Percobaan 2: GlobalHistogramBinarizer (optimal untuk pencahayaan redup / pantulan cahaya)
            try {
                val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
                val result = reader.decodeWithState(binaryBitmap)
                if (!result.text.isNullOrBlank()) {
                    return result.text
                }
            } catch (_: Exception) {
                reader.reset()
            }

            // Percobaan 3: Inversi Luminance untuk QR mode gelap (white-on-black)
            try {
                val invertedSource = source.invert()
                val binaryBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                val result = reader.decodeWithState(binaryBitmap)
                result.text
            } catch (_: Exception) {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Mendekode gambar QR dari Uri galeri perangkat.
     */
    fun decodeQrFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap?.let { decodeQrFromBitmap(it) }
        } catch (e: Exception) {
            null
        }
    }
}
