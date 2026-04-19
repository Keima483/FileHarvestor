package com.keima.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig  {

    public static final Path SOURCE_ROOT = Paths.get("D:\\Temp\\Sorted");
    public static final Path INPUT_BASE = Paths.get("D:\\Temp\\Storage\\Input");
    public static final Path OUTPUT_BASE = Paths.get("D:\\Temp\\Storage\\Output");

//    public static final Path TEMP_DIR = TEMP_BASE.resolve("Hoard");

//    public static final String[] TEMP_FOLDERS = {
//            "Asset", "Drift", "Hoard", "Loom", "Prime", "Probe", "Resolve"
//    };

//    public static final String[] TEMP_FOLDERS = {
//            "HTTP", "SMails", "MMails"
//    };

    public enum PROTO_FOLDERS {
        HTTP,
        SMails,
        MMails
    }

    public static final String[] SOURCE_FOLDERS = {
            "ADON","CVBX","GCPT","JXYL","KNCF",
            "MGLI","MVNT","NQSQ","NRWT","QEZJ",
            "QTTS","QUEI","RCZR","TSJL","TSKC",
            "UIDM","WBZB","WMUH","XCAF","ZKGM"
    };

}
