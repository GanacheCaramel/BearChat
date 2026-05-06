package cg.ltenshi.app.social.bearchat.utils;

import android.view.View;
import android.view.MotionEvent;
import android.animation.ValueAnimator;
import android.view.animation.OvershootInterpolator;

import android.content.Context;

import cg.ltenshi.app.social.bearchat.R;

public class LTSwipeListener {
    private Context context;
    private OnSwipeListener swipeListener;
    private int swipeThreshold = 100;

    public interface OnSwipeListener {
        void onSwipeLeft(View view, int position);
        void onSwipeRight(View view, int position);
        void onSwipeReply(View view, int position); // Pour la réponse comme les réseaux sociaux
    }

    public LTSwipeListener(Context context, OnSwipeListener listener) {
        this.context = context;
        this.swipeListener = listener;
    }

    public void attachToView(final View view, final int position) {
        view.setOnTouchListener(new View.OnTouchListener() {
				private float x1, x2;
				private long startTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							x1 = event.getX();
							startTime = System.currentTimeMillis();
							return true;

						case MotionEvent.ACTION_UP:
							x2 = event.getX();
							float deltaX = x2 - x1;
							long duration = System.currentTimeMillis() - startTime;

							// Détection du swipe
							if (Math.abs(deltaX) > swipeThreshold && duration < 300) {
								if (deltaX > 0) {
									// Swipe droite
									if (swipeListener != null) {
										swipeListener.onSwipeRight(view, position);
									}
								} else {
									// Swipe gauche - réponse au message
									if (swipeListener != null) {
										swipeListener.onSwipeLeft(view, position);
										// Ou directement la réponse :
										// swipeListener.onSwipeReply(view, position);
									}
								}
								return true;
							}
							break;
					}
					return false;
				}
			});
    }

    // Pour l'effet visuel de swipe comme les réseaux sociaux
    public void attachToViewWithVisualFeedback(final View view, final int position) {
        final View cardView = view.findViewById(R.id.sent_card_msg); // Ton layout p
        final View replyIndicator = view.findViewById(R.id.sent_reply_indicator); // Indicateur de réponse

        view.setOnTouchListener(new View.OnTouchListener() {
				private float x1, startX;
				private boolean isSwiping = false;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							x1 = event.getX();
							startX = cardView.getTranslationX();
							return true;

						case MotionEvent.ACTION_MOVE:
							float x2 = event.getX();
							float deltaX = x2 - x1;

							// Si le swipe dépasse un seuil minimal, on considère que c'est un swipe
							if (Math.abs(deltaX) > 10) {
								isSwiping = true;

								// Limiter le swipe à gauche seulement (pour répondre)
								if (deltaX < 0) {
									float translationX = Math.max(deltaX, -200); // Limite à -200px
									cardView.setTranslationX(translationX);

									// Afficher l'indicateur de réponse
									if (replyIndicator != null) {
										float alpha = Math.min(1, Math.abs(deltaX) / 200);
										replyIndicator.setAlpha(alpha);
									}
								}
							}
							return true;

						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							if (isSwiping) {
								float finalX = cardView.getTranslationX();

								// Si swipe suffisant vers la gauche, déclencher la réponse
								if (finalX < -100) {
									if (swipeListener != null) {
										swipeListener.onSwipeReply(view, position);
									}
								}

								// Animation de retour
								resetCardPosition(cardView, replyIndicator);
								isSwiping = false;
								return true;
							}
							break;
					}
					return false;
				}
			});
    }

    private void resetCardPosition(final View cardView, final View replyIndicator) {
        ValueAnimator animator = ValueAnimator.ofFloat(cardView.getTranslationX(), 0);
        animator.setDuration(200);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float value = (Float) animation.getAnimatedValue();
					cardView.setTranslationX(value);
					if (replyIndicator != null) {
						replyIndicator.setAlpha(1 - (Math.abs(value) / 200));
					}
				}
			});
        animator.start();
    }
}