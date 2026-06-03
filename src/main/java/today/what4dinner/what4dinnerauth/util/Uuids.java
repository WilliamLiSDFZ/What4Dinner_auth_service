package today.what4dinner.what4dinnerauth.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import java.util.UUID;

/**
 * Generates UUIDv7 identifiers (time-ordered). All application-generated ids use v7
 * so primary keys sort by creation time. The generator is thread-safe and reused.
 */
public final class Uuids {

    private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();

    private Uuids() {
    }

    public static UUID v7() {
        return GENERATOR.generate();
    }
}
