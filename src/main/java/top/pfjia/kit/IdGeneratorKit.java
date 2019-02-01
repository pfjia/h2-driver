package top.pfjia.kit;

import top.pfjia.protocol.enums.IdType;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author pfjia
 * @since 2019/2/1 20:18
 */
public class IdGeneratorKit {
    private static final AtomicIntegerArray ID = new AtomicIntegerArray(IdType.values().length);


    public static int getNextId(IdType idType) {
        return ID.getAndIncrement(idType.getId());
    }
}
