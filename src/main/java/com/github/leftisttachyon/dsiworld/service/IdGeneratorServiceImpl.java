package com.github.leftisttachyon.dsiworld.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * An implementation of the {@link IdGeneratorService} interface.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Service
@Scope("singleton")
public class IdGeneratorServiceImpl implements IdGeneratorService {
    /**
     * A set of already claimed id's
     */
    private HashSet<Integer> claimed;

    /**
     * Creates a new IdGeneratorServiceImpl instance.
     */
    public IdGeneratorServiceImpl() {
    }

    @Override
    public synchronized String getNextID() {
        long id;
        do {
            id = (long) (Math.random() * 4_738_381_338_321_570_240L + 46_656L);
        } while (claimed.contains(id));
        return Long.toString(id, 36);
    }
}
