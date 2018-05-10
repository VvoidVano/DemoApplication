package com.example.abc.demoapplication.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogUtils {

	private static String defaultTag = "Telstra";

	private static boolean isShow = true;

	public static void LogD(String tag, String strLog) {
		if(TextUtils.isEmpty(tag)) tag = defaultTag;
		StackTraceElement invoker = getInVoker();
		if (isShow) {
			Log.d(tag,
					"[" + invoker.getClassName() + "   "
							+ invoker.getMethodName() + "   "
							+ invoker.getLineNumber() + "  " + "]" + strLog);
		}
	}

	public static void LogI(String tag, String strLog) {
		if(TextUtils.isEmpty(tag)) tag = defaultTag;
		StackTraceElement invoker = getInVoker();
		if (isShow) {
			Log.i(tag,
					"[" + invoker.getClassName() + "   "
							+ invoker.getMethodName() + "   "
							+ invoker.getLineNumber() + "  " + "]" + strLog);
		}
	}

	public static void logE(String tag, String strLog) {
		if(TextUtils.isEmpty(tag)) tag = defaultTag;
		StackTraceElement invoker = getInVoker();
		if (isShow) {
			Log.e(tag,
					"[" + invoker.getClassName() + "   "
							+ invoker.getMethodName() + "   "
							+ invoker.getLineNumber() + "  " + "]" + strLog);
		}
	}

	public static void logException(Exception e) {
		if (isShow) {
			StackTraceElement invoker = getInVoker();
			Log.e(defaultTag,
					"[" + invoker.getClassName() + "   "
							+ invoker.getMethodName() + "   "
							+ invoker.getLineNumber() + "  " + "]" + e);
		}
	}

	public static StackTraceElement getInVoker() {
		return Thread.currentThread().getStackTrace()[4];
	}
}
