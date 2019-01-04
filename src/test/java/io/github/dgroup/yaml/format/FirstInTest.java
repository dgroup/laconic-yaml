/*
 * MIT License
 *
 * Copyright (c) 2018-2019 Yurii Dubinka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom
 * the Software is  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.dgroup.yaml.format;

import io.github.dgroup.yaml.YamlFormatException;
import org.cactoos.Scalar;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.llorllale.cactoos.matchers.ScalarHasValue;

/**
 * Test case for {@link FirstIn}.
 *
 * @since 0.1.0
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class FirstInTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void value() {
        MatcherAssert.assertThat(
            new FirstIn<>(
                (Scalar<Integer>) () -> {
                    throw new IllegalArgumentException("1");
                },
                () -> {
                    throw new IllegalArgumentException("2");
                },
                () -> 3,
                () -> 4
            ),
            new ScalarHasValue<>(3)
        );
    }

    @Test
    public void alternative() throws Exception {
        this.exception.expect(YamlFormatException.class);
        this.exception.expectMessage("The file has unsupported format");
        new FirstIn<>(
            new TextOf("The file has unsupported format"),
            () -> {
                throw new IllegalArgumentException("Can't parse version 1");
            },
            () -> {
                throw new IllegalArgumentException("Can't parse version 2");
            }
        ).value();
    }
}