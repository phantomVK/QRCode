package com.phantomvk.qrcode.core.widget;

import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.phantomvk.qrcode.core.R;
import com.phantomvk.qrcode.core.util.CoreUtil;

import static android.graphics.Canvas.ALL_SAVE_FLAG;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;

public class ScannerView extends View {

    private static final int COLOR_MASK = 0x33888888;
    private static final int COLOR_LINE = 0xFFFF0000;
    private static final int COLOR_LINE_ALPHA = 0x33FFFFFF;

    // Reuse objects
    private final RectF mRectF = new RectF();
    private final Paint mPaint = new Paint();

    // For onSizeChanged()
    private int left;
    private int top;
    private int right;
    private int bottom;

    // For scanner corners
    private float mLeftF;
    private float mTopF;
    private float mRightF;
    private float mBottomF;

    // Foreground mask
    private int mMaskColor;

    // Scanner itself
    private int mScannerWidth;
    private int mScannerHeight;
    private int mScannerMarginTop;

    // Scanner border
    private int mBorderSize;
    private int mBorderColor;

    // Scanner corners
    private int mCornerColor;
    private int mCornerSize;
    private int mCornerLength;
    private int mCornerStyle;
    private float mCornerSizeHalf;

    // Scanner line
    private float mLineAnimatedTop;
    private Bitmap mLineBitmap;
    private ValueAnimator mLineAnimator;

    private PorterDuffXfermode mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    public ScannerView(Context context) {
        super(context);
        init();
        initAttrs(null);
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
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
        int lineAlpha = ta.getColor(R.styleable.ScannerView_vk_code_scanner_line_alpha, COLOR_LINE_ALPHA);
        int lineHeight = ta.getDimensionPixelOffset(R.styleable.ScannerView_vk_code_scanner_line_height, (int) (dp1 * 2.5));
        int animatorId = ta.getResourceId(R.styleable.ScannerView_vk_code_scanner_line_animator, 0);
        boolean animatorEnable = ta.getBoolean(R.styleable.ScannerView_vk_code_scanner_line_enable, true);

        ta.recycle();

        final int[] colors = new int[]{lineAlpha, lineColor, lineColor, lineAlpha};
        setLineStyle(colors, lineHeight);
        setLineAnimator(animatorEnable, animatorId);
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
        mLineBitmap = b;
    }

    private void setLineAnimator(boolean animatorEnable, int animatorId) {
        if (!animatorEnable) return;

        if (animatorId != 0) {
            setAnimator((ValueAnimator) AnimatorInflater.loadAnimator(getContext(), animatorId));
        } else {
            setAnimator(getDefaultAnimator());
        }
    }

    /**
     * Get default animator.
     *
     * @return ValueAnimator
     */
    protected ValueAnimator getDefaultAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        return animator;
    }

    /**
     * Set new Animator
     *
     * @param animator ValueAnimator
     */
    public void setAnimator(@Nullable ValueAnimator animator) {
        if (mLineAnimator != null) {
            mLineAnimator.cancel();
            mLineAnimator = null;
        }

        if (animator != null) {
            mLineAnimator = animator;
            mLineAnimator.addUpdateListener(a -> {
                mLineAnimatedTop = a.getAnimatedFraction() * mScannerHeight;
                invalidate();
            });
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        changeAnimatorStatus(visibility == View.VISIBLE);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        changeAnimatorStatus(screenState == View.SCREEN_STATE_ON);
    }

    protected void changeAnimatorStatus(boolean start) {
        if (mLineAnimator == null) return;
        if (start) {
            mLineAnimator.start();
        } else {
            mLineAnimator.cancel();
        }
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

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mMaskColor);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int count = canvas.saveLayer(0, 0, width, height, mPaint, ALL_SAVE_FLAG);
        canvas.drawRect(0, 0, width, height, mPaint);
        mPaint.setXfermode(mClearMode);
        canvas.drawRect(left, top, right, bottom, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(count);

//        canvas.drawRect(0, 0, width, top, mPaint);
//        canvas.drawRect(0, top, left, bottom, mPaint);
//        canvas.drawRect(right, top, width, bottom, mPaint);
//        canvas.drawRect(0, bottom, width, canvas.getHeight(), mPaint);
    }

    private void drawBorder(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderSize);

        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    private void drawCorners(Canvas canvas) {
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

    /**
     * Do not draw scanner line if there is no animator.
     */
    private void drawLine(Canvas canvas) {
        if (mLineAnimator == null) return;

        float minBottom = Math.min(top + mLineAnimatedTop + mLineBitmap.getHeight(), bottom);
        mRectF.set(left, top + mLineAnimatedTop, right, minBottom);
        canvas.drawBitmap(mLineBitmap, null, mRectF, mPaint);
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
}
