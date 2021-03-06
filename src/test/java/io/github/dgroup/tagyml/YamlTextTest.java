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
package io.github.dgroup.tagyml;

import io.github.dgroup.tagyml.text.YamlText;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.llorllale.cactoos.matchers.TextIs;

/**
 * Test case for {@link YamlText}.
 *
 * @since 0.1.0
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class YamlTextTest {

    /**
     * The junit rule to handle the exception thrown within the unit tests.
     */
    @Rule
    public ExpectedException cause = ExpectedException.none();

    @Test
    public void text() {
        MatcherAssert.assertThat(
            new YamlText(
                Paths.get("src", "test", "resources", "yaml", "text.yaml")
            ),
            new TextIs("version: 1.0")
        );
    }

    @Test
    public void missingFile() throws YamlFormatException {
        this.cause.expect(YamlFormatException.class);
        this.cause.expectMessage("teams.yaml doesn't exist");
        new YamlText(
            Paths.get("src", "test", "teams.yaml")
        ).asString();
    }

    @Test
    public void tostring() {
        MatcherAssert.assertThat(
            new YamlText(
                Paths.get("src", "test", "resources", "yaml", "text.yaml")
            ).toString(),
            new StringContains("version: 1.0")
        );
    }

}
