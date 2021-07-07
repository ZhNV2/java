package ru.spbau.zhidkov;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Class for handling test classes
 */
public class Junit {

    private final static List<Class<? extends Annotation>> ANNOTATIONS
            = Arrays.asList(After.class, Before.class, Test.class, AfterClass.class, BeforeClass.class);


    /**
     * Execute test process detected from given not null object
     *
     * @param o object containing tests
     */
    public void execTests(@NotNull Object o) {
        try {
            Map<Class<? extends Annotation>, List<Method>> annotationMap = buildAnnotationMap(o.getClass().getDeclaredMethods());
            execMethods(annotationMap, BeforeClass.class, o);
            for (Method method : annotationMap.getOrDefault(Test.class, new ArrayList<>())) {
                execMethods(annotationMap, Before.class, o);
                testMethod(method, o);
                execMethods(annotationMap, After.class, o);
            }
            execMethods(annotationMap, AfterClass.class, o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<Class<? extends Annotation>, List<Method>> buildAnnotationMap(Method[] methods) {
        Map<Class<? extends Annotation>, List<Method>> annotationMap = new HashMap<>();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                for (Class<? extends Annotation> annotation : ANNOTATIONS) {
                    if (method.isAnnotationPresent(annotation)) {
                        List<Method> annotationMethods = annotationMap.getOrDefault(annotation, new ArrayList<>());
                        annotationMethods.add(method);
                        annotationMap.put(annotation, annotationMethods);
                    }
                }
            }
        }
        return annotationMap;
    }

    private void execMethods(Map<Class<? extends Annotation>, List<Method>> annotationMap,
                             Class<? extends Annotation> clazz, Object o) {
        for (Method method : annotationMap.getOrDefault(clazz, new ArrayList<>())) {
            try {
                method.invoke(o);
            } catch (Exception e) {
                System.out.println(method.getName() + " failed with exception " + e.getCause().getClass().getName());
            }
        }
    }

    private void testMethod(Method method, Object o) {
        if (method.isAnnotationPresent(Ignore.class)) {
            String cause = (method.getAnnotation(Ignore.class)).cause();
            System.out.println(method.getName() + " was ignored because of " + cause);
        } else {
            System.out.println(method.getName() + " method started");
            long curTime = System.currentTimeMillis();
            Class<? extends Throwable> expectedException = null;
            if (method.isAnnotationPresent(Expected.class)) {
                expectedException = method.getAnnotation(Expected.class).expectedException();
            }
            Class<? extends Throwable> runException = null;
            try {
                method.invoke(o);
                //System.out.println(method.getName() + " OK");
            } catch (Exception e) {
                runException = e.getCause().getClass();
                //System.out.println(method.getName() + " failed with exception " + e.getCause().getClass().getName());
            }
            if (expectedException != null) {
                if (runException == null) {
                    fail(method, "expected = " + expectedException.getName() + " but no exception was caught");
                } else if (!runException.equals(expectedException)) {
                    fail(method, "expected = " + expectedException.getName() + " but " + runException.getName() + " was caught");
                } else {
                    ok(method);
                }
            } else {
                if (runException == null) {
                    ok(method);
                } else {
                    fail(method, "exception " + runException.getName() + " was caught");
                }

            }
            long newTime = System.currentTimeMillis();
            System.out.println(method.getName() + " method took " + (newTime - curTime) + "ms");
        }
    }

    private void fail(Method method, String cause) {
        System.out.println(method.getName() + " failed because " + cause);
    }

    private void ok(Method method) {
        System.out.println(method.getName() + " OK");
    }


    @Parameter(names = "--help", help = true, description = "help information")
    private boolean help;

    private final static String HELP = "use --help for more information";

    @Parameter(names = "--dir", required = true, description = "folder where class lies")
    private String classFolder;

    @Parameter(names = "--class", required = true, description = "class name (with package)")
    private String className;

    /**
     * Presents command-line handling for testing class.
     * First arg is folder with class, second arg is class name.
     * @param args command line args
     */
    public static void main(String[] args) {
        try {
            Junit junit = new Junit();
            JCommander jCommander = new JCommander(junit, args);
            junit.execute(jCommander);
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
            printHelp();
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            printHelp();
        } catch (Exception e) {
            System.out.println("error, " + e.getClass() + " " + e.getCause());
            printHelp();
        }
    }

    private void execute(JCommander jCommander) throws MalformedURLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        if (help) {
            jCommander.usage();
            return;
        }
        ClassLoader cl = new URLClassLoader(new URL[]{new File(classFolder).toURI().toURL()});
        execTests(cl.loadClass(className).newInstance());
    }

    private static void printHelp() {
        System.out.println(HELP);
    }
}
