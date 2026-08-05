/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.sql;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Regression guard for the JUnit3 -> JUnit5 migration defect where {@code public void testXxx()}
 * methods silently stopped executing because they were never given a {@code @Test} annotation
 * (Jupiter only runs annotated methods; the old TestCase naming convention no longer applies).
 *
 * <p>Scoped to {@code bvt/sql} — the parser/dialect round-trip suite. Fails the build if any
 * no-argument {@code test*} method in that tree lacks a preceding annotation, so the safety net
 * can never silently erode again. Connection-dependent suites (bvt/pool, ...) are out of scope.
 */
public class SqlTestAnnotationGuardTest {
    private static final Pattern TEST_METHOD =
            Pattern.compile("^\\s*public\\s+void\\s+(test\\w*)\\s*\\(\\s*\\)");

    @Test
    public void everyTestMethodIsAnnotated() throws IOException {
        Path root = Paths.get("src/test/java/com/alibaba/druid/bvt/sql");
        assumeTrue(Files.isDirectory(root),
                "bvt/sql source tree not found from cwd=" + System.getProperty("user.dir"));

        List<String> violations = new ArrayList<>();
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> scan(p, violations));
        }

        if (!violations.isEmpty()) {
            fail("Found " + violations.size() + " test method(s) without @Test in bvt/sql"
                    + " — they would be silently skipped. Add @Test:\n  "
                    + String.join("\n  ", violations));
        }
    }

    private static void scan(Path file, List<String> violations) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException("cannot read " + file, e);
        }
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = TEST_METHOD.matcher(lines.get(i));
            if (!m.find()) {
                continue;
            }
            if (!precededByAnnotation(lines, i)) {
                violations.add(file + ":" + (i + 1) + "  " + m.group(1) + "()");
            }
        }
    }

    private static boolean precededByAnnotation(List<String> lines, int methodIdx) {
        for (int j = methodIdx - 1; j >= 0; j--) {
            String s = lines.get(j).trim();
            if (s.isEmpty()) {
                continue;
            }
            return s.startsWith("@");
        }
        return false;
    }
}
