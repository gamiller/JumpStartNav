package edu.dartmouth.cs.gracemiller.jumpstartnav.View;

/**
 * Auto-created classifier by Weka program to match accelerometer movements with
 * jumping jacks and sleeping motions. Used by MovementActivity.java.
 */

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N772c84140(i);
        return p;
    }
    static double N772c84140(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 277.068026) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 277.068026) {
            p = 1;
        }
        return p;
    }
}
