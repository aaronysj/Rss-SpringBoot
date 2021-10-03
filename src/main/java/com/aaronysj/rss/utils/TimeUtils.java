package com.aaronysj.rss.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TimeUtil
 * <p>
 * Created by changgq on 2019/03/29
 */
@Slf4j
public class TimeUtils {
	public static final int MS = 1;
	public static final int SECOND = 1000 * MS;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;

	public static final String DATE_TIME_PATTERN_STR = "yyyy-MM-dd HH-mm-ss";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_PATTERN_2 = "yyyy/MM/dd HH:mm:ss";
	public static final String MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String HOUR_PATTERN = "yyyy-MM-dd HH";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String MONTH_PATTERN = "yyyy-MM";
	public static final String YEAR_PATTERN = "yyyy";
	public static final String MINUTE_ONLY_PATTERN = "mm";
	public static final String HOUR_ONLY_PATTERN = "HH";

	public static final String UTC_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

	/**
	 * 日期相加减天数
	 *
	 */
	public static Date plus(Date day, int days, boolean includeTime) throws ParseException {
		if (day == null) {
			day = new Date();
		}
		if (!includeTime) {
			SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
			day = df.parse(df.format(day));
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * 时间格式化成字符串
	 *
	 */
	public static String dateFormat(Date date, String pattern) {
		if (StringUtils.isEmpty(pattern)) {
			pattern = DATE_PATTERN;
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String dateFormat(Date date) {
		return dateFormat(date, null);
	}

	/**
	 * 描述：解析日期字串
	 *
	 * @param dateStr 日期字串
	 * @param pattern 字串日期格式
	 * @return 对应日期类型数据
	 */
	public static Date dateParse(String dateStr, String pattern) {
		if (StringUtils.isEmpty(pattern)) {
			pattern = DATE_PATTERN;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		if (!StringUtils.isEmpty(dateStr)) {
			try {
				return dateFormat.parse(dateStr);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 比较两个时间字段的大小，结果大于零表示：start < end；结果小于零表示：start > end
	 *
	 */
	public static long diff(Date start, Date end) {
		return end.getTime() - start.getTime();
	}

	/**
	 * 校验两个时间是否在同一天内
	 *
	 */
	public static boolean checkInDay(Date start, Date end) {
		return dateFormat(start, DATE_PATTERN).equals(dateFormat(end, DATE_PATTERN));
	}

	/**
	 * 校验两个时间是否在同一月内
	 *
	 */
	public static boolean checkInMonth(Date start, Date end) {
		return dateFormat(start, MONTH_PATTERN).equals(dateFormat(end, MONTH_PATTERN));
	}


	public static boolean checkBetweenDate(Date checkTime, Date start, Date end) {
		if (start == null || end == null || checkTime == null) {
			return false;
		}
		return start.getTime() <= checkTime.getTime() && end.getTime() >= checkTime.getTime();
	}

	/**
	 * 校验两个时间是否在统一年内
	 *
	 */
	public static boolean checkInYear(Date start, Date end) {
		return dateFormat(start, YEAR_PATTERN).equals(dateFormat(end, YEAR_PATTERN));
	}

	/**
	 * toString
	 * <p>
	 * 格式：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 *
	 */
	public static String toStringOfUTC(Date date) {
		return date.toInstant().toString();
	}

	/**
	 * toString
	 * <p>
	 * 格式：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 */
	public static String toStringAtOffset(Date date, int hours) {
		return date.toInstant().atOffset(ZoneOffset.ofHours(hours)).toString();
	}

	/**
	 * toString
	 * <p>将时间字符串转化成 yyyy-MM-dd HH:mm:ss</>
	 *
	 */
	public static String getPatternTime(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN, Locale.SIMPLIFIED_CHINESE);
		String time = null;
		try {
			Date date = dateFormat.parse(dateStr.replace("T", " ").replace("Z", ""));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
			time = dateFormat.format(calendar.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 获取 N 个月 前/后 的格式化时间字符串
	 *
	 * @param month 月数 正数标识向后移，负数表示向前移
	 * @return String
	 */
	public static String getBeforeNMonthTimeStr(int month) {
		// 当前时间
		Date dNow = new Date();
		//得到日历
		Calendar calendar = Calendar.getInstance();
		//把当前时间赋给日历
		calendar.setTime(dNow);
		//设置为前N月
		calendar.add(Calendar.MONTH, month);
		//得到前N月的时间
		Date dBefore = calendar.getTime();
		//设置时间格式
		SimpleDateFormat sdf = new SimpleDateFormat(TimeUtils.DATE_TIME_PATTERN);
		//格式化前N月的时间
		return sdf.format(dBefore);
	}

	public static String getNowDateTimeStr() {
		return TimeUtils.dateFormat(new Date(), TimeUtils.DATE_TIME_PATTERN);
	}


	/**
	 * 获取 N 天 前/后 的格式化时间字符串
	 *
	 * @param days 天数 正数标识向后移，负数表示向前移
	 * @return String
	 */
	public static Date getDaysAfter(int days) {
		return getDaysAfter(new Date(), days);
	}

	public static Date getDaysAfter(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * 获取 某天0时0分
	 */
	public static Date getZeroTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}


	/**
	 * 根据时间间隔获取真正的开始时间点
	 * 输入->
	 * 2019-10-22 20:23:34
	 * 输出->
	 * 分钟：2019-10-22 20:23:00
	 * 小时：2019-10-22 20:00:00
	 * 天：2019-10-22 00:00:00
	 * 月：2019-10-01 00:00:00
	 *
	 * @param timeInterval   时间间隔，格式为[m|h|d|M]，分别代表[分|时|天|月]
	 */
	public static String convertStartTimePoint(String startTimePoint, String timeInterval) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DATE_TIME_PATTERN);
		Date date;
		try {
			date = simpleDateFormat.parse(startTimePoint);
		} catch (ParseException e) {
			return startTimePoint;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		switch (timeInterval) {
			case "m":
				calendar.set(Calendar.SECOND, 0);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			case "h":
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			case "d":
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			case "M":
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			default:
				return startTimePoint;
		}
	}

	/**
	 * 根据时间间隔获取真正的结束时间点
	 * 输入->
	 * 2019-10-22 20:23:34
	 * 输出->
	 * 分钟：2019-10-22 20:23:59
	 * 小时：2019-10-22 20:59:59
	 * 天：2019-10-22 23:59:59
	 * 月：2019-10-31 23:59:59
	 *
	 * @param timeInterval 时间间隔，格式为[m|h|d|M]，分别代表[分|时|天|月]
	 */
	public static String convertEndTimePoint(String endTimePoint, String timeInterval) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DATE_TIME_PATTERN);
		Date date;
		try {
			date = simpleDateFormat.parse(endTimePoint);
		} catch (ParseException e) {
			return endTimePoint;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		switch (timeInterval) {
			case "m":
				calendar.set(Calendar.SECOND, 59);
				// MySQL定时任务一分钟才会统计一次数据，endTime分钟数不能大于或等于当前时间的分钟数
				Calendar now = Calendar.getInstance();
				if (calendar.getTime().getTime() >= now.getTime().getTime()) {
					calendar = now;
					calendar.add(Calendar.MINUTE, -1);
					calendar.set(Calendar.SECOND, 59);
				}
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			case "h":
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MINUTE, 59);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			case "d":
				calendar.add(Calendar.DATE, -1);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			case "M":
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.MONTH, 1);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				return TimeUtils.dateFormat(calendar.getTime(), TimeUtils.DATE_TIME_PATTERN);
			default:
				return endTimePoint;
		}
	}
}
