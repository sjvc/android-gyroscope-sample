package es.ua.eps.giroscopio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class GyroscopeBitmap {
    private Bitmap mBitmap;
    private int[] mContainerSize;  // 0=x, 1=y
    private float[] mPosition;     // 0=x, 1=y. Usamos float en lugar de int para mantener la precisión, ya que la distancia a mover que recibimos es también un float
    private float mDistanceFactor; // Cuando se nos indique mover el bitmap una distancia, lo multiplicaremos por este valor

    public GyroscopeBitmap(Context context, int drawableResId, float distanceFactor) {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
        mDistanceFactor = distanceFactor;
    }

    // Establece el tamaño del contenedor
    public void onContainerSizeChanged(int containerSizeX, int containerSizeY) {
        mContainerSize = new int[] {containerSizeX, containerSizeY};

        // Al inicio el bitmap está centrado en su contenedor
        mPosition = new float [] {
                (mContainerSize[0] - mBitmap.getWidth())  / 2f,
                (mContainerSize[1] - mBitmap.getHeight()) / 2f
        };
    }

    // Dibuja el bitmap en el canvas en la posición actual
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, Math.round(mPosition[0]), Math.round( mPosition[1]), null);
    }

    // Mueve el bitmap una distancia en un eje (multiplicando por su mDistanceFactor)
    // Devolvemos cuanta distancia ha sido posible mover el bitmap, teniendo en cuenta los límites
    public float moveAxisBy(int axis, float distance) {
        if (mPosition == null || mContainerSize == null) return 0;

        float prevPos = mPosition[axis];
        mPosition[axis] -= distance * mDistanceFactor;

        // Límite por la parte superior / izquierda
        if (mPosition[axis] > 0) {
            mPosition[axis] = 0;
        }
        // Límite por la parte inferior / derecha
        else if (mPosition[axis] < mContainerSize[axis] - getBitmapSize(axis)) {
            mPosition[axis] = mContainerSize[axis] - getBitmapSize(axis);
        }

        // Distancia ha sido posible mover el bitmap, teniendo en cuenta los límites
        return prevPos - mPosition[axis];
    }

    private int getBitmapSize(int axis) {
        if (mBitmap == null) return 0;

        return axis == 0 ? mBitmap.getWidth() : mBitmap.getHeight();
    }
}
