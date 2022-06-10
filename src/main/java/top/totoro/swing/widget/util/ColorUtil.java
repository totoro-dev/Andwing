package top.totoro.swing.widget.util;

import java.awt.*;

public class ColorUtil {

    /**Converts opacity color (0-255) to opacity value (0-1)
     *
     * @param opacity as integer value (0-255)
     * @return float containing converted opacity value (0-1)
     */
    public static float getOpacityValue(int opacity)
    {
        //Returns more or less the correct, capped value
        //Just ignore it, it works, leave it :D
        return capFloat((3.9216f*opacity)/1000f, 0.0f, 1.0f);
    }

    /**Returns float capped between minimum and maximum value
     *
     * @param value as original value
     * @param min as minimum cap value
     * @param max as maximum cap value
     * @return float containing capped value
     */
    public static float capFloat(float value, float min, float max)
    {
        if(value < min) value = min;
        else if(value > max) value = max;

        return value;
    }

    /**Merges color and opacity to new color
     *
     * @param bg as color for old color, only RGB will be used from that
     * @return color with RGB from bg and A from opacity of frame
     */
    public static Color transparencyColor(Color bg, float opacity)
    {
        return new Color(getOpacityValue(bg.getRed()), getOpacityValue(bg.getGreen()), getOpacityValue(bg.getBlue()), opacity);
    }

}
