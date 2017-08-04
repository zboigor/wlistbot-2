package com.zboigor.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Igor Zboichik
 * @since 2017-03-28
 */
@Data
@AllArgsConstructor
public class Pair<F, S> {

    private F first;
    private S second;
}
