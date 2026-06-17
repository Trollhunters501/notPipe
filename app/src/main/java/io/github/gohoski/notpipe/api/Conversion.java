package io.github.gohoski.notpipe.api;

import java.io.IOException;

/**
 * Created by Gleb on 16.05.2026.
 */

public interface Conversion {
    /**
     * @param codec 0 = MPEG-4 Visual (a.k.a. DivX, Xvid); 1 = H.263
     * @return Converted video URL
     */
    String getConvUrl(String id, int codec) throws IOException;
}
