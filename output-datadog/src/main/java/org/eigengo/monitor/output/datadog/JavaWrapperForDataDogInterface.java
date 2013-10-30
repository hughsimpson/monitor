package org.eigengo.monitor.output.datadog;

import org.eigengo.monitor.output.CounterInterface;
import clojure.lang.Compiler;

public class JavaWrapperForDataDogInterface implements CounterInterface {
//    String clojureScript = "(ns user) (def hello (fn [] \"Hello world\"))";
//    String notionalScriptFileName="hello.clj";
//    String outputDirectory="/my/output/dir";
//
//    Var compilePath = RT.var("clojure.core", "*compile-path*");
//    Var compileFiles = RT.var("clojure.core", "*compile-files*");
//
//    Var.pushThreadBindings(RT.map(compilePath, outputDirectory, compileFiles, Boolean.TRUE));
//    Compiler.compile(new StringReader(clojureScript), notionalScriptFileName, notionalScriptFileName);
//    Var.popThreadBindings();

    @Override
    public void incrementCounter(String aspect, String... tags) {
//        org.eigengo.monitor.output.DataDogCounterInterface.incrementCounter(aspect, tags);
        // noop
    }

    @Override
    public void decrementCounter(String aspect, String... tags) {
        // noop
    }

    @Override
    public void recordGaugeValue(String aspect, int value, String... tags) {
        // noop
    }

    @Override
    public void recordExecutionTime(String aspect, int duration, String... tags) {
        // noop
    }
}