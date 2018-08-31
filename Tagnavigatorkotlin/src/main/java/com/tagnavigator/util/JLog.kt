package com.tagnavigator.util


import android.util.Log


/**
 * 設定Log紀錄等級 當logLevel設定為VERBOSE，(VERBOSE,DEBUG,INFO,WARN,ERROR)層級的log會被記錄
 * 當logLevel設定為DEBUG，(DEBUG,INFO,WARN,ERROR)層級的log會被記錄
 * 當logLevel設定為INFO，(INFO,WARN,ERROR)層級的log會被記錄
 * 當logLevel設定為WARN，(WARN,ERROR)層級的log會被記錄 當logLevel設定為ERROR，(ERROR)層級的log會被記錄
 * 當logLevel設定為ASSERT，所有層級的log皆不記錄
 *
 * @author JosephWang
 */
object JLog {
    var showLog = true
    val TAG = "topsong"
    val JosephWang = "JosephWang"

    /**
     * set level of log
     */
    private val logLevel = 2           // /主要設定範圍

    /**
     * Priority constant for the println method; use Log.v.
     */
    val VERBOSE = 2

    /**
     * Priority constant for the println method; use Log.d.
     */
    val DEBUG = 3

    /**
     * Priority constant for the println method; use Log.i.
     */
    val INFO = 4

    /**
     * Priority constant for the println method; use Log.w.
     */
    val WARN = 5

    /**
     * Priority constant for the println method; use Log.e.
     */
    val ERROR = 6

    /**
     * Set up for ignoring all Log. Priority constant for the println method.
     */
    val ASSERT = 7

    /**
     * Send a [.VERBOSE] log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun v(tag: String, msg: String) {
        if (logLevel <= VERBOSE && showLog) {
            Log.v(tag, msg)
        }
    }

    fun v(msg: String) {
        v(TAG, msg)
    }

    /**
     * Send a [.VERBOSE] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun v(tag: String, msg: String, tr: Throwable) {
        if (logLevel <= VERBOSE && showLog) {
            Log.v(tag, msg, tr)
        }
    }

    fun v(msg: String, tr: Throwable) {
        v(TAG, msg, tr)
    }

    /**
     * Send a [.DEBUG] log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun d(tag: String, msg: String) {
        if (logLevel <= DEBUG && showLog) {
            Log.d(tag, msg)
        }
    }

    fun d(msg: String) {
        d(TAG, msg)
    }

    /**
     * Send a [.DEBUG] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun d(tag: String, msg: String, tr: Throwable) {
        if (logLevel <= DEBUG && showLog) {
            Log.d(tag, msg, tr)
        }
    }

    fun d(msg: String, tr: Throwable) {
        d(TAG, msg, tr)
    }

    /**
     * Send an [.INFO] log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun i(tag: String, msg: String) {
        if (logLevel <= INFO && showLog) {
            Log.i(tag, msg)
        }
    }

    fun i(msg: String) {
        i(TAG, msg)
    }

    /**
     * Send a [.INFO] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun i(tag: String, msg: String, tr: Throwable) {
        if (logLevel <= INFO && showLog) {
            Log.i(tag, msg, tr)
        }
    }

    fun i(msg: String, tr: Throwable) {
        i(TAG, msg, tr)
    }

    /**
     * Send a [.WARN] log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun w(tag: String, msg: String) {
        if (logLevel <= WARN && showLog) {
            Log.w(tag, msg)
        }
    }

    fun w(msg: String) {
        w(TAG, msg)
    }

    /**
     * Send a [.WARN] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun w(tag: String, msg: String, tr: Throwable) {
        if (logLevel <= WARN && showLog) {
            Log.w(tag, msg, tr)
        }
    }

    /**
     * Send a [.WARN] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param tr  An exception to log
     */
    fun w(tag: String, tr: Throwable) {
        if (logLevel <= WARN && showLog) {
            Log.w(tag, tr)
        }
    }

    /**
     * Send an [.ERROR] log message.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun e(tag: String, msg: String) {
        if (logLevel <= ERROR && showLog) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        e(TAG, msg)
    }

    /**
     * Send a [.ERROR] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     * identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun e(tag: String, msg: String, tr: Throwable) {
        if (logLevel <= ERROR && showLog) {
            Log.e(tag, msg, tr)
        }
    }

    fun e(msg: String, tr: Throwable) {
        e(TAG, msg, tr)
    }
}