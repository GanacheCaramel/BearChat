package cg.ltenshi.app.social.bearchat.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.EditText;

public class CanvasHintEditText extends EditText {
    private final Rect rect = new Rect();
    private final TextPaint hintPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String hintText = "";

    public CanvasHintEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        hintPaint.setColor(Color.GRAY); // Définir la couleur du hint
        setHint(""); // On désactive le hint standard
    }

    public void setHintText(String text) {
        this.hintText = text;
        invalidate(); // Redessiner la vue
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Dessiner le hint personnalisé si le champ est vide
        if (hintText != null && getText().length() == 0) {
            hintPaint.setTextSize(getTextSize());
            hintPaint.getTextBounds(hintText, 0, hintText.length(), rect);

            // Positionner le hint à l'intérieur des bordures
            float x = getPaddingStart();
            float y = (getHeight() + rect.height()) / 2f;
            canvas.drawText(hintText, x, y, hintPaint);
        }
        super.onDraw(canvas); // Dessiner le texte normal
    }
}