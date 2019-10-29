package es.ua.eps.giroscopio;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class GyroscopeView extends View {
    private final static float DEFAULT_WIDTH_DP = 100;
    private final static float DEFAULT_HEIGHT_DP = 100;
    private final static float GYROSCOPE_DISTANCE_FACTOR = 20f; // Multiplicaremos el valor del giroscopio por este número

    private GyroscopeBitmap[] mLayers;

    public GyroscopeView(Context context) {
        super(context);
        init();
    }

    public GyroscopeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GyroscopeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GyroscopeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mLayers = new GyroscopeBitmap[] {
            new GyroscopeBitmap(getContext(), R.drawable.layer03, 1.5f),
            new GyroscopeBitmap(getContext(), R.drawable.layer02, 0.5f),
            new GyroscopeBitmap(getContext(), R.drawable.layer01, 0.5f),
            new GyroscopeBitmap(getContext(), R.drawable.layer00, 0.25f)
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = Utils.convertDpToPixel(DEFAULT_WIDTH_DP, getContext());
        int height = Utils.convertDpToPixel(DEFAULT_HEIGHT_DP, getContext());

        if (widthMode == MeasureSpec.EXACTLY || (widthMode == MeasureSpec.AT_MOST && width > widthSize)) {
            width = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY || (heightMode == MeasureSpec.AT_MOST && height > heightSize)) {
            height = heightSize;
        }

        setMeasuredDimension(width, height);

        // Una vez calculado el tamaño de la vista, se la pasamos a la clase que gestiona los bitmaps
        for (GyroscopeBitmap layer : mLayers) {
            layer.onContainerSizeChanged(width, height);
        }
    }

    public void onGyroscopeChanged(float dX, float dY) {
        // Según los valores obtenidos del giroscopio, obtenemos la distancia a la que moveremos los bitmaps
        float[] distanceMoved = new float[] {dX * GYROSCOPE_DISTANCE_FACTOR, dY * GYROSCOPE_DISTANCE_FACTOR};

        // La primera capa se mueve según la distancia almacenada en "distanceMoved" (obtenida usando el giroscopio)
        // y el resto de capas se mueven según la distancia a la que se ha podido mover la capa anterior
        for (GyroscopeBitmap layer : mLayers) {
            for (int axis=0; axis<2; axis++) {
                distanceMoved[axis] = layer.moveAxisBy(axis, distanceMoved[axis]);
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujamos todos los bitmaps en orden
        for (GyroscopeBitmap layer : mLayers) {
            layer.onDraw(canvas);
        }
    }
}
