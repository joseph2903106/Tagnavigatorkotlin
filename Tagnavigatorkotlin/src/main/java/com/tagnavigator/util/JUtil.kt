package com.tagnavigator.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.StrictMode
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import java.io.File
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


/***
 * Code Name Version
 * Api level (no code name) 1.0
 * API level 1 (no code name) 1.1
 * API level 2 Cupcake 1.5
 * API level 3, NDK 1 Donut 1.6
 * API level 4, NDK 2 Eclair 2.0
 * API level 5 Eclair 2.0.1
 * API level 6 Eclair 2.1
 * API level 7, NDK 3 Froyo 2.2.x
 * API level 8, NDK 4 Gingerbread 2.3 - 2.3.2
 * API level 9, NDK 5 Gingerbread 2.3.3 - 2.3.7
 * API level 10 Honeycomb 3.0
 * API level 11 Honeycomb 3.1
 * API level 12,NDK 6 Honeycomb 3.2.x
 * API level 13 Ice Cream Sandwich 4.0.1 - 4.0.2
 * API level 14,NDK 7 Ice Cream Sandwich 4.0.3 - 4.0.4
 * API level 15,NDK 8 Jelly Bean 4.1.x
 * API level 16 Jelly Bean 4.2.x
 * API level 17 Jelly Bean 4.3.x
 * API level 18 KitKat 4.4 - 4.4.2
 * API level 19 KitKat (for wearable)4.4
 * API level 20 Lollipop 5.0
 * API level 21
 */
object JUtil {

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    fun getString(context: Context?, @StringRes stringId: Int): String {
        return context!!.getResources().getString(stringId)
    }

    fun isSocialId(id: String): Boolean {
        if (TextUtils.isEmpty(id) || id.length < 10) {
            return false
        }
        val v = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "X", "Y", "W", "Z", "I", "O")
        var inte = -1
        val s1 = Character.toUpperCase(id[0]).toString()
        for (i in 0..25) {
            if (s1.compareTo(v[i]) == 0) {
                inte = i
                break
            }
        }
        var total = 0
        val all = IntArray(11)
        val E = (inte + 10).toString()
        val E1 = Integer.parseInt(E[0].toString())
        val E2 = Integer.parseInt(E[1].toString())
        all[0] = E1
        all[1] = E2

        for (j in 2..10) {
            all[j] = Integer.parseInt(id[j - 1].toString())
        }
        for (k in 1..9) {
            total += all[k] * (10 - k)
        }
        total += all[0] + all[10]

        return total % 10 == 0
    }

    fun notReclaimActivity(act: FragmentActivity?): Boolean {
        return act != null && !act.isFinishing
    }

    fun notReclaimActivity(act: Activity?): Boolean {
        return act != null && !act.isFinishing
    }

    fun notReclaimActivity(act: Context?): Boolean {
        return if (act != null) {
            if (act is FragmentActivity) {
                notReclaimActivity(act as FragmentActivity?)
            } else if (act is Activity) {
                notReclaimActivity(act as Activity?)
            } else {
                false
            }
        } else {
            false
        }
    }

    fun md5(string: String): String {
        try {
            val md5 = MessageDigest.getInstance("MD5")
            // md5.update(s.getBytes(), 0, s.length());
            md5.reset()
            md5.update(string.toByteArray(Charset.forName("UTF-8")))
            // String signature = new BigInteger(1, md5.digest()).toString(16);
            // // JLog.d(JLog.JosephWang, "Signature original " + s + " md5 " +
            // // signature);
            //
            // return signature;
            val result = toHexString(md5.digest())
            JLog.d(JLog.JosephWang, "md5 " + result.toUpperCase())
            return result
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    // 將字符串中的每個字符轉換為十六進制
    private fun toHexString(bytes: ByteArray): String {

        val hexstring = StringBuilder()
        for (b in bytes) {
            val hex = Integer.toHexString(0xFF and b.toInt())
            if (hex.length == 1) {
                hexstring.append('0')
            }
            hexstring.append(hex)

        }
        return hexstring.toString()
    }


    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // km (change this constant to get miles)
        val dLat = (lat2 - lat1) * Math.PI / 180
        val dLon = (lon2 - lon1) * Math.PI / 180
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val d = R * c
        return d * 1000
    }


    fun allowNetWorkRunOnUI() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun getIntentFilter(vararg action: String): IntentFilter {
        val filter = IntentFilter()
        for (each in action) {
            filter.addAction(each)
        }
        return filter
    }

    fun notEmpty(file: File?): Boolean {
        return file != null && file.isFile && file.exists() && file.length() > 0
    }

    fun notEmpty(list: ViewPager?): Boolean {
        return list != null && list.adapter != null && list.adapter!!.count > 0
    }

    fun notEmpty(list: FloatArray?): Boolean {
        return list != null && list.size > 0
    }

    fun notEmpty(data: Array<out Type>?): Boolean {
        return data != null && data.size > 0
    }

    fun notEmpty(list: IntArray?): Boolean {
        return list != null && list.size > 0
    }

    fun notEmpty(list: AdapterView<*>?): Boolean {
        return if (list != null && list.adapter != null && list.adapter.count > 0) {
            true
        } else false
    }

    fun notEmpty(list: AdapterView<*>?, position: Int): Boolean {
        return if (list != null && list.adapter != null && list.adapter.count > 0
                && position < list.adapter.count && list.adapter.getItem(position) != null) {
            true
        } else false
    }

    fun notEmpty(list: SparseArray<*>?): Boolean {
        return list != null && list.size() > 0
    }

    fun notEmpty(list: Collection<*>?): Boolean {
        return if (list != null && list.size > 0) {
            true
        } else false
    }

    fun notEmpty(map: Map<*, *>?): Boolean {
        return map != null && map.size > 0
    }

    fun notEmpty(data: Array<Any>?): Boolean {
        return data != null && data.size > 0
    }

    fun notEmpty(set: Set<*>?): Boolean {
        return set != null && set.size > 0
    }

//    fun startClearTopIntent(act: FragmentActivity, intent: Intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//            act.startActivity(intent)
//        } else {
//            val cn = intent.component
//            val mainIntent = IntentCompat.makeRestartActivityTask(cn)
//            act.startActivity(mainIntent)
//        }
//        act.finish()
//    }

    fun hasIntentExtras(intent: Intent?): Boolean {
        return if (intent != null && intent.extras != null) {
            true
        } else false
    }

    fun hasIntentExtras(act: Activity): Boolean {
        return hasIntentExtras(act.intent)
    }

    fun is7Tablet(act: FragmentActivity): Boolean {
        val metrics = DisplayMetrics()
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics)
        val widthInInches = metrics.widthPixels / metrics.xdpi
        val heightInInches = metrics.heightPixels / metrics.ydpi
        val sizeInInches = Math.sqrt(Math.pow(widthInInches.toDouble(), 2.0) + Math.pow(heightInInches.toDouble(), 2.0))
        //0.5" buffer for 7" devices
        return sizeInInches >= 6.5 && sizeInInches <= 7.5
    }

    fun is10Tablet(act: FragmentActivity): Boolean {
        val metrics = DisplayMetrics()
        act.getWindowManager().getDefaultDisplay().getMetrics(metrics)
        val widthInInches = metrics.widthPixels / metrics.xdpi
        val heightInInches = metrics.heightPixels / metrics.ydpi
        val sizeInInches = Math.sqrt(Math.pow(widthInInches.toDouble(), 2.0) + Math.pow(heightInInches.toDouble(), 2.0))
        //0.5" buffer for 7" devices
        return sizeInInches > 7.5
    }

    private fun halfMathContext(position: Int): MathContext {
        return MathContext(position, RoundingMode.HALF_UP)
    }

    fun subtract(v1: Float, v2: Float): Float {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        b1.setScale(5, RoundingMode.HALF_UP)
        b2.setScale(5, RoundingMode.HALF_UP)
        return b1.subtract(b2, halfMathContext(5)).toFloat()
    }

    fun multiply(v1: Float, v2: Float): Float {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        b1.setScale(5, RoundingMode.HALF_UP)
        b2.setScale(5, RoundingMode.HALF_UP)
        return b1.multiply(b2, halfMathContext(5)).toFloat()
    }

    fun divide(v1: Float, v2: Float): Float {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        b1.setScale(5, RoundingMode.HALF_UP)
        b2.setScale(5, RoundingMode.HALF_UP)
        return b1.divide(b2, halfMathContext(5)).toFloat()
    }

    fun divide(v1: Long, v2: Long): Long {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        b1.setScale(0, RoundingMode.HALF_UP)
        b2.setScale(0, RoundingMode.HALF_UP)
        return b1.divide(b2, halfMathContext(0)).toLong()
    }

    fun divide(v1: Float, v2: Float, position: Int): Float {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        b1.setScale(5, RoundingMode.HALF_UP)
        b2.setScale(5, RoundingMode.HALF_UP)
        return b1.divide(b2, halfMathContext(position)).toFloat()
    }

    @JvmOverloads
    fun divide(v1: Int, v2: Int, position: Int = 5): Float {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        return b1.divide(b2, halfMathContext(position)).toFloat()
    }

    fun add(v1: Float, v2: Float): Float {
        val b1 = BigDecimal("" + v1)
        val b2 = BigDecimal("" + v2)
        b1.setScale(5, RoundingMode.HALF_UP)
        b2.setScale(5, RoundingMode.HALF_UP)
        return b1.add(b2, halfMathContext(5)).toFloat()
    }

    fun add(vararg list: Float): Float {
        var start = 0f
        for (i in list.indices) {
            val b1 = BigDecimal("" + list[i])
            start = BigDecimal("" + start).add(b1, halfMathContext(5)).toFloat()
        }
        return start
    }

    fun isAPPInstall(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return false
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    fun sortStringDataByCountMutiple(deviceID: ArrayList<List<String>>, data: List<String>?, count: Int) {
        if (count > 0 && data != null && data.size > 0) {
            val mutiple = if (data.size % count == 0) data.size / count else data.size / count + 1// 6的除數，不是5的倍數，要多一組String_array
            for (index in 0 until mutiple) {
                if (mutiple > 1) {
                    if (index + 1 < mutiple && index > 0 && index < mutiple - 1) {// 其他部分
                        deviceID.add(data.subList(index * count, (index + 1) * count))
                    } else if (index == 0) {// 第一組
                        deviceID.add(data.subList(0, 1 * (count - 1) + 1))
                    } else if (index == mutiple - 1) {// 最後一組
                        deviceID.add(data.subList(count * index, data.size))
                    }
                } else {// 不足5個
                    deviceID.add(data.subList(0, data.size))
                }
            }
        }
    }

    fun hasInternet(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connManager.activeNetworkInfo
        return if (info == null || !info.isConnected || !info.isAvailable) {
            false
        } else {
            true
        }
    }

}