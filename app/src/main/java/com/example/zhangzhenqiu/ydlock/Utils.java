package com.example.zhangzhenqiu.ydlock;

import android.app.KeyguardManager;
import android.content.Context;

import java.util.Calendar;

public class Utils {

	/**
	 * get the current date 
	 * @since 2014-07-05
	 * @return string
	 */
	public static String getDate() {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1];
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String dayOfMonth = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		return month + "月" + dayOfMonth + "日" + "  " + dayOfWeek;
	}

	private static final String[] weekdays = {"星期日","星期一","星期二","星期三","星期四",
			"星期五","星期六"};

}
