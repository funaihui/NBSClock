package com.demo.nbsclock

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private var firstHour = 0
    private var firstMinute = 0
    private var timeList: ArrayList<Int>? = null
    private var alreadySuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_set_alarm.setOnClickListener {
            alreadySuccess = false

            dealAlarmTime()
            for (time: Int in timeList!!) {
                if (setSystemAlarmClock(
                        et_alarm_content.text.toString().trim(),
                        time,
                        firstMinute
                    ) && !alreadySuccess
                ) {
                    alreadySuccess = true
                    Toast.makeText(this, "设置成功，可到闹钟界面查看", Toast.LENGTH_SHORT).show()
                }
            }
        }
        et_first.setOnClickListener {
            val tpd = TimePickerDialog.newInstance(this@MainActivity, true)
            tpd.show(supportFragmentManager, "")
        }
        btn_dismiss_alarm.setOnClickListener {
            dealAlarmTime()
            for (time: Int in timeList!!) {
                dismissSystemAlarmClock(et_alarm_content.text.toString().trim(), time, firstMinute)
            }
        }
    }

    private fun dealAlarmTime() {
        val interval = et_interval.text.toString().trim()//间隔时间
        var intervalResult = 0
        if (interval.isNotEmpty()) {
            intervalResult = interval.toInt()
        }
        //设置集合的大小，防止间隔时间错乱
        val floor = ((24 / intervalResult) + 0.5).roundToInt()
        timeList = ArrayList(floor)
        var time = firstHour
        timeList?.clear()

        while (!timeList!!.contains(time) && timeList!!.size < floor) {
            timeList?.add(time)
            time = (time + intervalResult) % 24
        }
    }

    private fun setSystemAlarmClock(
        message: String,
        hour: Int,
        minute: Int
    ): Boolean {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, message)
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true)
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    private fun dismissSystemAlarmClock(
        message: String,
        hour: Int,
        minute: Int
    ): Boolean {
        val intent = Intent(AlarmClock.ACTION_DISMISS_TIMER)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, message)
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        firstHour = hourOfDay
        firstMinute = minute
        et_first.setText("$hourOfDay:$minute")
    }


}
