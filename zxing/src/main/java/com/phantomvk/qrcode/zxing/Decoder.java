package com.phantomvk.qrcode.zxing;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public final class Decoder {

    private static final String UTF8 = "utf-8";

    static final Map<DecodeHintType, Object> HINTS_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        List<BarcodeFormat> formats = new ArrayList<>(17);
        formats.add(BarcodeFormat.AZTEC);
        formats.add(BarcodeFormat.CODABAR);
        formats.add(BarcodeFormat.CODE_39);
        formats.add(BarcodeFormat.CODE_93);
        formats.add(BarcodeFormat.CODE_128);
        formats.add(BarcodeFormat.DATA_MATRIX);
        formats.add(BarcodeFormat.EAN_8);
        formats.add(BarcodeFormat.EAN_13);
        formats.add(BarcodeFormat.ITF);
        formats.add(BarcodeFormat.MAXICODE);
        formats.add(BarcodeFormat.PDF_417);
        formats.add(BarcodeFormat.QR_CODE);
        formats.add(BarcodeFormat.RSS_14);
        formats.add(BarcodeFormat.RSS_EXPANDED);
        formats.add(BarcodeFormat.UPC_A);
        formats.add(BarcodeFormat.UPC_E);
        formats.add(BarcodeFormat.UPC_EAN_EXTENSION);

        HINTS_MAP.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        HINTS_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        HINTS_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    static final Map<DecodeHintType, Object> ONE_DIMENSION_HINT_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.CODABAR);
        formats.add(BarcodeFormat.CODE_39);
        formats.add(BarcodeFormat.CODE_93);
        formats.add(BarcodeFormat.CODE_128);
        formats.add(BarcodeFormat.EAN_8);
        formats.add(BarcodeFormat.EAN_13);
        formats.add(BarcodeFormat.ITF);
        formats.add(BarcodeFormat.PDF_417);
        formats.add(BarcodeFormat.RSS_14);
        formats.add(BarcodeFormat.RSS_EXPANDED);
        formats.add(BarcodeFormat.UPC_A);
        formats.add(BarcodeFormat.UPC_E);
        formats.add(BarcodeFormat.UPC_EAN_EXTENSION);

        ONE_DIMENSION_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        ONE_DIMENSION_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        ONE_DIMENSION_HINT_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    static final Map<DecodeHintType, Object> TWO_DIMENSION_HINT_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.AZTEC);
        formats.add(BarcodeFormat.DATA_MATRIX);
        formats.add(BarcodeFormat.MAXICODE);
        formats.add(BarcodeFormat.QR_CODE);

        TWO_DIMENSION_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        TWO_DIMENSION_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        TWO_DIMENSION_HINT_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    static final Map<DecodeHintType, Object> QR_CODE_HINT_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        QR_CODE_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, singletonList(BarcodeFormat.QR_CODE));
        QR_CODE_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        QR_CODE_HINT_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    static final Map<DecodeHintType, Object> CODE_128_HINT_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        CODE_128_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, singletonList(BarcodeFormat.CODE_128));
        CODE_128_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        CODE_128_HINT_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    static final Map<DecodeHintType, Object> EAN_13_HINT_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        EAN_13_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, singletonList(BarcodeFormat.EAN_13));
        EAN_13_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        EAN_13_HINT_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    static final Map<DecodeHintType, Object> HIGH_FREQUENCY_HINT_MAP = new EnumMap<>(DecodeHintType.class);

    static {
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        formats.add(BarcodeFormat.UPC_A);
        formats.add(BarcodeFormat.EAN_13);
        formats.add(BarcodeFormat.CODE_128);

        HIGH_FREQUENCY_HINT_MAP.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        HIGH_FREQUENCY_HINT_MAP.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        HIGH_FREQUENCY_HINT_MAP.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    /**
     * Must not run on UiThread.
     *
     * @param bitmap Bitmap
     * @return decoded string
     */
    public static String decode(final Bitmap bitmap) {
        RGBLuminanceSource source = null;

        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int[] pixels = new int[w * h];

            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
            source = new RGBLuminanceSource(w, h, pixels);

            HybridBinarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Result result = new MultiFormatReader().decode(binaryBitmap, HINTS_MAP);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        try {
            GlobalHistogramBinarizer binarizer = new GlobalHistogramBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Result result = new MultiFormatReader().decode(binaryBitmap, HINTS_MAP);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int sWidth = -1;
    private static int sHeight = -1;
    private static int[] sIntArray = null;

    /**
     * Thread not safe and reuse IntArray.
     *
     * @param width  width int
     * @param height height int
     * @return int[width * height]
     */
    private static int[] getArrayInt(final int width, final int height) {
        if (width <= 0) throw new IllegalArgumentException("width must larger than 0.");
        if (height <= 0) throw new IllegalArgumentException("height must larger than 0.");
        if (width == sWidth && height == sHeight) return sIntArray;
        if (width == sHeight && height == sWidth) return sIntArray;

        sWidth = width;
        sHeight = height;
        sIntArray = new int[width * height];

        return sIntArray;
    }

    /**
     * Release allocated int[].
     */
    private static void releaseArrayInt() {
        sWidth = -1;
        sHeight = -1;
        sIntArray = null;
    }
}
