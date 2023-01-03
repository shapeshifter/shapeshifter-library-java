import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

@Mojo(name = "uftp-processor", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class UftpProcessorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "uftpApiPackage")
    String uftpApiPackage;

    @Parameter(property = "uftpMessageBaseClass")
    String uftpMessageBaseClass;

    @Parameter(property = "uftpAnnotationOutputPackage")
    String uftpAnnotationOutputPackage;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/uftp", required = true)
    private File outputJavaDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (this.project != null) {
                this.project.addCompileSourceRoot(this.outputJavaDirectory.getAbsolutePath());
            }
            this.outputJavaDirectory.mkdirs();
            generateJavaCode();
        } catch (Exception cause) {
            throw new MojoExecutionException("Could not generate Java source code!", cause);
        }
    }

    private void generateJavaCode() {
        try {
            Class<?> baseClass = Class.forName(this.uftpApiPackage + "." + this.uftpMessageBaseClass);
            Set<String> typeNames = findTypesForAnnotations(baseClass);
            for (String typeName : typeNames) {
                createAnnotationForType(typeName);
            }
        } catch (Exception cause) {
            throw new RuntimeException("Failed to find classes", cause);
        }
    }

    private <T> Set<String> findTypesForAnnotations(Class<T> baseClass) {
        try {
            Reflections reflections = new Reflections(this.uftpApiPackage);
            Set<Class<? extends T>> classes = reflections.getSubTypesOf(baseClass);
            Set<String> result = new HashSet<>();
            for (Class<? extends T> clazz : classes) {
                if (isNotAbstract(clazz)) {
                    result.add(clazz.getSimpleName());
                }
            }
            return result;
        } catch (Exception cause) {
            throw new RuntimeException("Failed to find classes", cause);
        }
    }

    private <T> boolean isNotAbstract(Class<? extends T> aClass) {
        return !Modifier.isAbstract(aClass.getModifiers());
    }

    private void createAnnotationForType(String typeName) {
        getLog().info("Generating annotation for UFTP type " + typeName);

        String annotationName = typeNameToAnnotationName(typeName);
        getLog().info("annotationName " + annotationName);
        try (Writer out = openOutputFile(annotationName)) {
            out.write("package " + this.uftpAnnotationOutputPackage + ";\n");
            out.write("\n");
            out.write("import " + this.uftpApiPackage + "." + typeName + ";\n");
            out.write("import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpMapping;\n");
            out.write("import java.lang.annotation.Documented;\n");
            out.write("import java.lang.annotation.ElementType;\n");
            out.write("import java.lang.annotation.Retention;\n");
            out.write("import java.lang.annotation.RetentionPolicy;\n");
            out.write("import java.lang.annotation.Target;\n");
            out.write("\n");
            out.write("@Target({ElementType.METHOD})\n");
            out.write("@Retention(RetentionPolicy.RUNTIME)\n");
            out.write("@Documented\n");
            out.write("@UftpMapping(\n");
            out.write("    type = " + typeName + ".class\n");
            out.write(")\n");
            out.write("public @interface " + annotationName + " {\n");
            out.write("\n");
            out.write("}\n");

            out.flush();
        } catch (IOException ignoreExceptionOnClose) {
        }
    }

    private String typeNameToAnnotationName(String typeName) {
        if (typeName.endsWith("Type")) {
            return typeName.replace("Type", "Mapping");
        }
        return typeName + "Mapping";
    }

    private Writer openOutputFile(String annotationName) {
        File destinationLocation = new File(outputJavaDirectory.getAbsolutePath(), uftpAnnotationOutputPackage.replace('.', '/'));
        destinationLocation.mkdirs();
        String fileName = annotationName + ".java";
        File outputFile = new File(destinationLocation.toString(), fileName);
        try {
            getLog().info("Generating annotation in " + outputFile);
            return new FileWriter(outputFile);
        } catch (IOException cause) {
            throw new RuntimeException(cause);
        }
    }
}