package it.asrv.accodame.utils

import it.asrv.accodame.Configuration

class DLog {
    companion object {
        private val BUFFER_LIMIT = 10000
        private fun LOG(priority: Int, tag: String, message: String) {
            if (message.length > BUFFER_LIMIT) {
                android.util.Log.println(priority, tag, message.substring(0, BUFFER_LIMIT))
                /*try {
                LOG(priority, tag, message.substring(BUFFER_LIMIT));
            } catch (OutOfMemoryError e) {
                DLog.e(tag, "Log message is too long!");
            }*/
            } else
                android.util.Log.println(priority, tag, message)
        }

        fun v(tag: String, msg: String) {
            if (Configuration.debug)
                LOG(android.util.Log.VERBOSE, tag, msg)
        }

        fun v(tag: String, msg: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.v(tag, msg, tr)
        }

        fun d(tag: String, msg: String) {
            if (Configuration.debug)
                LOG(android.util.Log.DEBUG, tag, msg)
        }

        fun d(tag: String, msg: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.d(tag, msg, tr)
        }

        fun i(tag: String, msg: String) {
            if (Configuration.debug)
                LOG(android.util.Log.INFO, tag, msg)
        }

        fun i(tag: String, msg: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.i(tag, msg, tr)
        }

        fun w(tag: String, msg: String) {
            if (Configuration.debug)
                LOG(android.util.Log.WARN, tag, msg)
        }

        fun w(tag: String, msg: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.w(tag, msg, tr)
        }

        fun w(tag: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.w(tag, tr)
        }

        fun e(tag: String, msg: String) {
            if (Configuration.debug)
                LOG(android.util.Log.ERROR, tag, msg)
        }

        fun e(tag: String, msg: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.e(tag, msg, tr)
        }

        fun wtf(tag: String, msg: String) {
            if (Configuration.debug)
                LOG(android.util.Log.ASSERT, tag, msg)
        }

        fun wtf(tag: String, msg: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.wtf(tag, msg, tr)
        }

        fun wtf(tag: String, tr: Throwable) {
            if (Configuration.debug)
                android.util.Log.wtf(tag, tr)
        }
    }
}