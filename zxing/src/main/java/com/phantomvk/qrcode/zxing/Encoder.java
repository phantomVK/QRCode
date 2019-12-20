package com.phantomvk.qrcode.zxing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

import static android.graphics.Bitmap.Config.RGB_565;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.DITHER_FLAG;
import static android.graphics.Paint.FILTER_BITMAP_FLAG;
import static com.google.zxing.BarcodeFormat.QR_CODE;

/**
 * Encode string contents to QrCode Bitmap.
 */
public final class Encoder {

    public static Bitmap encodeQrCode(@NonNull String contents,
                                      @IntRange(from = 0, to = Integer.MAX_VALUE) int size,
                                      @NonNull Map<EncodeHintType, ?> hints) throws WriterException {

        return encodeQrCode(contents, QR_CODE, size, size, BLACK, WHITE, RGB_565, hints);
    }

    public static Bitmap encodeQrCode(@NonNull String contents,
                                      @IntRange(from = 0, to = Integer.MAX_VALUE) int size,
                                      @ColorInt int foregroundColor,
                                      @ColorInt int backgroundColor,
                                      @NonNull Bitmap.Config config,
                                      @NonNull Map<EncodeHintType, ?> hints) throws WriterException {

        return encodeQrCode(contents, QR_CODE, size, size, foregroundColor, backgroundColor, config, hints);
    }

    public static Bitmap encodeQrCode(@NonNull String contents,
                                      @NonNull BarcodeFormat format,
                                      @IntRange(from = 0, to = Integer.MAX_VALUE) int width,
                                      @IntRange(from = 0, to = Integer.MAX_VALUE) int height,
                                      @ColorInt int foregroundColor,
                                      @ColorInt int backgroundColor,
                                      @NonNull Bitmap.Config config,
                                      @NonNull Map<EncodeHintType, ?> hints) throws WriterException {

        BitMatrix m = new MultiFormatWriter().encode(contents, format, width, height, hints);
        final int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = m.get(x, y) ? foregroundColor : backgroundColor;
            }
        }

        Bitmap b = Bitmap.createBitmap(width, height, config);
        b.setPixels(pixels, 0, width, 0, 0, width, height);
        return b;
    }

    /**
     * Draw the source bitmap into the destination bitmap.
     *
     * @param dst Specifies a mutable bitmap for the canvas to draw into
     * @param src Specifies a bitmap for the canvas to draw from
     */
    public static Bitmap drawBitmap(@NonNull Bitmap dst, @NonNull Bitmap src,
                                    @NonNull Paint paint) {

        return drawBitmap(dst, src, 1, 1, paint);
    }

    /**
     * Draw the source bitmap into the destination bitmap.
     *
     * @param dst   Specifies a mutable bitmap for the canvas to draw into
     * @param src   Specifies a bitmap for the canvas to draw from
     * @param sx    The amount to scale in X
     * @param sy    The amount to scale in Y
     * @param paint The paint used to draw the bitmap (may be null)
     */
    public static Bitmap drawBitmap(@NonNull Bitmap dst, @NonNull Bitmap src,
                                    float sx, float sy,
                                    @NonNull Paint paint) {

        final int dstW = dst.getWidth();
        final int dstH = dst.getHeight();
        final int srcW = src.getWidth();
        final int srcH = src.getHeight();

        final int left = (dstW - srcW) >> 1;
        final int top = (dstH - srcH) >> 1;

        Canvas c = new Canvas(dst);
        c.scale(sx, sy, dstW >> 1, dstH >> 1);
        c.drawBitmap(src, left, top, paint);

        return dst;
    }

    public static Map<EncodeHintType, Object> getHints() {
        Map<EncodeHintType, Object> h = new EnumMap<>(EncodeHintType.class);
        h.put(EncodeHintType.CHARACTER_SET, "utf-8");
        h.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        h.put(EncodeHintType.MARGIN, 0);
        return h;
    }

    public static Paint getPaint() {
        return new Paint(ANTI_ALIAS_FLAG | DITHER_FLAG | FILTER_BITMAP_FLAG);
    }
}
