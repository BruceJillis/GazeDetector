package brucejillis.gazedetector.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import brucejillis.gazedetector.GazeDetectorMod;
import cpw.mods.fml.common.FMLLog;

public class LogHelper {
	private static Logger logger = Logger.getLogger(GazeDetectorMod.ID);
	
	public static void init() {
		logger.setParent(FMLLog.getLogger());
	}
	
	public static void log(Level logLevel, String message) {
		logger.log(logLevel, message);
	}

	public static void log(Level level, Object o) {
		log(level, String.valueOf(o));
	}

	public static void log(Object m) {
		log(Level.INFO, String.valueOf(m));		
	}
	
	public static void log(String msg, Object... strings) {
		log(Level.INFO, String.format(msg, strings));		
	}	
	
}
