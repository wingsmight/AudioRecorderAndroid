package com.wingsmight.audiorecorder;

public class CloudStoragePlan {
//    public enum Plan {
//        case free200MB
//        case paid2GB
//
//
//        public var size: Int {
//            switch self {
//                case .free200MB:
//                return 200 * 1024 * 1024
//                case .paid2GB:
//                return 2 * 1024 * 1024 * 1024
//            }
//        }
//    }
    public static int getSize(Plan plan) {
        switch (plan){
            case free200MB:
                return 200 * 1024 * 1024;
            case paid2GB:
                return 2 * 1024 * 1024 * 1024;
        }

        return 200 * 1024 * 1024;
    }

    public enum Plan {
        free200MB,
        paid2GB
    }
}
