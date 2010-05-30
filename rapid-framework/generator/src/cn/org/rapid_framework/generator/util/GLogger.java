package cn.org.rapid_framework.generator.util;

public class GLogger {
	private static final int TRACE = 60;
	private static final int DEBUG = 70;
	private static final int INFO = 80;
	private static final int ERROR = 90;
	private static final int WARN = 100;

	public static int logLevel = INFO;

	public static void trace(String s) {
		if (logLevel <= TRACE)
			System.out.println("[Generator TRACE] " + s);
	}
	
	public static void debug(String s) {
		if (logLevel <= DEBUG)
			System.out.println("[Generator DEBUG] " + s);
	}

	public static void info(String s) {
		if (logLevel <= INFO)
			System.out.println("[Generator INFO] " + s);
	}

	public static void warn(String s) {
		if (logLevel <= WARN)
			System.err.println("[Generator WARN] " + s);
	}

	public static void warn(String s, Throwable e) {
		if (logLevel <= WARN) {
			System.err.println("[Generator WARN] " + s + " cause:"+e);
		}
	}

	public static void error(String s) {
		if (logLevel <= ERROR)
			System.err.println("[Generator ERROR] " + s + " cause:");
	}

	public static void error(String s, Throwable e) {
		if (logLevel <= ERROR) {
			System.err.println("[Generator ERROR] " + s + " cause:"+e);
//			e.printStackTrace();
		}
	}
}
