package com.mj.common.util;

/**
 * @author MJ
 */
public class Cmds {
    private static final Runtime RT = Runtime.getRuntime();
    public static final String WAIT = "/wait ";

    /**
     * 取消隐藏
     */
    public static Process cancelHidden(String path) {
        if (path == null) return null;
        try {
            return RT.exec("attrib -H \"" + path + "\"");
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拷贝
     */
    public static Process copy(String srcPath, String dstPath) {
        if (srcPath == null || dstPath == null) return null;
        try {
            return RT.exec("cmd /c copy \"" + srcPath + "\" \"" + dstPath + "\"");
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除
     */
    public static Process delete(String path) {
        if (path == null) return null;
        try {
            return RT.exec("cmd /c del \"" + path + "\"");
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process open(String url) {
        if (url == null) return null;
        try {
            return RT.exec("cmd /c start " + url);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process runExe(String exe) {
        if (exe == null) return null;
        try {
            return RT.exec(exe + " /k start");
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process ping(String ip) {
        if (ip == null) return null;
        return exec("cmd /k start cmd.exe /k ping " + ip);
    }

    public static Process exec(String cmd) {
        if (cmd == null) return null;
        try {
            return RT.exec(cmd);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process kill(String app) {
        return kill(app, false);
    }

    public static Process kill(String app, boolean tree) {
        if (app == null) return null;
        try {
            // /t Tree kill: 终止指定的进程和任何由此启动的子进程
            return exec("taskkill " + (tree ? "/t " : "") + "/f /im " + app);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
