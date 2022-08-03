package com.tplinkra.common.color;

public class ColorUtils {
//    /* renamed from: a */
//    public static float[] m47671a(int i, int i2, int i3) {
//        int i4 = i > i2 ? i : i2;
//        if (i3 > i4) {
//            i4 = i3;
//        }
//        int i5 = i < i2 ? i : i2;
//        if (i3 < i5) {
//            i5 = i3;
//        }
//        float f = (float) i4;
//        float f2 = f / 255.0f;
//        float f3 = SystemUtils.JAVA_VERSION_FLOAT;
//        float f4 = i4 != 0 ? ((float) (i4 - i5)) / f : SystemUtils.JAVA_VERSION_FLOAT;
//        if (f4 != SystemUtils.JAVA_VERSION_FLOAT) {
//            float f5 = (float) (i4 - i5);
//            float f6 = ((float) (i4 - i)) / f5;
//            float f7 = ((float) (i4 - i2)) / f5;
//            float f8 = ((float) (i4 - i3)) / f5;
//            float f9 = (i == i4 ? f8 - f7 : i2 == i4 ? (f6 + 2.0f) - f8 : (f7 + 4.0f) - f6) / 6.0f;
//            f3 = f9 < SystemUtils.JAVA_VERSION_FLOAT ? f9 + 1.0f : f9;
//        }
//        return new float[]{f3, f4, f2};
//    }
//
//    /* renamed from: b */
//    private static int[] m47677b(float f, float f2, float f3) {
//        return new int[]{(int) (((double) (f * 255.0f)) + 0.5d), (int) (((double) (f2 * 255.0f)) + 0.5d), (int) (((double) (f3 * 255.0f)) + 0.5d)};
//    }
//
//    /* renamed from: a */
//    public static int[] m47675a(float[] fArr) {
//        return new int[]{Integer.valueOf(new Float(fArr[0] * 360.0f).intValue()).intValue(), Integer.valueOf(new Float(fArr[1] * 100.0f).intValue()).intValue(), Integer.valueOf(new Float(fArr[2] * 100.0f).intValue()).intValue()};
//    }
//
//    /* renamed from: a */
//    public static int[] m47674a(String str) {
//        return m47675a(m47676b(str));
//    }
//
//    /* renamed from: b */
//    public static float[] m47676b(String str) {
//        return m47672a(m47678c(str));
//    }
//
//    /* renamed from: c */
//    public static RGB m47678c(String str) {
//        String leftPad = StringUtils.leftPad(str, 6, '0');
//        if (!leftPad.startsWith(UserProfile.SEPARATOR)) {
//            leftPad = UserProfile.SEPARATOR + leftPad;
//        }
//        return new RGB(Integer.valueOf(leftPad.substring(1, 3), 16), Integer.valueOf(leftPad.substring(3, 5), 16), Integer.valueOf(leftPad.substring(5, 7), 16));
//    }
//
//    /* renamed from: a */
//    public static int[] m47673a(float f, float f2, float f3) {
//        float floor = (f - ((float) Math.floor((double) f))) * 6.0f;
//        int i = (int) floor;
//        float f4 = floor - ((float) i);
//        float f5 = (1.0f - f2) * f3;
//        float f6 = (1.0f - (f4 * f2)) * f3;
//        float f7 = (1.0f - ((1.0f - f4) * f2)) * f3;
//        if (i == 0) {
//            return m47677b(f3, f7, f5);
//        }
//        if (i == 1) {
//            return m47677b(f6, f3, f5);
//        }
//        if (i == 2) {
//            return m47677b(f5, f3, f7);
//        }
//        if (i == 3) {
//            return m47677b(f5, f6, f3);
//        }
//        if (i == 4) {
//            return m47677b(f7, f5, f3);
//        }
//        if (i != 5) {
//            return null;
//        }
//        return m47677b(f3, f5, f6);
//    }
//
//    /* renamed from: a */
//    public static float[] m47672a(RGB rgb) {
//        return m47671a(rgb.getRed().intValue(), rgb.getGreen().intValue(), rgb.getBlue().intValue());
//    }
}
