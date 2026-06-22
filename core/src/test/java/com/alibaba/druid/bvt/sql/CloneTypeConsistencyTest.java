package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.sql.ast.SQLObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Systemic guard (review item MID1) against AST clone() type-slicing: reflectively loads every
 * compiled {@link SQLObject} class that overrides clone() and is no-arg constructible, clones an
 * instance, and fails if any clone returns null, returns a different runtime type than the original
 * (type-slicing — drops dialect-specific fields), or returns the same instance.
 *
 * <p>Classes whose clone() legitimately throws (the separate "clone not yet implemented" debt) or
 * that lack a no-arg constructor are out of scope and skipped — this test enforces only that a
 * clone() which DOES produce an object produces the right kind of object.
 */
public class CloneTypeConsistencyTest {
    @Test
    public void overriddenClonePreservesRuntimeType() throws IOException {
        Path root = Paths.get("target/classes/com/alibaba/druid/sql");
        assumeTrue(Files.isDirectory(root), "compiled classes not found at " + root.toAbsolutePath());

        List<Path> classFiles;
        try (Stream<Path> s = Files.walk(root)) {
            classFiles = s.filter(p -> p.toString().endsWith(".class") && !p.toString().contains("$"))
                    .sorted().collect(Collectors.toList());
        }

        List<String> violations = new ArrayList<>();
        int checked = 0;
        for (Path p : classFiles) {
            String tail = p.toString().substring(p.toString().indexOf("/com/alibaba/druid/sql"));
            String fqn = tail.substring(1).replace(".class", "").replace('/', '.');

            Class<?> cls;
            try {
                cls = Class.forName(fqn, false, getClass().getClassLoader());
            } catch (Throwable t) {
                continue;
            }
            if (!SQLObject.class.isAssignableFrom(cls)
                    || Modifier.isAbstract(cls.getModifiers()) || cls.isInterface()) {
                continue;
            }
            Method clone;
            Constructor<?> ctor;
            try {
                clone = cls.getDeclaredMethod("clone");
                ctor = cls.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                continue; // does not override clone() or has no no-arg constructor → out of scope
            }
            SQLObject obj;
            Object cloned;
            try {
                ctor.setAccessible(true);
                obj = (SQLObject) ctor.newInstance();
                clone.setAccessible(true);
                cloned = clone.invoke(obj);
            } catch (Throwable t) {
                continue; // constructor/clone throws (separate "not implemented" debt) → out of scope
            }
            checked++;
            if (cloned == null) {
                violations.add(fqn + ": clone() returned null");
            } else if (cloned == obj) {
                violations.add(fqn + ": clone() returned the same instance");
            } else if (cloned.getClass() != cls) {
                violations.add(fqn + ": clone() type-sliced to " + cloned.getClass().getName());
            }
        }

        assertTrue(violations.isEmpty(),
                "clone() type-consistency violations (checked " + checked + " classes):\n  "
                        + String.join("\n  ", violations));
    }
}
