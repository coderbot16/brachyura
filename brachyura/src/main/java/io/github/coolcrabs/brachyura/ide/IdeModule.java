package io.github.coolcrabs.brachyura.ide;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.util.Lazy;

public class IdeModule {
    public final String name;
    public final Path root;
    public final Lazy<List<JavaJarDependency>> dependencies;
    public final List<IdeModule> dependencyModules;
    public final List<RunConfig> runConfigs;
    public final List<TestRunConfig> testRunConfigs;
    public final List<Path> sourcePaths;
    public final List<Path> resourcePaths;
    public final int javaVersion;

    IdeModule(String name, Path root, Supplier<List<JavaJarDependency>> dependencies, List<IdeModule> dependencyModules, List<RunConfigBuilder> runConfigs, List<TestRunConfigBuilder> testRunConfigs, List<Path> sourcePaths, List<Path> resourcePaths, int javaVersion) {
        this.name = name;
        this.root = root;
        this.dependencies = new Lazy<>(dependencies);
        this.dependencyModules = dependencyModules;
        this.runConfigs = new ArrayList<>(runConfigs.size());
        for (RunConfigBuilder b : runConfigs) {
            this.runConfigs.add(b.build(this));
        }
        this.testRunConfigs = new ArrayList<>(testRunConfigs.size());
        for (TestRunConfigBuilder b : testRunConfigs) {
            this.testRunConfigs.add(b.build(this));
        }
        this.sourcePaths = sourcePaths;
        this.resourcePaths = resourcePaths;
        this.javaVersion = javaVersion;
    }

    public static class IdeModuleBuilder {
        private String name;
        private Path root;
        private Supplier<List<JavaJarDependency>> dependencies = Collections::emptyList;
        private List<IdeModule> dependencyModules = Collections.emptyList();
        private List<RunConfigBuilder> runConfigs = Collections.emptyList();
        private List<TestRunConfigBuilder> testRunConfigs = Collections.emptyList();
        private List<Path> sourcePaths = Collections.emptyList();
        private List<Path> resourcePaths = Collections.emptyList();
        private int javaVersion = 8;

        /**
         * Creates a new IdeModuleBuilder with default values.
         */
        public IdeModuleBuilder() {
            // no-op
        }

        /**
         * Creates a new IdeModuleBuilder filled in with values from the given built IdeModule.
         *
         * <p>This is useful if you have an IdeModule object already built by another piece of code, but want
         *    to extend or modify it in some way, such as by adding a run config or changing the java version.</p>
         *
         * @param basis The existing IdeModule to take values from
         */
        public IdeModuleBuilder(IdeModule basis) {
            this.name = basis.name;
            this.root = basis.root;
            this.dependencies = basis.dependencies;
            this.dependencyModules = basis.dependencyModules;

            this.runConfigs = new ArrayList<>(basis.runConfigs.size());

            for (RunConfig rc : basis.runConfigs) {
                this.runConfigs.add(new RunConfigBuilder(rc));
            }

            this.testRunConfigs = new ArrayList<>(basis.testRunConfigs.size());

            for (TestRunConfig rc : basis.testRunConfigs) {
                this.testRunConfigs.add(new TestRunConfigBuilder(rc));
            }

            this.sourcePaths = basis.sourcePaths;
            this.resourcePaths = basis.resourcePaths;
            this.javaVersion = basis.javaVersion;
        }

        public IdeModuleBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IdeModuleBuilder root(Path root) {
            this.root = root;
            return this;
        }

        public IdeModuleBuilder dependencies(Supplier<List<JavaJarDependency>> dependencies) {
            this.dependencies = dependencies;
            return this;
        }
        
        public IdeModuleBuilder dependencies(List<JavaJarDependency> dependencies) {
            this.dependencies = () -> dependencies;
            return this;
        }

        public IdeModuleBuilder dependencies(JavaJarDependency... dependencies) {
            this.dependencies = () -> Arrays.asList(dependencies);
            return this;
        }

        public IdeModuleBuilder dependencyModules(List<IdeModule> dependencyModules) {
            this.dependencyModules = dependencyModules;
            return this;
        }

        public IdeModuleBuilder dependencyModules(IdeModule... dependencyModules) {
            this.dependencyModules = Arrays.asList(dependencyModules);
            return this;
        }

        public IdeModuleBuilder runConfigs(List<RunConfigBuilder> runConfigs) {
            this.runConfigs = runConfigs;
            return this;
        }

        public IdeModuleBuilder runConfigs(RunConfigBuilder... runConfigs) {
            this.runConfigs = Arrays.asList(runConfigs);
            return this;
        }

        public IdeModuleBuilder testRunConfigs(List<TestRunConfigBuilder> testRunConfigs) {
            this.testRunConfigs = testRunConfigs;
            return this;
        }

        public IdeModuleBuilder testRunConfigs(TestRunConfigBuilder... testRunConfigs) {
            this.testRunConfigs = Arrays.asList(testRunConfigs);
            return this;
        }

        public IdeModuleBuilder sourcePaths(List<Path> sourcePaths) {
            this.sourcePaths = sourcePaths;
            return this;
        }

        public IdeModuleBuilder sourcePaths(Path... sourcePaths) {
            this.sourcePaths = Arrays.asList(sourcePaths);
            return this; 
        }

        public IdeModuleBuilder sourcePath(Path sourcePath) {
            this.sourcePaths = new ArrayList<>();
            sourcePaths.add(sourcePath);
            return this;
        }

        public IdeModuleBuilder resourcePaths(List<Path> resourcePaths) {
            this.resourcePaths = resourcePaths;
            return this;
        }

        public IdeModuleBuilder resourcePaths(Path... resourcePaths) {
            this.resourcePaths = Arrays.asList(resourcePaths);
            return this;
        }
        
        public IdeModuleBuilder javaVersion(int javaVersion) {
            this.javaVersion = javaVersion;
            return this;
        }

        public IdeModule build() {
            Objects.requireNonNull(name, "IdeModule missing name");
            Objects.requireNonNull(root, "IdeModule missing root");
            return new IdeModule(name, root, dependencies, dependencyModules, runConfigs, testRunConfigs, sourcePaths, resourcePaths, javaVersion);
        }
    }

    public class RunConfig {
        public final String name;
        public final String mainClass;
        public final Path cwd; // Make sure this exists
        public final Lazy<List<String>> vmArgs;
        public final Lazy<List<String>> args;
        public final Lazy<List<Path>> classpath;
        public final List<IdeModule> additionalModulesClasspath;
        public final List<Path> resourcePaths;

        RunConfig(String name, String mainClass, Path cwd, Supplier<List<String>> vmArgs, Supplier<List<String>> args, Supplier<List<Path>> classpath, List<IdeModule> additionalModulesClasspath, List<Path> resourcePaths) {
            this.name = name;
            this.mainClass = mainClass;
            this.cwd = cwd;
            this.vmArgs = new Lazy<>(vmArgs);
            this.args = new Lazy<>(args);
            this.classpath = new Lazy<>(classpath);
            this.additionalModulesClasspath = additionalModulesClasspath;
            this.resourcePaths = resourcePaths;
        }
    }

    public static class RunConfigBuilder {
        private String name;
        private String mainClass;
        private Path cwd;
        private Supplier<List<String>> vmArgs = Collections::emptyList;
        private Supplier<List<String>> args = Collections::emptyList;
        private Supplier<List<Path>> classpath = Collections::emptyList;
        private List<IdeModule> additionalModulesClasspath = Collections.emptyList();
        private List<Path> resourcePaths = Collections.emptyList();

        /**
         * Creates a new RunConfigBuilder filled in with default values.
         */
        public RunConfigBuilder() {

        }

        /**
         * Creates a new RunConfigBuilder with values filled in from the given existing RunConfig object.
         */
        public RunConfigBuilder(RunConfig basis) {
            this.name = basis.name;
            this.mainClass = basis.mainClass;
            this.cwd = basis.cwd;
            this.vmArgs = basis.vmArgs;
            this.args = basis.args;
            this.classpath = basis.classpath;
            this.additionalModulesClasspath = basis.additionalModulesClasspath;
            this.resourcePaths = basis.resourcePaths;
        }

        public RunConfigBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RunConfigBuilder mainClass(String mainClass) {
            this.mainClass = mainClass;
            return this;
        }

        public RunConfigBuilder cwd(Path cwd) {
            this.cwd = cwd;
            return this;
        }

        public RunConfigBuilder vmArgs(Supplier<List<String>> vmArgs) {
            this.vmArgs = vmArgs;
            return this;
        }

        public RunConfigBuilder vmArgs(List<String> vmArgs) {
            this.vmArgs = () -> vmArgs;
            return this;
        }

        public RunConfigBuilder vmArgs(String... vmArgs) {
            this.vmArgs = () -> Arrays.asList(vmArgs);
            return this;
        }

        public RunConfigBuilder args(Supplier<List<String>> args) {
            this.args = args;
            return this;
        }

        public RunConfigBuilder args(List<String> args) {
            this.args = () -> args;
            return this;
        }

        public RunConfigBuilder args(String... args) {
            this.args = () -> Arrays.asList(args);
            return this;
        }

        public RunConfigBuilder classpath(Supplier<List<Path>> classpath) {
            this.classpath = classpath;
            return this;
        }

        public RunConfigBuilder classpath(List<Path> classpath) {
            this.classpath = () -> classpath;
            return this;
        }

        public RunConfigBuilder classpath(Path... classpath) {
            this.classpath = () -> Arrays.asList(classpath);
            return this;
        }

        public RunConfigBuilder additionalModulesClasspath(List<IdeModule> additionalModulesClasspath) {
            this.additionalModulesClasspath = additionalModulesClasspath;
            return this;
        }
        
        public RunConfigBuilder additionalModulesClasspath(IdeModule... additionalModulesClasspath) {
            this.additionalModulesClasspath = Arrays.asList(additionalModulesClasspath);
            return this;
        }
        
        public RunConfigBuilder resourcePaths(List<Path> resourcePaths) {
            this.resourcePaths = resourcePaths;
            return this;
        }

        public RunConfigBuilder resourcePaths(Path... resourcePaths) {
            this.resourcePaths = Arrays.asList(resourcePaths);
            return this;
        }

        RunConfig build(IdeModule project) {
            Objects.requireNonNull(name, "Null name");
            Objects.requireNonNull(mainClass, "Null mainClass");
            Objects.requireNonNull(cwd, "Null cwd");
            return project.new RunConfig(name, mainClass, cwd, vmArgs, args, classpath, additionalModulesClasspath, resourcePaths);
        }
    }

    public class TestRunConfig {
        public final String name;
        public final String testPackage;
        public final Path cwd; // Make sure this exists
        public final Lazy<List<String>> vmArgs;
        public final Lazy<List<Path>> classpath;
        public final List<IdeModule> additionalModulesClasspath;
        public final List<Path> resourcePaths;

        TestRunConfig(String name, String testPackage, Path cwd, Supplier<List<String>> vmArgs, Supplier<List<Path>> classpath, List<IdeModule> additionalModulesClasspath, List<Path> resourcePaths) {
            this.name = name;
            this.testPackage = testPackage;
            this.cwd = cwd;
            this.vmArgs = new Lazy<>(vmArgs);
            this.classpath = new Lazy<>(classpath);
            this.additionalModulesClasspath = additionalModulesClasspath;
            this.resourcePaths = resourcePaths;
        }
    }

    public static class TestRunConfigBuilder {
        private String name;
        private String testPackage;
        private Path cwd;
        private Supplier<List<String>> vmArgs = Collections::emptyList;
        private Supplier<List<Path>> classpath = Collections::emptyList;
        private List<IdeModule> additionalModulesClasspath = Collections.emptyList();
        private List<Path> resourcePaths = Collections.emptyList();

        /**
         * Creates a new TestRunConfigBuilder filled in with default values.
         */
        public TestRunConfigBuilder() {

        }

        /**
         * Creates a new TestRunConfigBuilder with values filled in from the given existing TestRunConfig object.
         */
        public TestRunConfigBuilder(TestRunConfig basis) {
            this.name = basis.name;
            this.testPackage = basis.testPackage;
            this.cwd = basis.cwd;
            this.vmArgs = basis.vmArgs;
            this.classpath = basis.classpath;
            this.additionalModulesClasspath = basis.additionalModulesClasspath;
            this.resourcePaths = basis.resourcePaths;
        }

        public TestRunConfigBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TestRunConfigBuilder testPackage(String testPackage) {
            this.testPackage = testPackage;
            return this;
        }

        public TestRunConfigBuilder cwd(Path cwd) {
            this.cwd = cwd;
            return this;
        }

        public TestRunConfigBuilder vmArgs(Supplier<List<String>> vmArgs) {
            this.vmArgs = vmArgs;
            return this;
        }

        public TestRunConfigBuilder vmArgs(List<String> vmArgs) {
            this.vmArgs = () -> vmArgs;
            return this;
        }

        public TestRunConfigBuilder vmArgs(String... vmArgs) {
            this.vmArgs = () -> Arrays.asList(vmArgs);
            return this;
        }

        public TestRunConfigBuilder classpath(Supplier<List<Path>> classpath) {
            this.classpath = classpath;
            return this;
        }

        public TestRunConfigBuilder classpath(List<Path> classpath) {
            this.classpath = () -> classpath;
            return this;
        }

        public TestRunConfigBuilder classpath(Path... classpath) {
            this.classpath = () -> Arrays.asList(classpath);
            return this;
        }

        public TestRunConfigBuilder additionalModulesClasspath(List<IdeModule> additionalModulesClasspath) {
            this.additionalModulesClasspath = additionalModulesClasspath;
            return this;
        }

        public TestRunConfigBuilder additionalModulesClasspath(IdeModule... additionalModulesClasspath) {
            this.additionalModulesClasspath = Arrays.asList(additionalModulesClasspath);
            return this;
        }

        public TestRunConfigBuilder resourcePaths(List<Path> resourcePaths) {
            this.resourcePaths = resourcePaths;
            return this;
        }

        public TestRunConfigBuilder resourcePaths(Path... resourcePaths) {
            this.resourcePaths = Arrays.asList(resourcePaths);
            return this;
        }

        TestRunConfig build(IdeModule project) {
            Objects.requireNonNull(name, "Null name");
            Objects.requireNonNull(testPackage, "Null testPackage");
            Objects.requireNonNull(cwd, "Null cwd");
            return project.new TestRunConfig(name, testPackage, cwd, vmArgs, classpath, additionalModulesClasspath, resourcePaths);
        }
    }
}
