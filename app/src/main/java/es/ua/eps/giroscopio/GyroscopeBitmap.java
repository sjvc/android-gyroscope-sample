package es.ua.eps.giroscopio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/*
    Vamos a crear un ViewPort (Rect) cuyo tamaño será el del tamaño del contenedor dividido entre la escala.
    Cuando el giroscopio nos proporcione nuevos valores, lo que haremos será mover el ViewPort dentro de los límites del bitmap,
    y luego dibujar la parte del bitmap correspondiente al ViewPort, escalándolo hasta que ocupe el tamaño del contenedor.
 */

public class GyroscopeBitmap {
    private Bitmap mBitmap;                     // Bitmap a dibujar
    private float mScale;                       // Escala que aplicaremos al bitmap
    private float[] mViewPortSize;              // Tamaño del ViewPort (tamaño del contenedor entre la escala)
    private float[] mViewPortPosition;          // Necesitamos guardar la posición del ViewPort en floats para mantener la precisión
    private Rect mViewPortRect = new Rect();    // Rect del ViewPort para dibujarlo en un canvas
    private Rect mContainerRect = new Rect();   // Rect del contenedor para dibujarlo en un canvas
    private float mDistanceFactor;              // Cuando se nos indique mover el bitmap una distancia, lo multiplicaremos por este valor
    private Paint mDrawPaint = new Paint();     // Pintura para dibujar el bitmap

    public GyroscopeBitmap(Context context, int drawableResId, float distanceFactor, float scale) {
        mDistanceFactor = distanceFactor;
        mScale = scale;

        // Cargar Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // No escalar para evitar "blur"
        mBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId, options);

        // Deshabilitamos antialias y filtro de bitmap para que no le aplique "blur" a la imagen
        mDrawPaint.setAntiAlias(false);
        mDrawPaint.setFilterBitmap(false);
    }

    // Establece el tamaño del contenedor
    public void onContainerSizeChanged(int containerSizeX, int containerSizeY) {
        // Calcular tamaño del ViewPort
        mViewPortSize = new float[] {
            containerSizeX / mScale,
            containerSizeY / mScale
        };

        // Al inicio el viewport está centrado en el bitmap
        mViewPortPosition = new float[]{
            (mBitmap.getWidth()  - mViewPortSize[0]) * 0.5f,
            (mBitmap.getHeight() - mViewPortSize[1]) * 0.5f
        };

        // Calculamos el rect del contenedor donde se dibujará este bitmap
        mContainerRect.left   = 0;
        mContainerRect.top    = 0;
        mContainerRect.right  = containerSizeX;
        mContainerRect.bottom = containerSizeY;
    }

    // Dibuja el bitmap en el canvas en la posición actual
    public void onDraw(Canvas canvas) {
        // Actualizar rect del viewport con la posición actual
        mViewPortRect.left   = Math.round(mViewPortPosition[0]);
        mViewPortRect.top    = Math.round(mViewPortPosition[1]);
        mViewPortRect.right  = Math.round(mViewPortPosition[0] + mViewPortSize[0]);
        mViewPortRect.bottom = Math.round(mViewPortPosition[1] + mViewPortSize[1]);

        // Dibujar el ViewPort ampliándolo a pantalla completa
        canvas.drawBitmap(mBitmap, mViewPortRect, mContainerRect, mDrawPaint);
    }

    // Mueve el bitmap una distancia en un eje (multiplicando por su mDistanceFactor)
    // Devolvemos cuanta distancia ha sido posible mover el bitmap, teniendo en cuenta los límites
    public float moveAxisBy(int axis, float distance) {
        if (mViewPortPosition == null || mViewPortSize == null) return 0;

        // Guardar posición actual y calcular siguente posición
        float prevPos = mViewPortPosition[axis];
        mViewPortPosition[axis] -= distance * mDistanceFactor;

        // Límites del ViewPort si éste es más pequeño que el bitmap
        if (mViewPortSize[axis] < getBitmapSize(axis)) {
            // Límite por la parte superior / izquierda
            if (mViewPortPosition[axis] < 0) {
                mViewPortPosition[axis] = 0;
            }
            // Límite por la parte inferior / derecha
            else if (mViewPortPosition[axis] > getBitmapSize(axis) - mViewPortSize[axis]) {
                mViewPortPosition[axis] = getBitmapSize(axis) - mViewPortSize[axis];
            }
        }

        // Distancia que ha sido posible mover el bitmap, teniendo en cuenta los límites
        return prevPos - mViewPortPosition[axis];
    }


    // Obtiene el ancho / alto del bitmap
    private int getBitmapSize(int axis) {
        if (mBitmap == null) return 0;

        return axis == 0 ? mBitmap.getWidth() : mBitmap.getHeight();
    }
}
