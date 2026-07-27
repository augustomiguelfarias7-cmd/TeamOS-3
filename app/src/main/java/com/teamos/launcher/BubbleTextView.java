package com.teamos.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BubbleTextView extends LinearLayout {

    private FrameLayout iconFrameContainer;
    private View iconGlowAura;
    private ImageView iconImage;
    private ImageView iconBorder;
    private TextView appLabel;

    public BubbleTextView(Context context) {
        super(context);
    }

    public BubbleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        iconFrameContainer = findViewById(R.id.icon_frame_container);
        iconGlowAura = findViewById(R.id.icon_glow_aura);
        iconImage = findViewById(R.id.icon_image);
        iconBorder = findViewById(R.id.icon_border);
        appLabel = findViewById(R.id.app_label);
    }

    public void applyAppInfo(String label, Drawable originalIcon, String shapePref, boolean glowEnabled) {
        if (appLabel != null) {
            appLabel.setText(label);
        }

        if (iconImage != null && originalIcon != null) {
            Drawable framedIcon = getFramedIcon(getContext(), originalIcon, shapePref);
            iconImage.setImageDrawable(framedIcon);
        }

        // Apply glow effect and borders
        if (iconGlowAura != null && iconBorder != null) {
            if (glowEnabled && !"none".equals(shapePref)) {
                iconGlowAura.setAlpha(0.25f);
                iconBorder.setVisibility(View.VISIBLE);
                if ("hexagon".equals(shapePref)) {
                    iconBorder.setImageResource(R.drawable.shape_hexagon);
                    iconGlowAura.setBackgroundResource(R.drawable.shape_hexagon);
                    iconBorder.setColorFilter(Color.parseColor("#00F0FF"), PorterDuff.Mode.SRC_ATOP);
                } else if ("squircle".equals(shapePref)) {
                    iconBorder.setImageResource(R.drawable.shape_squircle);
                    iconGlowAura.setBackgroundResource(R.drawable.shape_squircle);
                    iconBorder.setColorFilter(Color.parseColor("#D000FF"), PorterDuff.Mode.SRC_ATOP);
                } else if ("circle".equals(shapePref)) {
                    iconBorder.setImageResource(R.drawable.shape_circle);
                    iconGlowAura.setBackgroundResource(R.drawable.shape_circle);
                    iconBorder.setColorFilter(Color.parseColor("#FF0055"), PorterDuff.Mode.SRC_ATOP);
                }
            } else {
                iconGlowAura.setAlpha(0f);
                iconBorder.setVisibility(View.GONE);
            }
        }
    }

    public static Drawable getFramedIcon(Context context, Drawable originalIcon, String shapePref) {
        if ("none".equals(shapePref) || shapePref == null) {
            return originalIcon;
        }

        int size = 120; // Size of generated bitmap
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Draw mask shape on canvas
        Drawable maskDrawable = null;
        if ("hexagon".equals(shapePref)) {
            maskDrawable = ContextCompat.getDrawable(context, R.drawable.shape_hexagon);
        } else if ("squircle".equals(shapePref)) {
            maskDrawable = ContextCompat.getDrawable(context, R.drawable.shape_squircle);
        } else if ("circle".equals(shapePref)) {
            maskDrawable = ContextCompat.getDrawable(context, R.drawable.shape_circle);
        }

        if (maskDrawable != null) {
            maskDrawable.setBounds(0, 0, size, size);
            maskDrawable.draw(canvas);
        } else {
            // Default to square mask if shape not found
            canvas.drawRect(new Rect(0, 0, size, size), paint);
        }

        // Set transfer mode to SRC_IN to mask the original icon inside the shape
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // Draw the original icon on top
        Bitmap originalBitmap = drawableToBitmap(originalIcon, size);
        canvas.drawBitmap(originalBitmap, 0, 0, paint);

        return new BitmapDrawable(context.getResources(), output);
    }

    private static Bitmap drawableToBitmap(Drawable drawable, int size) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            if (b != null) {
                return Bitmap.createScaledBitmap(b, size, size, true);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
