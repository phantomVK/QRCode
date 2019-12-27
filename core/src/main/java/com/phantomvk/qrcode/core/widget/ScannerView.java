package com.phantomvk.qrcode.core.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.phantomvk.qrcode.core.R;
import com.phantomvk.qrcode.core.util.CoreUtil;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.DITHER_FLAG;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;

public class ScannerView extends View {

    private static final int COLOR_MASK = 0x33888888;
    private static final int COLOR_LINE = 0xFFFF0000;
    private static final int COLOR_LINE_ALPHA = 0x33FFFFFF;

    private int left;
    private int top;
    private int right;
    private int bottom;

    // Scanner
    private int mScannerWidth;
    private int mScannerHeight;

    // Mask
    private final Path mMaskPath = new Path();
    private final Paint mMaskPaint = new Paint(ANTI_ALIAS_FLAG | DITHER_FLAG);

    // Border
    private final Paint mBorderPaint = new Paint(ANTI_ALIAS_FLAG | DITHER_FLAG);

    // Corner
    private int mCornerLength;
    private int mCornerStyle;
    private int mCornerSizeHalf;
    private final Path mCornerPath = new Path();
    private final Paint mCornerPaint = new Paint(ANTI_ALIAS_FLAG | DITHER_FLAG);

    // Line
    private Bitmap mLineBitmap;
    private ValueAnimator mLineAnimator;
    private final RectF mLineRectF = new RectF();
    private final Paint mLinePaint = new Paint(ANTI_ALIAS_FLAG | DITHER_FLAG);

    {
        mMaskPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStyle(Paint.Style.STROKE);
    }

    public ScannerView(Context context) {
        super(context);
        initAttrs(null);
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    private void initAttrs(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ScannerView);

        // Foreground mask color.
        int maskColor = ta.getColor(R.styleable.ScannerView_vk_code_mask_color, COLOR_MASK);

        // Scanner window
        final int dp1 = (int) CoreUtil.dp(getContext(), 1);
        final int dp200 = dp1 * 200;

        mScannerWidth = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_width, dp200);
        mScannerHeight = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_height, dp200);
        top = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_margin_top, dp200 >> 1);

        // Scanner border
        int borderSize = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_border_size, dp1);
        int borderColor = ta.getColor(R.styleable.ScannerView_vk_code_scanner_border_color, Color.GRAY);

        // Scanner corners
        int cornerColor = ta.getColor(R.styleable.ScannerView_vk_code_scanner_corner_color, Color.RED);
        int cornerSize = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_corner_size, dp1 * 3);
        mCornerLength = ta.getDimensionPixelSize(R.styleable.ScannerView_vk_code_scanner_corner_length, cornerSize * 7);
        mCornerStyle = ta.getInteger(R.styleable.ScannerView_vk_code_scanner_corner_style, 1);
        mCornerSizeHalf = Math.max(0, cornerSize >> 1);

        // Scanner line
        int lineColor = ta.getColor(R.styleable.ScannerView_vk_code_scanner_line_color, COLOR_LINE);
        int lineAlpha = ta.getColor(R.styleable.ScannerView_vk_code_scanner_line_alpha, COLOR_LINE_ALPHA);
        int lineHeight = ta.getDimensionPixelOffset(R.styleable.ScannerView_vk_code_scanner_line_height, dp1 * 3);
        int animatorId = ta.getResourceId(R.styleable.ScannerView_vk_code_scanner_line_animator, 0);
        boolean animatorEnable = ta.getBoolean(R.styleable.ScannerView_vk_code_scanner_line_enable, true);

        ta.recycle();

        final int[] colors = new int[]{lineAlpha, lineColor, lineColor, lineAlpha};
        setLineStyle(colors, mScannerWidth, lineHeight);
        setLineAnimator(animatorEnable, animatorId);

        mMaskPaint.setColor(maskColor);

        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStrokeWidth(borderSize);

        mCornerPaint.setColor(cornerColor);
        mCornerPaint.setStrokeWidth(cornerSize);
    }

    /**
     * Set scanner line style.
     *
     * @param width  line width
     * @param height line height
     */
    public void setLineStyle(@ColorInt int[] colors, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        GradientDrawable d = new GradientDrawable(LEFT_RIGHT, colors);
        d.setBounds(0, 0, width, height);
        d.draw(new Canvas(b));
        mLineBitmap = b;
    }

    private void setLineAnimator(boolean animatorEnable, int animatorId) {
        if (!animatorEnable) return;
        if (animatorId != 0) {
            Animator a = AnimatorInflater.loadAnimator(getContext(), animatorId);
            setAnimator((ValueAnimator) a);
        } else {
            setAnimator(createDefaultAnimator());
        }
    }

    /**
     * Get default animator.
     *
     * @return ValueAnimator
     */
    private ValueAnimator createDefaultAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        return animator;
    }

    /**
     * Set new Animator.
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
                float animatedTop = top + a.getAnimatedFraction() * mScannerHeight;
                float minBottom = Math.min(animatedTop + mLineBitmap.getHeight(), bottom);
                mLineRectF.set(left, animatedTop, right, minBottom);
                invalidate();
            });
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        animatorStatusChange(visibility == VISIBLE);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        animatorStatusChange(screenState == SCREEN_STATE_ON);
    }

    private void animatorStatusChange(boolean start) {
        if (mLineAnimator == null) return;
        if (start) {
            mLineAnimator.start();
        } else {
            mLineAnimator.end();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMaskPaint.getColor() != Color.TRANSPARENT) {
            canvas.drawPath(mMaskPath, mMaskPaint);
        }

        canvas.drawRect(left, top, right, bottom, mBorderPaint);
        canvas.drawPath(mCornerPath, mCornerPaint);

        if (mLineAnimator != null) {
            canvas.drawBitmap(mLineBitmap, null, mLineRectF, mLinePaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        left = (w - mScannerWidth) >> 1;
        right = left + mScannerWidth;
        bottom = top + mScannerHeight;

        onMaskPathChange(w, h, left, top, right, bottom);
        onCornerPathChange(left, top, right, bottom, mCornerLength);
    }

    /**
     * Calculate new scanner corner size if style has changed.
     */
    private void onCornerPathChange(int left, int top, int right, int bottom, int length) {
        float leftF, topF, rightF, bottomF;

        switch (mCornerStyle) {
            case 0:
                leftF = left + mCornerSizeHalf;
                topF = top + mCornerSizeHalf;
                rightF = right - mCornerSizeHalf;
                bottomF = bottom - mCornerSizeHalf;
                break;

            case 2:
                leftF = left - mCornerSizeHalf;
                topF = top - mCornerSizeHalf;
                rightF = right + mCornerSizeHalf;
                bottomF = bottom + mCornerSizeHalf;
                break;

            default:
                leftF = left;
                topF = top;
                rightF = right;
                bottomF = bottom;
        }

        mCornerPath.reset();

        // top left
        mCornerPath.moveTo(leftF, topF + length);
        mCornerPath.lineTo(leftF, topF);
        mCornerPath.lineTo(leftF + length, topF);

        // top right
        mCornerPath.moveTo(rightF - length, topF);
        mCornerPath.lineTo(rightF, topF);
        mCornerPath.lineTo(rightF, topF + length);

        // bottom right
        mCornerPath.moveTo(rightF, bottomF - length);
        mCornerPath.lineTo(rightF, bottomF);
        mCornerPath.lineTo(rightF - length, bottomF);

        // bottom left
        mCornerPath.moveTo(leftF + length, bottomF);
        mCornerPath.lineTo(leftF, bottomF);
        mCornerPath.lineTo(leftF, bottomF - length);
    }

    /**
     * Calculate new scanner mask path if style has changed.
     */
    private void onMaskPathChange(int w, int h, int left, int top, int right, int bottom) {
        mMaskPath.reset();
        mMaskPath.moveTo(0, 0);
        mMaskPath.lineTo(w, 0);
        mMaskPath.lineTo(w, h);
        mMaskPath.lineTo(right, h);
        mMaskPath.lineTo(right, top);
        mMaskPath.lineTo(left, top);
        mMaskPath.lineTo(left, bottom);
        mMaskPath.lineTo(right, bottom);
        mMaskPath.lineTo(right, h);
        mMaskPath.lineTo(0, h);
        mMaskPath.close();
    }
}
