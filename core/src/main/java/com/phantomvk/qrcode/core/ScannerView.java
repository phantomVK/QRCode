package com.phantomvk.qrcode.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.phantomvk.qrcode.core.util.CoreUtil;

import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;

public class ScannerView extends View {

    private static final int COLOR_MASK = 0x33888888;
    private static final int COLOR_LINE = 0xFFFF0000;
    private static final int COLOR_LINE_ALPHA = 0x33FFFFFF;

    // Reuse objects.
    private final Rect mRect = new Rect();
    private final RectF mRectF = new RectF();
    private final Paint mPaint = new Paint();

    // For onSizeChanged().
    private int left;
    private int top;
    private int right;
    private int bottom;

    // For scanner corner.
    private float mLeftF;
    private float mTopF;
    private float mRightF;
    private float mBottomF;

    // Foreground mask.
    private int mMaskColor;

    // Scanner itself
    private int mScannerWidth;
    private int mScannerHeight;
    private int mScannerMarginTop;

    // Scanner border
    private int mBorderSize;
    private int mBorderColor;

    // Scanner corner
    private int mCornerColor;
    private int mCornerSize;
    private int mCornerLength;
    private int mCornerStyle;
    private float mCornerSizeHalf;

    private Bitmap mScannerLineBitmap;

    private boolean mIsBarCode;


    public ScannerView(Context context) {
        super(context);
        initAttrs(null);
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ScannerView);

        // Foreground mask color.
        mMaskColor = ta.getColor(R.styleable.ScannerView_vk_code_mask_color, COLOR_MASK);

        // Scanner window
        final int dp1 = (int) CoreUtil.dp(getContext(), 1);
        final int dp200 = (int) CoreUtil.dp(getContext(), 200);

        mScannerWidth = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_width, dp200);
        mScannerHeight = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_height, dp200);
        mScannerMarginTop = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_margin_top, dp200 >> 1);

        mBorderSize = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_border_size, dp1);
        mBorderColor = ta.getColor(R.styleable.ScannerView_vk_code_scanner_border_color, Color.GRAY);

        mCornerColor = ta.getColor(R.styleable.ScannerView_vk_code_scanner_corner_color, Color.RED);
        mCornerSize = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_corner_size, dp1 * 3);
        mCornerLength = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_corner_length, mCornerSize * 7);
        mCornerStyle = ta.getInteger(R.styleable.ScannerView_vk_code_scanner_corner_style, 0);
        mCornerSizeHalf = Math.max(0, (mCornerSize * 1.0F) / 2);

        // Scanner line
        int lineColor = ta.getColor(R.styleable.ScannerView_vk_code_scanner_line_color, COLOR_LINE);
        int lineColorAlpha = ta.getColor(R.styleable.ScannerView_vk_code_scanner_line_color_alpha, COLOR_LINE_ALPHA);
        int lineHeight = ta.getDimensionPixelOffset(R.styleable.ScannerView_vk_code_scanner_line_height, (int) (dp1 * 2.5));

        ta.recycle();

        final int[] colors = new int[]{lineColorAlpha, lineColor, lineColor, lineColor};
        setLineStyle(colors, lineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMask(canvas);
        drawBorder(canvas);
        drawCorners(canvas);
        drawLine(canvas);
    }

    /**
     * Draw foreground mask.
     */
    private void drawMask(Canvas canvas) {
        if (mMaskColor == Color.TRANSPARENT) return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mMaskColor);

        canvas.drawRect(0, 0, width, top, mPaint);
        canvas.drawRect(0, top, left, bottom, mPaint);
        canvas.drawRect(right, top, width, bottom, mPaint);
        canvas.drawRect(0, bottom, width, height, mPaint);
    }

    private void drawBorder(Canvas canvas) {
        if (mBorderSize <= 0) return;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderSize);

        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawCorners(Canvas canvas) {
        if (mCornerSizeHalf <= 0) return;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mCornerColor);
        mPaint.setStrokeWidth(mCornerSize);

        if (mCornerStyle == 0) {
            canvas.drawLine(left, mTopF, left, mTopF + mCornerLength, mPaint);
            canvas.drawLine(mLeftF, top, mLeftF + mCornerLength, top, mPaint);

            canvas.drawLine(mRightF, top, mRightF - mCornerLength, top, mPaint);
            canvas.drawLine(right, mTopF, right, mTopF + mCornerLength, mPaint);

            canvas.drawLine(right, mBottomF, right, mBottomF - mCornerLength, mPaint);
            canvas.drawLine(mRightF, bottom, mRightF - mCornerLength, bottom, mPaint);

            canvas.drawLine(mLeftF, bottom, mLeftF + mCornerLength, bottom, mPaint);
            canvas.drawLine(left, mBottomF, left, mBottomF - mCornerLength, mPaint);
        } else {
            canvas.drawLine(mLeftF, top, mLeftF, top + mCornerLength, mPaint);
            canvas.drawLine(left, mTopF, left + mCornerLength, mTopF, mPaint);

            canvas.drawLine(right, mTopF, right - mCornerLength, mTopF, mPaint);
            canvas.drawLine(mRightF, top, mRightF, top + mCornerLength, mPaint);

            canvas.drawLine(mRightF, bottom, mRightF, bottom - mCornerLength, mPaint);
            canvas.drawLine(right, mBottomF, right - mCornerLength, mBottomF, mPaint);

            canvas.drawLine(left, mBottomF, left + mCornerLength, mBottomF, mPaint);
            canvas.drawLine(mLeftF, bottom, mLeftF, bottom - mCornerLength, mPaint);
        }
    }

    private void drawLine(Canvas canvas) {
        if (mIsBarCode) {
        } else {
            mRectF.set(left, top, right, top + mScannerLineBitmap.getHeight());
            canvas.drawBitmap(mScannerLineBitmap, null, mRectF, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        left = (w - mScannerWidth) >> 1;
        top = mScannerMarginTop;
        right = left + mScannerWidth;
        bottom = top + mScannerHeight;
        onCornerStyleChanged();
    }

    /**
     * Calculate new scanner corner size if style has changed.
     */
    private void onCornerStyleChanged() {
        if (mCornerStyle == 0) {
            mLeftF = left - mCornerSizeHalf;
            mTopF = top - mCornerSizeHalf;
            mRightF = right + mCornerSizeHalf;
            mBottomF = bottom + mCornerSizeHalf;
        } else {
            mLeftF = left + mCornerSizeHalf;
            mTopF = top + mCornerSizeHalf;
            mRightF = right - mCornerSizeHalf;
            mBottomF = bottom - mCornerSizeHalf;
        }
    }

    /**
     * Set scanner line style.
     *
     * @param height line height
     */
    public void setLineStyle(@ColorInt int[] colors, int height) {
        Bitmap b = Bitmap.createBitmap(mScannerWidth, height, Bitmap.Config.ARGB_8888);
        GradientDrawable d = new GradientDrawable(LEFT_RIGHT, colors);
        d.setBounds(0, 0, mScannerWidth, height);
        d.draw(new Canvas(b));
        mScannerLineBitmap = b;
    }
}
