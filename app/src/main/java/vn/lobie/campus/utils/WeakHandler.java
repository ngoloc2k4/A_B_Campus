import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

package vn.lobie.campus.utils;



public class WeakHandler {
    private final Handler.Callback mCallback;
    private final ExecHandler mExec;
    
    public WeakHandler() {
        mCallback = null;
        mExec = new ExecHandler();
    }

    public WeakHandler(Handler.Callback callback) {
        mCallback = callback;
        mExec = new ExecHandler(new WeakReference<>(callback));
    }

    public WeakHandler(Looper looper) {
        mCallback = null;
        mExec = new ExecHandler(looper);
    }

    public WeakHandler(Looper looper, Handler.Callback callback) {
        mCallback = callback;
        mExec = new ExecHandler(looper, new WeakReference<>(callback));
    }

    public final boolean post(Runnable r) {
        return mExec.post(r);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return mExec.postAtTime(r, uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return mExec.postDelayed(r, delayMillis);
    }

    public final void removeCallbacks(Runnable r) {
        mExec.removeCallbacks(r);
    }

    public final boolean sendMessage(Message msg) {
        return mExec.sendMessage(msg);
    }

    public final boolean sendEmptyMessage(int what) {
        return mExec.sendEmptyMessage(what);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        return mExec.sendEmptyMessageDelayed(what, delayMillis);
    }

    public final void removeMessages(int what) {
        mExec.removeMessages(what);
    }

    public final void removeCallbacksAndMessages(Object token) {
        mExec.removeCallbacksAndMessages(token);
    }

    private static class ExecHandler extends Handler {
        private final WeakReference<Handler.Callback> mCallback;

        ExecHandler() {
            mCallback = null;
        }

        ExecHandler(WeakReference<Handler.Callback> callback) {
            mCallback = callback;
        }

        ExecHandler(Looper looper) {
            super(looper);
            mCallback = null;
        }

        ExecHandler(Looper looper, WeakReference<Handler.Callback> callback) {
            super(looper);
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mCallback != null) {
                Handler.Callback callback = mCallback.get();
                if (callback != null) {
                    callback.handleMessage(msg);
                }
            }
        }
    }
}